package com.standny.gzly.repository;

import android.graphics.Bitmap;

public interface ImageDownloadCallback {
	void onCompleted(Bitmap bmp,int tag);
	void onError(String msg,int tag);
}
