package com.ningso.ningsodemo.downloadmanager.bizs;

import android.content.Context;


import com.ningso.ningsodemo.downloadmanager.cons.HttpConnPars;
import com.ningso.ningsodemo.downloadmanager.cons.PublicCons;
import com.ningso.ningsodemo.downloadmanager.entities.TaskInfo;
import com.ningso.ningsodemo.downloadmanager.entities.ThreadInfo;
import com.ningso.ningsodemo.downloadmanager.interfaces.DLTaskListener;
import com.ningso.ningsodemo.downloadmanager.interfaces.IDLThreadListener;
import com.ningso.ningsodemo.downloadmanager.utils.NetUtil;
import com.ningso.ningsodemo.utils.ILog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.HttpStatus;


/**
 * 下载管理器
 * 执行具体的下载操作
 *
 * @author AigeStudio 2015-05-09
 *         开始一个下载任务只需调用{@link #start}方法即可
 *         停止某个下载任务需要调用{@link #stop}方法
 *         停止下载任务仅仅会将对应下载任务移除下载队列而不删除相应数据，下次启动相同任务时会自动根据上一次停止时保存的数据重新开始下载
 *         取消某个下载任务需要调用{@link #cancel}方法 取消下载任务会删除掉相应的本地数据库数据但文件不会被删除
 *         相同url的下载任务视为相同任务
 * @author AigeStudio 2015-05-26
 *         对不支持断点下载的文件直接使用单线程下载 该操作将不会插入数据库
 *         对转向地址进行解析
 *         更改下载线程分配逻辑
 * @author AigeStudio 2015-05-29
 *         修改域名重定向后无法多线程下载问题
 *         修改域名重定向后无法暂停问题
 * @author ZhaoKaiQiang 2015-8-4
 *         添加部分注释
 */
public final class DLManager {

	private static final int THREAD_POOL_SIZE = 32;

	private static DLManager sManager;
	/**
	 * 用于保存正在下载的任务
	 **/
	private static Hashtable<String, DLTask> sTaskDLing;
	private static DBManager sDBManager;

	private ExecutorService mExecutor;
	private Context context;

	private String mFileName;

	public DLManager(Context context) {
		this.context = context;
		this.mExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		sDBManager = DBManager.getInstance();
		sTaskDLing = new Hashtable<>();
	}

	public static DLManager getInstance(Context context) {
		if (null == sManager) {
			sManager = new DLManager(context);
		}
		return sManager;
	}

	public void start(String url, String dirPath, String fileName, DLTaskListener listener) {
		DLPrepare dlPrepare = new DLPrepare(url, dirPath, listener);
		mFileName = fileName;
		mExecutor.execute(dlPrepare);
	}

	public void stop(String url) {
		if (sTaskDLing.containsKey(url)) {
			DLTask task = sTaskDLing.get(url);
			task.setStop(true);
		}
	}

	/**
	 * 停止下载任务并且把所有的任务信息与线程信息删除，下载的文件并不删除
	 *
	 * @param url
	 */
	public void cancel(String url) {
		stop(url);
		if (null != sDBManager.queryTaskInfoByUrl(url)) {
			sDBManager.deleteTaskInfo(url);
			List<ThreadInfo> infos = sDBManager.queryThreadInfos(url);
			if (null != infos && infos.size() != 0) {
				sDBManager.deleteThreadInfos(url);
			}
		}
	}

	/**
	 * <pre>
	 * 下载前的准备工作
	 *
	 * 1.如果有重定向，则解析出真正的下载地址
	 * 2.创建新的文件信息，开启Task完成下载任务
	 *
	 * <pre/>
	 **/
	private class DLPrepare implements Runnable {

		private String url, dirPath;
		private DLTaskListener listener;

		private DLPrepare(String url, String dirPath, DLTaskListener listener) {
			this.url = url;
			this.dirPath = dirPath;
			this.listener = listener;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			try {
				String realUrl = url;
				conn = NetUtil.buildConnection(url);
				conn.setInstanceFollowRedirects(false);
				conn.setRequestProperty(HttpConnPars.REFERER.content, url);
				//如果发生了301或者是302重定向，则获取真实的URL
				if (conn.getResponseCode() == HttpStatus.SC_MOVED_TEMPORARILY ||
						conn.getResponseCode() == HttpStatus.SC_MOVED_PERMANENTLY) {
					realUrl = conn.getHeaderField(HttpConnPars.LOCATION.content);
				}
				if (!sTaskDLing.containsKey(url)) {
					TaskInfo info = sDBManager.queryTaskInfoByUrl(url);

					if (null != listener) listener.onStart(mFileName, url);
					File file = new File(dirPath, mFileName);
					//第一次下载，创建下载文件
					if (null == info || !file.exists()) {
						info = new TaskInfo(createFile(dirPath, mFileName), url, realUrl, 0, 0);
					}
					//开启新的线程执行下载任务
					DLTask task = new DLTask(info, listener);
					mExecutor.execute(task);
				}
			} catch (IOException e) {
				e.printStackTrace();
				listener.onError("DLPrepare Error = " + e.getMessage(), url);
			} finally {
				if (null != conn) {
					conn.disconnect();
				}
			}
		}
	}

