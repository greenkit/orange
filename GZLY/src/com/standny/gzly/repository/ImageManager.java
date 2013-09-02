package com.standny.gzly.repository;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageManager {
	private static final String TAG = "ImageManager";

	static class DownloadTask {
		private String picUrl;
		private String orgPicUrl;
		private List<ImageDownloadCallback> callbackQueue;
		private List<CGSize> sizeArr;
		private List<Integer> tags;

		public void setPicUrl(String url) {
			orgPicUrl=url;
			this.picUrl = APIConfig.GetFullPath(url.replace('\\', '/'));
		}
		
		public String getOrgUrl(){
			return orgPicUrl;
		}

		public String getPicUrl() {
			return picUrl;
		}
		
		@SuppressLint("DefaultLocale")
		public String getThumbUrl(CGSize size){
			return String.format("%s_t_%d_%d%s", picUrl.substring(0,picUrl.lastIndexOf(".")),size.Width,size.Height,picUrl.substring(picUrl.lastIndexOf(".")));
		}

		public List<ImageDownloadCallback> getCallbackQueue() {
			return callbackQueue;
		}

		public void AddQueue(ImageDownloadCallback callback, CGSize size,int tag) {
			if (this.callbackQueue == null)
				this.callbackQueue = new ArrayList<ImageDownloadCallback>();
			if (this.callbackQueue.indexOf(callback) > 0)
				return;
			this.callbackQueue.add(callback);
			if (this.sizeArr == null)
				this.sizeArr = new ArrayList<CGSize>();
			this.sizeArr.add(size);
			if (this.tags == null)
				this.tags = new ArrayList<Integer>();
			this.tags.add(Integer.valueOf(tag));
		}
	}

	// 缓存下载过的图片的Map
	private Map<String, SoftReference<Bitmap>> caches;
	// 任务队列
	private List<DownloadTask> taskQueue;
	private boolean isRunning = false;
	private WebClient wc;
	private CGSize maxSize;

	public ImageManager(CGSize maxSize) {
		this.maxSize=maxSize;
		// 初始化变量
		caches = new HashMap<String, SoftReference<Bitmap>>();
		taskQueue = new ArrayList<DownloadTask>();
		wc = new WebClient(new WebClientCallback() {

			@Override
			public void onCompleted(boolean suc, Object content) {
				if (suc) {
					ImageDownloaded((InputStream) content);
				} else {
					ImageLoadError((String) content);
				}
				isRunning = false;
				if (taskQueue.size() > 0)
					RunLoadThread();
			}
		});
	}

	public void ImageDownloaded(InputStream stream) {
		if (taskQueue.size() > 0) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bmp = BitmapFactory.decodeStream(stream, null, options);
			CGSize orgSize = new CGSize();
			orgSize.Width = options.outWidth;
			orgSize.Height = options.outHeight;
			DownloadTask task = taskQueue.remove(0);
			for (int i = 0; i < task.callbackQueue.size(); i++) {
				CGSize newSize = AdjustSize(orgSize, task.sizeArr.get(i));
				options.outWidth = newSize.Width;
				options.outHeight = newSize.Height;
				options.inJustDecodeBounds = false;
				options.inSampleSize = Math.max(
						(orgSize.Width / newSize.Width),
						(orgSize.Height / newSize.Height));
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inPurgeable = true;
				options.inInputShareable = true;
				try {
					stream.reset();
					bmp = BitmapFactory.decodeStream(stream, null, options);
					/*byte[] data = readStream(stream);
					if (data != null) {
						bmp = BitmapFactory.decodeByteArray(data, 0,
								data.length);
					}*/
				} catch (Exception e) {
					e.printStackTrace();
				}
				// bmp = BitmapFactory.decodeStream(stream, null, options);
				task.callbackQueue.get(i).onCompleted(bmp,task.tags.get(i));
				SaveToCache(bmp, task.getOrgUrl(), task.sizeArr.get(i));
			}
		}
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 得到图片字节流 数组大小
	 */
	/*private static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}*/

	public void ImageLoadError(String msg) {
		if (taskQueue.size() > 0) {
			DownloadTask task = taskQueue.remove(0);
			for (int i = 0; i < task.callbackQueue.size(); i++) {
				task.callbackQueue.get(i).onError(msg,task.tags.get(i));
			}
		}
	}

	@SuppressLint("DefaultLocale")
	private void SaveToCache(Bitmap bmp, String url, CGSize size) {
		String key = String.format("%s_%d_%d", url, size.Width, size.Height);
		if (!caches.containsKey(key))
			caches.put(key, new SoftReference<Bitmap>(bmp));
	}

	@SuppressLint("DefaultLocale")
	private Bitmap GetWithCache(String url, CGSize size) {
		String key = String.format("%s_%d_%d", url, size.Width, size.Height);
		if (caches.containsKey(key)) {
			// 取出软引用
			SoftReference<Bitmap> rf = caches.get(key);
			// 通过软引用，获取图片
			Bitmap bitmap = rf.get();
			// 如果该图片已经被释放，则将该path对应的键从Map中移除掉
			if (bitmap == null) {
				caches.remove(key);
			} else {
				// 如果图片未被释放，直接返回该图片
				Log.i(TAG, "return image in cache" + key);
				return bitmap;
			}
		}
		return null;
	}

	private CGSize AdjustSize(CGSize orgSize, CGSize newSize) {
		if (orgSize.Width <= newSize.Width && orgSize.Height <= newSize.Height)
			return orgSize;
		CGSize tmpSize = new CGSize();
		tmpSize.Width =(int)Math.round((double)orgSize.Width / (double)orgSize.Height * (double)newSize.Height);
		if (tmpSize.Width < newSize.Width) {
			tmpSize.Height = (int)Math.round((double)newSize.Width / ((double)orgSize.Width / (double)orgSize.Height));
			tmpSize.Width = newSize.Width;
		} else
			tmpSize.Height = newSize.Height;
		Log.i(TAG, String.format("%d,%d",tmpSize.Width,tmpSize.Height));
		return tmpSize;
	}

	public Bitmap GetImage(String url, CGSize size,int tag,
			ImageDownloadCallback callback) {
		Bitmap bmp = GetWithCache(url, size);
		if (bmp != null)
			return bmp;
		boolean find = false;
		for (int i = 0; i < taskQueue.size(); i++) {
			if (taskQueue.get(i).picUrl == url) {
				taskQueue.get(i).AddQueue(callback, size,tag);
				find = true;
				break;
			}
			Log.i(TAG, "task is exists");
		}
		if (!find) {
			
			DownloadTask task = new DownloadTask();
			task.setPicUrl(url);
			;
			task.AddQueue(callback, size,tag);
			taskQueue.add(task);
			RunLoadThread();
		}
		return null;
	}

	private void RunLoadThread() {
		if (this.isRunning)
			return;
		if (taskQueue.size() > 0) {
			isRunning = true;
			DownloadTask task = taskQueue.get(0);
			wc.BeginGetStream(task.getThumbUrl(maxSize));
		}
	}
}
