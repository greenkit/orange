package com.standny.gzly;

import java.net.URLEncoder;

import com.standny.gzly.models.APIResultModel;
import com.standny.gzly.models.AlipayOrderModel;
import com.standny.gzly.models.HotelSearchModel;
import com.standny.gzly.models.NameAndIDCard;
import com.standny.gzly.models.RoomOrderInfo;
import com.standny.gzly.repository.APICallback;
import com.standny.gzly.repository.DateTime;
import com.standny.gzly.repository.HotelAPI;
import com.standny.gzly.repository.MyDatePicker;
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

public class BuyRoomActivity extends MasterActivity {
	static final String TAG = "BuyRoom";

	private HotelSearchModel filter;
	private TextView input_checkInTime;
	private TextView input_inDays;
	private TextView lbl_checkOutTime;
	private TextView lbl_price;
	private Button btn_buy;
	private MyDatePicker picker;
	private TextView title;
	private TextView roomTitle;
	private ItemPicker room_count;
	private ItemPicker people_count;
	private TableLayout table;
	private TextView input_comeTime;
	private ImageButton btn_addbook;
	private EditText input_contact;
	private EditText input_contactTel;

	private boolean dailogShow;
	private RoomOrderInfo orderInfo;
	private double price;
	private int people_num;
	private int startIndex;
	private int endIndex;
	private boolean isRunning;

