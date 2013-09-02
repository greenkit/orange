package com.standny.gzly.models;

import java.util.ArrayList;
import java.util.List;
import com.standny.gzly.repository.DateTime;

public class RoomOrderInfo {
	private int roomID;
	private int buyCount;
	private List<NameAndIDCard> buyer;
	private String comeStartTime;
	private String comeEndTime;
	private DateTime checkInTime;
	private DateTime checkOutTime;
	private String contact;
	private String contactTel;
	private String roomName;
	private String hotelName;
	private double price;
	private int orderStatus;
	private int orderID;
	public void setRoomID(int roomID){
		this.roomID=roomID;
	}
	public int getRoomID(){
		return this.roomID;
	}
	public int getBuyCount(){
		return buyCount;
	}
	public void setBuyCount(int count){
		this.buyCount=count;
	}
	public void setBuyer(List<NameAndIDCard> buyer){
		this.buyer=buyer;
		//this.buyCount=buyer.size();
	}
	public List<NameAndIDCard> getBuyer(){
		return this.buyer;
	}
	public void addBuyer(NameAndIDCard buyer){
		if (this.buyer==null)
			this.buyer=new ArrayList<NameAndIDCard>();
		this.buyer.add(buyer);
		//this.buyCount=this.buyer.size();
	}
	
	public void clearBuyer(){
		if (this.buyer!=null)
		this.buyer.clear();
		//this.buyCount=0;
	}
	
	public void setCheckInTime(DateTime date){
		this.checkInTime=date;
	}
	public DateTime getCheckInTime(){
		return this.checkInTime;
	}
	public void setCheckOutTime(DateTime date){
		this.checkOutTime=date;
	}
	public DateTime getCheckOutTime(){
		return this.checkOutTime;
	}
	public void setComeStartTime(String time){
		this.comeStartTime=time;
	}
	public String getComeStartTime(){
		if (this.comeStartTime==null)
			this.comeStartTime="08:00";
		return this.comeStartTime;
	}
	public void setComeEndTime(String time){
		this.comeEndTime=time;
	}
	public String getComeEndTime(){
		if (this.comeEndTime==null)
			this.comeEndTime="08:00";
		return this.comeEndTime;
	}
		public void setContact(String contact){
		this.contact=contact;
	}
	public String getContact(){
		return this.contact;
	}
	public void setContactTel(String tel){
		this.contactTel=tel;
	}
	public String getContactTel(){
		return this.contactTel;
	}
	public void setPrice(double price){
		this.price=price;
	}
	public double getPrice(){
		return price;
	}
	
	public void setOrderStatus(int status){
		this.orderStatus=status;
	}
	public int getOrderStatus(){
		return this.orderStatus;
	}
	
	public void setHotelName(String name){
		if (name.equalsIgnoreCase("null"))
			return;
		this.hotelName=name;
	}
	public String getHotelName(){
		return this.hotelName;
	}
	public void setRoomName(String name){
		if (name.equalsIgnoreCase("null"))
			return;
		this.roomName=name;
	}
	public String getRoomName(){
		return this.roomName;
	}
	public void setOrderID(int id){
		this.orderID=id;
	}
	public int getOrderID(){
		return this.orderID;
	}
	
	public String getOrderStatusName(){
        switch (this.orderStatus)
        {
            case 0:
                return "等待酒店确认";
            case 1:
                return "等待付款";
            case 2:
                return "已付款";
            case 3:
                return "已入住";
            case 4:
                return "等待商家同意退款";
            case 5:
                return "已退款";
            case 6:
                return "退款中";
            case 7:
                return "退款失败";
        }
        return "未知";
	}
	
}
