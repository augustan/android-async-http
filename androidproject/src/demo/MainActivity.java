
package demo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ex.HttpEnging;
import com.loopj.android.http.model.BaseNetRequest;
import com.loopj.android.http.model.HttpTag;
import com.loopj.android.http.model.IBinaryDataHandler;
import com.loopj.android.http.model.INetBinaryReponse;
import com.loopj.android.http.model.INetDownloadReponse;
import com.loopj.android.http.model.INetTextReponse;
import com.taobao.de.aligame.http.R;

public class MainActivity extends Activity {

    public static Context g_context;

    private Button btn_test = null;
    private Button btn_cancel = null;
    private ImageView image = null;

    private TextView progress_text = null;
    private ProgressBar progress = null;
    
    private RequestHandle runningRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        g_context = this;

        setContentView(R.layout.activity_main);

        HttpEnging.create(this, true);
        HttpEnging.setDebug(true);

        btn_test = (Button) findViewById(R.id.btn_test);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        image = (ImageView) findViewById(R.id.image);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress_text = (TextView) findViewById(R.id.progress_text);

        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (runningRequest != null) {
                    runningRequest.cancel(true);
                }
            }
        });

        btn_test.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                testSyncGet();
//                 testHttpGet();
//                 testGetImage();
                testDownload();
            }
        });
    }
    
    private void testSyncGet() {
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                Object o = HttpEnging.sendPostSync(HttpTag.TRANSPARENT_TAG, "http://www.baidu.com", null);
                if (o != null) {
                    o = null;
                }
            }
        }).start();
    }

    private void testHttpGet() {
        BaseNetRequest request = new BaseNetRequest("http://www.baidu.com");
        HttpEnging.sendGetAsync(request, new INetTextReponse() {

            @Override
            public void onHttpRecvError(BaseNetRequest request, Throwable error, String content) {
                // TODO Auto-generated method stub

                HttpEnging.setDebug(true);
            }

            @Override
            public void onHttpRecvCancelled(BaseNetRequest request) {
                // TODO Auto-generated method stub

                HttpEnging.setDebug(true);
            }

            @Override
            public void onBusinessOK(BaseNetRequest request, String result) {
                // TODO Auto-generated method stub

                HttpEnging.setDebug(true);
            }

        });
    }

    private void testGetImage() {

        String url = "http://img6.cache.netease.com/photo/0001/2014-11-21/ABIMUU6G00AN0001.jpg";
        BaseNetRequest request = new BaseNetRequest(url);

        final String filePath = getFilePath(url);

        HttpEnging.getBinaryDataAsync(request, new INetBinaryReponse() {

            @Override
            public void onHttpRecvError(BaseNetRequest request, Throwable error, String content) {
                // TODO Auto-generated method stub
                HttpEnging.setDebug(true);

            }

            @Override
            public void onHttpRecvCancelled(BaseNetRequest request) {
                // TODO Auto-generated method stub
                HttpEnging.setDebug(true);

            }

            @Override
            public void onDataReceived(BaseNetRequest request, IBinaryDataHandler dataHandler) {
                // TODO Auto-generated method stub
                HttpEnging.setDebug(true);

                if (dataHandler.processSuccess()) {
                    Object bmpObject = dataHandler.getDecodeData();
                    image.setImageBitmap((Bitmap) bmpObject);
                }
            }

            @Override
            public void onDataProgress(BaseNetRequest request, long receivedLength, long totalLength) {
                // TODO Auto-generated method stub
                HttpEnging.setDebug(true);

            }

        }, new IBinaryDataHandler() {

            Bitmap decodeBmp = null;

            @Override
            public boolean processSuccess() {
                return decodeBmp != null;
            }

            @Override
            public void onDataReceived(BaseNetRequest request, byte[] data) {
                Bitmap bmp = null;
                decodeBmp = null;
                if (data != null) {
                    bmp = ImageDecoder.decode(data);
                }
                if (bmp != null) {
                    decodeBmp = bmp;
                    // saveBitmapToFile(bmp, filePath);
                }
            }

            @Override
            public Object getDecodeData() {
                return decodeBmp;
            }
        });
    }

    private void testDownload() {

        String url = "http://gdown.baidu.com/data/wisegame/2c6a60c5cb96c593/QQ_182.apk";
        BaseNetRequest request = new BaseNetRequest(url);

        final String filePath = getFilePath(url) + ".apk";

        runningRequest = HttpEnging.downloadFile(request, new INetDownloadReponse() {

            @Override
            public void onHttpRecvError(BaseNetRequest request, Throwable error, String content) {
                // TODO Auto-generated method stub

                HttpEnging.setDebug(true);
            }

            @Override
            public void onHttpRecvCancelled(BaseNetRequest request) {
                // TODO Auto-generated method stub

                HttpEnging.setDebug(true);
            }

            @Override
            public void onFileSaved(BaseNetRequest request, File file) {
                // TODO Auto-generated method stub

                HttpEnging.setDebug(true);
                progress.setProgress(100);
                progress_text.setText("100%");
            }

            @Override
            public void onDataProgress(BaseNetRequest request, long receivedLength, long totalLength) {
                int p = (int) receivedLength * 100 / (int)totalLength;
                progress.setProgress(p);
                
                float per = (float)receivedLength * 100 / (float)totalLength;
                String perStr = String.format("%d / %d  %.02f%%", receivedLength, totalLength, per);
                progress_text.setText(perStr);
            }
        }, filePath);
    }

    public static String getFilePath(String url) {

        switch (Scheme.ofUri(url)) {
        /*
         * 生成本地文件url url = Scheme.FILE.wrap(path);
         */
            case FILE:
                return Scheme.FILE.crop(url);
            default:
                break;
        }
        return ImageUtil.getCacheImageFileName(url);
    }

    private void saveBitmapToFile(Bitmap bitmap, String path) {
        BufferedOutputStream os = null;
        try {
            File file = new File(path);
            if (file != null) {
                if (file.getParent() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                    file.delete();
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                }
            }
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } catch (Exception e) {
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
