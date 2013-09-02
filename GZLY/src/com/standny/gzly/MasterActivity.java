package com.standny.gzly;

import java.util.ArrayList;
import java.util.List;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.UserModel;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.CGSize;
import com.standny.gzly.repository.ImageManager;
import com.standny.gzly.repository.UserAPI;
import com.standny.ui.ActivityManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MasterActivity extends Activity {
	public static final int DIALOG_LOGIN = 100;
	public static final int DIALOG_REG = 101;
	public static final int DIALOG_NUMBER = 102;
	public static final int DIALOG_HOTELFILTER = 103;
	public static final int DIALOG_COMETIME = 104;
	public static final int DIALOG_CONTACT = 105;
	public static final int DIALOG_CAMERA = 106;
	public static final int DIALOG_PHOTOALBUM = 107;

	public static final int RESULTCODE_GOLOGIN = 1001;
	public static final int RESULTCODE_GOREG = 1002;

	protected static CGSize userFace_size;
	protected static ImageManager imgManager;
	private ActivityManager activityManager = ActivityManager.getActivityManager();

	private GridView tab;
	public int tabIndex;
	protected boolean isLogged;
	protected boolean dailogShow;
	private boolean goUserCenter;
	private String sessionKey;
	private int tryCount;
	protected UserModel user;
	private List<Integer> tab_img;
	private Integer[] tab_img_normal = { R.drawable.icon_home,
			R.drawable.icon_ticket, R.drawable.icon_hotel,
			R.drawable.icon_shopping, R.drawable.icon_user };
	private Integer[] tab_img_gray = { R.drawable.icon_home_gray,
			R.drawable.icon_ticket_gray, R.drawable.icon_hotel_gray,
			R.drawable.icon_shopping_gray, R.drawable.icon_user_gray };
	private Integer[] tab_title = { R.string.tab_home, R.string.tab_ticket,
			R.string.tab_hotel, R.string.tab_shopping, R.string.tab_user };
	private List<Integer> tab_bgImg;// = { R.drawable.tabbar_bg,0, 0,0, 0};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activityManager.putActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (userFace_size == null || imgManager == null) {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			userFace_size = new CGSize(Math.round(dm.widthPixels * 0.3f),
					Math.round(dm.widthPixels * 0.3f));
			imgManager = new ImageManager(new CGSize(dm.widthPixels,
					dm.heightPixels));
		}
		tryCount = 0;
		autoLogin();
	}

    @Override
    protected void onDestroy() {
            super.onDestroy();
            activityManager.removeActivity(this);
    }
    
   public void exit(){
	   activityManager.exit();
   }

	@Override
	protected void onStart() {
		super.onStart();
		tab = (GridView) findViewById(R.id.tab_box);
		if (tab != null) {
			tab_bgImg = new ArrayList<Integer>();
			for (int i = 0; i < tab_title.length; i++) {
				if (i == tabIndex)
					tab_bgImg.add(R.drawable.tabbar_bg);
				else
					tab_bgImg.add(0);
			}
			tab_img = new ArrayList<Integer>();
			for (int i = 0; i < tab_title.length; i++) {
				if (i == tabIndex)
					tab_img.add(tab_img_normal[i]);
				else
					tab_img.add(tab_img_gray[i]);
			}

			// requestWindowFeature(Window.FEATURE_NO_TITLE);
			tab.setNumColumns(5);
			tab.setAdapter(new MenuItemAdapter(tab_title, tab_img
					.toArray(new Integer[tab_img.size()]), tab_bgImg
					.toArray(new Integer[tab_bgImg.size()]), R.layout.tab_item,
					this));
			tab.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int agr2, long arg3) {
					if (arg3 == tabIndex)
						return;
					switch ((int) arg3) {
					case 0: {
						Intent intent = new Intent(MasterActivity.this,
								HomeActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_left,
								R.anim.slide_out_right);
						MasterActivity.this.finish();
					}
						break;
					case 1: {
						Intent intent = new Intent(MasterActivity.this,
								TicketActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
						MasterActivity.this.finish();
					}
						break;
					case 2: {
						Intent intent = new Intent(MasterActivity.this,
								HotelActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						overridePendingTransition(R.anim.slide_in_right,
								R.anim.slide_out_left);
						MasterActivity.this.finish();
					}
						break;
					case 4: {
						jumpToUserCenter();
					}
						break;
					}
				}
			});
		}
	}

	private void jumpToUserCenter() {
		if (isLogged) {
			goUserCenter = false;
			Intent intent = new Intent(MasterActivity.this,
					UserCenterActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			MasterActivity.this.finish();
		} else {
			goUserCenter = true;
			Login();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (isLogged) {
			menu.setGroupVisible(R.id.logged, true);
			menu.setGroupVisible(R.id.unlogin, false);
		} else {
			menu.setGroupVisible(R.id.logged, false);
			menu.setGroupVisible(R.id.unlogin, true);
		}
		return true;
	}

	protected void Login() {
		if (user != null && user.getPassword().length() > 0&&tryCount<3) {
			autoLogin(true);
			return;
		}
		if (!dailogShow) {
			dailogShow = true;
			Intent intent = new Intent();
			intent.setClass(MasterActivity.this, LoginActivity.class);
			startActivityForResult(intent, DIALOG_LOGIN);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_login: {
			Login();
		}
			// overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			// finish();
			break;
		case R.id.action_logout:
			if (isLogged) {
				UserAPI api = new UserAPI();
				api.BeginLogout(sessionKey, new APICallback<Object>() {

					@Override
					public void onCompleted(APIResultModel<Object> result) {
						if (result.getSuc()) {
							isLogged = false;
							user.setIsLogin(false);
							user.setPassword("");
							UserAPI.SaveUserInfo(user, getApplicationContext());
							Toast.makeText(MasterActivity.this, "退出登陆成功",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(MasterActivity.this,
									result.getMsg(), Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onError(String msg) {
						Toast.makeText(MasterActivity.this, msg,
								Toast.LENGTH_LONG).show();
					}

				});
			}
			break;
		case R.id.action_register: {
			if (!dailogShow) {
				dailogShow = true;
				Intent intent = new Intent();
				intent.setClass(MasterActivity.this, RegisterActivity.class);
				startActivityForResult(intent, DIALOG_REG);
			}
		}
			break;

		case R.id.action_settings:
			break;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		dailogShow = false;
		if (requestCode == DIALOG_LOGIN) {
			if (resultCode == RESULTCODE_GOREG) {// 转到注册
				Intent intent = new Intent();
				intent.setClass(MasterActivity.this, RegisterActivity.class);
				startActivityForResult(intent, DIALOG_REG);
			} else {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					isLogged = bundle.getBoolean("logged");
					if (isLogged) {
						sessionKey = bundle.getString("sessionKey");
						Toast.makeText(this, "登陆成功", Toast.LENGTH_LONG).show();
						if (goUserCenter) {
							jumpToUserCenter();
						} else
							onLogged();
					}
				}
			}
		} else if (requestCode == DIALOG_REG) {
			if (resultCode == RESULTCODE_GOLOGIN) {// 转到登录
				Login();
			} else {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					isLogged = bundle.getBoolean("register");
					if (isLogged) {
						sessionKey = bundle.getString("sessionKey");
						Toast.makeText(this, "注册成功", Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	}

	protected void onLogged() {

	}

	protected UserModel getUser() {
		if (user == null)
			user = UserAPI.ReadUserInfo(getApplicationContext());
		// user.setIsLogin(isLogged);
		return user;
	}

	protected void autoLogin() {
		autoLogin(false);
	}

	protected void autoLogin(final boolean needCallback) {
		getUser();
		if (user != null && user.getIsLogin()) {
			this.isLogged = true;
			return;
		}
		if (user != null && user.getPassword().length() > 0) {
			UserAPI api = new UserAPI();
			api.BeginLogin(user.getAccount(), user.getPassword(),
					new APICallback<UserModel>() {

						@Override
						public void onCompleted(APIResultModel<UserModel> result) {
							if (result.getSuc()) {
								UserModel user1 = result.getItems();
								isLogged = true;
								sessionKey = user1.getSessionKey();
								user1.setPassword(user.getPassword());
								user = user1;
								UserAPI.SaveUserInfo(user,
										getApplicationContext());
								if (goUserCenter) 
									jumpToUserCenter();
								else if (needCallback)
									onLogged();
							} else {
								tryCount++;
								Toast.makeText(MasterActivity.this,
										result.getMsg(), Toast.LENGTH_LONG)
										.show();
							}
						}

						@Override
						public void onError(String msg) {
							tryCount++;
							if (tryCount < 3) {
								autoLogin();
							}
							Toast.makeText(MasterActivity.this, msg,
									Toast.LENGTH_LONG).show();

						}

					});
		}
	}
}
