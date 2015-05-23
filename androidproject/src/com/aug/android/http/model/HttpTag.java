package com.aug.android.http.model;

import com.aug.android.http.utils.ALog;
import com.google.gson.Gson;

import java.util.concurrent.atomic.AtomicInteger;


public class HttpTag {

    private static AtomicInteger g_tag_id = new AtomicInteger(0);
    public static HttpTag TRANSPARENT_TAG = new TransparentTag();
    
    private final int tagId;
    private final String tagName;
    private final Class<?> clazz;
    
    /**
     * 每一个http接口对应一个tag
     * @param tagName 异步回调时，通过tagName区分是哪个接口的回调
     * @param clazz   用哪个class解析回调返回的string. See Object parseJson(String json)
     */
    public HttpTag(String tagName, Class<?> clazz) {
        this.tagId = g_tag_id.addAndGet(1);
        this.tagName = tagName;
        this.clazz = clazz;
        ALog.i("http_tag_init", tagName + " = " + tagId);
    }
    
    public int getTagId() {
        return tagId;
    }
    
    public String getTagName() {
        return tagName;
    }

    public Class<?> getClazz() {
        return clazz;
    }
    
    public Object parseJson(String json) throws Exception {
        Object result = null;
        if (clazz == null) {
            throw new Exception("Has not set any class to receive the data. check the defination of HttpTag: " + tagName);
        } else {
            try {
                result = new Gson().fromJson(json, clazz);
                if (result instanceof TopErrorRsp) {
                    result = ((TopErrorRsp) result).onPostParse();
                }
            } catch (Exception e) {
                ALog.e(e.toString());
                throw e;
            }
        }
        return result;
    }
}
