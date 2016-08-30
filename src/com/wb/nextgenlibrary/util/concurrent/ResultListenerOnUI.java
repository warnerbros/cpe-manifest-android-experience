package com.wb.nextgenlibrary.util.concurrent;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class ResultListenerOnUI<T> implements ResultListener<T> {
	private static final int POST_RESULT_TO_UI = 0x1;
	private static final int POST_EXCEPTION_TO_UI = 0x2;
	private final LocalHandler localHandler = new LocalHandler();
	private final EHandler exceptionHandler = new EHandler();
	
	public ResultListenerOnUI() {
		//TODO vandreev this one not working
		if (Thread.currentThread().getId() != Looper.getMainLooper().getThread().getId()) {
			throw new RuntimeException("ResultListenerOnUI must be implemented on UI thread!");
		}
	}
	
	public abstract void onResultOnUI(T result);
	
	public <E extends Exception> void  onExceptionOnUI(E e) {}
	
	@Override
	public void onResult(T result) {
		Message message = localHandler.obtainMessage(POST_RESULT_TO_UI, result);
		message.sendToTarget();
	}
	
	@Override
	public <E extends Exception> void onException(E e) {
		Message message = exceptionHandler.obtainMessage(POST_EXCEPTION_TO_UI, e);
		message.sendToTarget();
		
	};
	
	private class LocalHandler extends Handler {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			onResultOnUI((T) msg.obj);
		}
		
	}
	private class EHandler extends Handler {
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj instanceof Throwable) {
			onExceptionOnUI((Exception) msg.obj);
			}
			}
			
		}
}

