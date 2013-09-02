package com.standny.gzly.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.AlipayOrderModel;
import com.standny.gzly.models.HotelSearchModel;
import com.standny.gzly.models.HotelViewModel;
import com.standny.gzly.models.NameAndIDCard;
import com.standny.gzly.models.RoomModel;
import com.standny.gzly.models.RoomOrderInfo;

public class HotelAPI {
	private Integer pageSize;
	private APICallback<List<HotelViewModel>> getListCallback;
	private APICallback<HotelViewModel> getInfoCallback;
	private APICallback<List<RoomModel>> getRoolListCallback;
	private APICallback<AlipayOrderModel> sendOrderCallback;
	private APICallback<List<RoomOrderInfo>> getMyListCallback;
	private APICallback<Object> deleteOrderCallback;
	private APICallback<AlipayOrderModel> payOrderCallback;

	public HotelAPI() {
		pageSize = 10;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void beginSearch(HotelSearchModel filter, Integer pageIndex,
			APICallback<List<HotelViewModel>> apiCallback) {
		getListCallback = apiCallback;
		JsonAPIClient jc = new JsonAPIClient(new JsonAPICallback() {

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<List<HotelViewModel>> result = new APIResultModel<List<HotelViewModel>>();
				result.setSuc(jsonObj.optBoolean("Suc", false));
				result.setMsg(jsonObj.optString("Msg", ""));
				result.setCurrentPageIndex(jsonObj.optInt("PageIndex"));
				result.setTotalItemCount(jsonObj.optInt("TotalItemCount"));
				result.setTotalPageCount(jsonObj.optInt("TotalPageCount"));
				List<HotelViewModel> items = new ArrayList<HotelViewModel>();
				if (result.getSuc()) {
					JSONArray itemArray = jsonObj.optJSONArray("Items");
					if (items != null) {
						for (int i = 0; i < itemArray.length(); i++) {
							try {
								JSONObject json1 = ((JSONObject) itemArray
										.opt(i));
								HotelViewModel item = new HotelViewModel();
								item.setHotelID(json1.optInt("ID", 0));
								item.setHotelName(json1.optString("HotelName",
										""));
								item.setAddress(json1.optString("Address", ""));
								item.setPrice(json1.optDouble("Price", 0));
								item.setCoverPic(json1.optString("ImgSrc", ""));
								item.setHav24HotWater(json1.optBoolean(
										"Have24HotWater", false));
								item.setHavBreakfast(json1.optBoolean(
										"HaveBreakfast", false));
								item.setHavDiningRoom(json1.optBoolean(
										"HaveDiningRoom", false));
								item.setHavMeetingRoom(json1.optBoolean(
										"HaveMeetingRoom", false));
								item.setHavPark(json1.optBoolean("HavePark",
										false));
								item.setHotelType(json1.optString("HotelType",""));
								item.setRate((float) json1.optDouble("Rate", 0));
								item.setRateCount(json1.optInt("RateCount", 0));

								items.add(item);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				result.setItems(items);
				if (getListCallback != null)
					getListCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (getListCallback != null)
					getListCallback.onError(msg);
			}

		});
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (filter.getCity() != null && filter.getCity().length() > 0)
			params.add(new BasicNameValuePair("Region", filter.getCity()));
		if (filter.getKey() != null && filter.getKey().length() > 0)
			params.add(new BasicNameValuePair("SearchKey", filter.getKey()));
		if (filter.getStartPrice() > 0)
			params.add(new BasicNameValuePair("PriceStart", String
					.valueOf(filter.getStartPrice())));
		if (filter.getEndPrice() > 0 && filter.getEndPrice() < 1000)
			params.add(new BasicNameValuePair("PriceEnd", String.valueOf(filter
					.getEndPrice())));
		if (filter.getHavBreakfast()) {
			params.add(new BasicNameValuePair("HotelFacility", "2"));
		}
		if (filter.getHavDiningRoom()) {
			params.add(new BasicNameValuePair("HotelFacility", "0"));
		}
		if (filter.getHavHotWater()) {
			params.add(new BasicNameValuePair("HotelFacility", "3"));
		}
		if (filter.getHavMeetingRoom()) {
			params.add(new BasicNameValuePair("HotelFacility", "1"));
		}
		if (filter.getHavPark()) {
			params.add(new BasicNameValuePair("HotelFacility", "4"));
		}
		if (filter.getTypeID() > 0)
			params.add(new BasicNameValuePair("HotelType", String
					.valueOf(filter.getTypeID())));
		params.add(new BasicNameValuePair("id", pageIndex.toString()));
		params.add(new BasicNameValuePair("pageSize", pageSize.toString()));
		jc.BeginPost(APIConfig.ApiUrl + "HotelSearch", params);
	}

	public void beginGetInfo(Integer hotelID,
			APICallback<HotelViewModel> callback) {
		getInfoCallback = callback;
		JsonAPIClient jc = new JsonAPIClient(new JsonAPICallback() {

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<HotelViewModel> result = new APIResultModel<HotelViewModel>();
				result.setSuc(jsonObj.optBoolean("Suc", false));
				result.setMsg(jsonObj.optString("Msg", ""));
				if (result.getSuc()) {
					JSONObject itemObj = jsonObj.optJSONObject("Item");
					if (itemObj != null) {
						try {
							HotelViewModel item = new HotelViewModel();
							item.setHotelID(itemObj.optInt("ID", 0));
							item.setHotelName(itemObj.optString("HotelName", ""));
							item.setAddress(itemObj.optString("Address", ""));
							item.setPrice(itemObj.optDouble("Price", 0));
							item.setCoverPic(itemObj.optString("ImgSrc", ""));
							item.setHav24HotWater(itemObj.optBoolean("Have24HotWater", false));
							item.setHavBreakfast(itemObj.optBoolean("HaveBreakfast", false));
							item.setHavDiningRoom(itemObj.optBoolean("HaveDiningRoom", false));
							item.setHavMeetingRoom(itemObj.optBoolean("HaveMeetingRoom", false));
							item.setHavPark(itemObj.optBoolean("HavePark",false));
							item.setHotelType(itemObj.optString("HotelType", ""));
							item.setRate((float) itemObj.optDouble("Rate", 0));
							JSONArray picsArray = itemObj.getJSONArray("Pics");
							List<String> pics = new ArrayList<String>();
							if (picsArray != null) {
								for (int i = 0; i < picsArray.length(); i++) {
									pics.add((String) picsArray.opt(i));
								}
							}
							item.setPics(pics);
							result.setItems(item);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if (getInfoCallback != null)
					getInfoCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (getInfoCallback != null)
					getInfoCallback.onError(msg);
			}

		});
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", hotelID.toString()));
		jc.BeginPost(APIConfig.ApiUrl + "GetHotelInfo", params);

	}

	public void beginGetRoomList(Integer hotelID, Integer pageIndex, APICallback<List<RoomModel>> apiCallback){
		getRoolListCallback=apiCallback;
		JsonAPIClient jc=new JsonAPIClient(new JsonAPICallback(){

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<List<RoomModel>> result=new APIResultModel<List<RoomModel>>();
				result.setSuc(jsonObj.optBoolean("Suc",false));
				result.setMsg(jsonObj.optString("Msg", ""));
				result.setCurrentPageIndex(jsonObj.optInt("PageIndex"));
				result.setTotalItemCount(jsonObj.optInt("TotalItemCount"));
				result.setTotalPageCount(jsonObj.optInt("TotalPageCount"));
				List<RoomModel> items=new ArrayList<RoomModel>();
				if (result.getSuc()){
					 JSONArray itemArray = jsonObj.optJSONArray("Items"); 
					 if (items!=null){
						 for(int i=0;i<itemArray.length();i++){
							 try{
							 JSONObject json1 = ((JSONObject)itemArray.opt(i));
							 RoomModel item=new RoomModel();
							 item.setRoomID(json1.optInt("RoomID"));
							 item.setRoomName(json1.optString("RoomName"));
							 item.setPrice(json1.optDouble("Price"));
							 item.setBedType(json1.optString("BedType"));
							 item.setHavNetwork(json1.optBoolean("HaveNetwork"));
							 items.add(item);
							 }
							 catch(Exception e){
								 e.printStackTrace();
							 }
						 }
					 }
				}
				result.setItems(items);
				if (getRoolListCallback!=null)
					getRoolListCallback.onCompleted(result);
			}

			@Override
			public void onError(String msg) {
				if (getRoolListCallback!=null)
					getRoolListCallback.onError(msg);
			}
			
		});
		List<NameValuePair> params = new ArrayList <NameValuePair>();   
        params.add(new BasicNameValuePair("id", pageIndex.toString()));
        params.add(new BasicNameValuePair("hotelID", hotelID.toString()));
        params.add(new BasicNameValuePair("pageSize", pageSize.toString()));
		jc.BeginPost(APIConfig.ApiUrl+"GetRoomList", params);
	}
	
	public void beginSendOrder(String sessionKey, RoomOrderInfo order,APICallback<AlipayOrderModel> callback){
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
        params.add(new BasicNameValuePair("roomID",String.valueOf(order.getRoomID())));
        params.add(new BasicNameValuePair("contact",order.getContact()));
        params.add(new BasicNameValuePair("contactTel",order.getContactTel()));
        params.add(new BasicNameValuePair("checkInTime",order.getCheckInTime().toString("yyyy-MM-dd")));
        params.add(new BasicNameValuePair("checkOutTime",order.getCheckOutTime().toString("yyyy-MM-dd")));
        params.add(new BasicNameValuePair("goTime",new StringBuilder(order.getComeStartTime()).append("-").append(order.getComeEndTime()).toString()));
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
        params.add(new BasicNameValuePair("inNum",String.valueOf(items.size())));
		jc.BeginPost(APIConfig.ApiUrl+"BuyRoom", params);
	}
	
	public void beginGetMyList(String sessionKey, Integer pageIndex,APICallback<List<RoomOrderInfo>> apiCallback) {
		getMyListCallback = apiCallback;
		JsonAPIClient jc = new JsonAPIClient(new JsonAPICallback() {

			@Override
			public void onCompleted(JSONObject jsonObj) {
				APIResultModel<List<RoomOrderInfo>> result = new APIResultModel<List<RoomOrderInfo>>();
				result.setSuc(jsonObj.optBoolean("Suc", false));
				result.setMsg(jsonObj.optString("Msg", ""));
				result.setCurrentPageIndex(jsonObj.optInt("PageIndex"));
				result.setTotalItemCount(jsonObj.optInt("TotalItemCount"));
				result.setTotalPageCount(jsonObj.optInt("TotalPageCount"));
				List<RoomOrderInfo> items = new ArrayList<RoomOrderInfo>();
				if (result.getSuc()) {
					JSONArray itemArray = jsonObj.optJSONArray("Items");
					if (items != null) {
						for (int i = 0; i < itemArray.length(); i++) {
							try {
								JSONObject json1 = ((JSONObject) itemArray
										.opt(i));
								RoomOrderInfo item = new RoomOrderInfo();
								item.setRoomName(json1.optString("RoomName"));
								item.setHotelName(json1.optString("HotelName"));
								item.setOrderID(json1.optInt("OrderID",0));
								item.setBuyCount(json1.optInt("BuyCount",0));
								item.setCheckInTime(DateTime.fromJSONString(json1.optString("CheckInTime")));
								item.setCheckOutTime(DateTime.fromJSONString(json1.optString("CheckOutTime")));
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
		jc.BeginPost(APIConfig.ApiUrl + "GetMyHotelList", params);
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
		jc.BeginPost(APIConfig.ApiUrl + "DeleteMyHotel", params);
		
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
		jc.BeginPost(APIConfig.ApiUrl + "GetMyHotelPaySign", params);
		
	}
	
	
}
