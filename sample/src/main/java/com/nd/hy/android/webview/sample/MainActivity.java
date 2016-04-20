package com.nd.hy.android.webview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.nd.hy.android.webview.library.WebViewDelegate;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity {

    @InjectView(R.id.webview)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        WebViewDelegate.target(mWebView)
                .defaultSettings()
                .setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        mWebView.loadUrl(url);
                        return true;
//                        return super.shouldOverrideUrlLoading(view, url);
                    }
                })
                .addJavascriptInterface(new HostJsScope(this), "HostApp")
                .go("file:///android_asset/example.html");
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
