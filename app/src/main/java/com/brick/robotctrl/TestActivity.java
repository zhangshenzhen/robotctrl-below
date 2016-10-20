package com.brick.robotctrl;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.zhangyt.log.LogUtil;

public class TestActivity extends BaseActivity  {
    private final String TAG = "TestActivity";

//    UserTimer userTimer = null;
    RelativeLayout testActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        userTimer = new UserTimer();
    }

    @Override
    protected void onStop() {
        LogUtil.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        LogUtil.i(TAG, "onRestart");
        clearTimerCount();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
