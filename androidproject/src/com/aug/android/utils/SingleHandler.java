package com.aug.android.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.aug.android.http.utils.LogUtils;

/**
 * 基与Android Handler的统一异步处理队列，通过getInstance()方法区分ui和非ui两种
 * @author fengyun.zl
 */
public class SingleHandler 
{
	private static SafeHander ui; // ui线程
	private static SafeHander non_ui; // 非ui线程
	
	/**
	 * 获取异步操作队列
	 * 
	 * @param isUI true：ui线程，false：非ui线程
	 * @return SafeHander 系统handler对象
	 */
	public static SafeHander getInstance(boolean isUI)
	{
		SafeHander handler = null; // handler对象
		
		// UI
		if(isUI)
		{
			if (ui == null)
			{
				ui = new SafeHander(Looper.getMainLooper()); // 主线程
			}
			handler = ui;
		}
		else // 非UI
		{
			if (non_ui == null) // 只创建一次
			{
				HandlerThread ht = new HandlerThread("non-ui thread"); //新起动线程
				ht.start();
				non_ui = new SafeHander(ht.getLooper());
			}
			handler = non_ui;
		}
		
		return handler;
	}
	
	/**
	 * 内部处理异常的handler
	 */
	public static class SafeHander extends Handler
	{
		public SafeHander(Looper looper)
		{
			super(looper);
		}
		
		@Override
		public void dispatchMessage(Message msg)
		{
			try
			{
				super.dispatchMessage(msg);
			}
			catch (Exception e)
			{
				if(LogUtils.isDebug())
				{
					e.printStackTrace();
				}
			}
		}
	}
}
