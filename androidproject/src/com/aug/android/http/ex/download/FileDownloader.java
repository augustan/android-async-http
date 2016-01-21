package com.aug.android.http.ex.download;

import java.util.HashMap;
import java.util.Map;

import com.aug.android.http.model.INetDownloadReponse;
import com.aug.android.utils.SingleHandler;

public class FileDownloader implements IDownloadManage {
	
	private static FileDownloader instance = null;
	
	private int limitRunningTaskCount = 3;
	private HashMap<String, DownloadTask> downloadTasksQueue = new HashMap<String, DownloadTask>();
	private HashMap<String, DownloadTask> runningTasks = new HashMap<String, DownloadTask>();
	
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
	
	public void download(String url, INetDownloadReponse listener) {
		DownloadTask task = new DownloadTask(url, listener, this);
		synchronized (downloadTasksQueue) {
			downloadTasksQueue.put(task.getTaskKey(), task);
		}
		doSchedule();
	}
	
	private void doSchedule() {
		synchronized (runningTasks) {
			int cnt = runningTasks.size();
			if (cnt < limitRunningTaskCount) {
				DownloadTask task = getNextTask(downloadTasksQueue, true);
				if (task != null) {
					runningTasks.put(task.getTaskKey(), task);
					task.run();
				}
			}
		}
	}
	
	private DownloadTask getNextTask(HashMap<String, DownloadTask> list, boolean remove) {
		synchronized (list) {
			long newAddTime = 0;
			DownloadTask newTask = null;
			for (Map.Entry<String, DownloadTask> item : list.entrySet()) {
				DownloadTask task = item.getValue();
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

	@Override
	public void onTaskStatusChanged(final DownloadTask task, int lastStatus, int newStatus) {
		if (newStatus == DownloadTask.DL_TASK_STATUS_FINISH || 
				newStatus == DownloadTask.DL_TASK_STATUS_CANCELED || 
				newStatus == DownloadTask.DL_TASK_STATUS_ERROR) {
			
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
