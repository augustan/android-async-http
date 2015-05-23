package com.aug.android.http.exhandle;

import com.aug.android.http.ex.HttpEnging;
import com.aug.android.http.lib.BinaryHttpResponseHandler;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.IBinaryDataHandler;
import com.aug.android.http.model.INetBinaryReponse;

import org.apache.http.Header;

/**
 * 下载小的二进制数据。字节流数据会缓存在内存中，结束时回调。 如下载图片
 */
public class BinaryDataHandler extends BinaryHttpResponseHandler {

    private final BaseNetRequest request;
    private final INetBinaryReponse response;
    private final IBinaryDataHandler dataHandler;
    
    public BinaryDataHandler(BaseNetRequest request, INetBinaryReponse response, IBinaryDataHandler dataHandler) {
        this.request = request;
        this.response = response;
        this.dataHandler = dataHandler;
    }
    
    @Override
    public void onCancel() {
        super.onCancel();
        if (response != null) {
            notifyHttpCancelled();
        }
    }
    
    @Override
    public void onProgress(int bytesWritten, int totalSize) {
        if (response != null) {
            notifyRecvProgress(bytesWritten, totalSize);
        }
    }
    
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
        if (response != null && dataHandler != null) {
            if (binaryData == null) {
                return;
            }

            dataHandler.onDataReceived(request, binaryData);
            if (dataHandler.processSuccess()) {
                nofityDataReceived(request, dataHandler);
            } else {
                notifyHttpError(new Throwable("process bin data failed"), "");
            }
        
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
        if (response != null) {
            notifyHttpError(error, "");
        }
    }

    private void nofityDataReceived(final BaseNetRequest request, final IBinaryDataHandler dataHandler) {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onDataReceived(request, dataHandler);
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

    private void notifyHttpCancelled() {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onHttpRecvCancelled(request);
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
