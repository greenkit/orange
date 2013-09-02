package com.standny.gzly.models;

import java.io.Serializable;
import java.util.List;

public class HotelViewModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int hotelID;
	private String hotelName;
	private String coverPic;
	private double price;
	private String address;
	private float rate;
	private String hotelType;
	private boolean hav24HotWater;
	private boolean havMeetingRoom;
	private boolean havPark;
	private boolean havDiningRoom;
	private boolean havBreakfast;
	private int rateCount;
	private List<String> pics;
	public void setHotelID(int hotelID){
		this.hotelID=hotelID;
	}
	public int getHotelID(){
		return hotelID;
	}
	public void setHotelName(String hotelName){
		if (hotelName.equalsIgnoreCase("null"))
			return;
		this.hotelName=hotelName;
	}
	public String getHotelName(){
		return hotelName;
	}
	public void setAddress(String address){
		if (address.equalsIgnoreCase("null"))
			return;
		this.address=address;
	}
	public String getAddress(){
		return address;
	}
	public void setPrice(double price){
		this.price=price;
	}
	public double getPrice(){
		return price;
	}
	public void setCoverPic(String coverPic){
		if (coverPic.equalsIgnoreCase("null"))
			return;
		this.coverPic=coverPic;
	}
	public String getCoverPic(){
		return coverPic;
	}
	public void setRate(float rate){
		this.rate=rate;
	}
	public float getRate(){
		return this.rate;
	}
	public void setHotelType(String hotelType){
		if (hotelType.equalsIgnoreCase("null"))
			return;
		this.hotelType=hotelType;
	}
	public String getHotelType(){
		return this.hotelType;
	}
	public void setHav24HotWater(boolean hav){
		this.hav24HotWater=hav;
	}
	public boolean getHav24HotWater(){
		return this.hav24HotWater;
	}
	public void setHavMeetingRoom(boolean hav){
		this.havMeetingRoom=hav;
	}
	public boolean getHavMeetingRoom(){
		return this.havMeetingRoom;
	}
	public void setHavPark(boolean hav){
		this.havPark=hav;
	}
	public boolean getHavPark(){
		return this.havPark;
	}
	public void setHavDiningRoom(boolean hav){
		this.havDiningRoom=hav;
	}
	public boolean getHavDiningRoom(){
		return this.havDiningRoom;
	}
	public void setHavBreakfast(boolean hav){
		this.havBreakfast=hav;
	}
	public boolean getHavBreakfast(){
		return this.havBreakfast;
	}
	public void setPics(List<String> pics){
		this.pics=pics;
	}
	public List<String> getPics(){
		return pics;
	}
	public void setRateCount(int rate){
		this.rateCount=rate;
	}
	public int getRateCount(){
		return rateCount;
	}
}
