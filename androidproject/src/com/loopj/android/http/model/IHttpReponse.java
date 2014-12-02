
package com.loopj.android.http.model;





public interface IHttpReponse {

    /**
     * http 层的错误，发送过程中发送网络错误
     * @param request
     * @param error
     * @param content
     */
    void onHttpRecvError(BaseNetRequest request, Throwable error, String content);
    /**
     * http 层的错误，网络请求被取消
     * @param request
     */
    void onHttpRecvCancelled(BaseNetRequest request);
}
