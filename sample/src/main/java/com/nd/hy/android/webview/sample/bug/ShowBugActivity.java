package com.nd.hy.android.webview.sample.bug;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.nd.hy.android.webview.sample.R;

/**
 * @author Jackoder
 * @version 2015/7/13.
 */
public class ShowBugActivity extends Activity {

    WebView mWebView;
    Button  mBtnLoad;
    Button  mBtnRefresh;
    String mUrl   = "file:///android_asset/test.html";
    String mJs    = "file:///android_asset/js3.html";
    Object mJsObj = new JSInterface();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bug);

        mWebView = (WebView) findViewById(R.id.webview);
        mBtnLoad = (Button) findViewById(R.id.btn_load);
        mBtnRefresh = (Button) findViewById(R.id.btn_refresh);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JSInterface(), "jsInterface");
        mBtnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl(mUrl);
            }
        });
        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.reload();
            }
        });
    }

    class JSInterface {
        @JavascriptInterface
        public String onButtonClick(String text) {
            final String str = text;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("leehong2", "onButtonClick: text = " + str);
                    Toast.makeText(getApplicationContext(), "onButtonClick: text = " + str, Toast.LENGTH_LONG).show();
                }
            });

            return "This text is returned from Java layer.  js text = " + text;
        }

        @JavascriptInterface
        public void onImageClick(String url, int width, int height) {
            final String str = "onImageClick: text = " + url + "  width = " + width + "  height = " + height;
            Log.i("leehong2", str);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
