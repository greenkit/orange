package com.standny.gzly.repository;

import com.standny.gzly.models.APIResultModel;

public interface APICallback<T> {
	void onCompleted(APIResultModel<T> result);
	void onError(String msg);
}
