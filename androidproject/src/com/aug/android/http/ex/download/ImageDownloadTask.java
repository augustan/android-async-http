package com.aug.android.http.ex.download;

import java.io.File;

import android.graphics.Bitmap;

import com.aug.android.http.ex.HttpEnging;
import com.aug.android.http.lib.RequestHandle;
import com.aug.android.http.model.BaseNetRequest;
import com.aug.android.http.model.IFileDataHandler;
import com.aug.android.http.model.IImageDownloadReponse;
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
        		notifyStatusChanged(success ? DL_TASK_STATUS_DECODE_SUCCESS : DL_TASK_STATUS_DECODE_FAIL);
        	}
		}
    };
    
	private IFileDataHandler imagePostHandler = new IFileDataHandler() {

        Bitmap decodeBmp = null;

        @Override
        public boolean isProcessSuccess() {
            return decodeBmp != null;
        }

        @Override
        public Object getDecodeData() {
            return decodeBmp;
        }

		@Override
		public void onDataReceived(BaseNetRequest request, File file) {
            Bitmap bmp = null;
            decodeBmp = null;
            if (file != null) {
                bmp = ImageDecoder.decode(file.getPath());
            }
            if (bmp != null) {
                decodeBmp = bmp;
            }
		}
    };

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
