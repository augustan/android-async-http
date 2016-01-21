
package demo;

import java.io.File;

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

import com.aug.android.http.ex.HttpEnging;
import com.aug.android.http.ex.download.FileDownloader;
import com.aug.android.http.lib.RequestHandle;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.HttpTag;
import com.aug.android.http.model.IFileDataHandler;
import com.aug.android.http.model.IImageDownloadReponse;
import com.aug.android.http.model.INetDownloadReponse;
import com.aug.android.http.model.INetTextReponse;
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

        HttpEnging.create(this);
        HttpEnging.setNeedSSLAuth(true);
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
                 testGetImage();
//                testDownload();
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
        FileDownloader.getInstance().downloadImage(url, new IImageDownloadReponse() {
			
			@Override
			public void onHttpRecvError(BaseNetRequest request, Throwable error,
					String content) {
			}
			
			@Override
			public void onHttpRecvCancelled(BaseNetRequest request) {
			}
			
			@Override
			public void onFileSaved(BaseNetRequest request, File file) {
			}
			
			@Override
			public void onDataProgress(BaseNetRequest request, long receivedLength,
					long totalLength) {
			}
			
			@Override
			public void onDataPostProcessFinished(BaseNetRequest request,
					IFileDataHandler dataHandler) {
                if (dataHandler.isProcessSuccess()) {
                    Object bmpObject = dataHandler.getDecodeData();
                    image.setImageBitmap((Bitmap) bmpObject);
                }
			}
		});

    }

    private void testDownload() {

        String url = "http://gdown.baidu.com/data/wisegame/2c6a60c5cb96c593/QQ_182.apk";
        FileDownloader.getInstance().downloadFile(url, new INetDownloadReponse() {

            @Override
            public void onHttpRecvError(BaseNetRequest request, Throwable error, String content) {
                HttpEnging.setDebug(true);
            }

            @Override
            public void onHttpRecvCancelled(BaseNetRequest request) {
                HttpEnging.setDebug(true);
            }

            @Override
            public void onFileSaved(BaseNetRequest request, File file) {
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
        });
    }
}