	private class DLTask implements Runnable, IDLThreadListener {

		private static final int LENGTH_PER_THREAD = 2097152;

		private TaskInfo info;
		private DLTaskListener mListener;
		/**
		 * 总下载进度，单位是字节
		 */
		private int totalProgress;
		/**
		 * 文件总长度，单位是字节
		 */
		private int fileLength;
		/**
		 * 下载的百分比
		 */
		private int totalProgressIn100;
		private boolean isResume;
		private boolean isStop;
		private boolean isExists;
		private boolean isConnect = true;

		private List<ThreadInfo> mThreadInfos;

		private DLTask(TaskInfo info, DLTaskListener listener) {
			this.info = info;
			this.mListener = listener;
			this.totalProgress = info.progress;
			this.fileLength = info.length;

			//如果之前下载过
			if (null != sDBManager.queryTaskInfoByUrl(info.baseUrl)) {

				//如果下载文件被删除了，就把下载任务信息也删掉，从而重新下载
				if (!info.dlLocalFile.exists()) {
					sDBManager.deleteTaskInfo(info.baseUrl);
					//是否应该把ThreadInfo也删除掉？
					mThreadInfos = sDBManager.queryThreadInfos(info.baseUrl);
					if (null != mThreadInfos && mThreadInfos.size() != 0) {
						sDBManager.deleteThreadInfos(info.baseUrl);
					}
				} else {
					mThreadInfos = sDBManager.queryThreadInfos(info.baseUrl);
					//之前下载过，现在处于暂停状态
					if (null != mThreadInfos && mThreadInfos.size() != 0) {
						isResume = true;
					} else {
						sDBManager.deleteTaskInfo(info.baseUrl);
					}
				}
			}else{
				ILog.d("正在下载中");
			}
		}

		public void setStop(boolean isStop) {
			this.isStop = isStop;
			mListener.onStop(info.baseUrl);
		}

		/**
		 * 1.网络状态判断
		 * 2.文件的大小判断、线程的分配、数据库存储和异常捕获
		 */
		@Override
		public void run() {

			if (NetUtil.getNetWorkType(context) == PublicCons.NetType.INVALID) {
				if (null != mListener)
					mListener.onConnect(PublicCons.NetType.INVALID, "无网络连接");
				isConnect = false;
			} else if (NetUtil.getNetWorkType(context) == PublicCons.NetType.NO_WIFI) {
				if (null != mListener)
					isConnect = mListener.onConnect(PublicCons.NetType.NO_WIFI, "正在使用非WIFI网络下载");
			}

			if (isConnect) {

				sTaskDLing.put(info.baseUrl, this);
				//如果是暂停的，就重新启动线程进行下载
				if (isResume) {
					for (ThreadInfo i : mThreadInfos) {
						mExecutor.execute(new DLThread(i, this));
					}
				} else {
					HttpURLConnection conn = null;
					try {
						conn = NetUtil.buildConnection(info.realUrl);
						//添加此header参数，可以根据返回码判断是否支持断点续传，code=200为不支持断点续传，只能单线程下载；code=206
						// 为支持断点续传，可多线程下载
						conn.setRequestProperty("Range", "bytes=" + 0 + "-" + Integer.MAX_VALUE);

						if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
							fileLength = conn.getContentLength();
							if (info.dlLocalFile.exists() && info.dlLocalFile.length() == fileLength) {
								isExists = true;
								sTaskDLing.remove(info.baseUrl);
								if (null != mListener)
									mListener.onFinish(info.dlLocalFile, info.baseUrl);
							}
							if (!isExists) {
								info.length = fileLength;
								//下载任务添加到数据库
								sDBManager.insertTaskInfo(info);
								int threadSize;
								int length = LENGTH_PER_THREAD;
								if (fileLength <= LENGTH_PER_THREAD) {
									threadSize = 3;
									length = fileLength / threadSize;
								} else {
									threadSize = fileLength / LENGTH_PER_THREAD;
								}

								int remainder = fileLength % length;
								for (int i = 0; i < threadSize; i++) {
									int start = i * length;
									int end = start + length - 1;
									if (i == threadSize - 1) {
										end = start + length + remainder;
									}
									String id = UUID.randomUUID().toString();
									ThreadInfo ti = new ThreadInfo(info.dlLocalFile,
											info.baseUrl, info.realUrl, start, end, id);

									mExecutor.execute(new DLThread(ti, this));
								}
							}
						} else if (conn.getResponseCode() == HttpStatus.SC_OK) {
							//单线程下载
							fileLength = conn.getContentLength();
							if (info.dlLocalFile.exists() && info.dlLocalFile.length() == fileLength) {
								sTaskDLing.remove(info.baseUrl);
								if (null != mListener)
									mListener.onFinish(info.dlLocalFile, info.baseUrl);
							} else {
								ThreadInfo ti = new ThreadInfo(info.dlLocalFile, info.baseUrl,
										info.realUrl, 0, fileLength, UUID.randomUUID().toString());
								mExecutor.execute(new DLThread(ti, this));
							}
						}
					} catch (Exception e) {
						if (null != sDBManager.queryTaskInfoByUrl(info.baseUrl)) {
							info.progress = totalProgress;
							sDBManager.updateTaskInfo(info);
							sTaskDLing.remove(info.baseUrl);
						}
						if (null != mListener) mListener.onError(e.getMessage(), info.baseUrl);
					} finally {
						if (conn != null) {
							conn.disconnect();
						}
					}
				}
			}else{
				if (null != mListener) mListener.onError("no network connection", info.baseUrl);
			}
		}

