/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package alipay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.standny.gzly.R;

import alipay.FileDownloader.IDownloadProgress;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * ��ⰲȫ֧�������Ƿ���ȷ��װ�����û�а�װ���б��ذ�װ���������ذ�װ�� ��ⰲȫ֧������汾�����°汾ʱ�������ء�
 * 
 */
@SuppressLint("HandlerLeak")
public class MobileSecurePayHelper {
	static final String TAG = "MobileSecurePayHelper";

	private ProgressDialog mProgress = null;
	Context mContext = null;

	public MobileSecurePayHelper(Context context) {
		mContext = context;
	}

	/**
	 * ��ⰲȫ֧�������Ƿ�װ
	 * 
	 * @return
	 */
	public boolean detectMobile_sp() {
		boolean isMobile_spExist = isMobile_spExist();
		if (!isMobile_spExist) {
			//
			// get the cacheDir.
			// ��ȡϵͳ�������·�� ��ȡ/data/data//cacheĿ¼
			File cacheDir = mContext.getCacheDir();
			final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";
			//
			// ����װ
			retrieveApkFromAssets(mContext, PartnerConfig.ALIPAY_PLUGIN_NAME,
					cachePath);

			mProgress = BaseHelper.showProgress(mContext, null, "���ڼ�ⰲȫ֧������汾",
					false, true);

			// ʵ�����̼߳���Ƿ����°汾��������
			new Thread(new Runnable() {
				public void run() {
					//
					// ����Ƿ����µİ汾��
					PackageInfo apkInfo = getApkInfo(mContext, cachePath);
					String newApkdlUrl = checkNewUpdate(apkInfo);

					//
					// ��̬����
					if (newApkdlUrl != null) {
						FileDownloader fd = new FileDownloader();
						fd.setFileUrl(newApkdlUrl);
						fd.setSavePath(cachePath);
						fd.setProgressOutput(new IDownloadProgress() {
							@Override
							public void downloadSucess() {
								Message msg = mHandler.obtainMessage(
										AlixId.RQF_INSTALL_CHECK, cachePath);
								mHandler.sendMessage(msg);
							}

							@Override
							public void downloadProgress(float progress) {

							}

							@Override
							public void downloadFail() {
								Message msg = mHandler.obtainMessage(
										AlixId.RQF_DOWNLOAD_FAILED, cachePath);
								mHandler.sendMessage(msg);
							}
						});
						fd.start();
					}else{
						Message msg = mHandler.obtainMessage(
								AlixId.RQF_INSTALL_WITHOUT_DOWNLOAD, cachePath);
						mHandler.sendMessage(msg);
					}

					// send the result back to caller.
					// ���ͽ��

				}
			}).start();
		}
		// else ok.

		return isMobile_spExist;
	}

