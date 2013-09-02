package com.standny.gzly.models;


public class SightViewModel {
	private int sightID;
	private String sightName;
	private String address;
	private double price;
	private String coverPic;
	
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
	public void setCoverPic(String coverPic){
		if (coverPic.equalsIgnoreCase("null"))
			return;
		this.coverPic=coverPic;
	}
	public String getCoverPic(){
		return coverPic;
	}
	
}
