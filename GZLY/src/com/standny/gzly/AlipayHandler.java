package com.standny.gzly;

import java.lang.ref.WeakReference;

import android.os.Handler;

public class AlipayHandler<T>  extends Handler{
    //ע������ġ�PopupActivity������MyHandler�����ڵ��ⲿ�࣬�����ڵ�activity  
    WeakReference<T> mActivity;  
  
    AlipayHandler(T activity) {  
        mActivity = new WeakReference<T>(activity);  
    } 
}
