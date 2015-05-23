
package com.aug.android.http.ex;

import com.aug.android.http.exhandle.BinaryDataHandler;
import com.aug.android.http.exhandle.DownloadHandler;
import com.aug.android.http.exhandle.JsonObjectHandler;
import com.aug.android.http.exhandle.SyncJsonObjectHandler;
import com.aug.android.http.exhandle.TextDataHandler;
import com.aug.android.http.lib.AsyncHttpClient;
import com.aug.android.http.lib.AsyncHttpResponseHandler;
import com.aug.android.http.lib.RequestHandle;
import com.aug.android.http.lib.RequestParams;
import com.aug.android.http.lib.SyncHttpClient;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.HttpTag;
import com.aug.android.http.model.IBinaryDataHandler;
import com.aug.android.http.model.IHttpReponse;
import com.aug.android.http.model.INetBinaryReponse;
import com.aug.android.http.model.INetDownloadReponse;
import com.aug.android.http.model.INetJsonObjectReponse;
import com.aug.android.http.model.INetTextReponse;
import com.aug.android.http.utils.ALog;
import com.aug.android.http.utils.HttpUtils;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import java.lang.reflect.Constructor;
import java.util.Map;

public class HttpHelper {

    private SyncHttpClient mSyncHttpClient = new SyncHttpClient(getSchemeRegistry());
    private AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient(getSchemeRegistry());

    /**
     * 下载小的二进制数据。字节流数据会缓存在内存中，结束时回调。 如下载图片
     */
    public RequestHandle getBinaryDataAsync(BaseNetRequest request, INetBinaryReponse response,
            IBinaryDataHandler dataHandler) {
        AsyncHttpResponseHandler responseHandler = new BinaryDataHandler(request, response,
                dataHandler);
        RequestParams requestParams = new RequestParams(request.getParams());

        HttpUtils.dumpHttpRequest(request, requestParams);
        return mAsyncHttpClient.get(request.getUrl(), requestParams, responseHandler);
    }

    /**
     * 下载文件。字节流不在内存中缓存，直接写入文件
     */
    public RequestHandle downloadFile(BaseNetRequest request, INetDownloadReponse response,
            String filePath) {
        AsyncHttpResponseHandler responseHandler = new DownloadHandler(request, response, filePath);
        RequestParams requestParams = new RequestParams(request.getParams());

        HttpUtils.dumpHttpRequest(request, requestParams);
        return mAsyncHttpClient.get(request.getUrl(), requestParams, responseHandler);
    }

    /**
     * 异步post请求
     */
    public RequestHandle sendPostAsyncRequest(BaseNetRequest request, IHttpReponse response) {
        AsyncHttpResponseHandler responseHandler = null;
        if (response instanceof INetJsonObjectReponse) {
            responseHandler = new JsonObjectHandler(request, (INetJsonObjectReponse) response);
        } else if (response instanceof INetTextReponse) {
            responseHandler = new TextDataHandler(request, (INetTextReponse) response);
        } else {
            return null;
        }
        RequestParams requestParams = new RequestParams(request.getParams());

        HttpUtils.dumpHttpRequest(request, requestParams);
        return mAsyncHttpClient.post(request.getUrl(), requestParams, responseHandler);
    }

    /**
     * 异步get请求
     */
    public RequestHandle sendGetAsyncRequest(BaseNetRequest request, IHttpReponse response) {
        AsyncHttpResponseHandler responseHandler = null;
        if (response instanceof INetJsonObjectReponse) {
            responseHandler = new JsonObjectHandler(request, (INetJsonObjectReponse) response);
        } else if (response instanceof INetTextReponse) {
            responseHandler = new TextDataHandler(request, (INetTextReponse) response);
        } else {
            return null;
        }
        RequestParams requestParams = new RequestParams(request.getParams());
        HttpUtils.dumpHttpRequest(request, requestParams);
        return mAsyncHttpClient.get(request.getUrl(), requestParams, responseHandler);
    }

    /**
     * 同步post请求
     */
    public Object sendPostSyncRequest(HttpTag tag, String url, Map<String, String> params) {
        HttpUtils.dumpHttpRequest(url, params);

        BaseNetRequest fakeRequest = new BaseNetRequest();
        fakeRequest.setTag(tag);

        RequestParams requestParams = new RequestParams(params);
        SyncJsonObjectHandler handler = new SyncJsonObjectHandler(tag);
        mSyncHttpClient.post(url, requestParams, handler);
        return handler.getResultJsonObject();
    }

    public void release() {
        mSyncHttpClient = null;
        mAsyncHttpClient = null;
    }

    /**
     * 不使用SSL检验
     * 也可以不用这个方法，使用库自带的ssl fanctory初始化  SyncHttpClient(true, 80, 443)
     */
    private SchemeRegistry getSchemeRegistry() {
        SSLSocketFactory apacheSSLSocketFactory = null;
        if (HttpEnging.isNeedSSLAuth()) {
            apacheSSLSocketFactory = SSLSocketFactory.getSocketFactory();
        } else {
            X509HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            javax.net.ssl.SSLSocketFactory javaSSLSocketFactory = new HttpSSLSocketFactory();

            Constructor<SSLSocketFactory> cons;

            // 创建apache SSL Socket Factory实例
            try {
                cons = SSLSocketFactory.class.getConstructor(javax.net.ssl.SSLSocketFactory.class);
                apacheSSLSocketFactory = cons.newInstance(javaSSLSocketFactory);
                apacheSSLSocketFactory.setHostnameVerifier(hostnameVerifier);

            } catch (Exception e) {
                ALog.e("socket exception = " + e);
            }
        }

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", apacheSSLSocketFactory, 443));
        return schemeRegistry;
    }
}
