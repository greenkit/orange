package com.standny.gzly;

import com.standny.gzly.article.ArticleListActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class HomeActivity extends MasterActivity {
	private GridView gv;
	// private GridView tab;
	/*
	 * private RelativeLayout tab_1; private RelativeLayout tab_2; private
	 * RelativeLayout tab_3; private RelativeLayout tab_4; private
	 * RelativeLayout tab_5;
	 */
	private Integer[] img = { R.drawable.home_icon_about,
			R.drawable.home_icon_news, R.drawable.home_icon_sight,
			R.drawable.home_icon_hotel, R.drawable.home_icon_shopping,
			R.drawable.home_icon_travel_notes };
	private Integer[] bgImg = { R.drawable.grid_item_bg_01,
			R.drawable.grid_item_bg_02, R.drawable.grid_item_bg_03,
			R.drawable.grid_item_bg_04, R.drawable.grid_item_bg_05,
			R.drawable.grid_item_bg_06 };
	private Integer[] title = { R.string.menu_about, R.string.menu_news,
			R.string.menu_sight, R.string.menu_hotel, R.string.menu_shopping,
			R.string.menu_travel_notes };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		tabIndex = 0;
		gv = (GridView) findViewById(R.id.icon_grid);
		// 设置列数
		gv.setNumColumns(3);
		// 配置适配器
		gv.setAdapter(new MenuItemAdapter(title, img, bgImg,
				R.layout.grid_item, this));
		// 配置监听器，写对选中的图标的操作。在例子中都跳到SysManActivity
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1, int position,
					long arg3) {
                switch ((int) arg3) {
                    case 0: 
                    case 5:{
                        MenuItem item = (MenuItem) adapterView.getAdapter().getItem(position);
                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this,
                                ArticleListActivity.class);
                        intent.putExtra("parentCatgoryKey", arg3 == 0 ? "gzIntro" : "Raiders");
                        intent.putExtra("activity_title", getString(item.getTitleID()));
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                        break;
                    }
                    case 3: {

                        Intent intent = new Intent();
                        intent.setClass(HomeActivity.this, HotelActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);
                        HomeActivity.this.finish();
                        break;
                    }

                }
			}
		});

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 处理WebView跳转返回
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			exit();
			// return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
