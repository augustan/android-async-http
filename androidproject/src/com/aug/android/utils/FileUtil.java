package com.aug.android.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;

public class FileUtil {

	private static boolean makeDir(File file) {
		boolean success = true;
		try {
			if (file != null) {
				if (file.getParent() != null && !file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
					file.delete();
					if (!file.exists()) {
						file.createNewFile();
					}
				}
			}
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}

	public static void saveBitmapToFile(Bitmap bitmap, String path) {
		BufferedOutputStream os = null;
		try {
			File file = new File(path);
			if (makeDir(file)) {
				os = new BufferedOutputStream(new FileOutputStream(file));
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 文件转化为字节数组
	 */
	public static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1024 * 32];
			int n;
			while ((n = stream.read(b)) != -1) {
				out.write(b, 0, n);
			}
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * 把字节数组保存为一个文件
	 */
	public static File getFileFromBytes(byte[] b, String outputFile) {
		BufferedOutputStream stream = null;
		File file = null;
		try {
			file = new File(outputFile);
			if (makeDir(file)) {
				FileOutputStream fstream = new FileOutputStream(file);
				stream = new BufferedOutputStream(fstream);
				stream.write(b);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return file;
	}

}
