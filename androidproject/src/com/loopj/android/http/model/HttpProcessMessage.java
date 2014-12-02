package com.loopj.android.http.model;

public class HttpProcessMessage {

    public int what;
    public Object[] obj;
    
    private HttpProcessMessage(int responseMessageId, Object[] responseMessageData) {
        what = responseMessageId;
        obj = responseMessageData;
    }
    
    public static HttpProcessMessage obtain(int responseMessageId, Object[] responseMessageData) {
        return new HttpProcessMessage(responseMessageId, responseMessageData);
    }


}
