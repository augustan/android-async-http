package com.aug.android.http.exhandle;

import java.io.File;

import org.apache.http.Header;

import com.aug.android.http.ex.HttpEnging;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.IBinaryDataHandler;
import com.aug.android.http.model.IFileDataHandler;
import com.aug.android.http.model.IImageDownloadReponse;

/**
 * 下载文件。字节流不在内存中缓存，直接写入文件
 */
public class ImageDownloadHandler extends DownloadHandler {

    private final IFileDataHandler dataHandler;
    private IImageDownloadReponse response;
    
    public ImageDownloadHandler(BaseNetRequest request, IImageDownloadReponse response, String filePath, IFileDataHandler dataHandler) {
    	super(request, response, filePath);
    	this.response = response;
    	this.dataHandler = dataHandler;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, File file) {
    	super.onSuccess(statusCode, headers, file);
        processBinaryData(file);
    }
    
    private void processBinaryData(File file) {
        if (dataHandler != null) {
	        dataHandler.onDataReceived(request, file);
	        if (dataHandler.isProcessSuccess()) {
	        	nofityDataPostProcessFinished(request, dataHandler);
	        } else {
	            notifyHttpError(new Throwable("process bin data failed"), "");
	        }
    	
        }
    }

    private void nofityDataPostProcessFinished(final BaseNetRequest request, final IFileDataHandler dataHandler) {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onDataPostProcessFinished(request, dataHandler);
            }
        });
    }
}
