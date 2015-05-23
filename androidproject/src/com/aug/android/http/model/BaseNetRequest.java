
package com.aug.android.http.model;

import java.util.HashMap;
import java.util.Map;

public class BaseNetRequest implements INetRequest {

    private String mUrl;
    private String ecode;

    private Map<String, String> mParams;
    private boolean needLoadProgress = false;
    
    private HttpTag tag;

    public BaseNetRequest() {
        mParams = new HashMap<String, String>();
    }

    public BaseNetRequest(String url, Map<String, String> Params) {
        mUrl = url;
        if (Params != null) {
            mParams = Params;
        } else {
            mParams = new HashMap<String, String>();
        }
    }

    public BaseNetRequest(String url) {
        mUrl = url;
        mParams = new HashMap<String, String>();
    }

    public String getUrl() {
        return mUrl;
    }

    public boolean isNeedLoadProgress() {
        return needLoadProgress;
    }

    public void setNeedLoadProgress(boolean needLoadProgress) {
        this.needLoadProgress = needLoadProgress;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public void setUrl(String url) {
        this.mUrl = url;
    }

    public void setParams(Map<String, String> Params) {
        this.mParams = Params;
    }

    @Override
    public void addParams(Map<String, String> data) {
        mParams.putAll(data);
    }

    @Override
    public void addParam(String key, String value) {
        mParams.put(key, value);
    }

    public Object getValue(String key) {
        return mParams != null ? mParams.get(key) : "";
    }

    /**
     * @return the ecode
     */
    public String getEcode() {
        return ecode;
    }

    /**
     * @param ecode the ecode to set
     */
    public void setEcode(String ecode) {
        this.ecode = ecode;
    }

    public HttpTag getTag() {
        return tag;
    }

    public void setTag(HttpTag tag) {
        this.tag = tag;
    }
}
