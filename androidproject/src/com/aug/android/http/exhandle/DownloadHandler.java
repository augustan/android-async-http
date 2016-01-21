package com.aug.android.http.exhandle;

import java.io.File;

import org.apache.http.Header;

import com.aug.android.http.ex.HttpEnging;
import com.aug.android.http.lib.RangeFileAsyncHttpResponseHandler;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.INetDownloadReponse;
import com.aug.android.utils.StringUtil;

/**
 * 下载文件。字节流不在内存中缓存，直接写入文件
 */
public class DownloadHandler extends RangeFileAsyncHttpResponseHandler {

    protected final BaseNetRequest request;
    protected final INetDownloadReponse response;
    protected final String filePath;
    
    protected File savedFile = null;
    
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
//            LogUtils.v("down_load", String.format("%d / %d", bytesWritten, totalSize));
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
        notifyHttpError(throwable, "");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, File file) {
    	String path = file.getPath();
    	int index = path.lastIndexOf(StringUtil.TMP_FILE_POST_FIX);
    	if (index + StringUtil.TMP_FILE_POST_FIX.length() == path.length()) {
    		path = path.substring(0, index);
    		savedFile = new File(path);
    		if (savedFile.exists()) {
    			savedFile.delete();
    		}
    		if (!file.renameTo(savedFile)) {
        		savedFile = file;
    		}
    	} else {
    		savedFile = file;
    	}
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

    protected void notifyHttpError(final Throwable error, final String content) {
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
