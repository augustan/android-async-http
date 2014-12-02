package demo;

import java.io.File;


public class ImageUtil {

    public static final String CHARACTER_DIVIDER = File.separator;
    private static final String CACHE_DIR = "data" + CHARACTER_DIVIDER;
    private static final String CACHE_IMAGE_DIRECTION = "bd_image" + CHARACTER_DIVIDER;
    public static final String CACHE_IMAGE_SUFFIX = ".png";
    
    public static String getCacheImageFileName(String url){
        String md5 = StringUtil.toMd5(url);
        String path = StorageUtil.getCacheRootDir() + CACHE_DIR + CACHE_IMAGE_DIRECTION + md5.substring(0, 2) + CHARACTER_DIVIDER + md5 + CACHE_IMAGE_SUFFIX;
        return path;
    }
}
