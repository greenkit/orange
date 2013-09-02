package com.standny.gzly.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.AlipayOrderModel;
import com.standny.gzly.models.NameAndIDCard;
import com.standny.gzly.models.SightInfoModel;
import com.standny.gzly.models.SightViewModel;
import com.standny.gzly.models.TicketModel;
import com.standny.gzly.models.TicketOrderInfo;

public class TicketAPI {
	private Integer pageSize;
	private APICallback<List<SightViewModel>> getListCallback;
	private APICallback<List<TicketModel>> getTicketListCallback;
	private APICallback<SightInfoModel> getInfoCallback;
	private APICallback<AlipayOrderModel> sendOrderCallback;
	private APICallback<List<TicketOrderInfo>> getMyListCallback;
	private APICallback<Object> deleteOrderCallback;
	private APICallback<AlipayOrderModel> payOrderCallback;
	public TicketAPI(){
		pageSize=10;
	}
	
	public void setPageSize(Integer pageSize){
		this.pageSize=pageSize;
	}
	public Integer getPageSize(){
		return pageSize;
	}
	
	public void BeginGetInfo(Integer sightID,APICallback<SightInfoModel> callback){
		getInfoCallback=callback;
		JsonAPIClient jc=new JsonAPIClient(new JsonAPICallback(){

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<SightInfoModel> result=new APIResultModel<SightInfoModel>();
				result.setSuc(jsonObj.optBoolean("Suc",false));
				result.setMsg(jsonObj.optString("Msg", ""));
				if (result.getSuc()){
					JSONObject itemObj = jsonObj.optJSONObject("Item"); 
					 if (itemObj!=null){
							 try{
								 SightInfoModel item=new SightInfoModel();
							 item.setSightID(itemObj.getInt("SightID"));
							 item.setSightName(itemObj.getString("SightName"));
							 item.setAddress(itemObj.getString("Address"));
							 item.setPrice(itemObj.getDouble("Price"));
							 item.setRate(itemObj.getInt("Rate"));
							 item.setRateCount(itemObj.getInt("RateCount"));
							 JSONArray picsArray=itemObj.getJSONArray("Pics");
							 List<String> pics=new ArrayList<String>();
							 if(picsArray!=null){
								 for(int i=0;i<picsArray.length();i++){
//									 JSONObject json1 = ((JSONObject)picsArray.opt(i));
									 pics.add((String)picsArray.opt(i));
								 }
							 }
							 item.setPics(pics);
							 result.setItems(item);
							 }
							 catch(Exception e){
								 e.printStackTrace();
							 }
					 }
				}
				if (getInfoCallback!=null)
					getInfoCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (getInfoCallback!=null)
					getInfoCallback.onError(msg);
			}
			
		});
		List<NameValuePair> params = new ArrayList <NameValuePair>();   
        params.add(new BasicNameValuePair("id", sightID.toString()));
		jc.BeginPost(APIConfig.ApiUrl+"GetSightInfo", params);
		
	}
	
	public void BeginGetList(Integer pageIndex, APICallback<List<SightViewModel>> apiCallback){
		getListCallback=apiCallback;
		JsonAPIClient jc=new JsonAPIClient(new JsonAPICallback(){

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<List<SightViewModel>> result=new APIResultModel<List<SightViewModel>>();
				result.setSuc(jsonObj.optBoolean("Suc",false));
				result.setMsg(jsonObj.optString("Msg", ""));
				result.setCurrentPageIndex(jsonObj.optInt("PageIndex"));
				result.setTotalItemCount(jsonObj.optInt("TotalItemCount"));
				result.setTotalPageCount(jsonObj.optInt("TotalPageCount"));
				List<SightViewModel> items=new ArrayList<SightViewModel>();
				if (result.getSuc()){
					 JSONArray itemArray = jsonObj.optJSONArray("Items"); 
					 if (items!=null){
						 for(int i=0;i<itemArray.length();i++){
							 try{
							 JSONObject json1 = ((JSONObject)itemArray.opt(i));
							 SightViewModel item=new SightViewModel();
							 item.setSightID(json1.getInt("SightID"));
							 item.setSightName(json1.getString("SightName"));
							 item.setAddress(json1.getString("Address"));
							 item.setPrice(json1.getDouble("Price"));
							 item.setCoverPic(json1.getString("CoverPic"));
							 items.add(item);
							 }
							 catch(Exception e){
								 e.printStackTrace();
							 }
						 }
					 }
				}
				result.setItems(items);
				if (getListCallback!=null)
					getListCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (getListCallback!=null)
					getListCallback.onError(msg);
			}
			
		});
		List<NameValuePair> params = new ArrayList <NameValuePair>();   
        params.add(new BasicNameValuePair("id", pageIndex.toString()));
        params.add(new BasicNameValuePair("pageSize", pageSize.toString()));
		jc.BeginPost(APIConfig.ApiUrl+"GetSightList", params);
	}

	public void BeginGetTicketList(Integer sightID, Integer pageIndex, APICallback<List<TicketModel>> apiCallback){
		getTicketListCallback=apiCallback;
		JsonAPIClient jc=new JsonAPIClient(new JsonAPICallback(){

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<List<TicketModel>> result=new APIResultModel<List<TicketModel>>();
				result.setSuc(jsonObj.optBoolean("Suc",false));
				result.setMsg(jsonObj.optString("Msg", ""));
				result.setCurrentPageIndex(jsonObj.optInt("PageIndex"));
				result.setTotalItemCount(jsonObj.optInt("TotalItemCount"));
				result.setTotalPageCount(jsonObj.optInt("TotalPageCount"));
				List<TicketModel> items=new ArrayList<TicketModel>();
				if (result.getSuc()){
					 JSONArray itemArray = jsonObj.optJSONArray("Items"); 
					 if (items!=null){
						 for(int i=0;i<itemArray.length();i++){
							 try{
							 JSONObject json1 = ((JSONObject)itemArray.opt(i));
							 TicketModel item=new TicketModel();
							 item.setTicketID(json1.getInt("TicketID"));
							 item.setTicketName(json1.getString("TicketName"));
							 item.setPrice1(json1.getDouble("Price1"));
							 item.setPrice2(json1.getDouble("Price2"));
							 items.add(item);
							 }
							 catch(Exception e){
								 e.printStackTrace();
							 }
						 }
					 }
				}
				result.setItems(items);
				if (getTicketListCallback!=null)
					getTicketListCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (getTicketListCallback!=null)
					getTicketListCallback.onError(msg);
			}
			
		});
		List<NameValuePair> params = new ArrayList <NameValuePair>();   
        params.add(new BasicNameValuePair("id", pageIndex.toString()));
        params.add(new BasicNameValuePair("sightID", sightID.toString()));
        params.add(new BasicNameValuePair("pageSize", pageSize.toString()));
		jc.BeginPost(APIConfig.ApiUrl+"GetTicketList", params);
	}

	public void BeginSendOrder(String sessionKey, TicketOrderInfo order,APICallback<AlipayOrderModel> callback){
		sendOrderCallback=callback;
		JsonAPIClient jc=new JsonAPIClient(new JsonAPICallback(){

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<AlipayOrderModel> result=new APIResultModel<AlipayOrderModel>();
				result.setSuc(jsonObj.optBoolean("Suc",false));
				result.setMsg(jsonObj.optString("Msg", ""));
				if (result.getSuc()){
					JSONObject itemObj = jsonObj.optJSONObject("Item"); 
					 if (itemObj!=null){
							 try{
								 AlipayOrderModel item=new AlipayOrderModel();
								 item.content=itemObj.getString("content");
								 item.sign=itemObj.getString("sign");
								 result.setItems(item);
							 }
							 catch(Exception e){
								 e.printStackTrace();
							 }
					 }
				}
				if (sendOrderCallback!=null)
					sendOrderCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (sendOrderCallback!=null)
					sendOrderCallback.onError(msg);
			}
			
		});
		List<NameValuePair> params = new ArrayList <NameValuePair>();   
        params.add(new BasicNameValuePair("sessionKey", sessionKey));
        params.add(new BasicNameValuePair("buyCount",String.valueOf(order.getBuyCount())));
        params.add(new BasicNameValuePair("ticketID",String.valueOf(order.getTicketID())));
        params.add(new BasicNameValuePair("contact",order.getContact()));
        params.add(new BasicNameValuePair("contactTel",order.getContactTel()));
        params.add(new BasicNameValuePair("useTime",order.getUseTimeStr()));
        StringBuilder sb=new StringBuilder();
        List<NameAndIDCard> items=order.getBuyer();
        for(int i=0;i<items.size();i++){
        	if (i>0)
        		sb.append(",");
        	sb.append(items.get(i).Name);
        	sb.append("$");
        	sb.append(items.get(i).IDCard);
        }
        params.add(new BasicNameValuePair("buyerInfo",sb.toString()));
		jc.BeginPost(APIConfig.ApiUrl+"BuyTicket", params);
	}
	
	public void beginGetMyList(String sessionKey, Integer pageIndex,APICallback<List<TicketOrderInfo>> apiCallback) {
		getMyListCallback = apiCallback;
		JsonAPIClient jc = new JsonAPIClient(new JsonAPICallback() {

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<List<TicketOrderInfo>> result = new APIResultModel<List<TicketOrderInfo>>();
				result.setSuc(jsonObj.optBoolean("Suc", false));
				result.setMsg(jsonObj.optString("Msg", ""));
				result.setCurrentPageIndex(jsonObj.optInt("PageIndex"));
				result.setTotalItemCount(jsonObj.optInt("TotalItemCount"));
				result.setTotalPageCount(jsonObj.optInt("TotalPageCount"));
				List<TicketOrderInfo> items = new ArrayList<TicketOrderInfo>();
				if (result.getSuc()) {
					JSONArray itemArray = jsonObj.optJSONArray("Items");
					if (items != null) {
						for (int i = 0; i < itemArray.length(); i++) {
							try {
								JSONObject json1 = ((JSONObject) itemArray
										.opt(i));
								TicketOrderInfo item = new TicketOrderInfo();
								item.setTicketName(json1.optString("TicketName"));
								item.setSightName(json1.optString("SightName"));
								item.setOrderID(json1.optInt("OrderID",0));
								item.setBuyCount(json1.optInt("BuyCount",0));
								item.setUseTime(DateTime.fromJSONString(json1.optString("UseTime")).getDate());
								item.setPrice(json1.optDouble("Price",0));
								item.setOrderStatus(json1.optInt("Status"));
								String tmp=json1.optString("BuyerInfo");
								if (tmp!=null&&tmp.length()>0&&!tmp.equalsIgnoreCase("null")){
									String[] tmpArr=tmp.split(",");
									for(int j=0;j<tmpArr.length;j++){
										String[] itemArr=tmpArr[j].split("\\$");
										if (itemArr.length==2){
										NameAndIDCard buyer=new NameAndIDCard();
										buyer.Name=itemArr[0];
										buyer.IDCard=itemArr[1];
										item.addBuyer(buyer);
										}
									}
								}

								items.add(item);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				result.setItems(items);
				if (getMyListCallback != null)
					getMyListCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (getMyListCallback != null)
					getMyListCallback.onError(msg);
			}

		});
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sessionKey", sessionKey));
		params.add(new BasicNameValuePair("id", pageIndex.toString()));
		params.add(new BasicNameValuePair("pageSize", pageSize.toString()));
		jc.BeginPost(APIConfig.ApiUrl + "GetMyTicketList", params);
	}

	public void beginDeleteMyOrder(String sessionKey,int orderID,APICallback<Object> apiCallback){
		deleteOrderCallback = apiCallback;
		JsonAPIClient jc = new JsonAPIClient(new JsonAPICallback() {

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<Object> result = new APIResultModel<Object>();
				result.setSuc(jsonObj.optBoolean("Suc", false));
				result.setMsg(jsonObj.optString("Msg", ""));
				if (deleteOrderCallback != null)
					deleteOrderCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (deleteOrderCallback != null)
					deleteOrderCallback.onError(msg);
			}

		});
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sessionKey", sessionKey));
		params.add(new BasicNameValuePair("id", String.valueOf(orderID)));
		jc.BeginPost(APIConfig.ApiUrl + "DeleteMyTicket", params);
		
	}

	public void beginGetPaySign(String sessionKey,int orderID,APICallback<AlipayOrderModel> apiCallback){
		payOrderCallback = apiCallback;
		JsonAPIClient jc = new JsonAPIClient(new JsonAPICallback() {

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<AlipayOrderModel> result = new APIResultModel<AlipayOrderModel>();
				result.setSuc(jsonObj.optBoolean("Suc", false));
				result.setMsg(jsonObj.optString("Msg", ""));
				if (result.getSuc()){
					JSONObject itemObj = jsonObj.optJSONObject("Item"); 
					 if (itemObj!=null){
							 try{
								 AlipayOrderModel item=new AlipayOrderModel();
								 item.content=itemObj.getString("content");
								 item.sign=itemObj.getString("sign");
								 result.setItems(item);
							 }
							 catch(Exception e){
								 e.printStackTrace();
							 }
					 }
				}
				if (payOrderCallback != null)
					payOrderCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (payOrderCallback != null)
					payOrderCallback.onError(msg);
			}

		});
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("sessionKey", sessionKey));
		params.add(new BasicNameValuePair("id", String.valueOf(orderID)));
		jc.BeginPost(APIConfig.ApiUrl + "GetMyTicketPaySign", params);
		
	}
	
}
