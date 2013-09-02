package com.standny.gzly;

import java.util.ArrayList;
import java.util.List;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.HotelSearchModel;
import com.standny.gzly.models.HotelViewModel;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.CGSize;
import com.standny.gzly.repository.HotelAPI;
import com.standny.gzly.repository.ImageDownloadCallback;
import com.standny.gzly.repository.ImageManager;
import com.standny.ui.FingerTracker;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class HotelListActivity extends MasterActivity implements
		OnScrollListener {

	private ListView list;
	private ImageManager imgManager;
	private int visibleLastIndex = 0; // 最后的可视项索引
	private int pageIndex;
	private int totalItemCount;
	private PaginationAdapter adapter;
	private boolean isloading;
	private View loadMoreView;
	private HotelSearchModel filter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_list);
		Intent i = getIntent();
		filter = new HotelSearchModel();
		filter.setInDays(1);
		filter.setCheckInTime(i.getStringExtra("checkIn"));
		filter.setCity(i.getStringExtra("city"));
		filter.setHavBreakfast(i.getBooleanExtra("havBreakfast", false));
		filter.setHavDiningRoom(i.getBooleanExtra("havDiningRoom", false));
		filter.setHavHotWater(i.getBooleanExtra("havHotWater", false));
		filter.setHavMeetingRoom(i.getBooleanExtra("havMeetingRoom", false));
		filter.setHavPark(i.getBooleanExtra("havPark", false));
		filter.setInDays(i.getIntExtra("inDays", 0));
		filter.setKey(i.getStringExtra("key"));
		filter.setStartPrice(i.getIntExtra("priceStart", 0));
		filter.setEndPrice(i.getIntExtra("priceEnd", 0));
		filter.setTypeID(i.getIntExtra("type", 0));
		tabIndex = 2;
		list = (ListView) findViewById(R.id.list);
		list.setOnScrollListener(this);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View listItem,
					int position, long id) {
				if (listItem == loadMoreView&&!loadMoreView.getTag().equals("no")) {
					if (!isloading)
						loadItems(++pageIndex);
					return;
				}
				Intent intent = new Intent();
				intent.setClass(HotelListActivity.this, HotelViewActivity.class);
				intent.putExtra("hotelInfo",
						(HotelViewModel) adapter.getItem(position));
				intent.putExtra("checkIn",
						filter.getCheckInTimeStr("yyyy-MM-dd"));
				intent.putExtra("inDays", filter.getInDays());
				intent.putExtra("checkOut",
						filter.getCheckOutTimeStr("yyyy-MM-dd"));
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
				// HotelListActivity.this.finish();
			}
		});

		//if (Build.VERSION.SDK_INT <= 7) {
			list.setOnTouchListener(new FingerTracker(this));
		//}
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		imgManager = new ImageManager(new CGSize(
				(int) Math.round(dm.widthPixels * 0.4),
				(int) Math.round(dm.widthPixels * 0.4)));
		initializeAdapter();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int itemsLastIndex = adapter.getCount(); // 数据集最后一项的索引
		// int lastIndex = itemsLastIndex + 1;
		if (!isloading && scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& visibleLastIndex == itemsLastIndex
				&& itemsLastIndex < this.totalItemCount) {
			pageIndex++;
			loadItems(pageIndex);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// this.visibleItemCount = visibleItemCount;
		visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
		// 如果所有的记录选项等于数据集的条数，则移除列表底部视图
		if (this.totalItemCount > 0
				&& totalItemCount == this.totalItemCount + 1) {
			// removeLoadingRow();
			loadMoreView.setTag("no");
			this.changeLoadingRowText("没有更多了");
			// Toast.makeText(this, "数据全部加载完!", Toast.LENGTH_LONG).show();
		}
	}

	private void initializeAdapter() {
		adapter = new PaginationAdapter();
		addLoadingRow();
		list.setAdapter(adapter);
		pageIndex = 1;
		totalItemCount = 0;
		loadItems(pageIndex);
	}

	public void addLoadingRow() {
		// LayoutInflater inflater;
		// inflater = LayoutInflater.from(this);
		loadMoreView = getLayoutInflater().inflate(R.layout.table_loading_row,
				null);
		loadMoreView.setTag("yes");
		list.addFooterView(loadMoreView);
		// table.addView(row);
	}

	public void removeLoadingRow() {
		list.removeFooterView(loadMoreView);
		// table.removeViewAt(0);
	}

	public void changeLoadingRowText(String txt) {
		TextView lbl = (TextView) loadMoreView.findViewById(R.id.title);
		lbl.setText(txt);
	}

	public void loadItems(Integer pageIndex) {
		if (isloading) return;
		isloading = true;
		changeLoadingRowText("载入中...");
		HotelAPI api = new HotelAPI();
		api.beginSearch(filter, pageIndex,
				new APICallback<List<HotelViewModel>>() {

					@Override
					public void onCompleted(
							APIResultModel<List<HotelViewModel>> result) {
						isloading = false;
						changeLoadingRowText("查看更多...");
						if (result.getSuc()) {
							totalItemCount = result.getTotalItemCount();
							if (totalItemCount == 0) {
								changeLoadingRowText("没有符合条件的酒店.");
							} else {
								List<HotelViewModel> items = result.getItems();
								adapter.addItem(items);
							}
							// list.
						} else {
							Toast.makeText(HotelListActivity.this, result.getMsg(),
									Toast.LENGTH_LONG).show();

						}
					}

					@Override
					public void onError(String msg) {
						isloading = false;
						AlertDialog.Builder builder = new Builder(
								HotelListActivity.this);
						builder.setMessage(msg);
						builder.setTitle("载入数据失败");
						builder.setPositiveButton("重试", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								loadItems(HotelListActivity.this.pageIndex);
								dialog.dismiss();
							}
						});
						builder.setNegativeButton("取消", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
						try {
							builder.create().show();
						} catch (Exception e) {
							e.printStackTrace();
						}
						// Toast.makeText(TicketActivity.this, msg,
						// Toast.LENGTH_LONG).show();
					}

				});
	}

	class PaginationAdapter extends BaseAdapter {
		List<HotelViewModel> items;

		public PaginationAdapter() {
			this.items = new ArrayList<HotelViewModel>();
		}

		public PaginationAdapter(List<HotelViewModel> items) {
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			if (position < items.size())
				return items.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			HotelViewModel item = items.get(position);
			if (view == null)
				view = getLayoutInflater().inflate(R.layout.hotel_list_item,
						null);
			final ImageView image = (ImageView) view.findViewById(R.id.image);
			if (item.getCoverPic() != null && item.getCoverPic().length() > 0) {
				Bitmap bmp = imgManager.GetImage(item.getCoverPic(),
						new CGSize(100, 100), R.id.image,
						new ImageDownloadCallback() {

							@Override
							public void onCompleted(Bitmap bmp, int tag) {
								image.setImageBitmap(bmp);
							}

							@Override
							public void onError(String msg, int tag) {
								// Toast.makeText(HotelListActivity.this,
								// msg,Toast.LENGTH_LONG).show();
							}

						});
				if (bmp != null)
					image.setImageBitmap(bmp);
				else
					image.setImageResource(R.drawable.list_nopic);
			} else
				image.setImageResource(R.drawable.list_nopic);
			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText(item.getHotelName());
			TextView price = (TextView) view.findViewById(R.id.price);
			java.text.DecimalFormat df = new java.text.DecimalFormat("#");
			price.setText(df.format(item.getPrice()));
			view.setTag(item.getHotelID());
			TextView rate = (TextView) view.findViewById(R.id.rate);
			rate.setText(String.valueOf(item.getRate()));
			TextView type = (TextView) view.findViewById(R.id.type);
			type.setText(item.getHotelType());
			TextView address = (TextView) view.findViewById(R.id.address);
			address.setText(item.getAddress());
			ImageView park = (ImageView) view.findViewById(R.id.park);
			if (!item.getHavPark())
				park.setVisibility(View.GONE);
			else
				park.setVisibility(View.VISIBLE);
			ImageView water = (ImageView) view.findViewById(R.id.hotwater);
			if (!item.getHav24HotWater())
				water.setVisibility(View.GONE);
			else
				water.setVisibility(View.VISIBLE);
			ImageView meetingRoom = (ImageView) view.findViewById(R.id.meeting);
			if (!item.getHavMeetingRoom())
				meetingRoom.setVisibility(View.GONE);
			else
				meetingRoom.setVisibility(View.VISIBLE);
			ImageView diningRoom = (ImageView) view
					.findViewById(R.id.diningroom);
			if (!item.getHavDiningRoom())
				diningRoom.setVisibility(View.GONE);
			else
				diningRoom.setVisibility(View.VISIBLE);
			ImageView breakfast = (ImageView) view.findViewById(R.id.breakfast);
			if (!item.getHavBreakfast())
				breakfast.setVisibility(View.GONE);
			else
				breakfast.setVisibility(View.VISIBLE);

			return view;
		}

		public void addItem(List<HotelViewModel> items) {
			this.items.addAll(items);
			this.notifyDataSetChanged();
		}

		public void addItem(HotelViewModel item) {
			items.add(item);
			this.notifyDataSetChanged();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent intent = new Intent();
			intent.setClass(HotelListActivity.this, HotelActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
