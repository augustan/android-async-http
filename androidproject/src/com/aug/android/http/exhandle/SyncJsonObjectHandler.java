
package com.aug.android.http.exhandle;

import android.text.TextUtils;

import com.aug.android.http.lib.TextHttpResponseHandler;
import com.aug.android.http.model.HttpTag;
import com.aug.android.http.utils.ALog;
import com.aug.android.http.utils.HttpUtils;

import org.apache.http.Header;

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
