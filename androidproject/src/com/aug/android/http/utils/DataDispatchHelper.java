package com.aug.android.http.utils;

import android.os.Handler;
import android.os.Looper;

import com.aug.android.http.model.BaseNetRequest;

/**
 * 网络数据返回后，先在后台线程中经过dispatch（...）处理，再通过runOnUIThread（...)把转化后的class发到UI线程处理
 * @author jianjun.ajj
 *
 */
public class DataDispatchHelper {

    private Handler mHandler = null;
    
    public DataDispatchHelper() {
        try {
            mHandler = new Handler(Looper.getMainLooper());
        } catch (Exception e) {
            ALog.e(e.toString());
        }
    }
    
    public void runOnUIThread(Runnable runnable) {
        if (mHandler != null) {
            mHandler.post(runnable);
        }
    }

    public Object dispatch(BaseNetRequest request, String json) throws Exception {
        return request.getTag().parseJson(json);
    }
}
