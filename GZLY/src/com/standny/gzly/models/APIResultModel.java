package com.standny.gzly.models;

public class APIResultModel<T> {
	private boolean suc;
	private String msg;
	private T items;
	private int totalItemCount;
	private int totalPageCount;
	private int currentPageIndex;
	public void setSuc(boolean suc){
		this.suc=suc;
	}
	public boolean getSuc(){
		return suc;
	}
	public void setMsg(String msg){
		if (msg.equalsIgnoreCase("null"))
			return;
		this.msg=msg;
	}
	public String getMsg(){
		return msg;
	}
	public void setItems(T items){
		this.items=items;
	}
	public T getItems(){
		return items;
	}
	public void setTotalItemCount(int itemCount){
		this.totalItemCount=itemCount;
	}
	public int getTotalItemCount(){
		return totalItemCount;
	}
	public void setTotalPageCount(int pageCount){
		this.totalPageCount=pageCount;
	}
	public int getTotalPageCount(){
		return totalPageCount;
	}
	public void setCurrentPageIndex(int pageIndex){
		this.currentPageIndex=pageIndex;
	}
	public int getCurrentPageIndex(){
		return currentPageIndex;
	}
}
