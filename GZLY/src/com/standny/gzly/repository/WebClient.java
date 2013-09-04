package com.standny.gzly.repository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * For the ConnectionPoolTimeoutException always happen, 
 * Resolved in blog http://blog.csdn.net/shootyou/article/details/6615051
 * 1. setMaxTotalConnections & setMaxConnectionsPerRoute
 * 2. abort http request finally
 * 
 */
public class WebClient {
	static final String TAG="WebClient";
	private static HttpClient customerHttpClient;
    private static final String CHARSET = HTTP.UTF_8;

 	static class WebClientHandler extends Handler {
		WeakReference<WebClientCallback> callback;

		WebClientHandler(WebClientCallback callback) {
			this.callback = new WeakReference<WebClientCallback>(callback);
		}

		@Override
		public void handleMessage(Message msg) {
			WebClientCallback callback = this.callback.get();
			if (callback != null) {
				switch (msg.what) {
				case 0:
					callback.onCompleted(true, msg.obj);
					break;
				case 1:
					callback.onCompleted(false, msg.obj);
					break;
				}
			}
			super.handleMessage(msg);
		}

	}

	WebClientCallback callback;
	private WebClientHandler mHandler;

	public WebClient(WebClientCallback callback) {
		super();
		this.callback = callback;
		mHandler = new WebClientHandler(this.callback);
	}
	
	public static synchronized HttpClient getHttpClient() {
        if (null == customerHttpClient) {
            HttpParams params = new BasicHttpParams();
            // ����һЩ��������
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);
            // �������������  
            ConnManagerParams.setMaxTotalConnections(params, 800);  
            // ����ÿ��·�����������  
            ConnPerRouteBean connPerRoute = new ConnPerRouteBean(400);  
            ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);  
            //HttpProtocolParams.setUserAgent(params,"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            // ��ʱ����
            /* �����ӳ���ȡ���ӵĳ�ʱʱ�� */
            ConnManagerParams.setTimeout(params, 4000);
            /* ���ӳ�ʱ */
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            /* ����ʱ */
            HttpConnectionParams.setSoTimeout(params, 10000);
          
            // �������ǵ�HttpClient֧��HTTP��HTTPS����ģʽ
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));

            // ʹ���̰߳�ȫ�����ӹ���������HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);
            customerHttpClient = new DefaultHttpClient(conMgr, params);
        }
        return customerHttpClient;
    }

	public void BeginGet(final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				/* ����HTTP Get���� */
				HttpGet httpRequest = new HttpGet(url);
				try {
					/* ����HTTP request */
					HttpResponse httpResponse = getHttpClient().execute(httpRequest);
					/* ��״̬��Ϊ200 ok */
					if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
						/* ȡ����Ӧ�ַ��� */
						String strResult = EntityUtils.toString(httpResponse
								.getEntity());
						Message m = mHandler.obtainMessage(0, strResult);
						m.sendToTarget();
					} else {
						Message m = mHandler.obtainMessage(1, httpResponse.getStatusLine()
								.toString());
						m.sendToTarget();
					}
				}catch(ConnectionPoolTimeoutException e){
					Message m = mHandler.obtainMessage(1, "����ʱ");
					m.sendToTarget();
					
				} catch (ConnectTimeoutException e) {
					Message m = mHandler.obtainMessage(1, "����ʱ");
					m.sendToTarget();
				}catch(SocketTimeoutException e){
					Message m = mHandler.obtainMessage(1, "����ʱ");
					m.sendToTarget();
				} catch (ClientProtocolException e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "δ֪����";
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				} catch (IOException e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "δ֪����";
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				} catch (Exception e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "δ֪����";
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				} finally {
				    httpRequest.abort();
				}
			}
		}).start();
	}

	public void BeginGetStream(final String url) {
		Log.i(TAG, url);
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpGet httpRequest = new HttpGet(url);
				try {
					HttpResponse httpResponse = getHttpClient().execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
						HttpEntity httpEntity = httpResponse.getEntity();
						BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(
								httpEntity);
						InputStream instream = bufHttpEntity.getContent();
						BufferedInputStream bi = new BufferedInputStream(
								instream);
						InputStream inputStream = bi;
						Message m = mHandler.obtainMessage(0, inputStream);
						m.sendToTarget();
					} else {
						Message m = mHandler.obtainMessage(1, httpResponse
								.getStatusLine().toString());
						m.sendToTarget();
					}
				}catch(ConnectionPoolTimeoutException e){
					Message m = mHandler.obtainMessage(1, "����ʱ");
					m.sendToTarget();
					
				} catch (ConnectTimeoutException e) {
					Message m = mHandler.obtainMessage(1, "����ʱ");
					m.sendToTarget();
				}catch(SocketTimeoutException e){
					Message m = mHandler.obtainMessage(1, "����ʱ");
					m.sendToTarget();
				} catch (ClientProtocolException e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "δ֪����";
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				} catch (HttpHostConnectException e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "�������,�޷���ȡ����";
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				} catch (IOException e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "δ֪����";
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				}catch (Exception e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "δ֪����";
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				} finally {
                httpRequest.abort();
            }
			}
		}).start();
	}

	// @SuppressLint("NewApi")
	public void BeginPost(final String url, final List<NameValuePair> params) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG,url);
				/* ����HTTP Post���� */
				HttpPost httpRequest = new HttpPost(url);
				try {
					/* ����HTTP request */
					httpRequest.setEntity(new UrlEncodedFormEntity(params,
							HTTP.UTF_8));
					/* ȡ��HTTP response */
					HttpResponse httpResponse = getHttpClient().execute(httpRequest);
					if (httpResponse.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
						/* ȡ����Ӧ�ַ��� */
						String strResult = EntityUtils.toString(httpResponse
								.getEntity());
						Message m = mHandler.obtainMessage(0, strResult);
						m.sendToTarget();
					} else {
						Message m = mHandler.obtainMessage(1, httpResponse
								.getStatusLine().toString());
						m.sendToTarget();
					}
				}catch(ConnectionPoolTimeoutException e){
					Message m = mHandler.obtainMessage(1, "����ʱ");
					m.sendToTarget();
					
				} catch (ConnectTimeoutException e) {
					Message m = mHandler.obtainMessage(1, "����ʱ");
					m.sendToTarget();
				}catch(SocketTimeoutException e){
					Message m = mHandler.obtainMessage(1, "����ʱ");
					m.sendToTarget();
				}catch (ClientProtocolException e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "δ֪����";
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				} catch (HttpHostConnectException e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "�������,�޷���ȡ����";
					// if (callback != null) callback.onError(msg);
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				} catch (IOException e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "δ֪����";
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				}catch (Exception e) {
					String msg = e.getMessage();
					if (msg == null)
						msg = "δ֪����";
					Message m = mHandler.obtainMessage(1, msg);
					m.sendToTarget();
					e.printStackTrace();
				} finally {
                httpRequest.abort();
            }
			}
		}).start();
	}
}
