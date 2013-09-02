package com.standny.gzly;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.standny.gzly.models.ContactInfoModel;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ContactsActivity extends Activity {

	static class ReadContactsHandler extends Handler {
		WeakReference<PaginationAdapter> adapter;

		ReadContactsHandler(PaginationAdapter adapter) {
			this.adapter = new WeakReference<PaginationAdapter>(adapter);
			;
		}

		@Override
		public void handleMessage(Message msg) {
			PaginationAdapter adapter = this.adapter.get();
			switch (msg.what) {
			case 0:
				adapter.addItem((ContactInfoModel) msg.obj);
				break;
			}
			super.handleMessage(msg);
		}

	}

	private ListView list;
	private PaginationAdapter adapter;
	private ReadContactsHandler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		list = (ListView) findViewById(R.id.list);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View listItem,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(ContactsActivity.this, BuyRoomActivity.class);
				ContactsActivity.this.setResult(RESULT_OK, intent);
				Bundle b = new Bundle();
				ContactInfoModel item = getItemWithView(listItem);
				if (item != null) {
					b.putString("name", item.Name);
					b.putString("tel", item.Tel);
				}
				intent.putExtras(b);
				ContactsActivity.this.finish();
			}
		});
		initializeAdapter();
	}

	private ContactInfoModel getItemWithView(View view) {
		TextView name = (TextView) view.findViewById(R.id.lbl_name);
		TextView tel = (TextView) view.findViewById(R.id.lbl_tel);
		if (name != null && tel != null) {
			ContactInfoModel item = new ContactInfoModel();
			item.Name = name.getText().toString();
			item.Tel = tel.getText().toString();
			return item;
		}
		return null;
	}

	private void initializeAdapter() {
		adapter = new PaginationAdapter();
		list.setAdapter(adapter);
		mHandler = new ReadContactsHandler(this.adapter);
		loadItems();
	}

	private void loadItems() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 得到ContentResolver对象
				ContentResolver cr = getContentResolver();
				// 取得电话本中开始一项的光标
				Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
						null, null, null, null);
				// 向下移动光标
				while (cursor.moveToNext()) {
					// 取得联系人名字
					int nameFieldColumnIndex = cursor
							.getColumnIndex(PhoneLookup.DISPLAY_NAME);
					String contact = cursor.getString(nameFieldColumnIndex);
					// 取得电话号码
					String ContactId = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
					Cursor phone = cr.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "=" + ContactId, null, null);
					if (phone != null) {
						while (phone.moveToNext()) {
							String PhoneNumber = phone.getString(phone
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							ContactInfoModel contactItem = new ContactInfoModel();
							contactItem.Name = contact;
							contactItem.Tel = PhoneNumber;
							//mHandler.obtainMessage(0, "abc");
							mHandler.obtainMessage(0, contactItem).sendToTarget();
							//adapter.addItem(contactItem);
						}
						phone.close();
					}
				}
				cursor.close();
			}
		}).start();
	}

	class PaginationAdapter extends BaseAdapter {
		List<ContactInfoModel> items;

		public PaginationAdapter() {
			this.items = new ArrayList<ContactInfoModel>();
		}

		public PaginationAdapter(List<ContactInfoModel> items) {
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
			ContactInfoModel item = items.get(position);
			if (view == null)
				view = getLayoutInflater().inflate(R.layout.contact_list_item,
						null);
			TextView name = (TextView) view.findViewById(R.id.lbl_name);
			name.setText(item.Name);

			TextView tel = (TextView) view.findViewById(R.id.lbl_tel);
			tel.setText(item.Tel);

			return view;
		}

		public void addItem(List<ContactInfoModel> items) {
			this.items.addAll(items);
			this.notifyDataSetChanged();
		}

		public void addItem(ContactInfoModel item) {
			item.Tel = item.Tel.replace("-", "").replace("+86", "")
					.replace(" ", "");
			boolean find = false;
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i).Tel.equals(item.Tel)) {
					find = true;
					break;
				}
			}
			if (!find) {
				items.add(item);
				this.notifyDataSetChanged();
			}
		}
	}

	private void Cancel() {
		Intent intent = new Intent();
		intent.setClass(ContactsActivity.this, BuyRoomActivity.class);
		ContactsActivity.this.setResult(RESULT_CANCELED, intent);
		ContactsActivity.this.finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Cancel();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
