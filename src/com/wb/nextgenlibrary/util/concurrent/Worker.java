package com.wb.nextgenlibrary.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;

import android.os.Build;


public class Worker {
	
	private Worker() {
	}
	
	private final static int MAX_THREADS = 10;
	private final static ThreadPoolExecutor executor = new ThreadPoolExecutor(MAX_THREADS, MAX_THREADS, 60L,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	
	static {
		if(Build.VERSION.SDK_INT>8)
		executor.allowCoreThreadTimeOut(true);
	}
	
	public static <Result> void executeSerial(final Task<Result> listener) {
	};
	

	
	public static <Result> void execute(final Callable<Result> r,final ResultListener<Result> l) {
		executor.submit(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (l != null)	
						l.onResult(r.call());
					else
						r.call();

				} catch (Exception e) {
					NextGenLogger.e(F.TAG, "Worker.execute: Task throw exception " + e.getMessage(), e);
					l.onException(e);
				}
				
			}
		});
	}
	
	public static String status() {
		String status;
		if (executor.isShutdown()) {
			status = "SHUTDOWN";
		} else if (executor.isTerminated()) {
			status = "TERMINATED";
		} else {
			status = "RUNNING";
		}
		
		return "Worker status is: " + status + ", " + executor.getPoolSize() + " threads alive";
	}
	
	public static void shutdown() {
		executor.shutdown();
	}
}
