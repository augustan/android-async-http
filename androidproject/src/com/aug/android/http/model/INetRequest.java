
package com.aug.android.http.model;

import java.util.Map;

public interface INetRequest {

    public String getUrl();

    public Map<String, String> getParams();

    public void addParams(Map<String, String> data);

    public void addParam(String key, String value);

    public Object getValue(String key);

}
