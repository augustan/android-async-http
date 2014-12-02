
package demo;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

public class ImageDecoder {

    public static Bitmap decode(byte[] bytes) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
        }
        return bmp;
    }

    public static Bitmap decode(String filepath) {
        Bitmap bmp = null;
        try {
            Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Config.RGB_565;
            bmp = BitmapFactory.decodeFile(filepath, options);
        } catch (Exception e) {
        }
        return bmp;
    }

}
