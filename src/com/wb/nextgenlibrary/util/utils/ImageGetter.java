package com.wb.nextgenlibrary.util.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


import com.wb.nextgenlibrary.util.HttpImageHelper;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

/**
 * Current ImageOrder implementation is more convoluted than ImageRequest.
 */
public class ImageGetter {
    private static final ImageGetter INSTANCE = new ImageGetter();

    private final Queue<ImageRequest> imageQueue;

    private ImageGetter() {
        imageQueue = new LinkedBlockingQueue<ImageRequest>(); // Alternative: use LinkedBlockingQueue for auto blocking
        new Thread(new ImageRequestConsumer(imageQueue)).start();
    }

    public static ImageGetter instance() {
        return INSTANCE;
    }

    public void get(String url, Handler handler) {
    //    Logger.d("ImageGetter", "ImageGetter requesting: " + url);
        synchronized (imageQueue) {
            imageQueue.add(new ImageRequest(url, handler));
            if (imageQueue.size() == 1) {
                imageQueue.notifyAll();
            }
        }
    }

    private static class ImageRequest {
        public final String url;
        public final Handler handler;

        private ImageRequest(String url, Handler handler) {
            this.url = url;
            this.handler = handler;
        }
    }

    private static abstract class RequestConsumer<T> implements Runnable {
        private final Queue<T> queue;

        protected RequestConsumer(Queue<T> q) {
            queue = q;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    T object = null;
                    synchronized (queue) {
                        if (queue.size() == 0) {
                       //     Logger.d("ImageGetter", "RequestConsumer waiting");
                            queue.wait();
                        }
                        object = queue.remove();
                    }
                    consume(object);
                 //   Logger.d("ImageGetter", "RequestConsumer consuming");

                } catch (InterruptedException e) {
                    NextGenLogger.e(F.TAG, "ImageGetter.RequestConsumer.run", e);
                }
            }
        }

        protected abstract void consume(T object);
    }

    private static class ImageRequestConsumer extends RequestConsumer<ImageRequest> {
        protected ImageRequestConsumer(Queue<ImageRequest> q) {
            super(q);
        }

        @Override
        protected void consume(ImageRequest request) {
            URL url = null;
            try {
                url = new URL(request.url);
            } catch (MalformedURLException e) {
                NextGenLogger.e(F.TAG, "ImageRequestConsumer.consume url=" + url, e);
                return;
            }
            try {
                if (request.handler == null) {
                  //  HttpImageHelper.fetchImageDoNotCache(url);
                    Bitmap image = HttpImageHelper.fetchImage(url); // Caching enabled
                } else {
                    Bitmap image = HttpImageHelper.fetchImage(url); // Caching enabled
                    request.handler.sendMessage(Message.obtain(null, 0, image));
                }
            } catch (IOException e) {
                NextGenLogger.e(F.TAG, "ImageRequestConsumer.consume", e);
            }
        }
    }
}
