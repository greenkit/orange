package com.standny.gzly;

import com.standny.ui.itempicker.ItemPicker;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NumberPickerActivity extends Activity {
	
	private int min;
	private int max;
	private int value;
	
	private ItemPicker picker;
	private Button settingBtn;
	private Button cancelBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_picker);
		min=(this.getIntent().getIntExtra("min", 0));
		max=(this.getIntent().getIntExtra("max", 100));
		value=(this.getIntent().getIntExtra("value", 100));
		settingBtn=(Button)findViewById(R.id.setting_btn);
		cancelBtn=(Button)findViewById(R.id.cancel_btn);
		picker=(ItemPicker)findViewById(R.id.itemPicker);
		picker.setRange(min, max);
		picker.setCurrent(value);
		settingBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				settingBtn.requestFocusFromTouch();
				Intent intent = new Intent();
				intent.setClass(NumberPickerActivity.this, HomeActivity.class);
				NumberPickerActivity.this.setResult(RESULT_OK, intent); 
				Bundle b = new Bundle();  
		        b.putInt("itemValue", picker.getCurrent());
		        intent.putExtras(b);
		        NumberPickerActivity.this.finish();
			}
			
		});
		cancelBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Cancel();
			}
			
		});
	}
	
	private  void Cancel(){
		Intent intent = new Intent();
		intent.setClass(NumberPickerActivity.this, HomeActivity.class);
		NumberPickerActivity.this.setResult(RESULT_CANCELED, intent); 
        NumberPickerActivity.this.finish();
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
