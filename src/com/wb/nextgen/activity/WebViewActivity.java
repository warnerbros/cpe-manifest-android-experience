package com.wb.nextgen.activity;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

import com.wb.nextgen.R;
import com.wb.nextgen.util.utils.F;

/**
 * Created by gzcheng on 8/8/16.
 */
public class WebViewActivity extends NextGenHideStatusBarActivity {

    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        String url = getIntent().getStringExtra(F.URL);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

    }

}