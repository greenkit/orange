package com.standny.gzly.models;

public class TicketModel {
	private int ticketID;
	private String ticketName;
	private double price1;
	private double price2;
	public void setTicketID(int id){
		ticketID=id;
	}
	public int getTicketID(){
		return ticketID;
	}
	public void setTicketName(String name){
		if (name.equalsIgnoreCase("null"))
			return;
		ticketName=name;
	}
	public String getTicketName(){
		return ticketName;
	}
	public void setPrice1(double price){
		price1=price;
	}
	public double getPrice1(){
		return price1;
	}
	public void setPrice2(double price){
		price2=price;
	}
	public double getPrice2(){
		return price2;
	}
}
