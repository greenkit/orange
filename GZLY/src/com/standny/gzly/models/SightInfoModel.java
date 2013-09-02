package com.standny.gzly.models;

import java.util.List;

public class SightInfoModel {
	private int sightID;
	private String sightName;
	private String address;
	private double price;
	private List<String> pics;
	private int rate;
	private int rateCount;
	
	public void setSightID(int sightID){
		this.sightID=sightID;
	}
	public int getSightID(){
		return sightID;
	}
	public void setSightName(String sightName){
		if (sightName.equalsIgnoreCase("null"))
			return;
		this.sightName=sightName;
	}
	public String getSightName(){
		return sightName;
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
	public void setPics(List<String> pics){
		this.pics=pics;
	}
	public List<String> getPics(){
		return pics;
	}
	public void setRate(int rate){
		this.rate=rate;
	}
	public int getRate(){
		return rate;
	}
	public void setRateCount(int rate){
		this.rateCount=rate;
	}
	public int getRateCount(){
		return rateCount;
	}
}
