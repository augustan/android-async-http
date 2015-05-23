
package com.aug.android.http.model;

import java.io.File;





public interface INetDownloadReponse extends IHttpReponse {
    
    /**
     * 完成接收数据
     */
    void onFileSaved(BaseNetRequest request, File file);

    /**
     * 只需要处理进度。进度到100%后，会有success的回调
     * 
     * @param request
     * @param receivedLength    当前总共收到的字节数
     * @param totalLength       数据总长度。如果<=0，说明长度未知
     */
    void onDataProgress(BaseNetRequest request, long receivedLength, long totalLength);
}