	/**
	 * ��ʾȷ�ϰ�װ����ʾ
	 * 
	 * @param context
	 *            �����Ļ���
	 * @param cachePath
	 *            ��װ�ļ�·��
	 */
	public void showInstallConfirmDialog(final Context context,
			final String cachePath) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.info);
		builder.setTitle(context.getResources().getString(
				R.string.confirm_install_hint));
		builder.setMessage(context.getResources().getString(
				R.string.confirm_install));

		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//
						// �޸�apkȨ��
						BaseHelper.chmod("777", cachePath);

						//
						// install the apk.
						// ��װ��ȫ֧������APK
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.setDataAndType(Uri.parse("file://" + cachePath),
								"application/vnd.android.package-archive");
						context.startActivity(intent);
					}
				});

		builder.setNegativeButton(
				context.getResources().getString(android.R.string.cancel), null);

		builder.show();
	}

	/**
	 * ���������б��ж��Ƿ�װ��ȫ֧������
	 * 
	 * @return
	 */
	public boolean isMobile_spExist() {
		PackageManager manager = mContext.getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.alipay.android.app"))
				return true;
		}

		return false;
	}

	/**
	 * ��װ��ȫ֧�����񣬰�װassets�ļ����µ�apk
	 * 
	 * @param context
	 *            �����Ļ���
	 * @param fileName
	 *            apk����
	 * @param path
	 *            ��װ·��
	 * @return
	 */
	public boolean retrieveApkFromAssets(Context context, String fileName,
			String path) {
		boolean bRet = false;

		try {
			InputStream is = context.getAssets().open(fileName);

			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);

			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}

			fos.close();
			is.close();

			bRet = true;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return bRet;
	}

	/**
	 * ��ȡδ��װ��APK��Ϣ
	 * 
	 * @param context
	 * @param archiveFilePath
	 *            APK�ļ���·�����磺/sdcard/download/XX.apk
	 */
	public static PackageInfo getApkInfo(Context context, String archiveFilePath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo apkInfo = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_META_DATA);
		return apkInfo;
	}

	/**
	 * ����Ƿ����°汾������У�����apk���ص�ַ
	 * 
	 * @param packageInfo
	 *            {@link PackageInfo}
	 * @return
	 */
	public String checkNewUpdate(PackageInfo packageInfo) {
		String url = null;

		try {
			JSONObject resp = sendCheckNewUpdate(packageInfo.versionName);
			// JSONObject resp = sendCheckNewUpdate("1.0.0");
			if (resp.getString("needUpdate").equalsIgnoreCase("true")) {
				url = resp.getString("updateUrl");
			}
			// else ok.
		} catch (Exception e) {
			e.printStackTrace();
		}

		return url;
	}

	/**
	 * ���͵�ǰ�汾��Ϣ�������Ƿ���Ҫ���� �����Ҫ�������ظ���apk��ַ
	 * 
	 * @param versionName
	 *            ��ǰ�汾��
	 * @return
	 */
	public JSONObject sendCheckNewUpdate(String versionName) {
		JSONObject objResp = null;
		try {
			JSONObject req = new JSONObject();
			req.put(AlixDefine.action, AlixDefine.actionUpdate);

			JSONObject data = new JSONObject();
			data.put(AlixDefine.platform, "android");
			data.put(AlixDefine.VERSION, versionName);
			data.put(AlixDefine.partner, "");

			req.put(AlixDefine.data, data);

			objResp = sendRequest(req.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return objResp;
	}

	/**
	 * ����json����
	 * 
	 * @param content
	 * @return
	 */
	public JSONObject sendRequest(final String content) {
		NetworkManager nM = new NetworkManager(this.mContext);

		//
		JSONObject jsonResponse = null;
		try {
			String response = null;

			synchronized (nM) {
				//
				response = nM.SendAndWaitResponse(content, Constant.server_url);
			}

			jsonResponse = new JSONObject(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//
		if (jsonResponse != null)
			BaseHelper.log(TAG, jsonResponse.toString());

		return jsonResponse;
	}

	/**
	 * ��̬����apk
	 * 
	 * @param context
	 *            �����Ļ���
	 * @param url
	 *            ���ص�ַ
	 * @param filename
	 *            �ļ�����
	 * @return
	 */
	public boolean retrieveApkFromNet(Context context, String url,
			String filename) {
		boolean ret = false;

		try {
			NetworkManager nm = new NetworkManager(mContext);
			ret = nm.urlDownloadToFile(context, url, filename);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	//
	// close the progress bar
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
	// the handler use to receive the install check result.
	// �˴����հ�װ�����
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				Log.e(TAG, "msg = "+msg);
				switch (msg.what) {
				case AlixId.RQF_INSTALL_CHECK:
				case AlixId.RQF_INSTALL_WITHOUT_DOWNLOAD:
				case AlixId.RQF_DOWNLOAD_FAILED: {
					//
					Log.i(TAG, "show Install dialog");
					closeProgress();
					String cachePath = (String) msg.obj;

					showInstallConfirmDialog(mContext, cachePath);
				}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
