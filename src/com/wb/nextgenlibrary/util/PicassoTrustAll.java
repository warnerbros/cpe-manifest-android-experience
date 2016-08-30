package com.wb.nextgenlibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.wb.nextgenlibrary.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.wb.nextgenlibrary.util.utils.StringHelper;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by gzcheng on 12/4/15.
 * http://stackoverflow.com/questions/23562794/doesnt-picasso-support-to-download-images-which-uses-https-protocol
 */
public class PicassoTrustAll {

    private static Picasso mInstance = null;
    private final static List<String> cannotResetUris = new ArrayList<String>();

    private PicassoTrustAll(Context context) {
        OkHttpClient client = new OkHttpClient();
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        } };
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mInstance = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        //NextGenLogger.e("PICASSO", exception.getLocalizedMessage());
                        if (!cannotResetUris.contains(uri.toString())) {
                            cannotResetUris.add(uri.toString());
                            /*PicassoCannotResetDataSource picassoDS = new PicassoCannotResetDataSource(NextGenExperience.getContext());
                            picassoDS.open();
                            picassoDS.addToDB(uri.toString());
                            picassoDS.close();*/
                        }
                    }
                }).build();

    }

    public static void loadImageIntoView(Context context, String url, ImageView poster){
        if (StringHelper.isEmpty(url))
            return;
        if (cannotResetUris.contains(url)){
            new ImageLoadTask(url, poster).execute();
        }else
            PicassoTrustAll.getInstance(context).load(url).fit().centerInside().into(poster);
    }

    public static class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
            this.imageView.setTag(R.id.next_gen_detail_full_image, url);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (url.equals(imageView.getTag(R.id.next_gen_detail_full_image))) {
                imageView.setImageBitmap(result);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }

    }

    public static void initiateBadUrlHash(){
        /*PicassoCannotResetDataSource picassoDS = new PicassoCannotResetDataSource(FlixsterApplication.getContext());
        picassoDS.open();
        cannotResetUris.addAll(picassoDS.getAllBadUrls());
        picassoDS.close();*/
    }

    public static Picasso getInstance(Context context) {
        if (mInstance == null) {
            new PicassoTrustAll(context);
        }
        return mInstance;
    }
}