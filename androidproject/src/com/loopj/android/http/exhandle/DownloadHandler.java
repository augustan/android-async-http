package com.loopj.android.http.exhandle;

import android.util.Log;

import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;
import com.loopj.android.http.ex.HttpEnging;
import com.loopj.android.http.model.BaseNetRequest;
import com.loopj.android.http.model.INetDownloadReponse;

import org.apache.http.Header;

import java.io.File;

/**
 * 下载文件。字节流不在内存中缓存，直接写入文件
 */
public class DownloadHandler extends RangeFileAsyncHttpResponseHandler {

    private final BaseNetRequest request;
    private final INetDownloadReponse response;
    private final String filePath;
    
    private File savedFile = null;
    
    public DownloadHandler(BaseNetRequest request, INetDownloadReponse response, String filePath) {
        super(new File(filePath));
        this.request = request;
        this.response = response;
        this.filePath = filePath;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    @Override
    public void onCancel() {
        super.onCancel();
        notifyHttpCancelled();
    }
    
    @Override
    public void onProgress(int bytesWritten, int totalSize) {
        if (response != null) {
            notifyRecvProgress(bytesWritten, totalSize);
            Log.e("down_load", String.format("%d / %d", bytesWritten, totalSize));
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
        notifyHttpError(throwable, "");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, File file) {
        savedFile = file;
        notifyDownloadFinish(savedFile);
    }

    private void notifyHttpCancelled() {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onHttpRecvCancelled(request);
            }
        });
    }

    private void notifyHttpError(final Throwable error, final String content) {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onHttpRecvError(request, error, content);
            }
        });
    }

    private void notifyDownloadFinish(final File file) {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onFileSaved(request, file);
            }
        });
    }
    
    private void notifyRecvProgress(final long receivedLength, final long totalLength) {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onDataProgress(request, receivedLength, totalLength);
            }
        });
    }
}
