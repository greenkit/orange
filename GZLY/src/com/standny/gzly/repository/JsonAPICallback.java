package com.standny.gzly.repository;

import org.json.JSONObject;

public interface JsonAPICallback {
	void onCompleted(JSONObject jsonObj);
	void onError(String msg);
}
