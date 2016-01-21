package com.aug.android.http.exhandle;

import java.io.File;

import org.apache.http.Header;

import com.aug.android.http.ex.HttpEnging;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.IBinaryDataHandler;
import com.aug.android.http.model.IImageDownloadReponse;
import com.aug.android.utils.FileUtil;

/**
 * 下载文件。字节流不在内存中缓存，直接写入文件
 */
public class ImageDownloadHandler extends DownloadHandler {

    private final IBinaryDataHandler dataHandler;
    private IImageDownloadReponse response;
    
    public ImageDownloadHandler(BaseNetRequest request, IImageDownloadReponse response, String filePath, IBinaryDataHandler dataHandler) {
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
        	byte [] binaryData = FileUtil.getBytesFromFile(file);
        	if (binaryData != null) {
		        dataHandler.onDataReceived(request, binaryData);
		        if (dataHandler.isProcessSuccess()) {
		        	nofityDataPostProcessFinished(request, dataHandler);
		        } else {
		            notifyHttpError(new Throwable("process bin data failed"), "");
		        }
        	} else {
        		notifyHttpError(new Throwable("read file failed"), "");
        	}
        }
    }

    private void nofityDataPostProcessFinished(final BaseNetRequest request, final IBinaryDataHandler dataHandler) {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onDataPostProcessFinished(request, dataHandler);
            }
        });
    }
}
