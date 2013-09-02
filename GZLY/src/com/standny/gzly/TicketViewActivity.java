package com.standny.gzly;

import java.util.ArrayList;
import java.util.List;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.SightInfoModel;
import com.standny.gzly.models.TicketModel;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.APIConfig;
import com.standny.gzly.repository.CGSize;
import com.standny.gzly.repository.ImageDownloadCallback;
import com.standny.gzly.repository.ImageManager;
import com.standny.gzly.repository.TicketAPI;
import com.standny.ui.FingerTracker;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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

public class TicketViewActivity extends MasterActivity implements
		OnScrollListener {

	private TextView title;
	private TextView rate_value;
	private TextView rate_count;
	private TextView address;

	private Integer sightID;
	private SightInfoModel item;
	private View headerView;
	private ListView list;
	private ViewPager picView;
	private PicViewItemAdapter picViewAdapter;
	private PaginationAdapter adapter;
	private View loadMoreView;

	private ImageManager imgManager;

	private int visibleLastIndex = 0; // 最后的可视项索引
	private int pageIndex;
	private int totalItemCount;
	private boolean isloading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_view);
		tabIndex = 1;
		list = (ListView) findViewById(R.id.ticket_list);
		list.setOnScrollListener(this);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View listItem,
					int position, long id) {
				if (listItem == loadMoreView&&!loadMoreView.getTag().equals("no")){
					if(!isloading)
						loadItems(++pageIndex);
					return;
				}
				if (listItem == headerView)
					return;
				Intent intent = new Intent();
				if (listItem.getTag() == "order_tip") {
					intent.setClass(TicketViewActivity.this,
							DetailActivity.class);
					intent.putExtra("title", title.getText());
					intent.putExtra("url", String.format("%sdetail/0?type=10",APIConfig.ApiUrl));
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);

				} else if (listItem.getTag() == "item_detail") {
					intent.setClass(TicketViewActivity.this,
							DetailActivity.class);
					intent.putExtra("title", title.getText());
					intent.putExtra("url", String.format("%sdetail/%d?type=0",APIConfig.ApiUrl,sightID));
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				} else {
					intent.setClass(TicketViewActivity.this,
							BuyTicketActivity.class);
					TicketModel tm=(TicketModel)adapter.getItem(position-1);
					if (tm!=null){
					//Integer ticketID = Integer.valueOf(listItem.getTag(0)
							//.hashCode());
					//double price=Double.parseDouble((String)listItem.getTag(1));
						intent.putExtra("ticketName", tm.getTicketName());
						intent.putExtra("sightName", item.getSightName());
					intent.putExtra("ticketID", tm.getTicketID());
					intent.putExtra("price", tm.getPrice2());
					//startActivityForResult(intent, 1);
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
					//TicketViewActivity.this.finish();
					}
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
		initializeAdapter();
		setSightID(this.getIntent().getIntExtra("sightID", 0));
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int itemsLastIndex = adapter.getCount(); // 数据集最后一项的索引
		if (!isloading &&scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& visibleLastIndex == itemsLastIndex
				&& itemsLastIndex < this.totalItemCount) {
			pageIndex++;
			loadItems(pageIndex);
		}
	}

	private void initializeAdapter() {
		addHeaderView();
		picViewAdapter = new PicViewItemAdapter(this, R.drawable.default_pic);
		picView.setAdapter(picViewAdapter);
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
	
	private void loadPics(){
		if (item.getPics().size() > 0) {
			for (int i = 0; i < item.getPics().size(); i++) {
				// final int index = i;
				Bitmap bmp = imgManager.GetImage(item.getPics().get(i),
						new CGSize(480, 300), i, new ImageDownloadCallback() {

							@Override
							public void onCompleted(Bitmap bmp, int tag) {
								ImageView iv = (ImageView) TicketViewActivity.this
										.findViewById(tag + 10000);
								iv.setImageBitmap(bmp);
								picViewAdapter.update(bmp, tag);
							}

							@Override
							public void onError(String msg, int tag) {
								/*ImageView iv = (ImageView) TicketViewActivity.this
										.findViewById(tag + 10000);
								if (iv != null) {
									//iv.setImageResource(R.drawable.list_nopic);
								}*/
								//Toast.makeText(TicketViewActivity.this, msg,Toast.LENGTH_LONG).show();
							}

						});
				if (bmp == null) {
					// Resources res = this.getBaseContext().getResources();
					// Drawable drawable =
					// res.getDrawable(R.drawable.list_nopic);
					// Bitmap bitmap = utility.drawable2Bitmap(drawable);
					picViewAdapter.add();
				} else
					picViewAdapter.add(bmp);
			}
		}/* else {
			picViewAdapter.add();
		}*/
	}

	public void loadItems(Integer pageIndex) {
		if (isloading) return;
		isloading = true;
		changeLoadingRowText("载入中...");
		TicketAPI api = new TicketAPI();
		api.BeginGetTicketList(sightID, pageIndex,
				new APICallback<List<TicketModel>>() {

					@Override
					public void onCompleted(
							APIResultModel<List<TicketModel>> result) {
						isloading = false;
						loadPics();
						changeLoadingRowText("查看更多...");
						if (result.getSuc()) {
							totalItemCount = result.getTotalItemCount() + 2;
							List<TicketModel> items = result.getItems();
							adapter.addItem(items);
							// list.
						} else {
							Toast.makeText(TicketViewActivity.this, result.getMsg(),
									Toast.LENGTH_LONG).show();

						}
					}

					@Override
					public void onError(String msg) {
						isloading = false;
						AlertDialog.Builder builder = new Builder(
								TicketViewActivity.this);
						builder.setMessage(msg);
						builder.setTitle("载入数据失败");
						builder.setPositiveButton("重试", new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								loadItems(TicketViewActivity.this.pageIndex);
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
						//Toast.makeText(TicketViewActivity.this, msg,Toast.LENGTH_LONG).show();
					}

				});
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		visibleLastIndex = firstVisibleItem + visibleItemCount - 2;
		// 如果所有的记录选项等于数据集的条数，则移除列表底部视图
		if (this.totalItemCount > 0
				&& totalItemCount == this.totalItemCount + 2) {
			//removeLoadingRow();
			loadMoreView.setTag("no");
			this.changeLoadingRowText("没有更多了");
			//Toast.makeText(this, "数据全部加载完!", Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * private void addFooterView(){ footerView =
	 * getLayoutInflater().inflate(R.layout., null);
	 * title=(TextView)headerView.findViewById(R.id.title);
	 * rate_value=(TextView)headerView.findViewById(R.id.rate_value);
	 * rate_count=(TextView)headerView.findViewById(R.id.rate_desc);
	 * address=(TextView)headerView.findViewById(R.id.address);
	 * picView=(ViewPager)headerView.findViewById(R.id.picview);
	 * list.addHeaderView(headerView); }
	 */

	private void addHeaderView() {
		headerView = getLayoutInflater().inflate(R.layout.ticket_info, null);
		title = (TextView) headerView.findViewById(R.id.title);
		rate_value = (TextView) headerView.findViewById(R.id.rate_value);
		rate_count = (TextView) headerView.findViewById(R.id.rate_desc);
		address = (TextView) headerView.findViewById(R.id.address);
		picView = (ViewPager) headerView.findViewById(R.id.picview);
		list.addHeaderView(headerView);
	}

	public void setSightID(Integer id) {
		this.sightID = id;
		readTicketInfo();
	}

	private void ShowInfo() {
		title.setText(item.getSightName());
		rate_value.setText(String.valueOf(item.getRate()));
		rate_count.setText(String.format("来自 %d 人评价", item.getRateCount()));
		address.setText(item.getAddress());
		loadItems(pageIndex);
		// picViewAdapter.change(paths)
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent intent = new Intent();
			intent.setClass(TicketViewActivity.this, TicketActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void readTicketInfo() {
		title.setText(R.string.loading);
		TicketAPI api = new TicketAPI();
		api.BeginGetInfo(sightID, new APICallback<SightInfoModel>() {

			@Override
			public void onCompleted(APIResultModel<SightInfoModel> result) {
				if (result.getSuc()) {
					item = result.getItems();
					if (item != null)
						ShowInfo();
					// list.
				} else {
					Toast.makeText(TicketViewActivity.this, result.getMsg(), Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onError(String msg) {
				AlertDialog.Builder builder = new Builder(
						TicketViewActivity.this);
				builder.setMessage(msg);
				builder.setTitle("载入数据失败");
				builder.setPositiveButton("重试", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						readTicketInfo();
						dialog.dismiss();
					}
				});
				builder.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				//Toast.makeText(TicketViewActivity.this, msg, Toast.LENGTH_LONG).show();
			}
		});
	}

	class PaginationAdapter extends BaseAdapter {
		List<TicketModel> items;

		public PaginationAdapter() {
			this.items = new ArrayList<TicketModel>();
		}

		public PaginationAdapter(List<TicketModel> newsitems) {
			this.items = newsitems;
		}

		@Override
		public int getCount() {
			return items.size() + 2;
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
				TicketModel item = items.get(position);
				if (view == null || view.getId() == 10000||view.findViewById(R.id.price1)==null)
					view = getLayoutInflater().inflate(R.layout.ticket_item,
							null);
				TextView title = (TextView) view.findViewById(R.id.title);
				title.setText(item.getTicketName());
				TextView price1 = (TextView) view.findViewById(R.id.price1);
				java.text.DecimalFormat df = new java.text.DecimalFormat(
						"原价:￥#.00");
				price1.setText(df.format(item.getPrice1()));
				price1.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				df = new java.text.DecimalFormat("优惠价:￥#.00");
				TextView price2 = (TextView) view.findViewById(R.id.price2);
				price2.setText(df.format(item.getPrice2()));
				//view.setTag(item.getTicketID());
				//view.setTag(0, item.getTicketID());
				//view.setTag(1, item.getPrice2());
				return view;
			} else {
				if (view == null||view.findViewById(R.id.image)==null)
					view = getLayoutInflater().inflate(
							R.layout.table_detail_row, null);
				view.setId(10000);
				TextView title = (TextView) view.findViewById(R.id.title);
				ImageView image = (ImageView) view.findViewById(R.id.image);
				if (position == items.size()) {
					title.setText("购票须知");
					image.setImageResource(R.drawable.icon_tip);
					view.setTag("order_tip");
				} else {
					title.setText("查看图文详情");
					image.setImageResource(R.drawable.icon_detail);
					view.setTag("item_detail");
				}
				return view;
			}
		}

		public void addItem(List<TicketModel> items) {
			this.items.addAll(items);
			this.notifyDataSetChanged();
		}

		public void addItem(TicketModel item) {
			items.add(item);
			this.notifyDataSetChanged();
		}
	}
}
