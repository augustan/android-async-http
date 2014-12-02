package com.loopj.android.http.exhandle;

import com.google.gson.Gson;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.ex.HttpEnging;
import com.loopj.android.http.model.BaseNetRequest;
import com.loopj.android.http.model.INetJsonObjectReponse;
import com.loopj.android.http.model.TopErrorRsp;
import com.loopj.android.http.utils.ALog;
import com.loopj.android.http.utils.HttpUtils;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;

/**
 * 把返回的字符串用设置的request.getTag()格式化成类对象
 */
public class JsonObjectHandler extends BinaryHttpResponseHandler {

    private static final String TopErrorTag = TopErrorRsp.TOP_RESPONSE_BASE_TAG_ERROR_RESPONSE;
    // 猜测，如果返回错误标识，在前80个字符中一定有错误标识的tag
    private static final int ERROR_PREFIX_LENGTH = 80 + TopErrorTag.length();
    
    private final BaseNetRequest request;
    private final INetJsonObjectReponse response;
    
    public JsonObjectHandler(BaseNetRequest request, INetJsonObjectReponse response) {
        this.request = request;
        this.response = response;
    }
    
    @Override
    public void onCancel() {
        super.onCancel();
        notifyHttpCancelled();
    }
    
    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
        if (response != null) {
            if (binaryData == null) {
                return;
            }
            
            // TODO [ajj] : content 可预先解密
            
            String content = getResponseString(binaryData);
            String json = content.trim();
            HttpUtils.dumpJSONString(request.getTag(), json);

            boolean isBussnissSuccess = testBusinessSuccess(json);
            if (isBussnissSuccess) {
                Object jsonResult = null;
                try {
                    jsonResult = HttpEnging.getDispatch().dispatch(request, json);
                    if (jsonResult != null) {
                        notifyBusinessOK(jsonResult);
                    }
                } catch (Exception e) {
                    ALog.e(e.toString());
                    notifyHttpError(e, content);
                    return;
                }
            } else {
                TopErrorRsp resp = null;
                try {
                    resp = new Gson().fromJson(content, TopErrorRsp.class);
                } catch (Exception e) {
                    ALog.e(e.toString());
                    notifyHttpError(e, content);
                    return;
                }
                notifyBusinessError(resp);
            }
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
        String content = getResponseString(binaryData);
        notifyHttpError(error, content);
    }

    /**
     * 预判断业务逻辑是否返回的失败
     * 
     * @param json
     * @return
     */
    private static boolean testBusinessSuccess(String json) {
        boolean success = true;
        if (json != null) {
            int i = -1;
            if (json.length() > ERROR_PREFIX_LENGTH) {
                i = json.substring(0, ERROR_PREFIX_LENGTH).indexOf(TopErrorTag);
            } else {
                i = json.indexOf(TopErrorTag);
            }
            success = i < 0;
        }
        return success;
    }
    
    private static String getResponseString(byte[] data) {
        try {
            String toReturn = (data == null) ? null : new String(data, "UTF-8");
            if (toReturn != null && toReturn.startsWith(UTF8_BOM)) {
                return toReturn.substring(1);
            }
            return toReturn;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private void notifyBusinessOK(final Object result) {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onBusinessOK(request, result);
            }
        });
    }

    private void notifyBusinessError(final TopErrorRsp result) {
        HttpEnging.getDispatch().runOnUIThread(new Runnable() {
            @Override
            public void run() {
                response.onBusinessError(request, result);
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
