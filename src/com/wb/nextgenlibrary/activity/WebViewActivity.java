package com.wb.nextgenlibrary.activity;

import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.util.utils.F;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by gzcheng on 8/8/16.
 */
public class WebViewActivity extends NGEHideStatusBarActivity {

    protected WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        final String url = getIntent().getStringExtra(F.URL);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        /* Do whatever you need here */
                return super.onJsAlert(view, url, message, result);
            }
        });

        WebViewClient webViewMainWebClient = new WebViewClient()
        {
            // Override page so it's load on my view only
            @Override
            public boolean shouldOverrideUrlLoading(WebView  view, String  url)
            {
                // Return true to override url loading (In this case do nothing).
                return false;
            }
        };
        webView.setWebViewClient(webViewMainWebClient);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new JavascriptHandler(), "microHTMLInterface");
        webView.loadUrl(url);

    }


    protected class JavascriptHandler {
        JavascriptHandler() {
        }
        @JavascriptInterface
        public void AppShutdown(){
            WebViewActivity.this.finish();
        }
        @JavascriptInterface
        public void postMessage(String msg){
            if ("AppVisible".equals(msg)){

            }else if ("AppShutdown".equals(msg)){
                WebViewActivity.this.finish();
            }
        }

        @JavascriptInterface
        public void appLoaded() {

        }

        @JavascriptInterface
        public void error(String response) {
        }
    }

    private String readHtml(String remoteUrl) {
        String out = "";
        BufferedReader in = null;
        try {
            URL url = new URL(remoteUrl);
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                out += str;
            }
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return out;
    }

    @Override
    public void onDestroy(){
        if (webView != null)
            webView.destroy();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null){
            webView.pauseTimers();
            webView.onPause();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (webView != null){
            webView.resumeTimers();
            webView.onResume();
        }
    }
}