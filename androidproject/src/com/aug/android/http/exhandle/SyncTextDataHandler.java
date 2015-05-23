package com.aug.android.http.exhandle;

import org.apache.http.Header;

import com.aug.android.http.lib.TextHttpResponseHandler;

public class SyncTextDataHandler extends TextHttpResponseHandler {

    protected String result = "";
    
    public String getResultString() {
        return result;
    }
    
    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString,
            Throwable throwable) {
        result = responseString;        
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        result = responseString;
    }

}
