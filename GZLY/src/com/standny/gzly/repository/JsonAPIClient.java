package com.standny.gzly.repository;

import java.util.List;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonAPIClient {
	private JsonAPICallback callback;

	public JsonAPIClient(JsonAPICallback callback) {
		this.callback = callback;
	}

	public void BeginPost(String url, List<NameValuePair> params) {
		WebClient wc = new WebClient(new WebClientCallback() {

			@Override
			public void onCompleted(boolean suc, Object content) {
				if (suc) {
					JSONObject result = parseJson((String) content);
					if (result != null && callback != null)
						callback.onCompleted(result);
				} else {
					if (callback != null)
						callback.onError((String) content);
				}
			}
		});
		wc.BeginPost(url, params);
	}

	public void BeginGet(String url) {
		WebClient wc = new WebClient(new WebClientCallback() {
			@Override
			public void onCompleted(boolean suc, Object content) {
				if (suc) {
					JSONObject result = parseJson((String) content);
					if (result != null && callback != null)
						callback.onCompleted(result);
				} else {
					if (callback != null)
						callback.onError((String) content);
				}
			}
		});
		wc.BeginGet(url);
	}

	private JSONObject parseJson(String strResult) {
		try {
			return new JSONObject(strResult);
		} catch (JSONException e) {
			if (this.callback != null)
				callback.onError("数据格式不正确");
			//callback.onError(e.getMessage().toString());
			e.printStackTrace();
		}
		return null;
	}

}
