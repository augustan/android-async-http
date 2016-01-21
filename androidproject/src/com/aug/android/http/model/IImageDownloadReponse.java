
package com.aug.android.http.model;


public interface IImageDownloadReponse extends INetDownloadReponse {
    
    /**
     * 完成对接收数据的后期处理
     */
    void onDataPostProcessFinished(BaseNetRequest request, IFileDataHandler dataHandler);
}
