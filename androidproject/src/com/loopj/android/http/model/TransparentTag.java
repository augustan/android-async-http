package com.loopj.android.http.model;


/**
 * 简单透传的Tag
 * @author jianjun.ajj
 *
 */
public class TransparentTag extends HttpTag {

    public TransparentTag() {
        super("http.TransparentTag", TransparentTag.class);
    }
    
    @Override
    public Object parseJson(String json) throws Exception {
        return json;
    }
}
