package com.standny.gzly;

import com.standny.gzly.models.HotelSearchModel;
import com.standny.gzly.repository.DateTime;
import com.standny.gzly.repository.MyDatePicker;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class HotelActivity extends MasterActivity {

	private HotelSearchModel filter;

	private EditText input_city;
	private TextView input_checkInTime;
	private TextView input_inDays;
	private TextView lbl_checkOutTime;
	private EditText input_key;
	private TextView lbl_price;
	private Button search_btn;
	private MyDatePicker picker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel);
		tabIndex = 2;
		filter = new HotelSearchModel();
		filter.setInDays(1);
		input_city = (EditText) findViewById(R.id.input_city);
		input_checkInTime = (TextView) findViewById(R.id.input_checkin);
		input_inDays = (TextView) findViewById(R.id.input_indays);
		lbl_checkOutTime = (TextView) findViewById(R.id.lbl_checkout);
		input_key = (EditText) findViewById(R.id.input_key);
		lbl_price = (TextView) findViewById(R.id.lbl_price);
		search_btn=(Button)findViewById(R.id.search_btn);
		input_checkInTime.setText(filter.getCheckInTimeStr("yyyy年 M月d日"));
		lbl_checkOutTime.setText(filter.getCheckOutTimeStr("M月d日(周W) 离店"));
		input_inDays.setText(filter.getInDaysStr());
		input_checkInTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDateDialog();
			}

		});
		input_inDays.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!dailogShow){
					dailogShow=true;
				Intent intent = new Intent();
				intent.setClass(HotelActivity.this, NumberPickerActivity.class);
				intent.putExtra("min", 1);
				intent.putExtra("max", 30);
				intent.putExtra("value", filter.getInDays());
				startActivityForResult(intent, MasterActivity.DIALOG_NUMBER);
				}
			}

		});
		lbl_price.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!dailogShow){
					dailogShow=true;
				Intent intent = new Intent();
				intent.setClass(HotelActivity.this,
						HotelMoreFilterActivity.class);
				intent.putExtra("price", filter.getStartPrice());
				intent.putExtra("type", filter.getTypeID());
				intent.putExtra("havDiningRoom", filter.getHavDiningRoom());
				intent.putExtra("havBreakfast", filter.getHavBreakfast());
				intent.putExtra("havHotWater", filter.getHavHotWater());
				intent.putExtra("havMeetingRoom", filter.getHavMeetingRoom());
				intent.putExtra("havPark", filter.getHavPark());
				startActivityForResult(intent, MasterActivity.DIALOG_HOTELFILTER);
				}
			}

		});
		search_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				filter.setKey(input_key.getText().toString());
				filter.setCity(input_city.getText().toString());
				Intent intent = new Intent(HotelActivity.this,HotelListActivity.class);
				Bundle b = new Bundle();
				b.putString("key", filter.getKey());
				b.putString("city", filter.getCity());
				b.putString("checkIn", filter.getCheckInTimeStr("yyyy-MM-dd"));
				b.putInt("inDays", filter.getInDays());
				intent.putExtra("priceStart", filter.getStartPrice());
				intent.putExtra("priceEnd", filter.getEndPrice());
				intent.putExtra("type", filter.getTypeID());
				intent.putExtra("havDiningRoom", filter.getHavDiningRoom());
				intent.putExtra("havBreakfast", filter.getHavBreakfast());
				intent.putExtra("havHotWater", filter.getHavHotWater());
				intent.putExtra("havMeetingRoom", filter.getHavMeetingRoom());
				intent.putExtra("havPark", filter.getHavPark());
				
				intent.putExtras(b);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				finish();
			}
			
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		dailogShow = false;
		if (requestCode == MasterActivity.DIALOG_NUMBER) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				if (bundle!=null){
				filter.setInDays(bundle.getInt("itemValue", filter.getInDays()));
				input_inDays.setText(filter.getInDaysStr());
				lbl_checkOutTime.setText(filter.getCheckOutTimeStr("M月d日(周W) 离店"));
				}
			}
		} else if (requestCode == MasterActivity.DIALOG_HOTELFILTER) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				if (bundle!=null){
				filter.setStartPrice(data.getIntExtra("price", 0));
				switch (filter.getStartPrice()) {
				case 150:
					filter.setEndPrice(300);
					break;
				case 300:
					filter.setEndPrice(600);
					break;
				case 600:
					filter.setEndPrice(10000);
					break;
				default:
					filter.setEndPrice(0);
					break;
				}
				filter.setTypeID(bundle.getInt("type", 0));
				filter.setHavBreakfast(bundle.getBoolean("havBreakfast", false));
				filter.setHavDiningRoom(bundle.getBoolean("havDiningRoom", false));
				filter.setHavHotWater(bundle.getBoolean("havHotWater", false));
				filter.setHavMeetingRoom(bundle.getBoolean("havMeetingRoom", false));
				filter.setHavPark(bundle.getBoolean("havPark", false));
				StringBuilder sb=new StringBuilder();
				if (filter.getStartPrice()>0){
					sb.append(String.format("%d+",filter.getStartPrice()));
				}
				if (filter.getTypeID()>0){
					if (sb.length()>0)
						sb.append("/");
					switch(filter.getTypeID()){
					case 14:
						sb.append("星级酒店");break;
					case 15:
						sb.append("宾馆");break;
					case 16:
						sb.append("温泉酒店");break;
					case 17:
						sb.append("旅馆");break;
					case 18:
						sb.append("招待所");break;
					case 19:
						sb.append("客栈");break;
					case 20:
						sb.append("民居接待");break;
					}
				}
				
				if(filter.getHavBreakfast()){
					if (sb.length()>0)
						sb.append("/");
					sb.append("早餐");
				}
				if(filter.getHavDiningRoom()){
					if (sb.length()>0)
						sb.append("/");
					sb.append("餐厅");
				}
				if(filter.getHavMeetingRoom()){
					if (sb.length()>0)
						sb.append("/");
					sb.append("会议室");
				}
				if(filter.getHavPark()){
					if (sb.length()>0)
						sb.append("/");
					sb.append("停车场");
				}
				if(filter.getHavHotWater()){
					if (sb.length()>0)
						sb.append("/");
					sb.append("热水");
				}
				if(sb.length()==0)
					sb.append("不限");
				if (sb.length()<7){
					lbl_price.setTextSize(20);
				}
				else if (sb.length()<12)
					lbl_price.setTextSize(16);
				else 
					lbl_price.setTextSize(12);
					lbl_price.setText(sb.toString());
				}
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent intent = new Intent();
			intent.setClass(HotelActivity.this, HomeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showDateDialog() {
		if (picker==null){
			picker=new MyDatePicker(new OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							filter.setCheckInTime(new DateTime(year,monthOfYear+1,dayOfMonth).getDate());
							//filter.setCheckInTime(new DateTime(2000,1,1).getDate());
							input_checkInTime.setText(filter
									.getCheckInTimeStr("yyyy年 M月d日"));
							lbl_checkOutTime.setText(filter
									.getCheckOutTimeStr("M月d日(周W) 离店"));
						}
					}, new DateTime(filter.getCheckInTime()));
		}
		if (!picker.isShowing()){
			picker.setCurrentDate(new DateTime(filter.getCheckInTime()));
			picker.setMaxDate(DateTime.now().AddYear(1));
			picker.setMinDate(DateTime.now());
			picker.show(this);
		}
	}

}
