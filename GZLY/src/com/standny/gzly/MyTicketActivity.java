package com.standny.gzly;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.AlipayOrderModel;
import com.standny.gzly.models.TicketOrderInfo;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.DateTime;
import com.standny.gzly.repository.TicketAPI;
import com.standny.ui.FingerTracker;

import alipay.AlixId;
import alipay.BaseHelper;
import alipay.MobileSecurePayHelper;
import alipay.MobileSecurePayer;
import alipay.PartnerConfig;
import alipay.ResultChecker;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class MyTicketActivity extends MasterActivity implements OnScrollListener {
	static final String TAG = "MyTicket";

	private ListView list;

	private boolean isRunning;
	private int pageIndex;
	private int visibleLastIndex = 0; // ���Ŀ���������
	private int totalItemCount;
	private PaginationAdapter adapter;
	private View loadMoreView;

	private int payItemIndex;
	private boolean needPay;
	private AlipayOrderModel payContent;
	private ProgressDialog mProgress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_ticket);
		tabIndex = 4;
		isRunning = false;
		pageIndex = 0;
		tabIndex = 4;
		list = (ListView) findViewById(R.id.list);
		list.setOnScrollListener(this);
		list.setOnTouchListener(new FingerTracker(this));
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View listItem, int position,
					long id) {
				if (listItem == loadMoreView) {
					if (!isRunning&&!loadMoreView.getTag().equals("no"))
						readItems(++pageIndex);
					return;
				}
			}
		});
		initializeAdapter();
		//
		// check to see if the MobileSecurePay is already installed.
		// ��ⰲȫ֧�������Ƿ񱻰�װ
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(this);
		mspHelper.detectMobile_sp();

		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		filter.addDataScheme("package");
		registerReceiver(mPackageInstallationListener, filter);
	}

	private BroadcastReceiver mPackageInstallationListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String packageName = intent.getDataString();
			if (!TextUtils
					.equals(packageName, "package:com.alipay.android.app")) {
				return;
			}

			if (needPay) {
				goPay();
				needPay = false;
			}
		}
	};

	//
	// the handler use to receive the pay result.
	// �������֧�������֧�����ֻ���ͬ��֪ͨ
	public Handler mHandler = new AlipayHandler<MyTicketActivity>(
			MyTicketActivity.this) {
		public void handleMessage(Message msg) {
			try {
				String ret = (String) msg.obj;

				Log.e(TAG, ret); // strRet������resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010�¿�NIKE �Ϳ�902��������Ь �Ϳ���ŮЬ 386201 �׺�"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
				switch (msg.what) {
				case AlixId.RQF_PAY: {
					//
					closeProgress();

					BaseHelper.log(TAG, ret);

					// �����׽��
					try {
						// ��ȡ����״̬�룬����״̬������ο��ĵ�
						String tradeStatus = "resultStatus={";
						int imemoStart = ret.indexOf("resultStatus=");
						imemoStart += tradeStatus.length();
						int imemoEnd = ret.indexOf("};memo=");
						tradeStatus = ret.substring(imemoStart, imemoEnd);

						// ����ǩ֪ͨ
						ResultChecker resultChecker = new ResultChecker(ret);
						int retVal = resultChecker.checkSign();
						// ��ǩʧ��
						if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
							BaseHelper.showDialog(
									MyTicketActivity.this,
									"��ʾ",
									getResources().getString(
											R.string.check_sign_failed),
									android.R.drawable.ic_dialog_alert);
						} else {// ��ǩ�ɹ�����ǩ�ɹ������жϽ���״̬��
							if (tradeStatus.equals("9000"))// �жϽ���״̬�룬ֻ��9000��ʾ���׳ɹ�
							{
								BaseHelper.showDialog(MyTicketActivity.this,
										"��ʾ", "֧���ɹ�������״̬�룺" + tradeStatus,
										R.drawable.infoicon);
								View view = list.getChildAt(payItemIndex);
								if (view != null) {
									Button btn_pay = (Button) view
											.findViewById(R.id.btn_pay);
									Button btn_refund = (Button) view
											.findViewById(R.id.btn_refund);
									btn_pay.setVisibility(View.GONE);
									btn_refund.setVisibility(View.VISIBLE);
								}
								// finish();
							} else
								BaseHelper.showDialog(MyTicketActivity.this,
										"��ʾ", "֧��ʧ�ܡ�����״̬��:" + tradeStatus,
										R.drawable.infoicon);
						}

					} catch (Exception e) {
						e.printStackTrace();
						BaseHelper.showDialog(MyTicketActivity.this, "��ʾ", ret,
								R.drawable.infoicon);
					}
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	//
	// close the progress bar
	// �رս��ȿ�
	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	@Override
	public void onDestroy() {
		super.onDestroy();

		unregisterReceiver(mPackageInstallationListener);

		Log.v(TAG, "onDestroy");

		try {
			if (mProgress!=null)
				mProgress.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	//
	/**
	 * the OnCancelListener for lephone platform. lephoneϵͳʹ�õ���ȡ��dialog����
	 */
	public static class AlixOnCancelListener implements
			DialogInterface.OnCancelListener {
		Activity mcontext;

		public AlixOnCancelListener(Activity context) {
			mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}

	private void goPay() {
		//
		// check to see if the MobileSecurePay is already installed.
		// ��ⰲȫ֧�������Ƿ�װ
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(this);
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist) {
			needPay = true;
			return;
		}

		// check some info.
		// ���������Ϣ
		if (!checkInfo()) {
			BaseHelper
					.showDialog(
							MyTicketActivity.this,
							"��ʾ",
							"ȱ��partner����seller������src/com/alipay/android/appDemo4/PartnerConfig.java�����ӡ�",
							R.drawable.infoicon);
			return;
		}

		// start pay for this order.
		// ���ݶ�����Ϣ��ʼ����֧��
		try {
			// prepare the order info.
			// ׼��������Ϣ
			String orderInfo = payContent.content;
			String strsign = payContent.sign;
			Log.v("sign:", strsign);
			// ��ǩ�����б���
			strsign = URLEncoder.encode(strsign, "UTF-8");
			// ��װ�ò���
			String info = new StringBuilder(orderInfo).append("&sign=\"")
					.append(strsign).append("\"&sign_type=\"RSA\"").toString();
			Log.v("orderInfo:", info);
			// start the pay.
			// ����pay��������֧��
			MobileSecurePayer msp = new MobileSecurePayer();
			boolean bRet = msp.pay(info, mHandler, AlixId.RQF_PAY, this);

			if (bRet) {
				// show the progress bar to indicate that we have started
				// paying.
				// ��ʾ������֧����������
				closeProgress();
				mProgress = BaseHelper.showProgress(this, null, "����֧��", false,
						true);
			} else
				;
		} catch (Exception ex) {
			Toast.makeText(MyTicketActivity.this, R.string.remote_call_failed,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * check some info.the partner,seller etc. ���������Ϣ
	 * partnerid�̻�id��seller�տ��ʺŲ���Ϊ��
	 * 
	 * @return
	 */
	private boolean checkInfo() {
		String partner = PartnerConfig.PARTNER;
		String seller = PartnerConfig.SELLER;
		if (partner == null || partner.length() <= 0 || seller == null
				|| seller.length() <= 0)
			return false;

		return true;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int itemsLastIndex = adapter.getCount(); // ���ݼ����һ�������
		// int lastIndex = itemsLastIndex + 1;
		if (!isRunning && scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& visibleLastIndex == itemsLastIndex
				&& itemsLastIndex < this.totalItemCount) {
			pageIndex++;
			readItems(pageIndex);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// this.visibleItemCount = visibleItemCount;
		visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
		// ������еļ�¼ѡ��������ݼ������������Ƴ��б�ײ���ͼ
		if (this.totalItemCount > 0
				&& totalItemCount == this.totalItemCount + 1) {
			// removeLoadingRow();
			loadMoreView.setTag("no");
			this.changeLoadingRowText("û�и�����");
			// Toast.makeText(this, "����ȫ��������!", Toast.LENGTH_LONG).show();
		}
	}

	private void initializeAdapter() {
		adapter = new PaginationAdapter();
		addLoadingRow();
		list.setAdapter(adapter);
		pageIndex = 1;
		totalItemCount = 0;
		readItems(pageIndex);
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

	private void readItems(int pageIndex) {
		if (isRunning) return;
		if (isLogged) {
			TicketAPI api = new TicketAPI();
			api.beginGetMyList(user.getSessionKey(), pageIndex,
					new APICallback<List<TicketOrderInfo>>() {

						@Override
						public void onCompleted(
								APIResultModel<List<TicketOrderInfo>> result) {
							isRunning = false;
							changeLoadingRowText("�鿴����...");
							if (result.getSuc()) {
								totalItemCount = result.getTotalItemCount();
								if (totalItemCount == 0) {
									changeLoadingRowText("�㻹û�й������Ʊ.");
								} else {
									List<TicketOrderInfo> items = result
											.getItems();
									adapter.addItem(items);
								}
								// list.
							} else {
								Toast.makeText(MyTicketActivity.this, result.getMsg(),
										Toast.LENGTH_LONG).show();

							}
						}

						@Override
						public void onError(String msg) {
							isRunning = false;
							AlertDialog.Builder builder = new Builder(
									MyTicketActivity.this);
							builder.setMessage(msg);
							builder.setTitle("��������ʧ��");
							builder.setPositiveButton("����",
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											readItems(MyTicketActivity.this.pageIndex);
											dialog.dismiss();
										}
									});
							builder.setNegativeButton("ȡ��",
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									});
							try {
								builder.create().show();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					});
		} else {
			Login();
		}
	}

	class PaginationAdapter extends BaseAdapter {
		List<TicketOrderInfo> items;

		public PaginationAdapter() {
			this.items = new ArrayList<TicketOrderInfo>();
		}

		public PaginationAdapter(List<TicketOrderInfo> items) {
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
		public View getView(final int position, View view, ViewGroup parent) {
			TicketOrderInfo item = items.get(position);
			if (view == null)
				view = getLayoutInflater().inflate(R.layout.myhotel_list_item,
						null);
			TextView title = (TextView) view.findViewById(R.id.title);
			title.setText(new StringBuilder(item.getSightName()).append(" - ")
					.append(item.getTicketName()));
			TextView price = (TextView) view.findViewById(R.id.price);
			java.text.DecimalFormat df = new java.text.DecimalFormat("��#");
			price.setText(df.format(item.getPrice()));
			TextView buyCount = (TextView) view.findViewById(R.id.buy_count);
			buyCount.setText(new StringBuilder("x").append(item.getBuyCount()));
			TextView inDay = (TextView) view.findViewById(R.id.in_day);
			inDay.setText(new DateTime(item.getUseTime()).toString("yyyy-MM-dd"));
			TextView in_mans = (TextView) view.findViewById(R.id.in_mans);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < item.getBuyer().size(); i++) {
				if (i > 0)
					sb.append("\r\n");
				sb.append(item.getBuyer().get(i).Name);
				sb.append(" ");
				sb.append(item.getBuyer().get(i).IDCard);
			}
			in_mans.setText(sb.toString());
			TextView totalMoney = (TextView) view
					.findViewById(R.id.total_money);
			java.text.DecimalFormat df1 = new java.text.DecimalFormat("#.##");
			totalMoney
					.setText(df1.format(item.getPrice() * item.getBuyCount()));
			TextView orderStatus = (TextView) view
					.findViewById(R.id.order_status);
			orderStatus.setText(item.getOrderStatusName());

			Button btn_pay = (Button) view.findViewById(R.id.btn_pay);
			Button btn_delete = (Button) view.findViewById(R.id.btn_delete);
			Button btn_refund = (Button) view.findViewById(R.id.btn_refund);
			Button btn_unRefund = (Button) view.findViewById(R.id.btn_unrefund);
			btn_pay.setVisibility(View.GONE);
			btn_delete.setVisibility(View.GONE);
			btn_refund.setVisibility(View.GONE);
			btn_unRefund.setVisibility(View.GONE);
			btn_pay.setTag(item.getOrderID());
			btn_delete.setTag(item.getOrderID());
			btn_refund.setTag(item.getOrderID());
			btn_unRefund.setTag(item.getOrderID());
			btn_pay.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					payItemIndex=position;
					TicketAPI api = new TicketAPI();
					api.beginGetPaySign(user.getSessionKey(), v.getTag()
							.hashCode(), new APICallback<AlipayOrderModel>() {

						@Override
						public void onCompleted(
								APIResultModel<AlipayOrderModel> result) {
							if (result.getSuc()) {
								payContent = result.getItems();
								goPay();
							} else {
								Toast.makeText(MyTicketActivity.this,
										result.getMsg(), Toast.LENGTH_LONG)
										.show();
							}
						}

						@Override
						public void onError(String msg) {
							Toast.makeText(MyTicketActivity.this, msg,
									Toast.LENGTH_LONG).show();
						}

					});

				}

			});
			btn_delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					payItemIndex=position;
					TicketAPI api = new TicketAPI();
					api.beginDeleteMyOrder(user.getSessionKey(), v.getTag()
							.hashCode(), new APICallback<Object>() {

								@Override
								public void onCompleted(
										APIResultModel<Object> result) {
									if (result.getSuc()){
										adapter.items.remove(payItemIndex);
										adapter.notifyDataSetChanged();
										Toast.makeText(MyTicketActivity.this,
												"�����ɹ�", Toast.LENGTH_SHORT)
												.show();
										//list.removeViewAt(payItemIndex);
									}
									else{
										Toast.makeText(MyTicketActivity.this,
												result.getMsg(), Toast.LENGTH_LONG)
												.show();
									}
									
								}

								@Override
								public void onError(String msg) {
									Toast.makeText(MyTicketActivity.this, msg,
											Toast.LENGTH_LONG).show();
								}
					});
				}

			});
			btn_refund.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					payItemIndex=position;
					TicketAPI api = new TicketAPI();
					api.beginDeleteMyOrder(user.getSessionKey(), v.getTag()
							.hashCode(), new APICallback<Object>() {

								@Override
								public void onCompleted(
										APIResultModel<Object> result) {
									if (result.getSuc()){
										View view=list.getChildAt(payItemIndex);
										if(view!=null){
										Button btn_refund = (Button) view.findViewById(R.id.btn_refund);
										Button btn_unRefund = (Button) view.findViewById(R.id.btn_unrefund);
										btn_refund.setVisibility(View.GONE);
										btn_unRefund.setVisibility(View.VISIBLE);
										Toast.makeText(MyTicketActivity.this,
												"�����ɹ�", Toast.LENGTH_SHORT)
												.show();
										}
									}
									else{
										Toast.makeText(MyTicketActivity.this,
												result.getMsg(), Toast.LENGTH_LONG)
												.show();
									}
									
								}

								@Override
								public void onError(String msg) {
									Toast.makeText(MyTicketActivity.this, msg,
											Toast.LENGTH_LONG).show();
								}
					});
				}

			});
			btn_unRefund.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					payItemIndex=position;
					TicketAPI api = new TicketAPI();
					api.beginDeleteMyOrder(user.getSessionKey(), v.getTag()
							.hashCode(), new APICallback<Object>() {

								@Override
								public void onCompleted(
										APIResultModel<Object> result) {
									if (result.getSuc()){
										View view=list.getChildAt(payItemIndex);
										if(view!=null){
										Button btn_refund = (Button) view.findViewById(R.id.btn_refund);
										Button btn_unRefund = (Button) view.findViewById(R.id.btn_unrefund);
										btn_unRefund.setVisibility(View.GONE);
										btn_refund.setVisibility(View.VISIBLE);
										Toast.makeText(MyTicketActivity.this,
												"�����ɹ�", Toast.LENGTH_SHORT)
												.show();
										}
									}
									else{
										Toast.makeText(MyTicketActivity.this,
												result.getMsg(), Toast.LENGTH_LONG)
												.show();
									}
									
								}

								@Override
								public void onError(String msg) {
									Toast.makeText(MyTicketActivity.this, msg,
											Toast.LENGTH_LONG).show();
								}
					});
				}

			});
			switch (item.getOrderStatus()) {
			case 0:
				btn_delete.setVisibility(View.VISIBLE);
				btn_pay.setVisibility(View.VISIBLE);
				break;
			case 2:
			case 4:
				btn_delete.setVisibility(View.VISIBLE);
				break;
			case 1:
				btn_refund.setVisibility(View.VISIBLE);
				break;
			case 3:
				btn_unRefund.setVisibility(View.VISIBLE);
				break;
			}

			return view;
		}

		public void addItem(List<TicketOrderInfo> items) {
			this.items.addAll(items);
			this.notifyDataSetChanged();
		}

		public void addItem(TicketOrderInfo item) {
			items.add(item);
			this.notifyDataSetChanged();
		}
	}

	@Override
	protected void onLogged() {
		readItems(pageIndex);
	}

	private void cancel() {
		Intent intent = new Intent();
		intent.setClass(MyTicketActivity.this, UserCenterActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ����WebView��ת����
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			cancel();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
