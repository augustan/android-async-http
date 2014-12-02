package com.loopj.android.http.exhandle;

import org.apache.http.Header;

import com.loopj.android.http.TextHttpResponseHandler;

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
