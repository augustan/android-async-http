package com.aug.android.http.ex.download;

import java.util.HashMap;
import java.util.Map;

import com.aug.android.http.model.IImageDownloadReponse;
import com.aug.android.http.model.INetDownloadReponse;
import com.aug.android.http.utils.LogUtils;
import com.aug.android.utils.SingleHandler;

public class FileDownloader implements IDownloadManage {

	private static final String TAG = "fldl";
	private static FileDownloader instance = null;

	private int limitRunningTaskCount = 3;
	private HashMap<String, FileDownloadTask> downloadTasksQueue = new HashMap<String, FileDownloadTask>();
	private HashMap<String, FileDownloadTask> runningTasks = new HashMap<String, FileDownloadTask>();

	public static FileDownloader getInstance() {
		if (instance == null) {
			synchronized (FileDownloader.class) {
				if (instance == null) {
					instance = new FileDownloader();
				}
			}
		}
		return instance;
	}

	private FileDownloader() {
	}

	public void downloadFile(String url, INetDownloadReponse listener) {
		FileDownloadTask task = new FileDownloadTask(url, listener, this);
		synchronized (downloadTasksQueue) {
			downloadTasksQueue.put(task.getTaskKey(), task);
			LogUtils.v(TAG, "ADD task " + task.getTaskKey());
		}
		doSchedule();
	}

	public void downloadImage(String url, IImageDownloadReponse listener) {
		ImageDownloadTask task = new ImageDownloadTask(url, listener, this);
		synchronized (downloadTasksQueue) {
			downloadTasksQueue.put(task.getTaskKey(), task);
			LogUtils.v(TAG, "ADD task " + task.getTaskKey());
		}
		doSchedule();
	}

	private void doSchedule() {
		synchronized (runningTasks) {
			int cnt = runningTasks.size();
			if (cnt < limitRunningTaskCount) {
				FileDownloadTask task = getNextTask(downloadTasksQueue, true);
				if (task != null) {
					runningTasks.put(task.getTaskKey(), task);
					SingleHandler.getInstance(false).post(task);
				}
			}
		}
	}

	private FileDownloadTask getNextTask(HashMap<String, FileDownloadTask> list,
			boolean remove) {
		synchronized (list) {
			long newAddTime = 0;
			FileDownloadTask newTask = null;
			for (Map.Entry<String, FileDownloadTask> item : list.entrySet()) {
				FileDownloadTask task = item.getValue();
				if (task.getAddTime() > newAddTime) {
					newAddTime = task.getAddTime();
					newTask = task;
				}
			}
			if (remove && newTask != null) {
				list.remove(newTask.getTaskKey());
			}
			return newTask;
		}
	}

	private String getStatusStr(int status) {
		String str = "";
		switch (status) {
		case FileDownloadTask.DL_TASK_STATUS_ADDED:
			str = "ADD";
			break;
		case FileDownloadTask.DL_TASK_STATUS_RUNNING:
			str = "RUN";
			break;
		case FileDownloadTask.DL_TASK_STATUS_FINISH:
			str = "FINISH";
			break;
		case FileDownloadTask.DL_TASK_STATUS_CANCELED:
			str = "CALCEL";
			break;
		case FileDownloadTask.DL_TASK_STATUS_ERROR:
			str = "ERROR";
			break;
		case FileDownloadTask.DL_TASK_STATUS_DECODE_SUCCESS:
			str = "DECODE_SUCCESS";
			break;
		case FileDownloadTask.DL_TASK_STATUS_DECODE_FAIL:
			str = "DECODE_FAIL";
			break;
		}
		return str;
	}

	@Override
	public void onTaskStatusChanged(final FileDownloadTask task, int lastStatus,
			int newStatus) {
		LogUtils.v(TAG, getStatusStr(newStatus) + " task " + task.getTaskKey());
		
		if (newStatus == FileDownloadTask.DL_TASK_STATUS_FINISH
				|| newStatus == FileDownloadTask.DL_TASK_STATUS_CANCELED
				|| newStatus == FileDownloadTask.DL_TASK_STATUS_ERROR) {

			SingleHandler.getInstance(false).post(new Runnable() {

				@Override
				public void run() {
					synchronized (runningTasks) {
						runningTasks.remove(task.getTaskKey());
						doSchedule();
					}
				}
			});
		}
	}
}
