package com.standny.gzly.repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Build;
import android.widget.DatePicker;

public class MyDatePicker {
	private DateTime currentDate;
	private DateTime minDate;
	private DateTime maxDate;
	private DatePickerDialog dpd;
	
	private OnDateSetListener listener;
	final DateFormat format = new SimpleDateFormat("yyyy-m-d", Locale.CHINA); 
	
	public MyDatePicker(OnDateSetListener callBack,DateTime date) {
		this.currentDate=date;
		listener=callBack;
	}
	
	public void show(Context context){
		dpd = new DatePickerDialog(context,
				new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year,
					int monthOfYear, int dayOfMonth) {
				currentDate=new DateTime(year,monthOfYear+1,dayOfMonth);
				if (currentDate.Tick()<minDate.Tick()){
					//view.updateDate(minDate.Year(),minDate.Month(),minDate.Day());
					currentDate=minDate;
					//dpd.onDateChanged(view, currentDate.Year(), currentDate.Month()-1, currentDate.Day());
					dpd.updateDate(minDate.Year(),minDate.Month()-1,minDate.Day());
				}
				else if (currentDate.Tick()>maxDate.Tick()){
					//view.updateDate(maxDate.Year(), maxDate.Month(), maxDate.Day());
					dpd.updateDate(maxDate.Year(), maxDate.Month()-1, maxDate.Day());
					currentDate=maxDate;
					//dpd.onDateChanged(view, currentDate.Year(), currentDate.Month()-1, currentDate.Day());
				}
				if (listener!=null)
					listener.onDateSet(view, currentDate.Year(), currentDate.Month()-1, currentDate.Day());
			}
		}, currentDate.Year(),
		currentDate.Month()-1,
		currentDate.Day());
		if(Build.VERSION.SDK_INT >= 11){
			DatePicker picker =dpd.getDatePicker();
			picker.setMaxDate(this.maxDate.Tick());
			picker.setMinDate(this.minDate.Tick());
		}
		dpd.show();
		
	}
	
	public boolean isShowing(){
		if (dpd!=null)
			return dpd.isShowing();
		return false;
	}
	
	public void setCurrentDate(DateTime date){
		this.currentDate=date;
	}
	
	public void setMinDate(DateTime date){
		this.minDate=date;
	}
	public DateTime getMinDate(){
		if (this.minDate==null){
			this.minDate=new DateTime(2000,1,1);
		}
		return this.minDate;
	}
	public void setMaxDate(DateTime date){
		this.maxDate=date;
	}
	public DateTime getMaxDate(){
		if (this.maxDate==null){
			this.maxDate=new DateTime(2100,1,1);
		}
		return this.maxDate;
	}

}
