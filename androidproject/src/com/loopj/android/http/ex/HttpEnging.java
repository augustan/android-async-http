package com.loopj.android.http.ex;

import java.util.Map;

import android.content.Context;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.model.BaseNetRequest;
import com.loopj.android.http.model.HttpTag;
import com.loopj.android.http.model.IBinaryDataHandler;
import com.loopj.android.http.model.IHttpReponse;
import com.loopj.android.http.model.INetBinaryReponse;
import com.loopj.android.http.model.INetDownloadReponse;
import com.loopj.android.http.utils.ALog;
import com.loopj.android.http.utils.DataDispatchHelper;

public class HttpEnging {
    
    private static HttpEnging engine = null;

    private Context context;
    private boolean isOnlineEnv = false;
    private DataDispatchHelper dispatch;

    private HttpHelper mHttpHelper = null;
    
    // 初始化
    synchronized public static void create(Context context, boolean isOnlineEnv) {
        if (engine == null) {
            engine = new HttpEnging();
            engine.context = context;
            engine.isOnlineEnv = isOnlineEnv;
            engine.dispatch = new DataDispatchHelper();
            ALog.setDebug(ALog.isDebug() && !isOnlineEnv);

            engine.mHttpHelper = new HttpHelper();
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
        ALog.setDebug(debug);
    }

    public static Context getContext() {
        return engine.context;
    }

    public static boolean isOnlineEnv() {
        return engine.isOnlineEnv;
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
