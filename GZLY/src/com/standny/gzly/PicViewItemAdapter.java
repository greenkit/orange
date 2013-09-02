package com.standny.gzly;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class PicViewItemAdapter extends PagerAdapter {
	private List<Bitmap> pics;

	private Context mContext;
	//private int defID;

	public PicViewItemAdapter(Context cx,int defID) {
		//this.defID=defID;
		mContext = cx.getApplicationContext();
		pics=new ArrayList<Bitmap>();
	}

	public void change(List<Bitmap> bmps) {
		pics.addAll(bmps);
		this.notifyDataSetChanged();
	}
	
	public void add(){
		pics.add(null);
		this.notifyDataSetChanged();
	}
	
	public void add(Bitmap bmp){
		pics.add(bmp);
		this.notifyDataSetChanged();
	}
	
	public void update(Bitmap bmp,int index){
		if (index>=0&&index<pics.size()){
			pics.set(index, bmp);
		}
	}

	@Override
	public int getCount() {
		return pics.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == (View) obj;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView iv = new ImageView(mContext);
		iv.setId(position+10000);
		Bitmap bm = pics.get(position);
		if (bm==null){
			//iv.setImageResource(defID);
		}
		else{
		iv.setImageBitmap(bm);
		}
		iv.setScaleType(ScaleType.CENTER_CROP);
		//if (container.getChildCount()>=position)
			((ViewPager) container).addView(iv);
		return iv;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}
