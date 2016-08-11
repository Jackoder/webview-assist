package com.nd.hy.android.webview.library;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author Jackoder
 * @version 2015/7/13.
 */
public class WebViewDelegate {

    private Context             mContext;
    private WebView             mWebView;
    private WebSettings         mWebSettings;
    private SafeWebChromeClient mChromeClient;

    private WebViewDelegate(WebView webView) {
        this.mWebView = webView;
        this.mContext = mWebView.getContext();
        this.mWebSettings = mWebView.getSettings();
    }

    public static WebViewDelegate target(WebView webView) {
        if (null == webView) {
            throw new IllegalArgumentException("Argument 'webView' cannot be null!");
        }
        WebViewDelegate delegate = new WebViewDelegate(webView);
        delegate.removeSearchBoxImpl();
        return delegate;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void removeSearchBoxImpl() {
        if (hasHoneycomb() && !hasJellyBeanMR1()) {
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        }
    }


    public WebViewDelegate defaultSettings() {
        //User-Agent
//        if (sUserAgent == null) {
//            sUserAgent = UserAgentUtils.get(mContext);
//        }
//        mWebSettings.setUserAgentString(sUserAgent);
        //自动适配 PC 页面
        //支持html有viewport的meta标签，例如：设定支持缩放，最大两倍缩放
        //<meta name="viewport" content="width=device-width,user-scalable=yes  initial-scale=1.0, maximum-scale=2.0">
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        //Zoom
        enableZoom();
        //Zoom Controls
        disableZoomControls();
        //JavaScript
        enableJavaScript();
        //Database
        enableDatabase();
        //Geolocation
        enableGeolocation();
        //App Cache
        disableAppCache();
        //Accelerate
        mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //Save Password
        disableSavePassword();
        return this;
    }

    public WebViewDelegate go(String url) {
        mWebView.loadUrl(url);
        return this;
    }

    public WebViewDelegate setWebViewClient(WebViewClient webViewClient) {
        mWebView.setWebViewClient(webViewClient);
        return this;
    }

    @SuppressLint("JavascriptInterface")
    public WebViewDelegate addJavascriptInterface(Object obj, String interfaceName) {
        if (TextUtils.isEmpty(interfaceName)) {
            return this;
        }
        // 如果在4.2以上，直接调用 WebView 的方法来注册
        if (hasJellyBeanMR1()) {
            mWebView.addJavascriptInterface(obj, interfaceName);
        } else {
            if (mChromeClient == null) {
                mChromeClient = new SafeWebChromeClient();
            }
            mChromeClient.addJavascriptInterface(obj, interfaceName);
            mWebView.setWebChromeClient(mChromeClient);
        }
        return this;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public WebViewDelegate removeJavascriptInterface(String interfaceName) {
        if (TextUtils.isEmpty(interfaceName)) {
            return this;
        }
        // 如果在4.2以上，直接调用基类的方法来注册
        if (hasJellyBeanMR1()) {
            mWebView.removeJavascriptInterface(interfaceName);
        } else {
            if (mChromeClient != null) {
                mChromeClient.removeJavascriptInterface(mWebView, interfaceName);
            }
        }
        return this;
    }

    public WebViewDelegate setWebChromeClient(WebChromeClient webChromeClient) {
        if (mChromeClient != null && !(webChromeClient instanceof SafeWebChromeClient)) {
            throw new IllegalArgumentException("Argument 'webChromeClient' is not type of SafeWebChromeClient!");
        }
        mWebView.setWebChromeClient(webChromeClient);
        return this;
    }

    private boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    private boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    public WebViewDelegate setUserAgentString(String userAgent) {
        if (!TextUtils.isEmpty(userAgent)) {
            mWebSettings.setUserAgentString(userAgent);
        }
        return this;
    }

    public WebViewDelegate enableZoom() {
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setSupportZoom(true);
        return this;
    }

    public WebViewDelegate disableZoom() {
        mWebSettings.setBuiltInZoomControls(false);
        mWebSettings.setSupportZoom(false);
        return this;
    }

    public WebViewDelegate enableDatabase() {
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setDatabasePath(mContext.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath());
        return this;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public WebViewDelegate enableZoomControls() {
        if (hasHoneycomb()) {
            mWebView.getSettings().setDisplayZoomControls(true);
        }
        return this;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public WebViewDelegate disableZoomControls() {
        if (hasHoneycomb()) {
            mWebView.getSettings().setDisplayZoomControls(false);
        }
        return this;
    }

    public WebViewDelegate disableDatabase() {
        mWebSettings.setDatabaseEnabled(false);
        return this;
    }

    public WebViewDelegate enableJavaScript() {
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        return this;
    }

    public WebViewDelegate disableJavaScript() {
        mWebSettings.setJavaScriptEnabled(false);
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        return this;
    }

    /**
     * 需要额外添加权限：ACCESS_FINE_LOCATION 和 ACCESS_COARSE_LOCATION
     */
    public WebViewDelegate enableGeolocation() {
        mWebSettings.setGeolocationEnabled(true);
        mWebSettings.setGeolocationDatabasePath(mContext.getApplicationContext().getDir("location", Context.MODE_PRIVATE).getPath());
        return this;
    }

    public WebViewDelegate disableGeolocation() {
        mWebSettings.setGeolocationEnabled(false);
        return this;
    }

    public WebViewDelegate enableAppCache() {
        mWebSettings.setAppCacheEnabled(true);
        mWebSettings.setAppCachePath(mContext.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath());
        return this;
    }

    public WebViewDelegate disableAppCache() {
        mWebSettings.setAppCacheEnabled(false);
        return this;
    }

    public WebViewDelegate enableSavePassword() {
        mWebSettings.setSavePassword(true);
        return this;
    }

    public WebViewDelegate disableSavePassword() {
        mWebSettings.setSavePassword(false);
        return this;
    }

    public WebView getWebView() {
        return mWebView;
    }

    public WebSettings getWebSettings() {
        return mWebSettings;
    }

}
