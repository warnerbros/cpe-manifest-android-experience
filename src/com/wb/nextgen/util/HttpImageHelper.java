package com.wb.nextgen.util;

import java.io.IOException;
import java.net.URL;

import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.FlixsterLogger;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HttpImageHelper {

    /** Fetch and cache the image */
    public static Bitmap fetchImage(URL url) throws IOException {
        Bitmap bitmap = null;
        byte[] bitmapByteArray = HttpHelper.fetchUrlBytes(url, true, true, false);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inInputShareable = true;
        try {
        	bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length, options);
            //bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length);
        } catch (OutOfMemoryError err) {
        	FlixsterLogger.e(F.TAG, "HttpImageHelper.fetchImage: OutOfMemoryError, url: " + url);
        }
        
        if (bitmap == null) {
            // System.gc();
            options.inSampleSize = 4;
            try {
            	bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length, options);
            } catch (OutOfMemoryError err) {
            	FlixsterLogger.e(F.TAG, "HttpImageHelper.fetchImage: OutOfMemoryError, inSampleSize=4, url: " + url);
            }
        }
        return bitmap;
    }

    /** Fetch the image only (for analytics) */
    public static void fetchImageDoNotCache(URL url) throws IOException {
        HttpHelper.fetchUrlBytes(url, false, true, false);
    }
}
