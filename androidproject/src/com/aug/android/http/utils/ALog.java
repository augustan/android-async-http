
package com.aug.android.http.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

/***
 * 日志工具类
 * 
 * @author hxp
 */
public class ALog {

    private static boolean DEBUG = false;
    
    //是否把log展示给cp
    public static boolean TOCP = false;

    private static final int Log_Level_error = 4;

    private static final int Log_Level_warn = 3;

    private static final int Log_Level_info = 2;

    private static final int Log_Level_debug = 1;

    private static final int Log_Level_verbose = 0;

    /** Log信息级别>=logLevel的日志信息打印出来 */
    private static int logLevel = 0;

    // 是否将日志写到文件中
    private static final boolean Log_IN_FILE = false;

    private static final boolean Log_WITH_POSTION = false;

    private static final String LOG_TAG = "alisdk";

    private static final String LOG_FILE = LOG_TAG + ".log";

    private static long curTime;

    private static Context mContext = null;

    public ALog() {
    }
    
    public static boolean isDebug() {
        return DEBUG;
    }
    
    public static void setDebug(boolean debug) {
        DEBUG = debug;
    }

    public static void showLog(boolean isShow)	{
    	
    	TOCP = isShow;
    }
    
    public static void openLog(Context context) {
        mContext = context;
    }

    public static void startCal() {
        curTime = System.currentTimeMillis();
    }

    // 计算当前时间和上一次调用Cal（）或beginCal（）的时间间隔
    public static void Cal(String log) {

        if (DEBUG == false) {
            return;
        }

        long time = System.currentTimeMillis();
        w(log + " cost: " + (time - curTime));
        curTime = time;
    }

    /** 详细信息 */
    public static void v(String msg) {

        v(LOG_TAG, msg);
    }

    public static void v(String tag, String msg) {

        if (DEBUG == false && TOCP == false) {
            return;
        }

        if (Log_Level_verbose >= logLevel) {
            if (Log_WITH_POSTION) {
                msg = msg + "on"
                        + new Throwable().getStackTrace()[1].toString();
            }
            Log.v(tag, msg);

            if (Log_IN_FILE) {
                writeIntoFile(LOG_TAG + "v:" + msg);
            }
        }
    }

    /** 调试日志 */
    public static void d(String msg) {

        d(LOG_TAG, msg);
    }

    public static void d(String tag, String info) {

        if (DEBUG == false) {
            return;
        }

        if (Log_Level_debug >= logLevel) {
            if (Log_WITH_POSTION) {
                info = info + "on"
                        + new Throwable().getStackTrace()[1].toString();
            }
            Log.d(tag, info);
            if (Log_IN_FILE) {
                writeIntoFile(LOG_TAG + "d:" + info);
            }
        }
    }

    /** 信息日志 */
    public static void i(String info) {

        i(LOG_TAG, info);
    }

    public static void i(String tag, String info) {

        if (DEBUG == false) {
            return;
        }

        if (Log_Level_info >= logLevel) {
            if (Log_WITH_POSTION) {
                info = info + "on"
                        + new Throwable().getStackTrace()[1].toString();
            }
            Log.i(tag, info);
            if (Log_IN_FILE) {
                writeIntoFile(LOG_TAG + "i:" + info);
            }
        }
    }

    /** 警告日志 */
    public static void w(String msg) {

        w(LOG_TAG, msg);
    }

    public static void w(String tag, String info) {

        if (DEBUG == false) {
            return;
        }

        if (Log_Level_warn >= logLevel) {
            if (Log_WITH_POSTION) {
                info = info + "on"
                        + new Throwable().getStackTrace()[1].toString();
            }
            Log.w(tag, info);
            if (Log_IN_FILE) {
                writeIntoFile(LOG_TAG + " w: " + info);
            }
        }
    }

    /** 错误日志 */
    public static void e(String msg) {

        e(LOG_TAG, msg);
    }

    public static void e(String tag, String info) {

        if (DEBUG == false) {
            return;
        }

        if (Log_Level_error >= logLevel) {
            if (Log_WITH_POSTION) {
                info = info + " on "
                        + new Throwable().getStackTrace()[1].toString();
            }
            if (info != null) {
                Log.e(tag, info);
            } else {
                Log.e(LOG_TAG, "info null");
            }
            if (Log_IN_FILE) {
                writeIntoFile(LOG_TAG + " e: " + info);
            }
        }
    }

    public static void e(String title, Exception e) {

        if (DEBUG == false) {
            return;
        }

        String msg = null;
        if (e == null) {
            msg = title + ": " + "null";
        } else {
            msg = title + ": " + e.toString();
        }
        e(msg);
    }

    public static boolean writeIntoFile(String log) {

        if (DEBUG == false) {
            return false;
        }

        log = log + "\n";
        boolean res = false;
        try {
            FileOutputStream fOut = mContext.openFileOutput(LOG_FILE,
                    Context.MODE_APPEND);
            try {
                fOut.write(log.getBytes());
                res = true;
            } catch (IOException e) {
                ALog.e(LOG_TAG, e.toString());
            }
        } catch (FileNotFoundException e) {
            ALog.e(LOG_TAG, e.toString());
        }
        return res;
    }

}
