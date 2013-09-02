package com.standny.gzly.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.UserModel;

public class UserAPI {
	
	private APICallback<UserModel> loginCallback;
	private APICallback<UserModel> regCallback;
	private APICallback<Object> logoutCallback;
	private APICallback<String> saveInfoCallback;
	
	private static boolean isLogged;
	private static String sessionKey;
	
	public static boolean getIsLogged(){
		return isLogged;
	}
	
	public static String getSessionKey(){
		return sessionKey;
	}
	
	public static void SaveUserInfo(UserModel user,Context context){
		SharedPreferences.Editor sharedata = context.getSharedPreferences("user", Context.MODE_PRIVATE).edit();  
		sharedata.putString("account", user.getAccount());
		sharedata.putString("password", user.getPassword());
		sharedata.putString("realName", user.getRealName());
		sharedata.putString("IDCard", user.getIDcard());
		sharedata.putString("nickName", user.getNickName());
		sharedata.putString("tel",user.getTel());
		sharedata.putString("userFace",user.getUserFace());
		sharedata.commit();
	}
	
	public static UserModel ReadUserInfo(Context context){
		SharedPreferences sharedata=context.getSharedPreferences("user", Context.MODE_PRIVATE);
		UserModel user=new UserModel();
		user.setAccount(sharedata.getString("account", ""));
		user.setPassword(sharedata.getString("password", ""));
		user.setRealName(sharedata.getString("realName",""));
		user.setIDcard(sharedata.getString("IDCard", ""));
		user.setNickName(sharedata.getString("nickName", ""));
		user.setTel(sharedata.getString("tel",""));
		user.setUserFace(sharedata.getString("userFace", ""));
		user.setIsLogin(isLogged);
		user.setSessionKey(sessionKey);
		return user;
	}
	
	public void BeginLogin(String account,String password,APICallback<UserModel> callback){
		loginCallback=callback;
		JsonAPIClient jc=new JsonAPIClient(new JsonAPICallback(){

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<UserModel> result=new APIResultModel<UserModel>();
				result.setSuc(jsonObj.optBoolean("Suc",false));
				result.setMsg(jsonObj.optString("Msg", ""));
				if (result.getSuc()){
					JSONObject itemObj = jsonObj.optJSONObject("Item"); 
					 if (itemObj!=null){
							 try{
								 UserModel item=new UserModel();
							 item.setIsLogin(true);
							 item.setAccount(itemObj.optString("Account"));
							 item.setIDcard(itemObj.optString("IDCard",""));
							 item.setNickName(itemObj.optString("NickName",""));
							 item.setRealName(itemObj.optString("RealName",""));
							 item.setTel(itemObj.optString("Tel",""));
							 item.setUserFace(itemObj.optString("UserFace",""));
							 item.setSessionKey(itemObj.optString("SessionKey"));
							 result.setItems(item);
							 isLogged=true;
							 sessionKey=item.getSessionKey();
							 }
							 catch(Exception e){
									if (loginCallback!=null)
										loginCallback.onError(e.getLocalizedMessage());
									 result.setSuc(false);
									 result.setMsg(e.getLocalizedMessage());
								 e.printStackTrace();
							 }
					 }
					 else{
							if (loginCallback!=null)
								loginCallback.onError("没有返回内容");
						 result.setSuc(false);
						 result.setMsg("没有返回内容");
					 }
				}
				if (loginCallback!=null)
					loginCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (loginCallback!=null)
					loginCallback.onError(msg);
			}
			
		});
		List<NameValuePair> params = new ArrayList <NameValuePair>();   
        params.add(new BasicNameValuePair("account", account));
        params.add(new BasicNameValuePair("password", password));
		jc.BeginPost(APIConfig.ApiUrl+"Login", params);
		
	}
	
	public void BeginLogout(String sessionKey,APICallback<Object> callback){
		logoutCallback=callback;
		JsonAPIClient jc=new JsonAPIClient(new JsonAPICallback(){

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<Object> result=new APIResultModel<Object>();
				result.setSuc(jsonObj.optBoolean("Suc",false));
				result.setMsg(jsonObj.optString("Msg", ""));
				isLogged=(!result.getSuc())&&isLogged;
				UserAPI.sessionKey=null;
				if (logoutCallback!=null)
					logoutCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (logoutCallback!=null)
					logoutCallback.onError(msg);
			}
			
		});
		List<NameValuePair> params = new ArrayList <NameValuePair>();   
        params.add(new BasicNameValuePair("sessionKey", sessionKey));
		jc.BeginPost(APIConfig.ApiUrl+"Logout", params);
		
	}

	public void BeginRegister(String account,String password,String tel,APICallback<UserModel> callback){
		regCallback=callback;
		JsonAPIClient jc=new JsonAPIClient(new JsonAPICallback(){

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<UserModel> result=new APIResultModel<UserModel>();
				result.setSuc(jsonObj.optBoolean("Suc",false));
				result.setMsg(jsonObj.optString("Msg", ""));
				if (result.getSuc()){
					JSONObject itemObj = jsonObj.optJSONObject("Item"); 
					 if (itemObj!=null){
							 try{
								 UserModel item=new UserModel();
								 item.setIsLogin(true);
								 item.setAccount(itemObj.optString("Account"));
								 item.setIDcard(itemObj.optString("IDCard",""));
								 item.setNickName(itemObj.optString("NickName",""));
								 item.setRealName(itemObj.optString("RealName",""));
								 item.setTel(itemObj.optString("Tel",""));
								 item.setUserFace(itemObj.optString("UserFace",""));
								 item.setSessionKey(itemObj.optString("SessionKey"));
							 result.setItems(item);
							 }
							 catch(Exception e){
								 e.printStackTrace();
							 }
					 }
				}
				if (regCallback!=null)
					regCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (regCallback!=null)
					regCallback.onError(msg);
			}
			
		});
		List<NameValuePair> params = new ArrayList <NameValuePair>();   
        params.add(new BasicNameValuePair("Email", account));
        params.add(new BasicNameValuePair("Pwd1", password));
        params.add(new BasicNameValuePair("Tel", tel));
		jc.BeginPost(APIConfig.ApiUrl+"Register", params);
		
	}
	
	public void BeginSaveUserInfo(UserModel user,String userFace,APICallback<String> callback){
		saveInfoCallback=callback;
		JsonAPIClient jc=new JsonAPIClient(new JsonAPICallback(){

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<String> result=new APIResultModel<String>();
				result.setSuc(jsonObj.optBoolean("Suc",false));
				result.setMsg(jsonObj.optString("Msg", ""));
				if (result.getSuc()){
					String face=jsonObj.optString("UserFace");
					result.setItems(face);
				}
				if (saveInfoCallback!=null)
					saveInfoCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (saveInfoCallback!=null)
					saveInfoCallback.onError(msg);
			}
			
		});
		List<NameValuePair> params = new ArrayList <NameValuePair>();   
        params.add(new BasicNameValuePair("Account", user.getAccount()));
        params.add(new BasicNameValuePair("NickName", user.getNickName()));
        params.add(new BasicNameValuePair("IDCard", user.getIDcard()));
        params.add(new BasicNameValuePair("RealName", user.getRealName()));
        params.add(new BasicNameValuePair("Tel", user.getTel()));
        params.add(new BasicNameValuePair("userFaceBase64", userFace));
        params.add(new BasicNameValuePair("sessionKey", user.getSessionKey()));
		jc.BeginPost(APIConfig.ApiUrl+"SaveUserInfo", params);
	}
}
