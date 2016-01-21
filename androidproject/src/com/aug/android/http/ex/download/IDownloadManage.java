package com.aug.android.http.ex.download;

public interface IDownloadManage {
	
	void onTaskStatusChanged(FileDownloadTask task, int lastStatus, int newStatus);
}
