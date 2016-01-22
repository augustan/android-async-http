package com.aug.android.http.ex;

import java.util.Map;

import android.content.Context;

import com.aug.android.http.lib.RequestHandle;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.HttpTag;
import com.aug.android.http.model.IBinaryDataHandler;
import com.aug.android.http.model.IFileDataHandler;
import com.aug.android.http.model.IHttpReponse;
import com.aug.android.http.model.IImageDownloadReponse;
import com.aug.android.http.model.INetBinaryReponse;
import com.aug.android.http.model.INetDownloadReponse;
import com.aug.android.http.utils.DataDispatchHelper;
import com.aug.android.http.utils.LogUtils;
import com.aug.android.utils.StorageUtil;

public class HttpEnging {
    
    private static HttpEnging engine = null;

    private Context context;
    private static boolean needSSLAuth = true; // HTTPS使用SSL验证
    private DataDispatchHelper dispatch;

    private HttpHelper mHttpHelper = null;
    
    // 初始化
    synchronized public static void create(Context context) {
        if (engine == null) {
            engine = new HttpEnging();
            engine.context = context;
            engine.dispatch = new DataDispatchHelper();

            engine.mHttpHelper = new HttpHelper();
            
            StorageUtil.init(context);
        }
    }
    
    synchronized public static void release() {
        if (engine != null) {
            if (engine.mHttpHelper != null) {
                engine.mHttpHelper.release();
            }
            engine = null;
        }
    }
    
    public static void setDebug(boolean debug) {
        LogUtils.setDebug(debug);
    }

    public static Context getContext() {
        return engine.context;
    }

    synchronized public static void setNeedSSLAuth(boolean needSSLAuth) {
        if (engine != null) {
            if (HttpEnging.needSSLAuth != needSSLAuth) {
                if (engine.mHttpHelper != null) {
                    engine.mHttpHelper.release();
                }
                HttpEnging.needSSLAuth = needSSLAuth;
                engine.mHttpHelper = new HttpHelper();
            }
        } else {
            HttpEnging.needSSLAuth = needSSLAuth;
        }
    }
    
    public static boolean isNeedSSLAuth() {
        return HttpEnging.needSSLAuth;
    }

    public static DataDispatchHelper getDispatch() {
        return engine.dispatch;
    }
    
    public static void getBinaryDataAsync(BaseNetRequest request, INetBinaryReponse response, IBinaryDataHandler dataHandler) {
        engine.mHttpHelper.getBinaryDataAsync(request, response, dataHandler);
    }

    public static RequestHandle downloadFile(BaseNetRequest request, INetDownloadReponse response, String filePath) {
        return engine.mHttpHelper.downloadFile(request, response, filePath);
    }

    public static RequestHandle downloadImage(BaseNetRequest request, IImageDownloadReponse response, String filePath, IFileDataHandler dataHandler) {
        return engine.mHttpHelper.downloadImage(request, response, filePath, dataHandler);
    }
    
    public static void sendPostAsync(BaseNetRequest request, IHttpReponse response) {
        engine.mHttpHelper.sendPostAsyncRequest(request, response);
    }
    
    public static void sendGetAsync(BaseNetRequest request, IHttpReponse response) {
        engine.mHttpHelper.sendGetAsyncRequest(request, response);
    }
    
    public static Object sendPostSync(HttpTag tag, String url, Map<String, String> params) {
        return engine.mHttpHelper.sendPostSyncRequest(tag, url, params);
    }
}
