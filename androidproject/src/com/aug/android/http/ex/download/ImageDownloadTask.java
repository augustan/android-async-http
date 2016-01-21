package com.aug.android.http.ex.download;

import java.io.File;

import android.graphics.Bitmap;

import com.aug.android.http.ex.HttpEnging;
import com.aug.android.http.lib.RequestHandle;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.IFileDataHandler;
import com.aug.android.http.model.IImageDownloadReponse;
import com.aug.android.image.ImageCacheManager;
import com.aug.android.image.ImageDecoder;
import com.aug.android.utils.SingleHandler;

public class ImageDownloadTask extends FileDownloadTask {

	private IImageDownloadReponse listener;
	private IImageDownloadReponse imageRespHandler = new CImageDownloadReponse();

	protected class CImageDownloadReponse extends CNetDownloadReponse implements IImageDownloadReponse {

		@Override
		public void onDataPostProcessFinished(BaseNetRequest request,
				IFileDataHandler dataHandler) {
        	if (listener != null) {
        		listener.onDataPostProcessFinished(request, dataHandler);
        	}
        	if (dataHandler != null) {
        		boolean success = dataHandler.isProcessSuccess();
        		int status = success ? DL_TASK_STATUS_DECODE_SUCCESS : DL_TASK_STATUS_DECODE_FAIL;
        		if (success && request == null) {
        			status = DL_TASK_STATUS_GET_FROM_SDCARD;
        			if (dataHandler instanceof BitmapHolder) {
        				boolean fromCache = ((BitmapHolder)dataHandler).isGetFromCache();
        				if (fromCache) {
        					status = DL_TASK_STATUS_GET_FROM_CACHE;
        				}
        			}
        		}
        		notifyStatusChanged(status);
        	}
		}
    };
    
    private class BitmapHolder implements IFileDataHandler {

        Bitmap decodeBmp = null;
        boolean getFromCache = false;

        @Override
        public boolean isProcessSuccess() {
            return decodeBmp != null;
        }
        
        private boolean isGetFromCache() {
        	return getFromCache;
        }
        
        private void setCachedBitmap(Bitmap bmp) {
        	getFromCache = true;
        	decodeBmp = bmp;
        }

        @Override
        public Object getDecodeData() {
            return decodeBmp;
        }

		@Override
		public void onDataReceived(BaseNetRequest request, File file) {
            Bitmap bmp = null;
            decodeBmp = null;
            getFromCache = false;
            if (file != null) {
                bmp = ImageDecoder.decode(file.getPath());
            }
            if (bmp != null) {
                decodeBmp = bmp;
                ImageCacheManager.getInstance().putBitmap(getTaskKey(), bmp);
            }
		}
    }
    
	private BitmapHolder imagePostHandler = new BitmapHolder();

	public ImageDownloadTask(String url, IImageDownloadReponse listener,
			IDownloadManage manager) {
		super(url, listener, manager);
		this.listener = listener;
	}
	
	@Override
	public String getTaskKey() {
		return "IMG_" + fileKey;
	}
	
	@Override
	public void run() {
		String key = getTaskKey();
		Bitmap bmp = ImageCacheManager.getInstance().getBitmap(key);
		imagePostHandler.setCachedBitmap(bmp);
		if (bmp != null) {
			SingleHandler.getInstance(true).post(new Runnable() {
				@Override
				public void run() {
					imageRespHandler.onDataPostProcessFinished(null, imagePostHandler);
				}
			});
		} else {
			super.run();
		}
	}

	@Override
	protected boolean abortDownloadOnFileExist(String path) {
		boolean abordDownload = false;
		super.abortDownloadOnFileExist(path);
		File newFile = new File(path);
		if (newFile.exists()) {
			abordDownload = true;
			imagePostHandler.onDataReceived(null, newFile);
			SingleHandler.getInstance(true).post(new Runnable() {
				@Override
				public void run() {
					imageRespHandler.onDataPostProcessFinished(null, imagePostHandler);
				}
			});
		}
		return abordDownload;
	}
	
	@Override
	protected RequestHandle sendDownloadRequest(BaseNetRequest request, String filePath) {
		return HttpEnging.downloadImage(request, imageRespHandler, filePath, imagePostHandler);
	}

}
