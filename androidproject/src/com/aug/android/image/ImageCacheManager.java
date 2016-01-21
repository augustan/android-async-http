package com.aug.android.image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.graphics.Bitmap;

public class ImageCacheManager {

	private int CACHE_CLEAR_COUNT = 5; // 每次清出几个缓存项
	private int CACHE_SIZE = 15;

	private class BitmapContainer implements Comparable<BitmapContainer> {
		String key;
		Bitmap bmp;
		long accessedTime = 0;

		private BitmapContainer(String key, Bitmap bmp) {
			this.key = key;
			this.bmp = bmp;
			accessedTime = System.currentTimeMillis();
		}
		
		public String getKey() {
			return key;
		}

		public Bitmap getBmp() {
			return bmp;
		}

		private void updateAccessedTime() {
			accessedTime = System.currentTimeMillis();
		}

		@Override
		public int compareTo(BitmapContainer another) {
			if (this.accessedTime - another.accessedTime == 0) {
				return 0;
			} else if (this.accessedTime - another.accessedTime > 0) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	private Object locker = new Object();
	private HashMap<String, BitmapContainer> imageCache = new HashMap<String, BitmapContainer>();
	private ArrayList<BitmapContainer> sortImageList = new ArrayList<BitmapContainer>();

	private static ImageCacheManager instance = null;

	public static ImageCacheManager getInstance() {
		if (instance == null) {
			synchronized (ImageCacheManager.class) {
				if (instance == null) {
					instance = new ImageCacheManager();
				}
			}
		}
		return instance;
	}

	private ImageCacheManager() {
	}

	public void putBitmap(String key, Bitmap bmp) {
		synchronized (locker) {
			BitmapContainer container = imageCache.get(key);
			if (container != null) {
				container.updateAccessedTime();
				return;
			}
			if (imageCache.size() > CACHE_SIZE) {
				clearCache(CACHE_CLEAR_COUNT);
			}
			container = new BitmapContainer(key, bmp);
			imageCache.put(key, container);
			sortImageList.add(container);
		}
	}

	private void clearCache(int clearCount) {
		synchronized (locker) {
			Collections.sort(sortImageList);
			while (clearCount > 0 && sortImageList.size() > 0) {
				BitmapContainer item = sortImageList.remove(0);
				imageCache.remove(item.getKey());
			}
		}
	}

	public Bitmap getBitmap(String key) {
		synchronized (locker) {
			BitmapContainer container = imageCache.get(key);
			return container != null ? container.getBmp() : null;
		}
	}
}
