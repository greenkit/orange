package com.standny.gzly;

import com.standny.ui.itempicker.ItemPicker;
import com.standny.ui.itempicker.ItemPicker.OnChangedListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ComeTimePickerActivity extends Activity {

	private ItemPicker itemPicker1;
	private ItemPicker itemPicker2;
	private Button settingBtn;
	private Button cancelBtn;
	public static final String[] items={"08:00","09:00","10:00","11:00","12:00","13:00"
			,"14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00","00:00","01:00","02:00","03:00"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_come_time_picker);
		itemPicker1=(ItemPicker)findViewById(R.id.itemPicker1);
		itemPicker2=(ItemPicker)findViewById(R.id.itemPicker2);
		settingBtn=(Button)findViewById(R.id.setting_btn);
		cancelBtn=(Button)findViewById(R.id.cancel_btn);
		itemPicker1.setRange(0, items.length-4, items);
		//itemPicker1.setRange(0, 3,items);
		Intent i = this.getIntent();
		itemPicker1.setCurrent(i.getIntExtra("startIndex", 0));
		itemPicker2.setRange(itemPicker1.getCurrent(),Math.min(itemPicker1.getCurrent()+3,items.length-1),items);
		itemPicker2.setCurrent(i.getIntExtra("endIndex", itemPicker1.getCurrent()));
		
		itemPicker1.setOnChangeListener(new OnChangedListener(){

			@Override
			public void onChanged(ItemPicker picker, int oldVal, int newVal) {
				
				itemPicker2.setRange(newVal, Math.min(newVal+3,items.length-1),items);
			}
			
		});
		settingBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				settingBtn.requestFocusFromTouch();
				Intent intent = new Intent();
				intent.setClass(ComeTimePickerActivity.this, HomeActivity.class);
				ComeTimePickerActivity.this.setResult(RESULT_OK, intent); 
				Bundle b = new Bundle();  
		        b.putString("startValue", ComeTimePickerActivity.items[itemPicker1.getCurrent()]);
		        b.putString("endValue", ComeTimePickerActivity.items[itemPicker2.getCurrent()]);
		        b.putInt("startIndex", itemPicker1.getCurrent());
		        b.putInt("endIndex", itemPicker2.getCurrent());
		        intent.putExtras(b);
		        ComeTimePickerActivity.this.finish();
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
		intent.setClass(ComeTimePickerActivity.this, HomeActivity.class);
		ComeTimePickerActivity.this.setResult(RESULT_CANCELED, intent); 
		ComeTimePickerActivity.this.finish();
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
