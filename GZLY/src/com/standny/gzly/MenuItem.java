package com.standny.gzly;

public class MenuItem {
	private Integer titleID;
	private Integer imgID;
	private Integer bgImgID;
	public MenuItem(){
		super();
	}
	public MenuItem(Integer title,Integer img,Integer bgImgID){
		super();
		this.titleID=title;
		this.imgID=img;
		this.bgImgID=bgImgID;
	}
	public Integer getTitleID(){
			return titleID;
	}
	public Integer getImgID(){
		return imgID;
	}
	public Integer getBgImgID(){
		return bgImgID;
	}
}
