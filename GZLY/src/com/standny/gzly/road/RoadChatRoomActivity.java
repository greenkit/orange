package com.standny.gzly.road;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.standny.gzly.MasterActivity;
import com.standny.gzly.MenuItem;
import com.standny.gzly.R;

public class RoadChatRoomActivity extends MasterActivity {
    
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.road_chat_room_activity);

        initUI();

    }
    
    private void initUI () {
        ((TextView) findViewById(R.id.tv_activity_title)).setText(R.string.in_the_road);
        listView = (ListView) findViewById(R.id.list_chat);
        listView.setAdapter(new ChatAdapter(this));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View arg1,
                    int position, long arg3) {
                
            }
        });
    }

    static class ChatAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<MenuItem> gridItemList;
        private Context mContext;

        public ChatAdapter(Context context) {
            mContext = context;
            gridItemList = new ArrayList<MenuItem>();
            inflater = LayoutInflater.from(context);
        }

        
        @Override
        public int getCount() {
            return 11 + 1;
        }

        @Override
        public Object getItem(int position) {
            return gridItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.road_chat_room_list_item, null);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
                viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
                viewHolder.ivPicture = (ImageView) convertView.findViewById(R.id.iv_picture);
                viewHolder.ivPortrait = (ImageView) convertView.findViewById(R.id.iv_portrait);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            return convertView;
        }
        
        class ViewHolder {
            ImageView ivPortrait;
            TextView tvName;
            TextView tvContent;
            TextView tvAddress;
            ImageView ivPicture;
        }
    }

}