		@Override
		public void onThreadProgress(int progress) {
			synchronized (this) {
				totalProgress += progress;
				int tmp = (int) (totalProgress * 1.0 / fileLength * 100);
				if (null != mListener && tmp != totalProgressIn100) {
					mListener.onProgress(tmp, info.baseUrl);
					totalProgressIn100 = tmp;
				}
				if (fileLength == totalProgress) {
					sDBManager.deleteTaskInfo(info.baseUrl);
					sTaskDLing.remove(info.baseUrl);
					if (null != mListener) mListener.onFinish(info.dlLocalFile, info.baseUrl);
				}
				if (isStop) {
					info.progress = totalProgress;
					sDBManager.updateTaskInfo(info);
					sTaskDLing.remove(info.baseUrl);
				}
			}
		}


		private class DLThread implements Runnable {

			private ThreadInfo info;
			private IDLThreadListener mListener;
			private int progress;

			public DLThread(ThreadInfo info, IDLThreadListener listener) {
				this.info = info;
				this.mListener = listener;
			}

			@Override
			public void run() {
				HttpURLConnection conn = null;
				RandomAccessFile raf = null;
				InputStream is = null;
				try {
					conn = NetUtil.buildConnection(info.realUrl);
					conn.setRequestProperty("Range", "bytes=" + info.start + "-" + info.end);

					raf = new RandomAccessFile(info.dlLocalFile,
							PublicCons.AccessModes.ACCESS_MODE_RWD);
					if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT) {
						if (!isResume) {
							sDBManager.insertThreadInfo(info);
						}
						is = conn.getInputStream();
						raf.seek(info.start);
						int total = info.end - info.start;
						byte[] b = new byte[1024];
						int len;
						while (!isStop && (len = is.read(b)) != -1) {
							raf.write(b, 0, len);
							progress += len;
							mListener.onThreadProgress(len);
							if (progress >= total) {
								sDBManager.deleteThreadInfoById(info.id);
							}
						}
						if (isStop && null != sDBManager.queryThreadInfoById(info.id)) {
							info.start = info.start + progress;
							sDBManager.updateThreadInfo(info);
						}
					} else if (conn.getResponseCode() == HttpStatus.SC_OK) {
						is = conn.getInputStream();
						raf.seek(info.start);
						byte[] b = new byte[1024];
						int len;
						while (!isStop && (len = is.read(b)) != -1) {
							raf.write(b, 0, len);
							mListener.onThreadProgress(len);
						}
					}
				} catch (Exception e) {
					if (null != sDBManager.queryThreadInfoById(info.id)) {
						info.start = info.start + progress;
						sDBManager.updateThreadInfo(info);
					}
				} finally {
					try {
						if (null != is) {
							is.close();
						}
						if (null != raf) {
							raf.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (null != conn) {
						conn.disconnect();
					}
				}
			}
		}
	}

	public static File createFile(String path, String fileName) {
		File file = new File(makeDir(path), fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static File makeDir(String path) {
		File dir = new File(path);
		if (dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}

}
