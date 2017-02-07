package com.brick.robotctrl;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.display.DisplayManager;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.presentation.ActivityViewWrapper;
import com.presentation.Okienko;
import com.presentation.SamplePresentation;
import com.rg2.activity.*;

/**
 * Created by shenzhen on 2017/1/7.
 */

public class SplashActivity extends com.rg2.activity.BaseActivity {

    private ImageView imageView;


    private SamplePresentation mPresentation;
    private TextView tvNext;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);//主屏幕;
        Log.e("activity","....1");
    }

    public void updatePresentation() {
        Log.e("调用副屏","....1");
       // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay =  route  !=  null ? route.getPresentationDisplay() : null;

        // 注释 : Dismiss the current presentation if the display has changed.
        if (mPresentation != null && mPresentation.getDisplay() !=  presentationDisplay) {
            mPresentation.dismiss();
            mPresentation = null;
        }
        if (mPresentation == null &&  presentationDisplay != null) {

            // Initialise a new Presentation for the Display

            mPresentation = new SamplePresentation(this,  presentationDisplay);
            // Log.d(TAG, "updatePresentation: this: "+ this.toString());
            mPresentation.setOnDismissListener(mOnDismissListener);

            // Try to show the presentation, this might fail if the display has
            // gone away in the mean time
            try {
                mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed
                // Log.d(TAG, "updatePresentation: failed");
                mPresentation = null;
            }
        }
    }

    @Override
    protected void initData() {
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    @Override
    protected void onResume() {
        Log.e("获取焦点","....1");
       // Log.d(TAG, "onResume: ");
        super.onResume();
        // Register a callback for all events related to live video devices
        mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);
        // Update the displays based on the currently active routes
        updatePresentation();
    }
    @Override
    protected void onPause() {
       // Log.d(TAG, "onPause: ");
        super.onPause();
        Log.e("失去焦点","....1");
        // Stop listening for changes to media routes.
        updatePresentation();
        Log.e("失去焦点","....11");
    }


    @Override
    protected void onStop() {
        //Log.d(TAG, "onStop: ");
        super.onStop();
        // Dismiss the presentation when the activity is not visible.
        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    @Override  //动画
    protected void initEvent() {
        //动画旋转
        RotateAnimation animation = new RotateAnimation(0,1080, Animation.RELATIVE_TO_SELF
                ,0.5f , Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1600);
        animation.setFillAfter(true);//保持结束状态
        //设置缩放
        ScaleAnimation scaleAnimation = new ScaleAnimation(0,1,0,1,Animation.RELATIVE_TO_SELF
                ,0.5f , Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1600);
        scaleAnimation.setFillAfter(true);
        //设置渐变色
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setFillAfter(true);

        //开启动画集合;
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(animation);
        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);
        imageView.startAnimation(set);

      /*  //动画的时机的处理事件监听事件;
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override // @Override 动画结束后调用的方法;
            public void onAnimationEnd(Animation animation) {
               // SystemClock.sleep(1000);//睡眠
                Intent intent = new Intent(SplashActivity.this,  RobotInfoActivity.class);
                 startActivity(intent);
            }
            @Override    //动画开始执行的方法;
            public void onAnimationStart(Animation animation) {
            }
            @Override   //重复动画的时执行的方法;
            public void onAnimationRepeat(Animation animation) {

            }
        });
*/
    }

    @Override
    protected void initViewData() {
        tvNext = (TextView) findViewById(R.id.tv_next);
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(SplashActivity.this, MainActivity.class);
             startActivity(intent);
            }
        });
       }
}
