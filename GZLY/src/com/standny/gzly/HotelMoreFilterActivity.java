package com.standny.gzly;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

public class HotelMoreFilterActivity extends Activity {

	private TabHost mTabHost;
	private Button okBtn;
	private Button cancelBtn;
	private int price;
	private int type;// 14-20
	private boolean havDiningRoom;
	private boolean havBreakfast;
	private boolean havHotWater;
	private boolean havMeetingRoom;
	private boolean havHavPark;

	private RadioButton price0;
	private RadioButton price1;
	private RadioButton price2;
	private RadioButton price3;
	private RadioButton type0;
	private RadioButton type1;
	private RadioButton type2;
	private RadioButton type3;
	private RadioButton type4;
	private RadioButton type5;
	private RadioButton type6;
	private RadioButton type7;
	private CheckBox facility_all;
	private CheckBox facility_havDiningRoom;
	private CheckBox facility_havMeetingRoom;
	private CheckBox facility_havBreakfast;
	private CheckBox facility_havHotWater;
	private CheckBox facility_havPark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_more_filter);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec("价格").setIndicator("价格")
				.setContent(R.id.tab1));
		mTabHost.addTab(mTabHost.newTabSpec("类型").setIndicator("类型")
				.setContent(R.id.tab2));
		mTabHost.addTab(mTabHost.newTabSpec("配置").setIndicator("配置")
				.setContent(R.id.tab3));

		mTabHost.setCurrentTab(0);

		// 标签切换事件处理，setOnTabChangedListener
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			// TODO Auto-generated method stub
			@Override
			public void onTabChanged(String tabId) {
			}
		});
		okBtn = (Button) findViewById(R.id.ok_btn);
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				okBtn.requestFocusFromTouch();
				Intent intent = new Intent();
				intent.setClass(HotelMoreFilterActivity.this,
						HomeActivity.class);
				HotelMoreFilterActivity.this.setResult(RESULT_OK, intent);
				Bundle b = new Bundle();
				b.putInt("price", price);
				b.putInt("type", type);
				b.putBoolean("havDiningRoom", havDiningRoom);
				b.putBoolean("havBreakfast", havBreakfast);
				b.putBoolean("havHotWater", havHotWater);
				b.putBoolean("havMeetingRoom", havMeetingRoom);
				b.putBoolean("havPark", havHavPark);
				intent.putExtras(b);
				HotelMoreFilterActivity.this.finish();
			}

		});
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Cancel();
			}

		});

		Intent i = this.getIntent();
		price = (i.getIntExtra("price", 0));
		type = i.getIntExtra("type", 0);
		havDiningRoom = i.getBooleanExtra("havDiningRoom", false);
		havBreakfast = i.getBooleanExtra("havBreakfast", false);
		havHotWater = i.getBooleanExtra("havHotWater", false);
		havMeetingRoom = i.getBooleanExtra("havMeetingRoom", false);
		havHavPark = i.getBooleanExtra("havPark", false);

		price0 = (RadioButton) findViewById(R.id.price0);
		price1 = (RadioButton) findViewById(R.id.price1);
		price2 = (RadioButton) findViewById(R.id.price2);
		price3 = (RadioButton) findViewById(R.id.price3);
		switch (price) {
		case 150:
			price1.setChecked(true);
			break;
		case 300:
			price2.setChecked(true);
			break;
		case 600:
			price3.setChecked(true);
			break;
		default:
			price0.setChecked(true);
			break;
		}
		price0.setTag(0);
		price1.setTag(150);
		price2.setTag(300);
		price3.setTag(600);
		price0.setOnCheckedChangeListener(priceChangeListener);
		price1.setOnCheckedChangeListener(priceChangeListener);
		price2.setOnCheckedChangeListener(priceChangeListener);
		price3.setOnCheckedChangeListener(priceChangeListener);

		type0 = (RadioButton) findViewById(R.id.type0);
		type1 = (RadioButton) findViewById(R.id.type1);
		type2 = (RadioButton) findViewById(R.id.type2);
		type3 = (RadioButton) findViewById(R.id.type3);
		type4 = (RadioButton) findViewById(R.id.type4);
		type5 = (RadioButton) findViewById(R.id.type5);
		type6 = (RadioButton) findViewById(R.id.type6);
		type7 = (RadioButton) findViewById(R.id.type7);
		switch (type) {
		case 14:
			type1.setChecked(true);
			break;
		case 15:
			type2.setChecked(true);
			break;
		case 16:
			type3.setChecked(true);
			break;
		case 17:
			type4.setChecked(true);
			break;
		case 18:
			type5.setChecked(true);
			break;
		case 19:
			type6.setChecked(true);
			break;
		case 20:
			type7.setChecked(true);
			break;
		default:
			type0.setChecked(true);
			break;
		}
		type0.setTag(0);
		type1.setTag(14);
		type2.setTag(15);
		type3.setTag(16);
		type4.setTag(17);
		type5.setTag(18);
		type5.setTag(19);
		type7.setTag(20);
		type0.setOnCheckedChangeListener(typeChangeListener);
		type1.setOnCheckedChangeListener(typeChangeListener);
		type2.setOnCheckedChangeListener(typeChangeListener);
		type3.setOnCheckedChangeListener(typeChangeListener);
		type4.setOnCheckedChangeListener(typeChangeListener);
		type5.setOnCheckedChangeListener(typeChangeListener);
		type6.setOnCheckedChangeListener(typeChangeListener);
		type7.setOnCheckedChangeListener(typeChangeListener);
		facility_all = (CheckBox) findViewById(R.id.facility_all);
		facility_havDiningRoom = (CheckBox) findViewById(R.id.facility_havDiningRoom);
		facility_havBreakfast = (CheckBox) findViewById(R.id.facility_havBreakfast);
		facility_havHotWater = (CheckBox) findViewById(R.id.facility_havHotWater);
		facility_havMeetingRoom = (CheckBox) findViewById(R.id.facility_havMeetingRoom);
		facility_havPark = (CheckBox) findViewById(R.id.facility_havPark);
		boolean finded = false;
		if (havDiningRoom) {
			facility_havDiningRoom.setChecked(true);
			finded = true;
		}
		if (havBreakfast) {
			facility_havBreakfast.setChecked(true);
			finded = true;
		}
		if (havHotWater) {
			facility_havHotWater.setChecked(true);
			finded = true;
		}
		if (havMeetingRoom) {
			facility_havMeetingRoom.setChecked(true);
			finded = true;
		}
		if (havHavPark) {
			facility_havPark.setChecked(true);
			finded = true;
		}
		if (!finded) {
			facility_all.setChecked(true);
		}
		facility_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				 if (isChecked){
				//facility_all.setChecked(true);
				facility_havDiningRoom.setChecked(false);
				facility_havBreakfast.setChecked(false);
				facility_havHotWater.setChecked(false);
				facility_havMeetingRoom.setChecked(false);
				facility_havPark.setChecked(false);
				havDiningRoom = havMeetingRoom = havBreakfast = havHotWater = havHavPark = false;
				}

			}

		});
		facility_havDiningRoom
				.setOnCheckedChangeListener(checkedChangeListener);
		facility_havBreakfast.setOnCheckedChangeListener(checkedChangeListener);
		facility_havHotWater.setOnCheckedChangeListener(checkedChangeListener);
		facility_havMeetingRoom
				.setOnCheckedChangeListener(checkedChangeListener);
		facility_havPark.setOnCheckedChangeListener(checkedChangeListener);
	}

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			facility_all.setChecked(false);
			havDiningRoom = (buttonView == facility_havDiningRoom) ? isChecked
					: havDiningRoom;
			havMeetingRoom = (buttonView == facility_havMeetingRoom) ? isChecked
					: havMeetingRoom;
			havBreakfast = (buttonView == facility_havBreakfast) ? isChecked
					: havBreakfast;
			havHotWater = (buttonView == facility_havHotWater) ? isChecked
					: havHotWater;
			havHavPark = (buttonView == facility_havPark) ? isChecked
					: havHavPark;
		}

	};

	private OnCheckedChangeListener typeChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked)
			type = buttonView.getTag().hashCode();
		}

	};

	private OnCheckedChangeListener priceChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked)
			price = buttonView.getTag().hashCode();
		}

	};

	private void Cancel() {
		Intent intent = new Intent();
		intent.setClass(HotelMoreFilterActivity.this, HotelActivity.class);
		HotelMoreFilterActivity.this.setResult(RESULT_CANCELED, intent);
		HotelMoreFilterActivity.this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Cancel();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
