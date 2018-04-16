package com.rg2.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.brick.robotctrl.R;

/**
 * Created by lx on 2018-03-28.
 */

public class BankActivity extends com.brick.robotctrl.BaseActivity {

    public WebView webView;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.wecther_activity);
    }

    @Override
    protected void initData() {
        webView = (WebView) findViewById(R.id.wv2);

    }
    @Override
    protected void initEvent() {
        webView.getSettings().setJavaScriptEnabled(true);
        // webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setAppCacheEnabled(true);
        //设置 缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUserAgentString("电脑");

        //设置自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN );
        webView.getSettings().setLoadWithOverviewMode ( true );
        String path = "http://www.boc.cn/";
        webView.loadUrl(path);
        //设置使用WevView加载 不使用系统浏览器加载;
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
        finish();//结束退出程序
        return false;
    }

    @Override
    protected void initViewData() {

    }

    public static void startActionBank(Context context,int index) {
        Intent changeMotionIntent = new Intent();
        changeMotionIntent.setClass(context, BankActivity.class);
        Log.d("TAG", "changeExpression: 开启银行");
        context.startActivity(changeMotionIntent);
    }
}
