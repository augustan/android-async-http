
package com.aug.android.http.model;





public interface INetBinaryReponse extends IHttpReponse {
    
    /**
     * 完成接收数据
     * @param request
     * @param data
     */
    void onDataReceived(BaseNetRequest request, IBinaryDataHandler dataHandler);

    /**
     * 只需要处理进度。进度到100%后，会有success的回调
     * 
     * @param request
     * @param receivedLength    当前总共收到的字节数
     * @param totalLength       数据总长度。如果<=0，说明长度未知
     */
    void onDataProgress(BaseNetRequest request, long receivedLength, long totalLength);
//    void onDataProgress(BaseNetRequest request, long receivedLength, long totalLength, byte [] newData);
}
