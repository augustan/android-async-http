package com.aug.android.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class StorageUtil {
	private static String mCacheRootDir = null;

	private static Context appContext = null;
	private static boolean mUseExternalCache = true;

	private static final String CHARACTER_DIVIDER = File.separator;
    private static final String CACHE_DIR = "data" + CHARACTER_DIVIDER;
    private static final String CACHE_DIRECTION = "cache" + CHARACTER_DIVIDER;
    
    public static void init(Context c) {
        appContext = c.getApplicationContext();
        setCacheRootDir(c, mUseExternalCache, true);
    }

	public static String getCacheRootDir() {
		if (TextUtils.isEmpty(mCacheRootDir)) {
            setCacheRootDir();
		}
		return mCacheRootDir;
	}

	private static String setCacheRootDir() {
        setCacheRootDir(appContext, mUseExternalCache, true);
        return mCacheRootDir;
    }
    
	private static void setCacheRootDir(Context context,
			boolean preferExternal, boolean withLastSeparator) {
		File appCacheDir = null;

		if (preferExternal && hasSDCardMounted()) {
			appCacheDir = getExternalCacheDir(context);
		}

		if (appCacheDir == null) {
			appCacheDir = getInternalCacheDir(context);
		}

		mCacheRootDir = (appCacheDir != null ? appCacheDir.getAbsolutePath()
				: "");

		if (withLastSeparator) {
			mCacheRootDir = appendWithSeparator(mCacheRootDir);
		}
	}

	private static File getExternalCacheDir(Context context) {
		File appCacheDir = null;

		if (appCacheDir == null) {
			if (Version.hasKitKat()) {
				if ((appCacheDir = context.getExternalFilesDir(null)) == null) {
					appCacheDir = new File(new File(
							Environment.getExternalStorageDirectory(),
							"Android"), "data");
					appCacheDir = new File(new File(appCacheDir,
							context.getPackageName()), "files");
				}
			}
		}

		if (appCacheDir == null) {
			appCacheDir = new File(new File(
					Environment.getExternalStorageDirectory(), "aligame"),
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
		File appCacheDir = getInternalCacheDir(appContext);
		String path = (appCacheDir != null ? appCacheDir.getAbsolutePath() : "");
		return appendWithSeparator(path);
	}

	private static File getInternalCacheDir(Context context) {
		File appCacheDir = null;

		if ((appCacheDir = context.getFilesDir()) == null) {
			String cacheDirPath = "/data/data/" + context.getPackageName()
					+ "/files/";
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

	public static void setUseExternalCache(boolean useExternalCache) {
		mUseExternalCache = useExternalCache;
	}

	public static String getCacheFilePath(String url) {
		if (TextUtils.isEmpty(url)) {
			return "";
		}
		
		switch (Scheme.ofUri(url)) {
		/*
		 * 生成本地文件url url = Scheme.FILE.wrap(path);
		 */
		case FILE:
			return Scheme.FILE.crop(url);
		default:
			break;
		}
		
		int index = url.lastIndexOf('.');
		String postFix = "";
		if (index >= 0 && url.length() - index <= 8) {
			postFix = url.substring(index);
		}
		
		return getCacheFile(url, postFix);
	}
	
	public static String getFileKey(String url) {
		return StringUtil.toMd5(url);
	}

    private static String getCacheFile(String url, String postFix){
        String md5 = getFileKey(url);
        String path = StorageUtil.getCacheRootDir() + CACHE_DIR + CACHE_DIRECTION + md5.substring(0, 2) + CHARACTER_DIVIDER + md5 + postFix;
        return path;
    }
}
