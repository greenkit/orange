package com.standny.gzly;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.UserModel;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.UserAPI;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	private TextView account;
	private TextView password;
	private TextView re_password;
	private CheckBox remember_me;
	private TextView tel;
	private Button login_btn;
	private Button register_btn;
	private UserAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		account=(TextView)findViewById(R.id.account);
		password=(TextView)findViewById(R.id.password);
		re_password=(TextView)findViewById(R.id.re_password);
		remember_me=(CheckBox)findViewById(R.id.remember_me);
		tel=(TextView)findViewById(R.id.tel);
		login_btn=(Button)findViewById(R.id.login_btn);
		register_btn=(Button)findViewById(R.id.register_btn);
		api=new UserAPI();
		register_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String email=String.valueOf(account.getText());
				String pwd=String.valueOf(password.getText());
				String re_pwd=String.valueOf(re_password.getText());
				String mobile=String.valueOf(tel.getText());
				if (email.length()==0){
					Toast.makeText(RegisterActivity.this, "请输入登录名", Toast.LENGTH_LONG).show();
					return;
				}
				if (pwd.length()==0){
					Toast.makeText(RegisterActivity.this, "请输入登录密码", Toast.LENGTH_LONG).show();
					return;
				}
				if (mobile.length()==0){
					Toast.makeText(RegisterActivity.this, "请输入你的手机号码", Toast.LENGTH_LONG).show();
					return;
				}
				if (!pwd.equals(re_pwd)){
					Toast.makeText(RegisterActivity.this, "两次密码输入不一致", Toast.LENGTH_LONG).show();
					return;
				}
				enableControls(false);
				api.BeginRegister(email, pwd,mobile, new APICallback<UserModel>(){

					@Override
					public void onCompleted(APIResultModel<UserModel> result) {
						enableControls(true);
						if (result.getSuc()){
							onLogged(result.getItems());
						}
						else{
							Toast.makeText(RegisterActivity.this, result.getMsg(), Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onError(String msg) {
						enableControls(true);
						Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
						
					}
					
				});
			}
		});
		login_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, HomeActivity.class);
				RegisterActivity.this.setResult(MasterActivity.RESULTCODE_GOLOGIN, intent); 
				Bundle b = new Bundle();  
	            b.putBoolean("register", false);  
	            intent.putExtras(b);
				//overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
	            RegisterActivity.this.finish();
				/*Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				finish();*/
			}
		
		});
	}
	
	public void enableControls(boolean enabled){
		login_btn.setEnabled(enabled);
		register_btn.setEnabled(enabled);
		account.setEnabled(enabled);
		password.setEnabled(enabled);
		re_password.setEnabled(enabled);
		tel.setEnabled(enabled);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent intent = new Intent();
			intent.setClass(RegisterActivity.this, HomeActivity.class);
			RegisterActivity.this.setResult(RESULT_OK, intent); 
			Bundle b = new Bundle();  
            b.putBoolean("register", false);  
            intent.putExtras(b);
			//overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            RegisterActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void onLogged(UserModel user){
		//user.Account=String)account.getText();
		if (remember_me.isChecked()){
			user.setPassword(String.valueOf(password.getText()));
		}
		UserAPI.SaveUserInfo(user, getApplicationContext());
		Intent intent = new Intent();
		intent.setClass(RegisterActivity.this, HomeActivity.class);
		RegisterActivity.this.setResult(RESULT_OK, intent); 
		Bundle b = new Bundle();  
        b.putBoolean("register", true);
        b.putString("sessionKey", user.getSessionKey());
        intent.putExtras(b);
        RegisterActivity.this.finish();
	}

}
