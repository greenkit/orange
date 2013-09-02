package com.standny.gzly;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class UserCenterActivity extends MasterActivity {

	private View userInfoRow;
	private View myHotelRow;
	private View myTicketRow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		tabIndex = 4;
		userInfoRow = findViewById(R.id.row_myinfo);
		userInfoRow.setOnClickListener(rowClick);
		myHotelRow = findViewById(R.id.row_myhotel);
		myHotelRow.setOnClickListener(rowClick);
		myTicketRow = findViewById(R.id.row_myticket);
		myTicketRow.setOnClickListener(rowClick);
	}

	private OnClickListener rowClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.row_myinfo: {
				Intent intent = new Intent();
				intent.setClass(UserCenterActivity.this, MyInfoActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				finish();
			}
				break;
			case R.id.row_myhotel: {
				Intent intent = new Intent();
				intent.setClass(UserCenterActivity.this, MyHotelActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				finish();
			}
				break;
			case R.id.row_myticket: {
				Intent intent = new Intent();
				intent.setClass(UserCenterActivity.this, MyTicketActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				finish();
			}
				break;
			}
		}

	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent intent = new Intent();
			intent.setClass(UserCenterActivity.this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
