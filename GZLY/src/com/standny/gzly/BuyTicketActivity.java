package com.standny.gzly;

import java.net.URLEncoder;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.AlipayOrderModel;
import com.standny.gzly.models.NameAndIDCard;
import com.standny.gzly.models.TicketOrderInfo;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.DateTime;
import com.standny.gzly.repository.MyDatePicker;
import com.standny.gzly.repository.TicketAPI;
import com.standny.ui.itempicker.ItemPicker;
import com.standny.ui.itempicker.ItemPicker.OnChangedListener;

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
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BuyTicketActivity extends MasterActivity {
	static final String TAG = "BuyTicket";

	private TextView input_useTime;
	private TextView lbl_price;
	private Button btn_buy;
	private MyDatePicker picker;
	private TextView title;
	private TextView ticketTitle;
	private ItemPicker buy_count;
	private TableLayout table;
	private ImageButton btn_addbook;
	private EditText input_contact;
	private EditText input_contactTel;

	private boolean dailogShow;
	private TicketOrderInfo orderInfo;
	private double price;
	private int buy_num;
	private boolean isRunning;

	private boolean needPay;
	private AlipayOrderModel payContent;
	private ProgressDialog mProgress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_ticket);
		Intent i = this.getIntent();
		price = i.getDoubleExtra("price", 0);
		input_useTime = (TextView) findViewById(R.id.input_usetime);
		lbl_price = (TextView) findViewById(R.id.lbl_price);
		btn_buy = (Button) findViewById(R.id.btn_buy);
		title = (TextView) findViewById(R.id.title);
		ticketTitle = (TextView) findViewById(R.id.ticket_title);
		buy_count = (ItemPicker) findViewById(R.id.buy_count);
		table = (TableLayout) findViewById(R.id.table);
		btn_addbook = (ImageButton) findViewById(R.id.btn_addbook);
		input_contact = (EditText) findViewById(R.id.input_contact);
		input_contactTel = (EditText) findViewById(R.id.input_contact_tel);
		title.setText(i.getStringExtra("sightName"));
		ticketTitle.setText(i.getStringExtra("ticketName"));
		orderInfo = new TicketOrderInfo();
		orderInfo.setTicketID(i.getIntExtra("ticketID", 0));
		orderInfo.setBuyCount(1);
		orderInfo.setUseTime(DateTime.now().getDate());
		orderInfo.setPrice(price);
		input_useTime.setText(new DateTime(orderInfo.getUseTime()).toString("yyyy年 M月d日"));
		lbl_price.setText(new StringBuilder("合计:").append(String.valueOf(price)));
		buy_count.setRange(1, 10);
		buy_num = 1;
		input_contact.setText(user.getRealName());
		input_contactTel.setText(user.getTel());

		addBuyerItem(0);
		buy_count.setOnChangeListener(new OnChangedListener() {

			@Override
			public void onChanged(ItemPicker picker, int oldVal, int newVal) {
				orderInfo.setBuyCount(newVal);
				if (newVal > buy_num) {
					for (int i = buy_num; i < newVal; i++) {
						addBuyerItem(i);
						// View view =
						// getLayoutInflater().inflate(R.layout.user_info_item,null);
						// table.addView(view, 8+i);
					}
					buy_num = newVal;
				} else if (newVal < buy_num) {
					for (int i = newVal; i < buy_num; i++) {
						table.removeViewAt(buy_num-i+4);
					}
					buy_num = newVal;
				}
				lbl_price.setText(new StringBuilder("合计:").append(String
						.valueOf(price * orderInfo.getBuyCount())));
			}

		});

		input_useTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDateDialog();
			}

		});
		btn_addbook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!dailogShow) {
					dailogShow = true;
					Intent intent = new Intent(BuyTicketActivity.this,
							ContactsActivity.class);
					startActivityForResult(intent,
							MasterActivity.DIALOG_CONTACT);
				}
			}

		});

		btn_buy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRunning){
					Toast.makeText(BuyTicketActivity.this, "订单提交中,请稍候...!",
							Toast.LENGTH_LONG).show();
					return;
				}
				orderInfo.setContact(input_contact.getText().toString());
				orderInfo.setContactTel(input_contactTel.getText().toString());
				if (orderInfo.getContact() == null
						|| orderInfo.getContact().length() == 0) {
					Toast.makeText(BuyTicketActivity.this, "请输入联系人姓名!",
							Toast.LENGTH_LONG).show();
					input_contact.requestFocus();
					return;
				}
				if (orderInfo.getContactTel() == null
						|| orderInfo.getContactTel().length() == 0) {
					Toast.makeText(BuyTicketActivity.this, "请输入联系人电话!",
							Toast.LENGTH_LONG).show();
					input_contactTel.requestFocus();
					return;
				}

				orderInfo.clearBuyer();
				boolean findErr=false;
				for (int i = 4; i < table.getChildCount(); i++) {
					View view = table.getChildAt(i);
					EditText nameView = (EditText) view
							.findViewById(R.id.input_name);
					EditText idcardView = (EditText) view
							.findViewById(R.id.input_idcard);
					if (nameView == null || idcardView == null)
						break;
					NameAndIDCard item = new NameAndIDCard();
					item.Name = nameView.getText().toString();
					item.IDCard = idcardView.getText().toString();
					if (item.Name == null || item.Name.length() == 0) {
						findErr=true;
						Toast.makeText(BuyTicketActivity.this, "请输入使用人姓名!",
								Toast.LENGTH_LONG).show();
						nameView.requestFocus();
						return;
					}
					if (item.IDCard == null || item.IDCard.length() == 0) {
						findErr=true;
						Toast.makeText(BuyTicketActivity.this, "请输入使用人身份证号!",
								Toast.LENGTH_LONG).show();
						idcardView.requestFocus();
						return;
					}
					orderInfo.addBuyer(item);
				}
				if (!findErr){

				final Button btn=(Button)v;
				btn.setText("订单提交中...");
				isRunning=true;
				TicketAPI api=new TicketAPI();
				api.BeginSendOrder(user.getSessionKey(), orderInfo, new APICallback<AlipayOrderModel>(){

					@Override
					public void onCompleted(
							APIResultModel<AlipayOrderModel> result) {
						isRunning=false;
						btn.setText(R.string.go_pay);
						if (result.getSuc()){
							payContent=result.getItems();
							Log.i(TAG, payContent.content);
							goPay();
							Toast.makeText(BuyTicketActivity.this, "订单提交成功!",
									Toast.LENGTH_LONG).show();
						}
						else{
							Toast.makeText(BuyTicketActivity.this, result.getMsg(),
									Toast.LENGTH_LONG).show();
						}
						
					}

					@Override
					public void onError(String msg) {
						isRunning=false;
						btn.setText(R.string.go_pay);
						Toast.makeText(BuyTicketActivity.this, msg,
								Toast.LENGTH_LONG).show();
					}
					
				});
				}
				

			}

		});

		if (!isLogged) {
			btn_buy.setVisibility(View.INVISIBLE);
			Login();
		} else
			onLogged();
		isRunning=false;


		//
		// check to see if the MobileSecurePay is already installed.
		// 检测安全支付服务是否被安装
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
				needPay=false;
			}
		}
	};

	//
	// the handler use to receive the pay result.
	// 这里接收支付结果，支付宝手机端同步通知
	public Handler mHandler = new AlipayHandler<BuyTicketActivity>(BuyTicketActivity.this) {
		public void handleMessage(Message msg) {
			try {
				String ret = (String) msg.obj;

				Log.e(TAG, ret); // strRet范例：resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
				switch (msg.what) {
				case AlixId.RQF_PAY: {
					//
					closeProgress();

					BaseHelper.log(TAG, ret);

					// 处理交易结果
					try {
						// 获取交易状态码，具体状态代码请参看文档
						String tradeStatus = "resultStatus={";
						int imemoStart = ret.indexOf("resultStatus=");
						imemoStart += tradeStatus.length();
						int imemoEnd = ret.indexOf("};memo=");
						tradeStatus = ret.substring(imemoStart, imemoEnd);

						// 先验签通知
						ResultChecker resultChecker = new ResultChecker(ret);
						int retVal = resultChecker.checkSign();
						// 验签失败
						if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
							BaseHelper.showDialog(
									BuyTicketActivity.this,
									"提示",
									getResources().getString(
											R.string.check_sign_failed),
									android.R.drawable.ic_dialog_alert);
						} else {// 验签成功。验签成功后再判断交易状态码
							if (tradeStatus.equals("9000"))// 判断交易状态码，只有9000表示交易成功
							{
								BaseHelper.showDialog(BuyTicketActivity.this, "提示",
										"支付成功。交易状态码：" + tradeStatus,
										R.drawable.infoicon);
								Intent intent = new Intent();
								intent.setClass(BuyTicketActivity.this, MyTicketActivity.class);
								startActivity(intent);
								overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
								finish();
							}
							else
								BaseHelper.showDialog(BuyTicketActivity.this, "提示",
										"支付失败。交易状态码:" + tradeStatus,
										R.drawable.infoicon);
						}

					} catch (Exception e) {
						e.printStackTrace();
						BaseHelper.showDialog(BuyTicketActivity.this, "提示", ret,
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
	// 关闭进度框
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
			mProgress.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


	//
	//
	/**
	 * the OnCancelListener for lephone platform. lephone系统使用到的取消dialog监听
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
	
	private void goPay(){
		//
		// check to see if the MobileSecurePay is already installed.
		// 检测安全支付服务是否安装
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(this);
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist) {
			needPay=true;
			return;
		}

		// check some info.
		// 检测配置信息
		if (!checkInfo()) {
			BaseHelper
					.showDialog(
							BuyTicketActivity.this,
							"提示",
							"缺少partner或者seller，请在src/com/alipay/android/appDemo4/PartnerConfig.java中增加。",
							R.drawable.infoicon);
			return;
		}

		// start pay for this order.
		// 根据订单信息开始进行支付
		try {
			// prepare the order info.
			// 准备订单信息
			String orderInfo = payContent.content;
			String strsign = payContent.sign;
			Log.v("sign:", strsign);
			// 对签名进行编码
			strsign = URLEncoder.encode(strsign, "UTF-8");
			// 组装好参数
			String info = new StringBuilder(orderInfo).append("&sign=\"").append(strsign).append("\"&sign_type=\"RSA\"").toString();
			Log.v("orderInfo:", info);
			// start the pay.
			// 调用pay方法进行支付
			MobileSecurePayer msp = new MobileSecurePayer();
			boolean bRet = msp.pay(info, mHandler, AlixId.RQF_PAY, this);

			if (bRet) {
				// show the progress bar to indicate that we have started
				// paying.
				// 显示“正在支付”进度条
				closeProgress();
				mProgress = BaseHelper.showProgress(this, null, "正在支付", false,
						true);
			} else
				;
		} catch (Exception ex) {
			Toast.makeText(BuyTicketActivity.this, R.string.remote_call_failed,
					Toast.LENGTH_SHORT).show();
		}
	}


	/**
	 * check some info.the partner,seller etc. 检测配置信息
	 * partnerid商户id，seller收款帐号不能为空
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
	protected void onLogged() {
		orderInfo.setContact(user.getRealName());
		orderInfo.setContactTel(user.getTel());

		// NameAndIDCard item = new NameAndIDCard();
		// item.Name = user.getRealName();
		// item.IDCard = user.getIDcard();
		View view = table.getChildAt(4);
		EditText nameView = (EditText) view.findViewById(R.id.input_name);
		if (nameView != null)
			nameView.setText("abc");
		// nameView.setText(user.getRealName());
		EditText idcardView = (EditText) view.findViewById(R.id.input_idcard);
		if (idcardView != null)
			idcardView.setText(user.getIDcard());
		btn_buy.setVisibility(View.VISIBLE);
	}

	private void addBuyerItem(int index) {
		View view = getLayoutInflater().inflate(R.layout.user_info_item, null);
		table.addView(view, 4 + index);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		dailogShow = false;
		if (requestCode == MasterActivity.DIALOG_CONTACT) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					orderInfo.setContact(bundle.getString("name"));
					orderInfo.setContactTel(bundle.getString("tel"));
					input_contactTel.setText(orderInfo.getContactTel());
					input_contact.setText(orderInfo.getContact());
				}
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showDateDialog() {
		if (picker == null) {
			picker = new MyDatePicker(new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					DateTime time=new DateTime(year, monthOfYear + 1,
							dayOfMonth);
					orderInfo.setUseTime(time.getDate());
					input_useTime.setText(time.toString("yyyy年 M月d日"));
				}
			}, new DateTime(orderInfo.getUseTime()));
		}
		if (!picker.isShowing()) {
			picker.setCurrentDate(new DateTime(orderInfo.getUseTime()));
			picker.setMaxDate(DateTime.now().AddYear(1));
			picker.setMinDate(DateTime.now());
			picker.show(this);
		}
	}

}
