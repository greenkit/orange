package com.standny.gzly;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends Activity {
	
	private TextView title;
	private WebView webbox;
	private Boolean loadError;
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		title=(TextView)findViewById(R.id.title);
		webbox=(WebView)findViewById(R.id.webView1);
		Intent intent=this.getIntent();
		title.setText(intent.getExtras().getString("title"));
		webbox.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        WebSettings webSettings = webbox.getSettings();   
        webSettings.setJavaScriptEnabled(false); 
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
		loadError=false;
		progress=(ProgressBar)findViewById(R.id.progressBar1);
		webbox.setWebViewClient(new WebViewClient(){

			 public void onPageStarted(android.webkit.WebView view, java.lang.String url, android.graphics.Bitmap favicon){
				 ShowLoading();
			 }
			 public void onPageFinished(android.webkit.WebView view, java.lang.String url){
				 if (!loadError)
					 webbox.setVisibility(0);
				 HideLoading();
			 }
			 public void onReceivedError(android.webkit.WebView view, int errorCode, java.lang.String description, java.lang.String failingUrl){
				 webbox.setVisibility(4);
				 loadError=true;
				 Toast.makeText(DetailActivity.this, description, Toast.LENGTH_LONG).show();
			 }
		});
		webbox.loadUrl(intent.getExtras().getString("url"));
	}
	 public void ShowLoading(){
		 progress.setVisibility(0);
	 }
	 
	 public void HideLoading(){
		 progress.setVisibility(4);
	 }
	 

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_reload: 
				webbox.reload();
				break;
			}
			return true;
		}

		public boolean onKeyDown(int keyCode, KeyEvent event) {
			// 处理WebView跳转返回
			if ((keyCode == KeyEvent.KEYCODE_BACK)) {
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				finish();
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

}
