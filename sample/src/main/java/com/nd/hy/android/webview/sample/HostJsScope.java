/**
 * Summary: js脚本所能执行的函数空间
 * Version 1.0
 * Date: 13-11-20
 * Time: 下午4:40
 * Copyright: Copyright (c) 2013
 */
package com.nd.hy.android.webview.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.TelephonyManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.nd.hy.android.webview.library.BaseJSInterface;

public class HostJsScope extends BaseJSInterface {

    private Context mContext;

    public HostJsScope(Context context) {
        mContext = context;
    }
    /**
     * 短暂气泡提醒
     * @param message 提示信息
     * */
    @JavascriptInterface
    public void toast (String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 可选择时间长短的气泡提醒
     * @param message 提示信息
     * @param isShowLong 提醒时间方式
     * */
    @JavascriptInterface
    public void toast (String message, int isShowLong) {
        Toast.makeText(mContext, message, isShowLong).show();
    }

    /**
     * 弹出记录的测试JS层到Java层代码执行损耗时间差
     * @param timeStamp js层执行时的时间戳
     * */
    @JavascriptInterface
    public void testLossTime (int timeStamp) {
        timeStamp = (int) (System.currentTimeMillis() - timeStamp);
        alert(String.valueOf(timeStamp));
    }

    /**
     * 系统弹出提示框
     * @param message 提示信息
     * */
    @JavascriptInterface
    public void alert (String message) {
        // 构建一个Builder来显示网页中的alert对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.dialog_title_system_msg));
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    /**
     * 获取设备IMSI
     * @return 设备IMSI
     * */
    @JavascriptInterface
    public String getIMSI () {
        return ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }

    //---------------- 界面切换类 ------------------

    /**
     * 结束当前窗口
     * */
    @JavascriptInterface
    public void goBack () {
        if (mContext instanceof Activity) {
            ((Activity)mContext).finish();
        }
    }

    @JavascriptInterface
    public int overloadMethod(int val) {
        return val;
    }

    @JavascriptInterface
    public String overloadMethod(String val) {
        return val;
    }

    @JavascriptInterface
    public long passLongType (int i) {
        return i;
    }

    public static class JavaObj {
        public int code;
        public String message;
    }

    @JavascriptInterface
    public String passJsonString (String str) {
        JavaObj obj = convertJson2Data(JavaObj.class, str);
        return obj.message;
    }
}