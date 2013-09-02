package com.standny.gzly;

import java.util.ArrayList;
import java.util.List;
import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.HotelSearchModel;
import com.standny.gzly.models.HotelViewModel;
import com.standny.gzly.models.RoomModel;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.APIConfig;
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
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class HotelViewActivity extends MasterActivity implements
		OnScrollListener {
	private TextView title;
	private TextView rate_value;
	private TextView rate_count;
	private TextView address;
	private CheckBox havDiningRoom;
	private CheckBox havMeetingRoom;
	private CheckBox havBreakfast;
	private CheckBox havHotWater;
	private CheckBox havPark;
	private Button btn_order_notify;
	private Button btn_detail;
	private ImageView coverPic;
	//private TextView checkIn;
	//private TextView checkOut;

	private View headerView;
	private ListView list;
	private PaginationAdapter adapter;
	private View loadMoreView;

	private ImageManager imgManager;
	private HotelViewModel hotelInfo;

	private int visibleLastIndex = 0; // 最后的可视项索引
	private int pageIndex;
	private int totalItemCount;
	private int hotelID;
	private HotelSearchModel filter;
	private boolean isloading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_view);
		tabIndex = 2;
		list = (ListView) findViewById(R.id.list);
		list.setOnScrollListener(this);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View listItem,
					int position, long id) {
				if (listItem == loadMoreView
						&& !loadMoreView.getTag().equals("no")) {
					if (!isloading)
						loadItems(++pageIndex);
					return;
				}
				if (listItem == headerView)
					return;
				if (listItem.getTag().equals("no")) {
					Toast.makeText(HotelViewActivity.this, "该房间暂不支持线上预订!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent();
				intent.setClass(HotelViewActivity.this, BuyRoomActivity.class);
				RoomModel tm = (RoomModel) adapter.getItem(position - 1);
				if (tm != null) {
					intent.putExtra("checkIn",
							filter.getCheckInTimeStr("yyyy-MM-dd"));
					intent.putExtra("inDays", filter.getInDays());
					intent.putExtra("checkOut",
							filter.getCheckOutTimeStr("yyyy-MM-dd"));
					intent.putExtra("roomID", tm.getRoomID());
					intent.putExtra("price", tm.getPrice());
					intent.putExtra("roomName", tm.getRoomName());
					intent.putExtra("hotelName", hotelInfo.getHotelName());
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				}
			}
		});

		//if (Build.VERSION.SDK_INT <= 7) {
			list.setOnTouchListener(new FingerTracker(this));
		//}
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		imgManager = new ImageManager(new CGSize(dm.widthPixels,
				(int) Math.round((double) dm.widthPixels / (480.0 / 300.0))));
		Intent i = this.getIntent();
		filter = new HotelSearchModel();
		filter.setCheckInTime(i.getStringExtra("checkIn"));
		filter.setInDays(i.getIntExtra("inDays", 1));
		hotelInfo = (HotelViewModel) i.getSerializableExtra("hotelInfo");
		hotelID = hotelInfo.getHotelID();
		initializeAdapter();
		ShowInfo();
		// readTicketInfo();
		// setSightID(this.getIntent().getIntExtra("hotelID", 0));
	}

	private void initializeAdapter() {
		addHeaderView();
		adapter = new PaginationAdapter();
		addLoadingRow();
		list.setAdapter(adapter);
		pageIndex = 1;
		totalItemCount = 0;
	}

	public void addLoadingRow() {
		loadMoreView = getLayoutInflater().inflate(R.layout.table_loading_row,
				null);
		loadMoreView.setTag("yes");
		list.addFooterView(loadMoreView);
	}

	public void removeLoadingRow() {
		list.removeFooterView(loadMoreView);
	}

	public void changeLoadingRowText(String txt) {
		TextView lbl = (TextView) loadMoreView.findViewById(R.id.title);
		lbl.setText(txt);
	}

	private void addHeaderView() {
		headerView = getLayoutInflater().inflate(R.layout.hotel_info, null);
		title = (TextView) headerView.findViewById(R.id.title);
		rate_value = (TextView) headerView.findViewById(R.id.rate_value);
		rate_count = (TextView) headerView.findViewById(R.id.rate_desc);
		address = (TextView) headerView.findViewById(R.id.address);
		coverPic = (ImageView) headerView.findViewById(R.id.image);
		havDiningRoom = (CheckBox) headerView.findViewById(R.id.havDiningRoom);
		havBreakfast = (CheckBox) headerView.findViewById(R.id.havBreakfast);
		havHotWater = (CheckBox) headerView.findViewById(R.id.havHotWater);
		havMeetingRoom = (CheckBox) headerView
				.findViewById(R.id.havMeetingRoom);
		havPark = (CheckBox) headerView.findViewById(R.id.havPark);
		btn_order_notify = (Button) headerView
				.findViewById(R.id.btn_order_notify);
		btn_detail = (Button) headerView.findViewById(R.id.btn_detail);
		//checkIn = (TextView) headerView.findViewById(R.id.checkin);
		//checkOut = (TextView) headerView.findViewById(R.id.checkout);
		//DateFormat format = new SimpleDateFormat("入住  M月d日", Locale.CHINA);
		//DateFormat format1 = new SimpleDateFormat("离店  M月d日", Locale.CHINA);
		//checkIn.setText(format.format(filter.getCheckInTime()));
		//checkOut.setText(format1.format(filter.getCheckOutTime()));
		btn_order_notify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HotelViewActivity.this,
						DetailActivity.class);
				intent.putExtra("title", title.getText());
				intent.putExtra("url",
						String.format("%sdetail/1?type=10", APIConfig.ApiUrl));
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}

		});
		btn_detail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HotelViewActivity.this,
						DetailActivity.class);
				intent.putExtra("title", title.getText());
				intent.putExtra("url",
						String.format("%sdetail/1?type=0", APIConfig.ApiUrl));
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}

		});

		list.addHeaderView(headerView);
	}

	/*
	 * public void setSightID(Integer id) { hotelID=id; readTicketInfo(); }
	 */

	/*
	 * private void readTicketInfo() { title.setText(R.string.loading); HotelAPI
	 * api = new HotelAPI(); api.BeginGetInfo(hotelID, new
	 * APICallback<HotelViewModel>() {
	 * 
	 * @Override public void onCompleted(APIResultModel<HotelViewModel> result)
	 * { if (result.getSuc()) { hotelInfo = result.getItems(); if (hotelInfo !=
	 * null) ShowInfo(); // list. } else {
	 * Toast.makeText(HotelViewActivity.this, result.getMsg(),
	 * Toast.LENGTH_LONG).show(); } }
	 * 
	 * @Override public void onError(String msg) { AlertDialog.Builder builder =
	 * new Builder( HotelViewActivity.this); builder.setMessage(msg);
	 * builder.setTitle("载入数据失败"); builder.setPositiveButton("重试", new
	 * android.content.DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * readTicketInfo(); dialog.dismiss(); } }); builder.setNegativeButton("取消",
	 * new android.content.DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * dialog.dismiss(); } }); builder.create().show();
	 * //Toast.makeText(TicketViewActivity.this, msg, Toast.LENGTH_LONG).show();
	 * } }); }
	 */

	public void loadItems(Integer pageIndex) {
		if (isloading) return;
		isloading = true;
		changeLoadingRowText("载入中...");
		HotelAPI api = new HotelAPI();
		api.beginGetRoomList(hotelID, pageIndex,
				new APICallback<List<RoomModel>>() {

					@Override
					public void onCompleted(
							APIResultModel<List<RoomModel>> result) {
						isloading = false;
						if (hotelInfo.getCoverPic()!=null)
							loadPic(hotelInfo.getCoverPic());
						// loadPics();
						changeLoadingRowText("查看更多...");
						if (result.getSuc()) {
							totalItemCount = result.getTotalItemCount() + 2;
							List<RoomModel> items = result.getItems();
							adapter.addItem(items);
							// list.
						} else {
							Toast.makeText(HotelViewActivity.this, result.getMsg(),
									Toast.LENGTH_LONG).show();

						}
					}

					@Override
					public void onError(String msg) {
						isloading = false;
						AlertDialog.Builder builder = new Builder(
								HotelViewActivity.this);
						builder.setMessage(msg);
						builder.setTitle("载入数据失败");
						builder.setPositiveButton("重试", new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								loadItems(HotelViewActivity.this.pageIndex);
								dialog.dismiss();
							}
						});
						builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
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
						//Toast.makeText(HotelViewActivity.this, msg,Toast.LENGTH_LONG).show();
					}

				});
	}

	private void ShowInfo() {
		title.setText(hotelInfo.getHotelName());
		rate_value.setText(String.valueOf(hotelInfo.getRate()));
		rate_count
				.setText(String.format("来自 %d 人评价", hotelInfo.getRateCount()));
		address.setText(hotelInfo.getAddress());
		havDiningRoom.setChecked(hotelInfo.getHavDiningRoom());
		havMeetingRoom.setChecked(hotelInfo.getHavMeetingRoom());
		havBreakfast.setChecked(hotelInfo.getHavBreakfast());
		havHotWater.setChecked(hotelInfo.getHav24HotWater());
		havPark.setChecked(hotelInfo.getHavPark());
		//if (hotelInfo.getCoverPic()!=null)
			//loadPic(hotelInfo.getCoverPic());
		//else
			loadItems(pageIndex);
	}

	private void loadPic(String picPath) {

		Bitmap bmp = imgManager.GetImage(picPath, new CGSize(480, 300), 0,
				new ImageDownloadCallback() {

					@Override
					public void onCompleted(Bitmap bmp, int tag) {
						coverPic.setImageBitmap(bmp);
						//loadItems(pageIndex);
					}

					@Override
					public void onError(String msg, int tag) {
						// coverPic.setImageResource(R.drawable.list_nopic);
						//loadItems(pageIndex);
						// Toast.makeText(TicketViewActivity.this,
						// msg,Toast.LENGTH_LONG).show();
					}

				});
		if (bmp != null) {
			coverPic.setImageBitmap(bmp);
			//loadItems(pageIndex);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent intent = new Intent();
			intent.setClass(HotelViewActivity.this, HotelListActivity.class);
			// HotelViewActivity.this.setResult(RESULT_CANCELED, intent);
			HotelViewActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int itemsLastIndex = adapter.getCount(); // 数据集最后一项的索引
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
		visibleLastIndex = firstVisibleItem + visibleItemCount;
		// 如果所有的记录选项等于数据集的条数，则移除列表底部视图
		if (this.totalItemCount > 0 && totalItemCount == this.totalItemCount) {
			loadMoreView.setTag("no");
			this.changeLoadingRowText("没有更多了");
			// removeLoadingRow();
			// Toast.makeText(this, "数据全部加载完!", Toast.LENGTH_SHORT).show();
		}
	}

	class PaginationAdapter extends BaseAdapter {
		List<RoomModel> items;

		public PaginationAdapter() {
			this.items = new ArrayList<RoomModel>();
		}

		public PaginationAdapter(List<RoomModel> newsitems) {
			this.items = newsitems;
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

		// @SuppressLint("DefaultLocale")
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (position < items.size()) {
				RoomModel item = items.get(position);
				if (view == null || view.getId() == 10000
						|| view.findViewById(R.id.price) == null)
					view = getLayoutInflater().inflate(R.layout.room_list_item,
							null);
				TextView title = (TextView) view.findViewById(R.id.title);
				title.setText(item.getRoomName());
				TextView price = (TextView) view.findViewById(R.id.price);
				Button btn_buy = (Button) view.findViewById(R.id.btn_buy);
				if (item.getPrice() > 0) {
					view.setTag("yes");
					java.text.DecimalFormat df = new java.text.DecimalFormat(
							"#");
					price.setText(df.format(item.getPrice()));
					btn_buy.setVisibility(View.VISIBLE);
				} else {
					view.setTag("no");
					price.setText("--");
					btn_buy.setVisibility(View.GONE);
				}
				TextView detail = (TextView) view.findViewById(R.id.detail);
				detail.setText(new StringBuilder((item.getHavNetwork() ? "宽带/"
						: "")).append((item.getBedType() == null) ? "不详" : item
						.getBedType()));
				return view;
			}
			return null;
		}

		public void addItem(List<RoomModel> items) {
			this.items.addAll(items);
			this.notifyDataSetChanged();
		}

		public void addItem(RoomModel item) {
			items.add(item);
			this.notifyDataSetChanged();
		}
	}
}
