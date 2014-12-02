
package demo;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class StorageUtil {
    private static String mCacheRootDir = null;

    private static boolean mUseExternalCache = true;

    public static String getCacheRootDir() {
        if (TextUtils.isEmpty(mCacheRootDir)) {
            setCacheRootDir();
        }

        return mCacheRootDir;
    }

    public static String setCacheRootDir() {
        setCacheRootDir(MainActivity.g_context, mUseExternalCache, true);
        return mCacheRootDir;
    }

    private static void setCacheRootDir(Context context, boolean preferExternal,
            boolean withLastSeparator) {
        File appCacheDir = null;

        if (preferExternal && hasSDCardMounted()) {
            appCacheDir = getExternalCacheDir(context);
        }

        if (appCacheDir == null) {
            appCacheDir = getInternalCacheDir(context);
        }

        mCacheRootDir = (appCacheDir != null ? appCacheDir.getAbsolutePath() : "");

        if (withLastSeparator) {
            mCacheRootDir = appendWithSeparator(mCacheRootDir);
        }
    }

    private static File getExternalCacheDir(Context context) {
        File appCacheDir = null;

        if (appCacheDir == null) {
            if (Version.hasKitKat()) {
                if ((appCacheDir = context.getExternalFilesDir(null)) == null) {
                    appCacheDir = new File(new File(Environment.getExternalStorageDirectory(),
                            "Android"), "data");
                    appCacheDir = new File(new File(appCacheDir, context.getPackageName()), "files");
                }
            }
        }

        if (appCacheDir == null) {
            appCacheDir = new File(new File(Environment.getExternalStorageDirectory(), "aligame"),
                    "baodian");
        }

        if (appCacheDir != null && !appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
        }

        return appCacheDir;
    }

    public static String getInternalCacheDir() {
        File appCacheDir = getInternalCacheDir(MainActivity.g_context);
        String path = (appCacheDir != null ? appCacheDir.getAbsolutePath() : "");
        return appendWithSeparator(path);
    }

    private static File getInternalCacheDir(Context context) {
        File appCacheDir = null;

        if ((appCacheDir = context.getFilesDir()) == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/files/";
            appCacheDir = new File(cacheDirPath);
        }

        if (appCacheDir != null && !appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
        }

        return appCacheDir;
    }

    public static boolean hasSDCardMounted() {
        String state = Environment.getExternalStorageState();
        if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private static String appendWithSeparator(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (path.substring(path.length() - 1).equals(File.separator) == false) {
                path += File.separator;
            }
        }

        return path;
    }

    public static boolean isUseExternalCache() {
        return mUseExternalCache;
    }

    public static void setUseExternalCache(boolean mUseExternalCache) {
        StorageUtil.mUseExternalCache = mUseExternalCache;
    }
}
