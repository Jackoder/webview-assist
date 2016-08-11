# WebView
[![](https://www.jitpack.io/v/Jackoder/webview-assist.svg)](https://www.jitpack.io/#Jackoder/webview-assist)

## 特性
- 统一 WebView 配置
- 提供安全的 JS 接口调用解决方案

## 添加依赖
- Maven

```xml
<dependency>
    <groupId>com.github.Jackoder</groupId>
    <artifactId>webview-assist</artifactId>
    <version>1.1</version>
</dependency>
```
- Gradle
```gradle
compile 'com.github.Jackoder:webview-assist:1.1'
```

## 使用
### 通用方法
```Java
WebViewDelegate.target(mWebView)
	.defaultSettings()
	.setWebViewClient(new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	//do something
            return super.shouldOverrideUrlLoading(view, url);
		}
	})
    .go("file:///android_asset/example.html");
```
### Javascript 与 Android 间调用

#### Javascript 调用 Android 接口

首先，添加接口

```Java
public class MyInterface extends BaseJSInterface {

	private Context mContext;

	public MyInterface(Context context) {
		mContext = context;
	}

	/**
	 * 短暂气泡提醒
	 * @param message 提示信息
	 */
	@JavascriptInterface
	public void toast (String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}
}
```
注册接口到 WebView

```Java
WebViewDelegate.target(mWebView)
	.defaultSettings()
	.setWebViewClient(new WebViewClient() {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			mWebView.loadUrl(url);
            return true;
        }
    })
    .addJavascriptInterface(new MyInterface(this), "HyAndroid")
    .go("file:///android_asset/example.html");
```
Javascript 调用
```Javascript
HyAndroid.toast("Hello world!");
```
#### Android 调用 Javascript 接口

在 Html 页面添加 JS 脚本

```Javascript
<html>
<head>
  <title></title>
    <script type="text/javascript">
        function setData(data)
        {
        	//do something
        }
    </script>
</head>
<body>
</body>
</html>
```
WebView 调用 Javscript 接口

```Java
public void example() {
	mWebView.loadUrl("javscript:setData('" + getData() + "')");
}
public static String getData(){
	try {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("code", 200);
		jsonObj.put("message", "Success");
		jsonObj.put("data", "Android call javascript example.");
        return jsonObj.toString();
    } catch (JSONException e) {
        e.printStackTrace();
    }
    return null;
}
```

## 设计与实现
### 通用配置
WebView 的配置包括页面缩放、JS 支持、数据库、缓存以及用户代理等等，因此需要对配置做统一的封装，减少项目开发中的配置工作。WebViewDelegate 就提供了这个功能。调用 WebViewDelegate 的 `defaultSettings` 方法，为 WebView 设置通用配置，配置信息如下：
```Java
public WebViewDelegate defaultSettings() {
	//用户代理
    if (sUserAgent == null) {
    	sUserAgent = UserAgentUtils.get(mContext);
	}
	mWebSettings.setUserAgentString(sUserAgent);
	//自动适配 PC 页面
    mWebSettings.setUseWideViewPort(true);
    mWebSettings.setLoadWithOverviewMode(true);
    //开启缩放
	mWebSettings.setBuiltInZoomControls(true);
	mWebSettings.setSupportZoom(true);
	//开启 JavaScript
    mWebSettings.setJavaScriptEnabled(true);
    mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    //启用 Dom Storage
    mWebSettings.setDomStorageEnabled(true);
    //启用数据库，配置数据库路径
    mWebSettings.setDatabaseEnabled(true);
    mWebSettings.setDatabasePath(mContext.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath());
    //地理信息支持，需要添加 ACCESS_FINE_LOCATION 和 ACCESS_COARSE_LOCATION 权限才有效
	mWebSettings.setGeolocationEnabled(true);
	mWebSettings.setGeolocationDatabasePath(mContext.getApplicationContext().getDir("location", Context.MODE_PRIVATE).getPath());
    //不启用本地缓存，始终从 Internet 获取
    mWebSettings.setAppCacheEnabled(false);
    //使用加速渲染
    mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
    //不启用密码保存（明文密码会保存到应用数据目录的databases/webview.db中，如果手机被root就可以被获取，敏感数据泄漏。）
    disableSavePassword();
    return this;
}
```
如果项目中需要开关这些选项可以调用 WebViewDelegate 内的方法：

|方法|描述|
|---|---|
|WebViewDelegate enableZoom()|启用缩放|
|WebViewDelegate disableZoom()|不启用缩放|
|WebViewDelegate enableDatabase()|启用数据库|
|WebViewDelegate disableDatabase()|不启用数据库|
|WebViewDelegate enableJavaScript()|启用 Javascript|
|WebViewDelegate disableJavaScript()|不启用 Javascript|
|WebViewDelegate enableGeolocation()|启用地理信息|
|WebViewDelegate disableGeolocation()|不启用地理信息|
|WebViewDelegate enableAppCache()|启用本地缓存|
|WebViewDelegate disableAppCache()|不启用本地缓存|
|WebViewDelegate enableDomStorage()|启用Dom存储|
|WebViewDelegate disableDomStorage()|不启用Dom存储|
|WebViewDelegate enableSavePassword()|启用密码保存|
|WebViewDelegate disableSavePassword()|不启用密码保存|

### 安全漏洞
首先，看一篇乌云的漏洞报告: [http://drops.wooyun.org/papers/548](http://drops.wooyun.org/papers/548)
针对　WebView 的安全漏洞，Google 在 4.2 及以上版本解决了这个问题，但对 4.2 以下的版本并没有开发修复补丁的打算。网上提供了一套解决方案，能够避免该漏洞。将 JS 中函数调用的方法和参数封装后利用 `prompt()` 方法触发 WebChromeClient 的 `onJsPrompt` 回调，根据方法与参数调用 Java 接口。

WebViewDelegate 中已经封装了该实现，使用方法如下：
```Java
WebViewDelegate.target(mWebView).defaultSettings()
	.addJavscriptInterface(myInterface, "InterfaceName")
    .go(url);
```
方法与参数和 WebView 自带的 `addJavascriptInterface` 相同，看一下 WebViewDelegate 的内部实现：
```Java
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
```
可以看出该解决方案仅处理 4.2 以下版本，系统版本在 4.2 及以上的直接使用原生方法，做到更好的兼容，并保持了 API 的一致性。

## 相关资料
Understanding WebView and Android security patches：[http://www.androidcentral.com/android-webview-security](http://www.androidcentral.com/android-webview-security)

Android WebView的Js对象注入漏洞解决方案：[http://blog.csdn.net/leehong2005/article/details/11808557](http://blog.csdn.net/leehong2005/article/details/11808557)

WebView中的Java与JavaScript提供【安全可靠】的多样互通方案：[https://github.com/pedant/safe-java-js-webview-bridge](https://github.com/pedant/safe-java-js-webview-bridge)

【疑难杂症】Android WebView 无法打开天猫页面：[http://ryanhoo.github.io/blog/2014/09/17/android-webview-setdomstorageenabled](http://ryanhoo.github.io/blog/2014/09/17/android-webview-setdomstorageenabled)