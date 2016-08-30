package com.wb.nextgenlibrary.util.utils;

import android.os.Looper;

/**
 * Creates and manages the lifecycle of worker threads
 */
public class WorkerThreads {

    private WorkerThreads() {
    }

    private static class SingletonHolder {
        public static final WorkerThreads INSTANCE = new WorkerThreads();
    }

    public static WorkerThreads instance() {
        return SingletonHolder.INSTANCE;
    }

    public void invokeLater(Runnable r) {
        // TODO: Use a more robust solution like
        // http://developer.android.com/reference/java/util/concurrent/ThreadPoolExecutor.html

        // Temporary solution
        new Thread(r).start();
    }

    /** @return If the current thread is not the main thread */
    public static boolean isNotMainThread() {
        return Thread.currentThread().getId() != Looper.getMainLooper().getThread().getId();
    }
}
