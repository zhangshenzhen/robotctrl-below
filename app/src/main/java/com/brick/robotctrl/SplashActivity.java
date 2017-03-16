package com.brick.robotctrl;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.display.DisplayManager;
import android.media.MediaPlayer;
import android.media.MediaRouter;
import android.net.Uri;
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
import android.widget.VideoView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.presentation.ActivityViewWrapper;
import com.presentation.Okienko;
import com.presentation.SamplePresentation;
import com.rg2.activity.*;
import com.rg2.activity.BaseActivity;

import zime.ui.ZIMEAVDemoService;

/**
 * Created by shenzhen on 2017/1/7.
 */

public class SplashActivity extends BaseActivity {

    private static final String TAG ="SplashActivity" ;
    private SamplePresentation mPresentation;
    private VideoView vv;
    private  Uri mUri;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);//主屏幕;
        vv = (VideoView) findViewById(R.id.vv);
    }

    @Override
    protected void initData() {

        Intent startIntent = new Intent(this, ZIMEAVDemoService.class);
        startService(startIntent); // 启动服务

         mUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.red);
         vv.setVideoURI(Uri.parse(String.valueOf(mUri)));
         vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
          @Override
          public void onPrepared(MediaPlayer mediaPlayer) {
                vv.start();
          }
        });
    }
    @Override
    protected void initViewData() {
    }

    @Override
    protected void initEvent() {
        //视频播放的监听事件;
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
            startActivity( new Intent(SplashActivity.this, MainActivity.class));
            }
        });
    }



    @Override
    protected void onResume() {
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
      updatePresentation();
    }

    @Override
    protected void onStop() {
         super.onStop();
        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    public void updatePresentation() {
        // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;

        // 注释 : Dismiss the current presentation if the display has changed.
        if (mPresentation != null && mPresentation.getDisplay() != presentationDisplay) {
            mPresentation.dismiss();
            mPresentation = null;
        }
        if (mPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mPresentation = new SamplePresentation(this, presentationDisplay);
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

}
