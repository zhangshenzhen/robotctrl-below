package com.brick.robotctrl;

import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.card.ApplyForActivity;
import com.presentation.RobtInfoPresentation;

public class RobotInfoActivity extends com.rg2.activity.BaseActivity {
    private final String TAG = "RobotInfoActivity";

//    UserTimer userTimer = null;

    private RobtInfoPresentation mrobtInfoPresentation;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Log.d(TAG, "RobotInfoActivity: ...............robot..2");
        setContentView(R.layout.activity_robotinfo);
        //  getdisplay();//获取多个屏幕的方法;抽取到BaseActivity
        //  userTimer = new UserTimer();
    }


    public void updatePresentation() {
        Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay =  route  !=  null ? route.getPresentationDisplay() : null;

        // 注释 : Dismiss the current presentation if the display has changed.
        if (mrobtInfoPresentation != null && mrobtInfoPresentation.getDisplay() !=  presentationDisplay) {
            mrobtInfoPresentation.dismiss();
            mrobtInfoPresentation = null;
        }
        if (mrobtInfoPresentation == null &&  presentationDisplay != null) {
            // Initialise a new Presentation for the Display

            mrobtInfoPresentation = new RobtInfoPresentation(this,  presentationDisplay);

            Log.d(TAG, "updatePresentation: this: "+ this.toString());
            mrobtInfoPresentation.setOnDismissListener(mOnDismissListener);

            // Try to show the presentation, this might fail if the display has
            // gone away in the mean time
            try {

                mrobtInfoPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed
                Log.d(TAG, "updatePresentation: failed");
                mrobtInfoPresentation = null;
            }
        }
    }

    @Override
    protected void initData() {

    }



    @Override
    protected void initEvent() {

    }

    @Override
    protected void initViewData() {
    }


    public void clickEntery(View view){
        Log.e("RobotInfoActivity","1----"+System.currentTimeMillis());
         Intent intent = new Intent(RobotInfoActivity.this, MainActivity.class );
         startActivity(intent);
        Log.e("RobotInfoActivity","2----"+System.currentTimeMillis());

    }
    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        // Dismiss the presentation when the activity is not visible.
        if (mrobtInfoPresentation != null) {
            mrobtInfoPresentation.dismiss();
            mrobtInfoPresentation = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register a callback for all events related to live video devices
       mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);
        // Update the displays based on the currently active routes
       // updatePresentation();//在弗雷中被调用
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        //clearTimerCount();
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

    }
}
