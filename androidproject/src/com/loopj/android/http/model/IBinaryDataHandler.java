package com.loopj.android.http.model;

/**
 * 下载完二进制数据后，处理数据的类。改接口在非UI线程中调用。处理完成后，会通知UI线程
 * @author jianjun.ajj
 *
 */
public interface IBinaryDataHandler {

    /**
     * 完成接收数据，做后续处理
     * @param request
     * @param data
     */
    void onDataReceived(BaseNetRequest request, byte [] data);
    
    boolean processSuccess();
    
    Object getDecodeData();
}
