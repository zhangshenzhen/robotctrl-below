package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends BaseActivity implements View.OnTouchListener {
    private final String TAG = "TestActivity";

    UserTimer userTimer = null;
    RelativeLayout testActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        testActivity = (RelativeLayout) findViewById(R.id.testActivity);
        testActivity.setOnTouchListener(this);
        userTimer = new UserTimer();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        userTimer.clearTimerCount();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        Log.d(TAG, "OnTouch: Touch Screen");
        userTimer.clearTimerCount();
        return true;
    }
}
