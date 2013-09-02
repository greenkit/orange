package com.standny.gzly.models;

public class RoomModel {
	private int roomID;
	private String roomName;
	private double price;
	private boolean havNetwork;
	private String bedType;
	public void setRoomID(int id){
		roomID=id;
	}
	public int getRoomID(){
		return roomID;
	}
	public void setRoomName(String name){
		if (name.equalsIgnoreCase("null"))
			return;
		roomName=name;
	}
	public String getRoomName(){
		return roomName;
	}
	public void setPrice(double price){
		this.price=price;
	}
	public double getPrice(){
		return price;
	}
	public void setHavNetwork(boolean hav){
		havNetwork=hav;
	}
	public boolean getHavNetwork(){
		return havNetwork;
	}
	public void setBedType(String type){
		if (type.equalsIgnoreCase("null")||type.equals("-1"))
			return;
		this.bedType=type;
	}
	public String getBedType(){
		return this.bedType;
	}
}
