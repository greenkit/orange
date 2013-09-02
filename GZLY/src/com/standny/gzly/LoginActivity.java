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

public class LoginActivity extends Activity {
	
	private TextView account;
	private TextView password;
	private CheckBox remember_me;
	private Button login_btn;
	private Button register_btn;
	private UserAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		account=(TextView)findViewById(R.id.account);
		password=(TextView)findViewById(R.id.password);
		remember_me=(CheckBox)findViewById(R.id.remember_me);
		login_btn=(Button)findViewById(R.id.login_btn);
		register_btn=(Button)findViewById(R.id.register_btn);
		UserModel user = UserAPI.ReadUserInfo(getApplicationContext());
			account.setText(user.getAccount());
			password.setText(user.getPassword());
			remember_me.setChecked(user.getPassword().length()>0);
		api=new UserAPI();
		login_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				enableControls(false);
				api.BeginLogin(String.valueOf(account.getText()), String.valueOf(password.getText()), new APICallback<UserModel>(){

					@Override
					public void onCompleted(APIResultModel<UserModel> result) {
						enableControls(true);
						if (result.getSuc()){
							onLogged(result.getItems());
						}
						else{
							Toast.makeText(LoginActivity.this, result.getMsg(), Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onError(String msg) {
						enableControls(true);
						Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
						
					}
					
				});
			}
		});

		register_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, HomeActivity.class);
				LoginActivity.this.setResult(MasterActivity.RESULTCODE_GOREG, intent); 
				Bundle b = new Bundle();  
	            b.putBoolean("logged", false);  
	            intent.putExtras(b);
	            LoginActivity.this.finish();
			}
		
		});
	}
	
	public void enableControls(boolean enabled){
		login_btn.setEnabled(enabled);
		register_btn.setEnabled(enabled);
		account.setEnabled(enabled);
		password.setEnabled(enabled);
		remember_me.setEnabled(enabled);
	}
	
	public void onLogged(UserModel user){
		//user.Account=String)account.getText();
		if (remember_me.isChecked()){
			user.setPassword(String.valueOf(password.getText()));
		}
		UserAPI.SaveUserInfo(user, getApplicationContext());
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, HomeActivity.class);
		LoginActivity.this.setResult(RESULT_OK, intent); 
		Bundle b = new Bundle();  
        b.putBoolean("logged", true);
        b.putString("sessionKey", user.getSessionKey());
        intent.putExtras(b);
		LoginActivity.this.finish();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
     return true;
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, HomeActivity.class);
			LoginActivity.this.setResult(RESULT_OK, intent); 
			Bundle b = new Bundle();  
            b.putBoolean("logged", false);  
            intent.putExtras(b);
			//overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
			LoginActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
