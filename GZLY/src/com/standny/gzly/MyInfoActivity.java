package com.standny.gzly;

import java.io.ByteArrayOutputStream;
import java.io.File;
import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.ImageDownloadCallback;
import com.standny.gzly.repository.UserAPI;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import com.standny.gzly.repository.DateTime;

public class MyInfoActivity extends MasterActivity {	

	/*拍照的照片存储位置*/  
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	
	private TextView lbl_account;
	private EditText input_nickname;
	private EditText input_realname;
	private EditText input_idcard;
	private EditText input_tel;
	private ImageView user_face;
	private Button btn_save;
	private Button btn_cancel;
	private Button btn_camera;
	private Button btn_photoAlbum;
	
	private File mCurrentPhotoFile;//照相机拍照得到的图片
	private String photoBase64;//保存当前设置头像图片的base64Code
	private boolean isRunning;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_info);
		tabIndex=4;
		isRunning=false;
		lbl_account=(TextView)findViewById(R.id.lbl_account);
		input_nickname=(EditText)findViewById(R.id.input_nickname);
		input_realname=(EditText)findViewById(R.id.input_realname);
		input_idcard=(EditText)findViewById(R.id.input_idcard);
		input_tel=(EditText)findViewById(R.id.input_tel);
		user_face=(ImageView)findViewById(R.id.image);
		btn_save=(Button)findViewById(R.id.btn_save);
		btn_cancel=(Button)findViewById(R.id.btn_cancel);
		btn_camera=(Button)findViewById(R.id.btn_camera);
		btn_photoAlbum=(Button)findViewById(R.id.btn_photo_album);
		user_face.setAdjustViewBounds(true);
		user_face.setMaxWidth(userFace_size.Width);
		user_face.setMaxHeight(userFace_size.Height);
		user_face.setScaleType(ScaleType.CENTER_CROP);
		btn_save.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				saveInfo();
			}
			
		});
		
		btn_cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				cancel();
			}
			
		});
		
		btn_camera.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				tackPhoto();
			}
			
		});
		btn_photoAlbum.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				doPickPhotoFromGallery();
			}
			
		});
		showInfo();
	}
	
	private void saveInfo(){
		if (isRunning){
			Toast.makeText(MyInfoActivity.this, "保存中,请稍候...", Toast.LENGTH_LONG).show();
			return;
		}
		btn_save.setText("保存中...");
		isRunning=true;
		user.setIDcard(input_idcard.getText().toString());
		user.setNickName(input_nickname.getText().toString());
		user.setRealName(input_realname.getText().toString());
		user.setTel(input_tel.getText().toString());
		UserAPI api = new UserAPI();
		api.BeginSaveUserInfo(user, photoBase64, new APICallback<String>() {

			@Override
			public void onCompleted(APIResultModel<String> result) {
				isRunning=false;
				btn_save.setText(R.string.btn_save);
				if (result.getSuc()){
					if (result.getItems()!=null&&result.getItems().length()>0&&!result.getItems().equalsIgnoreCase("null"))
						user.setUserFace(result.getItems());
					UserAPI.SaveUserInfo(user, getApplicationContext());
					Toast.makeText(MyInfoActivity.this, "保存成功!", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(MyInfoActivity.this, result.getMsg(), Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onError(String msg) {
				isRunning=false;
				btn_save.setText(R.string.btn_save);
				AlertDialog.Builder builder = new Builder(MyInfoActivity.this);
				builder.setMessage(msg);
				builder.setTitle("保存资料失败");
				builder.setPositiveButton("重试", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						saveInfo();
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				try{
				builder.create().show();
				}
				catch(Exception e){}
			}
		});
	}
	
	private void showInfo(){
		if (isLogged){
			lbl_account.setText(user.getAccount());
			input_realname.setText(user.getRealName());
			input_nickname.setText(user.getNickName());
			input_idcard.setText(user.getIDcard());
			input_tel.setText(user.getTel());
			loadPic(user.getUserFace());
		}
		else{
			Login();
		}
	}
	


	private void loadPic(String picPath) {
		if (picPath==null) return;
		Bitmap bmp = imgManager.GetImage(picPath, userFace_size, 0,
				new ImageDownloadCallback() {
					@Override
					public void onCompleted(Bitmap bmp, int tag) {
						user_face.setImageBitmap(bmp);
					}

					@Override
					public void onError(String msg, int tag) {
					}

				});
		if (bmp != null) {
			user_face.setImageBitmap(bmp);
		}
	}
	
	private void tackPhoto(){
        String status=Environment.getExternalStorageState();  
        if(status.equals(Environment.MEDIA_MOUNTED)){//判断是否有SD卡  
	        try {  
	            // Launch camera to take photo for selected contact  
	            PHOTO_DIR.mkdirs();// 创建照片的存储目录 
	            //photoPath=MainActivity.this.getFilesDir().getPath().toString()+"/"+URLEncoder.encode(getPhotoFileName());
	            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名  
	        	//ShowMsg(Uri.fromFile(mCurrentPhotoFile).toString()); 
	            final Intent intent = getTakePickIntent(mCurrentPhotoFile);  
	            startActivityForResult(intent, DIALOG_CAMERA);
	        } catch (ActivityNotFoundException e) {  
	            Toast.makeText(this, e.getLocalizedMessage(),  
	                    Toast.LENGTH_LONG).show();  
	            e.printStackTrace();
	        }  
        }  
        else{  
        	new AlertDialog.Builder(this).setTitle("消息").setMessage("没有SD卡").setPositiveButton("确定", null).show(); 
        }  
	}
    public static Intent getTakePickIntent(File f) {  
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));  
        return intent;  
    }  
	private String getPhotoFileName() {  
        return DateTime.now().toString("'IMG'_yyyy-MM-dd_HH_mm_ss'.jpg'");  
    } 
	 
    // 请求Gallery程序  
    protected void doPickPhotoFromGallery() {  
        try {  
            // Launch picker to choose photo for selected contact  
            final Intent intent = getPhotoPickIntent();  
            startActivityForResult(intent, DIALOG_PHOTOALBUM);  
        } catch (ActivityNotFoundException e) {  
            Toast.makeText(this,e.getLocalizedMessage(),  
                    Toast.LENGTH_LONG).show();  
            e.printStackTrace();
        }  
    }  
    // 封装请求Gallery的intent  
    public static Intent getPhotoPickIntent() {  
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);  
        intent.setType("image/*");  
        intent.putExtra("crop", "true");  
        intent.putExtra("aspectX", 1);  
        intent.putExtra("aspectY", 1);  
        intent.putExtra("outputX", 330);  
        intent.putExtra("outputY", 330);  
        intent.putExtra("return-data", true);  
        return intent;  
    } 
    protected void doCropPhoto(File f) {  
        try {  
            // 启动gallery去剪辑这个照片  
            final Intent intent = getCropImageIntent(Uri.fromFile(f));  
            startActivityForResult(intent, DIALOG_PHOTOALBUM);  
        } catch (Exception e) {  
            Toast.makeText(this, e.getLocalizedMessage(),Toast.LENGTH_LONG).show();  
            e.printStackTrace();
        }  
    } 
    /**  
     * Constructs an intent for image cropping. 调用图片剪辑程序  
     */  
     public static Intent getCropImageIntent(Uri photoUri) {  
         Intent intent = new Intent("com.android.camera.action.CROP");  
         intent.setDataAndType(photoUri, "image/*");  
         intent.putExtra("crop", "true");  
         intent.putExtra("aspectX", 1);  
         intent.putExtra("aspectY", 1);  
         intent.putExtra("outputX", 330);  
         intent.putExtra("outputY", 330);  
         intent.putExtra("return-data", true);  
         return intent;  
     }  
	

	@Override
	protected void onLogged() {
		showInfo();
	}
	
	private void cancel(){
		Intent intent = new Intent();
		intent.setClass(MyInfoActivity.this, UserCenterActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left,
				R.anim.slide_out_right);
		finish();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			cancel();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	

    //因为调用了Camera和Gallery所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode != RESULT_OK)  
            return;  
        switch (requestCode) {  
            case DIALOG_PHOTOALBUM: {// 调用Gallery返回的  
                Bitmap photo = data.getParcelableExtra("data"); 
                ByteArrayOutputStream baos = new ByteArrayOutputStream();  
                photo.compress(Bitmap.CompressFormat.JPEG, 100, baos); //photo is the bitmap object   
                byte[] b = baos.toByteArray(); 
                photoBase64 = Base64.encodeToString(b, Base64.DEFAULT);
                photo=Bitmap.createScaledBitmap(photo, userFace_size.Width, userFace_size.Height, true); 
                user_face.setImageBitmap(photo);
                break;  
            }  
            case DIALOG_CAMERA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片  
           	 doCropPhoto(mCurrentPhotoFile);  
                break;  
            }  
        }  
    }  

}
