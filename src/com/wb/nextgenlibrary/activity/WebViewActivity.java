package com.wb.nextgenlibrary.activity;

import android.os.Bundle;
import android.webkit.WebView;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.util.utils.F;

/**
 * Created by gzcheng on 8/8/16.
 */
public class WebViewActivity extends NextGenHideStatusBarActivity {

    protected WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        String url = getIntent().getStringExtra(F.URL);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

    }

}