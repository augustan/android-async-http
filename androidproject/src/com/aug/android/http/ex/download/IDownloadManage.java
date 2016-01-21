package com.aug.android.http.ex.download;

public interface IDownloadManage {
	
	void onTaskStatusChanged(DownloadTask task, int lastStatus, int newStatus);
}
