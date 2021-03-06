package com.brick.robotctrl;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/* 网页
* */
/**
 * Created by lx on 2018-03-28.
 */

  /* 设置支持Js,必须设置的,不然网页基本上不能看
    mWebView.getSettings().setJavaScriptEnabled(true);
    设置缓存模式,我这里使用的默认,不做多讲解
    mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
   设置为true表示支持使用js打开新的窗口
   mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
  大部分网页需要自己保存一些数据,这个时候就的设置下面这个属性
  mWebView.getSettings().setDomStorageEnabled(true);
  设置为使用webview推荐的窗口
  mWebView.getSettings().setUseWideViewPort(true);
  设置网页自适应屏幕大小 ---这个属性应该是跟上面一个属性一起用
  mWebView.getSettings().setLoadWithOverviewMode(true);
  HTML5的地理位置服务,设置为true,启用地理定位
  mWebView.getSettings().setGeolocationEnabled(true);
 设置是否允许webview使用缩放的功能,我这里设为false,不允许
  mWebView.getSettings().setBuiltInZoomControls(false);
  提高网页渲染的优先级
  mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
 设置显示水平滚动条,就是网页右边的滚动条.我这里设置的不显示
  mWebView.setHorizontalScrollBarEnabled(false);
    指定垂直滚动条是否有叠加样式
   mWebView.setVerticalScrollbarOverlay(true);
   设置滚动条的样式
   mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
  这个不用说了,重写WebChromeClient监听网页加载的进度,从而实现进度条
   mWebView.setWebChromeClient(new WebChromeClient());
   同上,重写WebViewClient可以监听网页的跳转和资源加载等等...
        */
public class WeatherActivity extends BaseActivity {

    private static final String TAG ="WeatherActivity.class" ;
    public  static WebView webView;
    public int index;
    public static CameraManager cameraManager;
    public static Context mcontext;
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.wecther_activity);
        mcontext = WeatherActivity.this;
    }

    @Override
    protected void initData() {
        webView = (WebView) findViewById(R.id.wv2);
        Intent intent = getIntent();
        index = intent.getIntExtra("index",0);
        Log.d(TAG,"index = "+ index);
      }
    @Override
    protected void initEvent() {
          /* 设置支持Js,必须设置的,不然网页基本上不能看 */
        webView.getSettings().setJavaScriptEnabled(true);
       // webView.getSettings().setBlockNetworkImage(flase);
        webView.getSettings().setAppCacheEnabled(true);
        //设置 缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUserAgentString("电脑");
        //设定支持缩放
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        //设置自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN );
        webView.getSettings().setLoadWithOverviewMode ( true );
        webView.getSettings().setDatabaseEnabled(true);
        //设置字体大小
        webView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
          loadWeb(index);
    }
    public static void loadWeb(final int index){
           //南京天气
          String url = "http://www.weather.com.cn/weather/101190101.shtml";

          //广州天气http://www.weather.com.cn/weather/101280101.shtml

       // String url =  "https://tianqi.so.com/weather/101010100";
       //广发 http://www.cgbchina.com.cn/
        //中国银行http://www.boc.cn/

        String url2 = "http://www.jsredstonetech.cn/"; //公司网页
            //设置使用WevView加载 不使用系统浏览器加载;
        webView.setWebViewClient(new WebViewClient(){
                @Override
             public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                 return true;
                }
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("WebView", "onPageStarted");
                super.onPageStarted(view, url, favicon);
              }

        });
       // webView.setWebChromeClient(new PublicWebChromeClient());
        //index 是客服点的表情序号 根据那个index 加载哪个网页
         if (index==10){// 只允许着两个？
                webView.loadUrl(url);
          }else if(index ==11) {
                webView.loadUrl(url2);
           }//.......我知道，但是最高表情好像只有11个，网页我可以添加最多多扫个？
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
    protected void initViewData() {}

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "生命-------: onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "生命-------: onPause");
        onStop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "生命-------: onStop");
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "生命-------: onDestroy");
    }

    public static void startActionweathert(Context context, int index) {
        Intent Net = new Intent();
        Net.setClass(context, WeatherActivity.class);
        Log.d("TAG", "changeExpression: 开启网页");
        Net.putExtra("index",index);
        context.startActivity(Net);
    }
}
