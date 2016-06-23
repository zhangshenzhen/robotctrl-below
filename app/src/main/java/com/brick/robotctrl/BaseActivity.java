package com.brick.robotctrl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public abstract class BaseActivity extends AppCompatActivity {
    private String TAG = "BaseActivity";
    UserTimer userTimer = null;
    private static int timerOutCount = 0;

    public void clearTimerCount() {
        this.timerOutCount = 0;
    }

    public void addTimerCount() {
        this.timerOutCount++;
    }

    public int getTimerCount() {
        return this.timerOutCount;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
//        Hide both the navigation bar and the status bar.
//        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        a general rule, you should design your app to hide the status bar whenever you
//        hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        View decorView = getWindow().getDecorView();
//        Hide both the navigation bar and the status bar.
//        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        a general rule, you should design your app to hide the status bar whenever you
//        hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
