package com.brick.robotctrl;

import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

/**
 * Created by li on 2016/8/8.
 */
public class AboutActivity extends BaseActivity {
    private final String TAG = "AboutActivity";

    //    UserTimer userTimer = null;
    RelativeLayout aboutActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
//        userTimer = new UserTimer();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        clearTimerCount();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
