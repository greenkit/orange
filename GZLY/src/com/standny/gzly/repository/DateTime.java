package com.standny.gzly.repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTime {
	//private static final long TicksToMillisOffset = 621355968000000000L;
    //private static final long TicksPerMillisecond = 10000L;
	
	private Date date;
	private Calendar calendar;
	private int year;
	private int month;
	private int day;
	private int week;
	private int hour;
	private int minute;
	private int second;
	private long tick;
	public void setDate(Date date){
		this.date=date;
		calendar.setTime(date);
		this.year=calendar.get(Calendar.YEAR);
		this.month=calendar.get(Calendar.MONTH)+1;
		this.day=calendar.get(Calendar.DAY_OF_MONTH);
		this.week=calendar.get(Calendar.DAY_OF_WEEK);
		this.hour=calendar.get(Calendar.HOUR_OF_DAY);
		this.minute=calendar.get(Calendar.MINUTE);
		this.second=calendar.get(Calendar.SECOND);
		this.tick=calendar.getTimeInMillis();
		//int offset = calendar.get(Calendar.DST_OFFSET) * 60;
        //long ms = date.getTime();

        //this.tick= (ms + offset) * TicksPerMillisecond + TicksToMillisOffset;
	}
	
	public Date getDate(){
		return this.date;
	}
	
	public DateTime(long ticks){
		calendar=Calendar.getInstance();
		setDate(new Date(ticks));
	}
	
	public DateTime(Date date){
		calendar=Calendar.getInstance();
		setDate(date);
	}
	
	public DateTime(int year,int month,int day){
		calendar=Calendar.getInstance();
		calendar.set(year, month-1, day);
		setDate(calendar.getTime());
	}
	
	public DateTime(int year,int month,int day,int hour,int minute){
		calendar=Calendar.getInstance();
		calendar.set(year, month-1, day, hour, minute);
		setDate(calendar.getTime());
	}
	
	public DateTime(int year,int month,int day,int hour,int minute,int second){
		calendar=Calendar.getInstance();
		calendar.set(year, month-1, day, hour, minute,second);
		setDate(calendar.getTime());
	}

	public String toString(String format){
		DateFormat df = new SimpleDateFormat(format, Locale.CHINA); 
		return df.format(getDate());
	}
	
	public int Year(){
		return this.year;
	}
	public int Month(){
		return this.month;
	}
	public int Day(){
		return this.day;
	}
	public int Hour(){
		return this.hour;
	}
	public int Minute(){
		return this.minute;
	}
	public int Second(){
		return this.second;
	}
	public int Week(){
		return this.week;
	}
	public long Tick(){
		return this.tick;
	}
	
	public DateTime AddYear(int year){
		calendar.setTime(this.getDate());
		calendar.add(Calendar.YEAR, year);
		return new DateTime(calendar.getTime());
	}
	public DateTime AddMonth(int month){
		calendar.setTime(this.getDate());
		calendar.add(Calendar.MONTH, month);
		return new DateTime(calendar.getTime());
	}
	public DateTime AddDay(int day){
		calendar.setTime(this.getDate());
		calendar.add(Calendar.DAY_OF_MONTH, day);
		return new DateTime(calendar.getTime());
	}
	
	public static DateTime parseDate(String dateStr,String format){
		try{
			DateFormat df = new SimpleDateFormat(format, Locale.CHINA); 
			return new DateTime(df.parse(dateStr));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static DateTime now(){
		return new DateTime(new Date());
	}
	
	/*public static DateTime fromTicks(long ticks){
		try{
        return new DateTime( new Date((ticks - TicksToMillisOffset) / TicksPerMillisecond));
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}*/
	
	public String toJSONString() {
        return date != null ? new StringBuilder("/Date(").append(this.tick).append(")/").toString(): null;
    }

    public static DateTime fromJSONString(String string) {
        if (string.matches("^/Date\\(\\d+\\)/$")) {
            String value = string.replaceAll("^/Date\\((\\d+)\\)/$", "$1");
            return new DateTime(Long.valueOf(value));
        }
        else {
            return null;
        }

    }
}
