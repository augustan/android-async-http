package com.aug.android.http.ex.download;

import java.io.File;

import com.aug.android.http.ex.HttpEnging;
import com.aug.android.http.lib.RequestHandle;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.INetDownloadReponse;
import com.aug.android.utils.StorageUtil;

public class DownloadTask implements Runnable {

	public static final int DL_TASK_STATUS_ADDED = 0;
	public static final int DL_TASK_STATUS_RUNNING = 1;
	public static final int DL_TASK_STATUS_FINISH = 2;
	public static final int DL_TASK_STATUS_CANCELED = 3;
	public static final int DL_TASK_STATUS_ERROR = 4;

	private String url;
	private INetDownloadReponse listener;
	private IDownloadManage manager;
	
	private int priority = 1;
	private long addTime = 0;
	private int status = DL_TASK_STATUS_ADDED;
	private String fileKey = "";
	private String filePath = "";
	
	private RequestHandle runningRequest;
	
	public DownloadTask(String url, INetDownloadReponse listener, IDownloadManage manager) {
		this.url = url;
		this.listener = listener;
		this.manager = manager;
		addTime = System.currentTimeMillis();
		filePath = StorageUtil.getCacheFilePath(url);
		fileKey = StorageUtil.getFileKey(url);
	}
	
	public long getAddTime() {
		return addTime;
	}
	
	public String getTaskKey() {
		return fileKey;
	}

	@Override
	public void run() {
		BaseNetRequest request = new BaseNetRequest(url);
		runningRequest = HttpEnging.downloadFile(request, new INetDownloadReponse() {

            @Override
            public void onHttpRecvError(BaseNetRequest request, Throwable error, String content) {
            	if (listener != null) {
            		listener.onHttpRecvError(request, error, content);
            	}
            	notifyStatusChanged(DL_TASK_STATUS_ERROR);
            }

            @Override
            public void onHttpRecvCancelled(BaseNetRequest request) {
            	if (listener != null) {
            		listener.onHttpRecvCancelled(request);
            	}
            	notifyStatusChanged(DL_TASK_STATUS_CANCELED);
            }

            @Override
            public void onFileSaved(BaseNetRequest request, File file) {
            	if (listener != null) {
            		listener.onFileSaved(request, file);
            	}
        		notifyStatusChanged(DL_TASK_STATUS_FINISH);
            }

            @Override
            public void onDataProgress(BaseNetRequest request, long receivedLength, long totalLength) {
            	if (listener != null) {
            		listener.onDataProgress(request, receivedLength, totalLength);
            	}
//                int p = (int) receivedLength * 100 / (int)totalLength;
//                progress.setProgress(p);
//                
//                float per = (float)receivedLength * 100 / (float)totalLength;
//                String perStr = String.format("%d / %d  %.02f%%", receivedLength, totalLength, per);
//                progress_text.setText(perStr);
            }
        }, filePath);

		notifyStatusChanged(DL_TASK_STATUS_RUNNING);
	}
	
	private void notifyStatusChanged(int newStatsu) {
		int last = status;
		status = newStatsu;
		if (manager != null) {
			manager.onTaskStatusChanged(this, last, newStatsu);
		}
	}

}
