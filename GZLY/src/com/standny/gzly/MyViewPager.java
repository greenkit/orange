package com.standny.gzly;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {  
	  
    public MyViewPager(Context context) {  
        super(context);  
    }  
      
    public MyViewPager(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    @Override  
    public boolean dispatchTouchEvent(MotionEvent ev) {  
        getParent().requestDisallowInterceptTouchEvent(true);//��仰������ ���߸�view���ҵĵ����¼������д�����Ҫ�谭�ҡ�    
        return super.dispatchTouchEvent(ev);  
    }  
}  