	private boolean needPay;
	private AlipayOrderModel payContent;
	private ProgressDialog mProgress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_room);
		Intent i = this.getIntent();
		filter = new HotelSearchModel();
		filter.setCheckInTime(i.getStringExtra("checkIn"));
		filter.setInDays(i.getIntExtra("inDays", 1));
		price = i.getDoubleExtra("price", 0);
		input_checkInTime = (TextView) findViewById(R.id.input_checkin);
		input_inDays = (TextView) findViewById(R.id.input_indays);
		lbl_checkOutTime = (TextView) findViewById(R.id.lbl_checkout);
		lbl_price = (TextView) findViewById(R.id.lbl_price);
		btn_buy = (Button) findViewById(R.id.btn_buy);
		title = (TextView) findViewById(R.id.title);
		roomTitle = (TextView) findViewById(R.id.room_title);
		room_count = (ItemPicker) findViewById(R.id.room_count);
		people_count = (ItemPicker) findViewById(R.id.people_count);
		table = (TableLayout) findViewById(R.id.table);
		input_comeTime = (TextView) findViewById(R.id.input_come_time);
		btn_addbook = (ImageButton) findViewById(R.id.btn_addbook);
		input_contact = (EditText) findViewById(R.id.input_contact);
		input_contactTel = (EditText) findViewById(R.id.input_contact_tel);
		title.setText(i.getStringExtra("hotelName"));
		roomTitle.setText(i.getStringExtra("roomName"));
		orderInfo = new RoomOrderInfo();
		orderInfo.setRoomID(i.getIntExtra("roomID", 0));
		orderInfo.setBuyCount(1);
		orderInfo.setCheckInTime(new DateTime(filter.getCheckInTime()));
		orderInfo.setCheckOutTime(new DateTime(filter.getCheckOutTime()));
		orderInfo.setPrice(price);
		input_checkInTime.setText(filter.getCheckInTimeStr("yyyy�� M��d��"));
		lbl_checkOutTime.setText(filter.getCheckOutTimeStr("M��d��(��W) ���"));
		input_inDays.setText(filter.getInDaysStr());
		lbl_price
				.setText(new StringBuilder("�ϼ�:").append(String.valueOf(price)));
		room_count.setRange(1, 10);
		people_count.setRange(1, 2);
		people_num = 1;
		DateTime now = DateTime.now();
		if (orderInfo.getCheckInTime().Year() == now.Year()
				&& orderInfo.getCheckInTime().Month() == now.Month()
				&& orderInfo.getCheckInTime().Day() == now.Day()) {
			startIndex = Math.max(now.Hour(), 8) - 8;
		} else
			startIndex = 0;
		endIndex = startIndex;
		input_comeTime.setText(new StringBuilder(
				ComeTimePickerActivity.items[startIndex]).append(" - ").append(
				ComeTimePickerActivity.items[endIndex]));
		orderInfo.setComeStartTime(ComeTimePickerActivity.items[startIndex]);
		orderInfo.setComeEndTime(ComeTimePickerActivity.items[endIndex]);
		input_contact.setText(user.getRealName());
		input_contactTel.setText(user.getTel());

		addBuyerItem(0);
		room_count.setOnChangeListener(new OnChangedListener() {

			@Override
			public void onChanged(ItemPicker picker, int oldVal, int newVal) {
				orderInfo.setBuyCount(newVal);

				people_count.setRange(newVal, newVal * 2);
				people_count.notifyChange();
				lbl_price.setText(new StringBuilder("�ϼ�:").append(String
						.valueOf(price * orderInfo.getBuyCount())));
			}

		});

		people_count.setOnChangeListener(new OnChangedListener() {

			@Override
			public void onChanged(ItemPicker picker, int oldVal, int newVal) {
				if (newVal > people_num) {
					for (int i = people_num; i < newVal; i++) {
						addBuyerItem(i);
						// View view =
						// getLayoutInflater().inflate(R.layout.user_info_item,null);
						// table.addView(view, 8+i);
					}
					people_num = newVal;
				} else if (newVal < people_num) {
					for (int i = newVal; i < people_num; i++) {
						table.removeViewAt(people_num-i+8);
						//table.removeViewAt(8);
					}
					people_num = newVal;
				}

			}

		});

		input_checkInTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDateDialog();
			}

		});
		input_inDays.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!dailogShow) {
					dailogShow = true;
					Intent intent = new Intent();
					intent.setClass(BuyRoomActivity.this,
							NumberPickerActivity.class);
					intent.putExtra("min", 1);
					intent.putExtra("max", 30);
					intent.putExtra("value", filter.getInDays());
					startActivityForResult(intent, MasterActivity.DIALOG_NUMBER);
				}
			}

		});

		input_comeTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!dailogShow) {
					dailogShow = true;
					Intent intent = new Intent();
					intent.setClass(BuyRoomActivity.this,
							ComeTimePickerActivity.class);
					intent.putExtra("startIndex", startIndex);
					intent.putExtra("endIndex", endIndex);
					startActivityForResult(intent,
							MasterActivity.DIALOG_COMETIME);
				}
			}

		});
		btn_addbook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!dailogShow) {
					dailogShow = true;
					Intent intent = new Intent(BuyRoomActivity.this,
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
					Toast.makeText(BuyRoomActivity.this, "�����ύ��,���Ժ�...!",
							Toast.LENGTH_LONG).show();
					return;
				}
				orderInfo.setContact(input_contact.getText().toString());
				orderInfo.setContactTel(input_contactTel.getText().toString());
				if (orderInfo.getContact() == null
						|| orderInfo.getContact().length() == 0) {
					Toast.makeText(BuyRoomActivity.this, "��������ϵ������!",
							Toast.LENGTH_LONG).show();
					input_contact.requestFocus();
					return;
				}
				if (orderInfo.getContactTel() == null
						|| orderInfo.getContactTel().length() == 0) {
					Toast.makeText(BuyRoomActivity.this, "��������ϵ�˵绰!",
							Toast.LENGTH_LONG).show();
					input_contactTel.requestFocus();
					return;
				}

				orderInfo.clearBuyer();
				boolean findErr=false;
				for (int i = 8; i < table.getChildCount(); i++) {
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
						Toast.makeText(BuyRoomActivity.this, "������ס��������!",
								Toast.LENGTH_LONG).show();
						nameView.requestFocus();
						return;
					}
					if (item.IDCard == null || item.IDCard.length() == 0) {
						findErr=true;
						Toast.makeText(BuyRoomActivity.this, "������ס�������֤��!",
								Toast.LENGTH_LONG).show();
						idcardView.requestFocus();
						return;
					}
					orderInfo.addBuyer(item);
				}
				if (!findErr){

				final Button btn=(Button)v;
				btn.setText("�����ύ��...");
				isRunning=true;
				HotelAPI api=new HotelAPI();
				api.beginSendOrder(user.getSessionKey(), orderInfo, new APICallback<AlipayOrderModel>(){

					@Override
					public void onCompleted(
							APIResultModel<AlipayOrderModel> result) {
						isRunning=false;
						btn.setText(R.string.go_pay);
						if (result.getSuc()){
							payContent=result.getItems();
							goPay();
							Toast.makeText(BuyRoomActivity.this, "�����ύ�ɹ�!",
									Toast.LENGTH_LONG).show();
						}
						else{
							Toast.makeText(BuyRoomActivity.this, result.getMsg(),
									Toast.LENGTH_LONG).show();
						}
						
					}

					@Override
					public void onError(String msg) {
						isRunning=false;
						btn.setText(R.string.go_pay);
						Toast.makeText(BuyRoomActivity.this, msg,
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
				needPay=false;
			}
		}
	};

	//
	// the handler use to receive the pay result.
	// �������֧�������֧�����ֻ���ͬ��֪ͨ
	public Handler mHandler = new AlipayHandler<BuyRoomActivity>(BuyRoomActivity.this) {
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
									BuyRoomActivity.this,
									"��ʾ",
									getResources().getString(
											R.string.check_sign_failed),
									android.R.drawable.ic_dialog_alert);
						} else {// ��ǩ�ɹ�����ǩ�ɹ������жϽ���״̬��
							if (tradeStatus.equals("9000"))// �жϽ���״̬�룬ֻ��9000��ʾ���׳ɹ�
							{
								BaseHelper.showDialog(BuyRoomActivity.this, "��ʾ",
										"֧���ɹ�������״̬�룺" + tradeStatus,
										R.drawable.infoicon);
								Intent intent = new Intent();
								intent.setClass(BuyRoomActivity.this, MyHotelActivity.class);
								startActivity(intent);
								overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
								finish();
							}
							else
								BaseHelper.showDialog(BuyRoomActivity.this, "��ʾ",
										"֧��ʧ�ܡ�����״̬��:" + tradeStatus,
										R.drawable.infoicon);
						}

					} catch (Exception e) {
						e.printStackTrace();
						BaseHelper.showDialog(BuyRoomActivity.this, "��ʾ", ret,
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
	
	private void goPay(){
		//
		// check to see if the MobileSecurePay is already installed.
		// ��ⰲȫ֧�������Ƿ�װ
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(this);
		boolean isMobile_spExist = mspHelper.detectMobile_sp();
		if (!isMobile_spExist) {
			needPay=true;
			return;
		}

		// check some info.
		// ���������Ϣ
		if (!checkInfo()) {
			BaseHelper
					.showDialog(
							BuyRoomActivity.this,
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
			String info = new StringBuilder(orderInfo).append("&sign=\"").append(strsign).append("\"&sign_type=\"RSA\"").toString();
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
			Toast.makeText(BuyRoomActivity.this, R.string.remote_call_failed,
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
	protected void onLogged() {
		orderInfo.setContact(user.getRealName());
		orderInfo.setContactTel(user.getTel());

		// NameAndIDCard item = new NameAndIDCard();
		// item.Name = user.getRealName();
		// item.IDCard = user.getIDcard();
		View view = table.getChildAt(8);
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
		table.addView(view, 8 + index);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		dailogShow = false;
		if (requestCode == MasterActivity.DIALOG_NUMBER) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					filter.setInDays(bundle.getInt("itemValue",
							filter.getInDays()));
					orderInfo.setCheckOutTime(new DateTime(filter
							.getCheckOutTime()));
					input_inDays.setText(filter.getInDaysStr());
					lbl_checkOutTime.setText(filter
							.getCheckOutTimeStr("M��d��(��W) ���"));
				}
			}
		} else if (requestCode == MasterActivity.DIALOG_COMETIME) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					orderInfo.setComeStartTime(bundle.getString("startValue"));
					orderInfo.setComeEndTime(bundle.getString("endValue"));
					startIndex = bundle.getInt("startIndex");
					endIndex = bundle.getInt("endIndex");
					input_comeTime.setText(new StringBuilder(
							ComeTimePickerActivity.items[startIndex]).append(
							" - ").append(
							ComeTimePickerActivity.items[endIndex]));
					// input_inDays.setText(filter.getInDaysStr());
					// lbl_checkOutTime.setText(filter.getCheckOutTimeStr("M��d��(��W) ���"));
				}
			}
		} else if (requestCode == MasterActivity.DIALOG_CONTACT) {
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
		// ����WebView��ת����
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// Intent intent = new Intent();
			// intent.setClass(BuyRoomActivity.this, HotelViewActivity.class);
			// startActivity(intent);
			// overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
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
					filter.setCheckInTime(new DateTime(year, monthOfYear + 1,
							dayOfMonth).getDate());
					orderInfo.setCheckInTime(new DateTime(filter
							.getCheckInTime()));
					orderInfo.setCheckOutTime(new DateTime(filter
							.getCheckOutTime()));
					// filter.setCheckInTime(new DateTime(2000,1,1).getDate());
					input_checkInTime.setText(filter
							.getCheckInTimeStr("yyyy�� M��d��"));
					lbl_checkOutTime.setText(filter
							.getCheckOutTimeStr("M��d��(��W) ���"));
				}
			}, new DateTime(filter.getCheckInTime()));
		}
		if (!picker.isShowing()) {
			picker.setCurrentDate(new DateTime(filter.getCheckInTime()));
			picker.setMaxDate(DateTime.now().AddYear(1));
			picker.setMinDate(DateTime.now());
			picker.show(this);
		}
	}

}
