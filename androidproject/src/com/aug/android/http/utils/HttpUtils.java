package com.aug.android.http.utils;

import com.aug.android.http.lib.AsyncHttpClient;
import com.aug.android.http.lib.RequestParams;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.HttpTag;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;


public class HttpUtils {

    private static int jsonIndex = 1000;
    private static final int JSON_MAX_INDEX = 10000;
    
    public static void dumpHttpRequest(BaseNetRequest request, RequestParams requestParams) {
        if (ALog.isDebug()) {
            String str = AsyncHttpClient.getUrlWithQueryString(true, request.getUrl(), requestParams);
            try {
                str = URLDecoder.decode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            ALog.i("HTTP", String.format("[ASYNC] %s", str));
        }
    }
    
    public static void dumpHttpRequest(String url, Map<String, String> params) {
        if (ALog.isDebug()) {
            RequestParams requestParams = new RequestParams(params);
            String str = AsyncHttpClient.getUrlWithQueryString(true, url, requestParams);
            try {
                str = URLDecoder.decode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            ALog.i("HTTP", String.format("[SYNC] %s", str));
        }
    }

    public static void dumpJSONString(HttpTag tag, String json) {
        if (ALog.isDebug()) {
            jsonIndex++;
            if (jsonIndex >= JSON_MAX_INDEX) {
                jsonIndex = 0;
            }
            ALog.i("JSON", String.format("[%d] [%s] JSON数据  = %s", jsonIndex, tag.getTagName(), json));
        }
    }
}
