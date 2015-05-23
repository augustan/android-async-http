
package com.aug.android.http.model;




public interface INetJsonObjectReponse extends IHttpReponse {

    /**
     * http 层已经成功，业务逻辑成功
     * @param request
     * @param result
     */
    void onBusinessOK(BaseNetRequest request, Object result);
    /**
     * http 层已经成功，业务逻辑失败
     * @param request
     * @param result
     */
    void onBusinessError(BaseNetRequest request, TopErrorRsp result);

}
