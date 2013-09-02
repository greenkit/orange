package com.standny.gzly;

import java.lang.ref.WeakReference;

import android.os.Handler;

public class AlipayHandler<T>  extends Handler{
    //注意下面的“PopupActivity”类是MyHandler类所在的外部类，即所在的activity  
    WeakReference<T> mActivity;  
  
    AlipayHandler(T activity) {  
        mActivity = new WeakReference<T>(activity);  
    } 
}
