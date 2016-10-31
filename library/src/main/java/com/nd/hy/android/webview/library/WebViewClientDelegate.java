package com.nd.hy.android.webview.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author Jackoder
 * @version 2016/10/31
 */
public class WebViewClientDelegate extends WebViewClient {

    private Context       mContext;
    private WebViewClient mWebViewClient;

    private boolean mSSlCheck = true;

    public WebViewClientDelegate(Context context, WebViewClient webViewClient, boolean sslCheck) {
        mContext = context;
        mWebViewClient = webViewClient;
        mSSlCheck = sslCheck;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return mWebViewClient.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        mWebViewClient.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        mWebViewClient.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        mWebViewClient.onLoadResource(view, url);
    }

    @Deprecated
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return mWebViewClient.shouldInterceptRequest(view, url);
        }
        return null;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mWebViewClient.shouldInterceptRequest(view, request);
        }
        return null;
    }

    @Deprecated
    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        mWebViewClient.onTooManyRedirects(view, cancelMsg, continueMsg);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        mWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        mWebViewClient.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        mWebViewClient.doUpdateVisitedHistory(view, url, isReload);
    }

    //http://stackoverflow.com/questions/36050741/webview-avoid-security-alert-from-google-play-upon-implementation-of-onreceiveds
    @Override
    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
        if (mSSlCheck) {
            new AlertDialog.Builder(mContext)
                    .setMessage(R.string.wv_error_ssl_cert_invalid)
                    .setPositiveButton(R.string.wv_continue, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.proceed();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.wv_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.cancel();
                            dialog.dismiss();
                        }
                    }).create().show();
        } else {
            mWebViewClient.onReceivedSslError(view, handler, error);
        }
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebViewClient.onReceivedClientCertRequest(view, request);
        }
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        mWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return mWebViewClient.shouldOverrideKeyEvent(view, event);
    }

    @Deprecated
    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        mWebViewClient.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onUnhandledInputEvent(WebView view, InputEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebViewClient.onUnhandledInputEvent(view, event);
        }
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        mWebViewClient.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mWebViewClient.onReceivedLoginRequest(view, realm, account, args);
        }
    }

}
