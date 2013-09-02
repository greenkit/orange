package com.standny.gzly.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HotelSearchModel {
	private String city;
	private Date checkInTime;
	private Date checkOutTime;
	private int inDays;
	private String key;
	private int startPrice;
	private int endPrice;
	private int typeID;
	private boolean havDiningRoom;
	private boolean havMeetingRoom;
	private boolean havBreakfast;
	private boolean havHotWater;
	private boolean havPark;
	public void setCity(String city){
		this.city=city;
	}
	public String getCity(){
		return this.city;
	}
	public void setCheckInTime(Date time){
		this.checkInTime=time;
		setInDays(this.inDays);
	}
	public void setCheckInTime(String timeStr){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA); 
		try {  
			this.checkInTime= format.parse(timeStr);
			setInDays(this.inDays);
		} catch (ParseException e) {    
		    e.printStackTrace();    
		}
	}
	
	public Date getCheckInTime(){
		if (this.checkInTime==null)
			return new Date();
		return this.checkInTime;
	}
	
	public String getCheckInTimeStr(String format){
		DateFormat df = new SimpleDateFormat(format, Locale.CHINA); 
		return df.format(this.getCheckInTime());
	}

	public void setInDays(int days){
		if (days>0){
		this.inDays=days;
		Calendar nowTime = Calendar.getInstance();
		nowTime.setTime(getCheckInTime());
		nowTime.add(Calendar.DAY_OF_MONTH, days);
		this.checkOutTime=nowTime.getTime();
		}
	}
	public int getInDays(){
		return this.inDays;
	}
	public String getInDaysStr(){
		return new StringBuilder(String.valueOf(this.inDays)).append("Ìì").toString();
	}
	public Date getCheckOutTime(){
		if (this.checkOutTime==null)
			return new Date();
		return this.checkOutTime;
	}
	

	public String getCheckOutTimeStr(String format){
		DateFormat df = new SimpleDateFormat(format, Locale.CHINA); 
		return df.format(this.getCheckOutTime());
	}

	public void setKey(String key){
		this.key=key;
	}
	public String getKey(){
		return this.key;
	}
	public void setStartPrice(int price){
		this.startPrice=price;
	}
	public int getStartPrice(){
		return this.startPrice;
	}
	public void setEndPrice(int price){
		this.endPrice=price;
	}
	public int getEndPrice(){
		return this.endPrice;
	}
	public void setTypeID(int typeID){
		this.typeID=typeID;
	}
	public int getTypeID(){
		return this.typeID;
	}
	public void setHavDiningRoom(boolean hav){
		this.havDiningRoom=hav;
	}
	public boolean getHavDiningRoom(){
		return this.havDiningRoom;
	}
	public void setHavMeetingRoom(boolean hav){
		this.havMeetingRoom=hav;
	}
	public boolean getHavMeetingRoom(){
		return this.havMeetingRoom;
	}
	public void setHavBreakfast(boolean hav){
		this.havBreakfast=hav;
	}
	public boolean getHavBreakfast(){
		return this.havBreakfast;
	}
	public void setHavHotWater(boolean hav){
		this.havHotWater=hav;
	}
	public boolean getHavHotWater(){
		return this.havHotWater;
	}
	public void setHavPark(boolean hav){
		this.havPark=hav;
	}
	public boolean getHavPark(){
		return this.havPark;
	}
}
