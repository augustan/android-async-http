
package com.loopj.android.http.exhandle;

import org.apache.http.Header;

import android.text.TextUtils;

import com.loopj.android.http.TextHttpResponseHandler;
import com.loopj.android.http.model.HttpTag;
import com.loopj.android.http.utils.ALog;
import com.loopj.android.http.utils.HttpUtils;

public class SyncJsonObjectHandler extends TextHttpResponseHandler {

    private HttpTag tag;
    protected Object result = null;

    public SyncJsonObjectHandler(HttpTag tag) {
        this.tag = tag;
    }

    public Object getResultJsonObject() {
        return result;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString,
            Throwable throwable) {
        result = throwable.toString();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        HttpUtils.dumpJSONString(tag, responseString);

        if (!TextUtils.isEmpty(responseString)) {
            try {
                result = tag.parseJson(responseString);
            } catch (Exception e) {
                ALog.e(e.toString());
            }
        }
    }

}
