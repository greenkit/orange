package com.standny.gzly.models;

public class UserModel {
	private String account;
	private String password;
	private String realName;
	private String nickName;
	private String idcard;
	private String tel;
	private boolean isLogin;
	private String sessionKey;
	private String userFace;
	public void setAccount(String account){
		if (account.equalsIgnoreCase("null"))
			return;
		this.account=account;
	}
	public String getAccount(){
		return account;
	}
	public void setPassword(String password){
		if (password.equalsIgnoreCase("null"))
			return;
		this.password=password;
	}
	public String getPassword(){
		return password;
	}
	public void setRealName(String realName){
		if (realName.equalsIgnoreCase("null"))
			return;
		this.realName=realName;
	}
	public String getRealName(){
		return realName;
	}
	public void setNickName(String nickName){
		if (nickName.equalsIgnoreCase("null"))
			return;
		this.nickName=nickName;
	}
	public String getNickName(){
		return nickName;
	}
	public void setIDcard(String idcard){
		if (idcard.equalsIgnoreCase("null"))
			return;
		this.idcard=idcard;
	}
	public String getIDcard(){
		return idcard;
	}
	public void setTel(String tel){
		if (tel.equalsIgnoreCase("null"))
			return;
		this.tel=tel;
	}
	public String getTel(){
		return tel;
	}
	public void setIsLogin(boolean isLogin){
		this.isLogin=isLogin;
	}
	public boolean getIsLogin(){
		return isLogin;
	}
	public void setSessionKey(String sessionKey){
		if (sessionKey!=null&&sessionKey.equalsIgnoreCase("null"))
			this.sessionKey=null;
		else
		this.sessionKey=sessionKey;
	}
	public String getSessionKey(){
		return sessionKey;
	}
	public void setUserFace(String userFace){
		if (userFace.equalsIgnoreCase("null"))
			return;
		this.userFace=userFace;
	}
	public String getUserFace(){
		return userFace;
	}
	
}
