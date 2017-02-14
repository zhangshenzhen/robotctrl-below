package com.rg2.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.display.DisplayManager;
import android.media.MediaRouter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;



/**
 * 作者：王先云 on 2016/7/1 10:23
 * 邮箱：wangxianyun1@163.com
 * 描述：一句话简单描述
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {
    public Context mContext;
       // 媒体路由器
    public MediaRouter mMediaRouter;
    //屏幕管理器
    public DisplayManager mDisplayManager;
    public int mTotalDisplays = 0;//屏幕个数
    public String mSecondaryTouch;

    public Object mPresentation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;

        super.onCreate(savedInstanceState);
        //设置为横屏幕;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        initViews(savedInstanceState);//主屏幕
        Log.e("Baseactivity","....1");
        //获取媒体路由器
        mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);         // 控制和管理路由的媒体服务
        //获取双屏异显的设备
        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);        // 与显示设备交互服务
        //     列出可用的屏幕
        while (true) {
            Display display = mDisplayManager.getDisplay(mTotalDisplays);   // Gets information about a logical display.
            if (display == null)
                break;
            mTotalDisplays++;//  mTotalDisplays = 1;
        }
        mSecondaryTouch = System.getProperty("persist.secondary.touch", "ft5x06");

        //intent中获取数据
        initData();
        // 代码和初始化数据
        initViewData();
        //事件;
        initEvent();
    }


    //回调函数;
    public final MediaRouter.SimpleCallback mMediaRouterCallback =
            new MediaRouter.SimpleCallback() {
                @Override
                public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {

                    updatePresentation();
                }
                @Override
                public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {

                    updatePresentation();
                }
                @Override
                public void onRoutePresentationDisplayChanged(MediaRouter router, MediaRouter.RouteInfo info) {
                   // Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info);

                    updatePresentation();
                }
            };

        protected abstract void updatePresentation();

    public final DialogInterface.OnDismissListener mOnDismissListener =
            new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    // Log.d(TAG, "onDismiss: ");
                    if (dialog == mPresentation) {
                        Log.e("mOnDismissListener","...........666");
                        mPresentation = null;
                    }
                }
            };


    /**
     * 从intent中获取数据
     */
    protected abstract void initData();

    /**
     * 存放从xml中获取ui,例如 findViewById
     */
    protected abstract void initViews(Bundle savedInstanceState);


    /**
     * 初始化页面UI事件,例如 setOnClickListener
     */
    protected abstract void initEvent();

    /**
     * 存放刷新页面的代码和初始化数据
     */
    protected abstract void initViewData();

    @Override
    public void onClick(View v){
    }

    @Override
    protected void onPause() {
        super.onPause();
        updatePresentation();
     mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        View decorView = getWindow().getDecorView();
        //        Hide both the navigation bar and the status bar.
        //        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        //        a general rule, you should design your app to hide the status bar whenever you
        //        hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        super.onResume();
        updatePresentation();
    }

    //@Override
    /*public boolean dispatchTouchEvent(MotionEvent event) /*{
        float x,y;
        x = event.getX();
        //250
        y=event.getY();
        Log.d("TAG", "dispatchTouchEvent: "+x+":"+" "+y);
        event.setLocation(x*1280/1024,y*750/768);

        return super.dispatchTouchEvent(event);
    }*/
}
