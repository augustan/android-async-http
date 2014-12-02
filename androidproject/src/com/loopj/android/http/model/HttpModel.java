package com.loopj.android.http.model;

import com.google.gson.Gson;

public class HttpModel {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
