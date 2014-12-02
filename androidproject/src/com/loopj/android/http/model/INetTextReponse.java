
package com.loopj.android.http.model;




public interface INetTextReponse extends IHttpReponse {

    /**
     * http 层已经成功，业务逻辑成功
     * @param request
     * @param result
     */
    void onBusinessOK(BaseNetRequest request, String result);

}
