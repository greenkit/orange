package com.standny.gzly.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TicketOrderInfo {
	private int ticketID;
	private String userAccount;
	private int buyCount;
	private List<NameAndIDCard> buyer;
	private Date useTime;
	private String contact;
	private String contactTel;
	private double price;
	private String sightName;
	private String ticketName;
	private int orderStatus;
	private int orderID;
	public void setTicketID(int ticketID){
		this.ticketID=ticketID;
	}
	public int getTicketID(){
		return this.ticketID;
	}
	public void setUserAccount(String account){
		this.userAccount=account;
	}
	public String getUserAccount(){
		return this.userAccount;
	}
	public int getBuyCount(){
		return buyCount;
	}
	public void setBuyCount(int count){
		this.buyCount=count;
	}
	public void setBuyer(List<NameAndIDCard> buyer){
		this.buyer=buyer;
		this.buyCount=buyer.size();
	}
	
	public void clearBuyer(){
		if (this.buyer!=null)
		this.buyer.clear();
		this.buyCount=0;
	}
	public List<NameAndIDCard> getBuyer(){
		return this.buyer;
	}
	public void addBuyer(NameAndIDCard buyer){
		if (this.buyer==null)
			this.buyer=new ArrayList<NameAndIDCard>();
		this.buyer.add(buyer);
		this.buyCount=this.buyer.size();
	}
	public void setUseTime(String timeStr){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA); 
		try {  
			useTime= format.parse(timeStr);
		} catch (ParseException e) {    
		    e.printStackTrace();    
		}    
	}
	public void setUseTime(Date useTime){
		this.useTime=useTime;
	}
	public Date getUseTime(){
		if (this.useTime==null)
			return new Date();
		return this.useTime;
	}
	public String getUseTimeStr(){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA); 
		return format.format(this.getUseTime());
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
	public void setSightName(String name){
		if (name.equalsIgnoreCase("null"))
			return;
		this.sightName=name;
	}
	public String getSightName(){
		return this.sightName;
	}
	public void setTicketName(String name){
		if (name.equalsIgnoreCase("null"))
			return;
		this.ticketName=name;
	}
	public String getTicketName(){
		return this.ticketName;
	}
	public void setOrderID(int id){
		this.orderID=id;
	}
	public int getOrderID(){
		return this.orderID;
	}
	
	public void setOrderStatus(int status){
		this.orderStatus=status;
	}
	public int getOrderStatus(){
		return this.orderStatus;
	}
	
	public String getOrderStatusName(){
        switch (this.orderStatus)
        {
            case 0:
                return "�ȴ�����";
            case 1:
                return "�Ѹ���";
            case 2:
                return "��ʹ��";
            case 3:
                return "�ȴ��̼�ͬ���˿�";
            case 4:
                return "���˿�";
            case 5:
                return "�˿���";
            case 6:
                return "�˿�ʧ��";
        }
        return "δ֪";
	}
}
