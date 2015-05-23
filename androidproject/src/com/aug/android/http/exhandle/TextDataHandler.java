package com.aug.android.http.exhandle;

import com.aug.android.http.ex.HttpEnging;
import com.aug.android.http.lib.TextHttpResponseHandler;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.INetTextReponse;

import org.apache.http.Header;

/**
 * 返回纯文本形式的结果，如拉取html
 */
public class TextDataHandler extends TextHttpResponseHandler {

    private final BaseNetRequest request;
    private final INetTextReponse response;
    
    public TextDataHandler(BaseNetRequest request, INetTextReponse response) {
        this.request = request;
        this.response = response;
    }
    
    @Override
    public void onCancel() {
        super.onCancel();
        notifyHttpCancelled();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString,
            Throwable error) {
        notifyHttpError(error, responseString);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        notifyBusinessOK(responseString);
    }

    private void notifyBusinessOK(final String result) {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onBusinessOK(request, result);
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
}
