package com.standny.gzly;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuItemAdapter  extends BaseAdapter  {
	 private LayoutInflater inflater;   
	    private List<MenuItem> gridItemList;   
	    private Integer itemLayoutID;
	   
	    public MenuItemAdapter(Integer[] title, Integer[] img,Integer[] bgImg,Integer itemLayoutID, Context context)   
	    {   
	        super();   
	        this.itemLayoutID=itemLayoutID;
	        gridItemList = new ArrayList<MenuItem>();   
	        inflater = LayoutInflater.from(context);   
	        for (int i = 0; i < img.length; i++)   
	        {   
	        	MenuItem picture = new MenuItem(title[i], img[i],bgImg[i]);   
	            gridItemList.add(picture);   
	        }   
	    }   
	    @Override  
	    public int getCount( )  
	    {  
	        if (null != gridItemList)   
	        {   
	            return gridItemList.size();   
	        }   
	        else  
	        {   
	            return 0;   
	        }   
	    }  
	  
	    @Override  
	    public Object getItem( int position )  
	    {  
	        return gridItemList.get(position);   
	    }  
	  
	    @Override  
	    public long getItemId( int position )  
	    {  
	        return position;   
	    }  
	  
	    @SuppressWarnings("deprecation")
		@Override  
	    public View getView( int position, View convertView, ViewGroup parent )  
	    {  
	        MenuItemViewHelper viewHolder;   
	        if (convertView == null)   
	        {   
	            convertView = inflater.inflate(itemLayoutID, null);   
	            viewHolder = new MenuItemViewHelper();   
	            viewHolder.title = (TextView) convertView.findViewById(R.id.title);   
	            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);  
	            convertView.setTag(viewHolder);   
	        } else  
	        {   
	            viewHolder = (MenuItemViewHelper) convertView.getTag();   
	        }   
	        Integer bgID=gridItemList.get(position).getBgImgID();
	        if (bgID>0){
	        Resources res = convertView.getContext().getResources();
	        Drawable drawable = res.getDrawable(bgID);
	        if(Build.VERSION.SDK_INT >= 16){
	        	convertView.setBackground(drawable);
	        }else{
	        	convertView.setBackgroundDrawable(drawable);
	        }
	        }
	        //convertView .setBackground(drawable);
	        viewHolder.title.setText(gridItemList.get(position).getTitleID());  
	        viewHolder.image.setImageResource(gridItemList.get(position).getImgID());   
	        return convertView;   
	    }  
}
