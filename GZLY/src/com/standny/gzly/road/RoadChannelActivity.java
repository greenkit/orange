package com.standny.gzly.road;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.standny.gzly.MasterActivity;
import com.standny.gzly.MenuItem;
import com.standny.gzly.R;
import com.standny.gzly.utils.DialogUtils;

public class RoadChannelActivity extends MasterActivity {
    
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.road_channel_activity);

        initUI();

    }
    
    private void initUI () {
        ((TextView) findViewById(R.id.tv_activity_title)).setText(R.string.in_the_road);
        gridView = (GridView) findViewById(R.id.grid_channel);
        gridView.setNumColumns(2);
        gridView.setAdapter(new ChannelAdapter(this));
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View arg1,
                    int position, long arg3) {
                if(position == adapterView.getAdapter().getCount() - 1) {
                    DialogUtils.showCreateChannelDialog(RoadChannelActivity.this);
                } else {
                    Intent intent = new Intent(RoadChannelActivity.this, RoadChatRoomActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    static class ChannelAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<MenuItem> gridItemList;
        private Context mContext;
        private TypedArray channelBgColor;
        private Random random;

        public ChannelAdapter(Context context) {
            mContext = context;
            gridItemList = new ArrayList<MenuItem>();
            inflater = LayoutInflater.from(context);
            channelBgColor = mContext.getResources().obtainTypedArray(R.array.channel_random_bg);
            random = new Random();
        }

        
        @Override
        protected void finalize() throws Throwable {
            if(channelBgColor != null) {
                channelBgColor.recycle();
            }
            super.finalize();
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
        
        private int getRandomBgColor() {
            return channelBgColor.getColor(random.nextInt(6), 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null || convertView.getTag() == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.road_channel_item, null);
                viewHolder.channelLayout = convertView.findViewById(R.id.rl_item);
                viewHolder.addChannel = convertView.findViewById(R.id.tv_add_channel);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tvSummary = (TextView) convertView.findViewById(R.id.tv_summary);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            convertView.setBackgroundColor(getRandomBgColor());
            
            if(getCount() == position + 1) {
                viewHolder.addChannel.setVisibility(View.VISIBLE);
                viewHolder.channelLayout.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.channelLayout.setVisibility(View.VISIBLE);
                viewHolder.addChannel.setVisibility(View.GONE);
            }
            viewHolder.tvTitle.setText("" + viewHolder.tvTitle.getText() + position);
            return convertView;
        }
        
        class ViewHolder {
            View channelLayout;
            TextView tvTitle;
            TextView tvSummary;
            View addChannel;
        }
    }

}
