/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2009 All Rights Reserved.
 */
package alipay;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 
 * @author jianmin.jiang
 * 
 * @version $Id: FileFetch.java, v 0.1 2012-2-14 ����8:55:40 jianmin.jiang Exp $
 */
final class FileFetch implements Runnable {

	private String fileUrl;
	private String savePath;
	private FileDownloader downloader;
	private boolean stop = false;
	private long fileStart;
	private long fileEnd;

	public FileFetch(String fileUrl, String savePath, FileDownloader downloader) {
		this.fileUrl = fileUrl;
		this.savePath = savePath;
		this.downloader = downloader;
	}

	public void run() {
		if (downloader.showProgress()) {
			if (fileEnd <= 0 || fileStart >= fileEnd) {
				this.stop = true;
				return;
			}
		}
		boolean canStop = false;
		FileAccess fileAccess = new FileAccess();
		while (!this.stop) {
			InputStream input = null;
			int responseCode = 0;
			try {
				try {
					HttpGet httpGet = new HttpGet(fileUrl);
					// ȡ��HttpClient
					HttpClient httpClient = new DefaultHttpClient();
					if (downloader.showProgress()) {
						String property = "bytes=" + fileStart + "-" + fileEnd;
						// conn.setRequestProperty("RANGE", property);
						httpGet.addHeader("Range", property);
					}
					// ����HttpClient�����HttpResponce
					HttpResponse response = httpClient.execute(httpGet);
					// ����ɹ�
					responseCode = response.getStatusLine().getStatusCode();
					switch (responseCode) {
					case HttpStatus.SC_OK:
					case HttpStatus.SC_CREATED:
					case HttpStatus.SC_ACCEPTED:
					case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION:
					case HttpStatus.SC_NO_CONTENT:
					case HttpStatus.SC_RESET_CONTENT:
					case HttpStatus.SC_PARTIAL_CONTENT:
					case HttpStatus.SC_MULTI_STATUS:
						input = response.getEntity().getContent();
						break;
					default:
						this.stop = true;
						break;
					}
					if (this.stop) {
						break;// ����ʧ�ܽ��������Ҫ�������ļ�
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (input == null) {
					continue;
				}
				int size;
				byte[] buffer = new byte[1024];
				do {
					size = input.read(buffer, 0, buffer.length);
					if (size != -1) {
						this.fileStart += fileAccess.write(buffer, 0, size);
						this.downloader.writeTempFile();
					}
					canStop = downloader.showProgress() ? fileStart < fileEnd
							: true;
					canStop = !stop && canStop;
				} while (size > -1 && canStop);
				this.stop = true;
			} catch (SocketTimeoutException e) {
				if (responseCode == 0) {
					this.stop = true;
				}
			} catch (IOException e) {
				this.stop = true;
			} catch (Exception e) {
				this.stop = true;
			} finally {
				if (input != null)
					try {
						input.close();
					} catch (Exception e) {
					}
			}
		}
		fileAccess.close();
	}

	public final long getFileStart() {
		return fileStart;
	}

	public final void setFileStart(long fileStart) {
		this.fileStart = fileStart;
	}

	public final long getFileEnd() {
		return fileEnd;
	}

	public final void setFileEnd(long fileEnd) {
		this.fileEnd = fileEnd;
	}

	public final boolean isStop() {
		return this.stop;
	}

	public final void stop() {
		stop = true;
	}

	final class FileAccess {

		private FileOutputStream outStream;

		public FileAccess() {
			try {
				/**
				 * ֻ�ܱ����ڳ����filesĿ¼�¡��������files�����ļ����£�����ֶ�ȡȨ�޵�����
				 * �ڶ�����������Ϊtrue����ʾ����׷�����ݣ�ʵ�ֶϵ�������
				 * 
				 * �ڴ˴�д����ļ��ǲ��ɶ��ģ�����������ɺ�ĳɿɶ���
				 */
				outStream = new FileOutputStream(savePath, true);
			} catch (FileNotFoundException e) {
				// ������bean.createFile()���Ѵ����ļ�����������������
				e.printStackTrace();
			}
		}

		public synchronized int write(byte[] b, int start, int len)
				throws IOException {
			outStream.write(b, start, len);
			return len;
		}

		public void close() {
			try {
				outStream.close();
			} catch (Exception e) {
			}
		}
	}
}
