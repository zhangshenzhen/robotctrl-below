package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rg2.activity.*;

/**
 * Created by shenzhen on 2017/1/7.
 */

public class SplashActivity extends com.rg2.activity.BaseActivity {

    private ImageView imageView;

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        imageView = (ImageView) findViewById(R.id.imageView);
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

          //动画的时机的处理事件监听事件;
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override // @Override 动画结束后调用的方法;
            public void onAnimationEnd(Animation animation) {
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

    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initViewData() {

    }
}
