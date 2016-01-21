package com.aug.android.http.model;

import java.io.File;

/**
 * 下载完二进制数据后，处理数据的类。该接口在非UI线程中调用。处理完成后，会通知UI线程
 * @author jianjun.ajj
 *
 */
public interface IFileDataHandler {

    /**
     * 完成接收数据，做后续处理
     * @param request
     * @param file
     */
    void onDataReceived(BaseNetRequest request, File file);
    
    /**
     * 处理接收到的数据是否成功
     * @return
     */
    boolean isProcessSuccess();
    
    /**
     * 获取处理后的数据
     * @return
     */
    Object getDecodeData();
}
