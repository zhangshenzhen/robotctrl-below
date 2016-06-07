package com.brick.robotctrl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "MainActivity";

    SharedPreferences.OnSharedPreferenceChangeListener presChangeListener = null;

    public static ImageView pointView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pointView = (ImageView) findViewById(R.id.pointView);
        pointView.setOnTouchListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //去除ToolBar上的文字
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        //NOTE OnSharedPreferenceChangeListener 侦听配置改变
        presChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            private final String robotName = getString(R.string.robotName);
            private final String serverIp = getString(R.string.serverIp);
            private final String serverPort = getString(R.string.serverPort);
            private final String controlType = getString(R.string.controlType);

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(controlType)) {
                    boolean val = sharedPreferences.getBoolean(key, false);
                    changeCtrlType(val);
                    Log.i(TAG, "onSharedPreferenceChanged: " + key + " " + val);
                } else {
                    String val = null;
                    try {
                        val = sharedPreferences.getString(key, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (key.equals(robotName)) {
                        // do some thing
                    } else if (key.equals(serverIp)) {
                        // do some thing
                    } else if (key.equals(serverPort)) {
                        // do some thing
                    }
                    Log.i(TAG, "onSharedPreferenceChanged: " + key + " " + val);
                }
            }
        };
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(presChangeListener);
    }

    // relative menu
    Menu menu = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: set menu UI");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    boolean gravityCtrlEnable = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onCreateOptionsMenu: "+item);
        switch (item.getItemId()) {
            // menu context
            case R.id.actionSettings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, 0);
                // do some thing else
                break;
            case R.id.actionSwitchCtrl:
                changeCtrlType(!gravityCtrlEnable);
                // do some thing else
                break;
            default:
                break;
        }
        return true;
    }

    private void changeCtrlType(boolean enable) {
        gravityCtrlEnable = enable;
        if (menu != null) {
            menu.findItem(R.id.actionSwitchCtrl).setIcon(enable ?
                    R.drawable.ic_action_changectrl :
                    R.drawable.ic_action_changectrl_disable);
        }
    }


    PointF lastPoint = new PointF(), initPoint = new PointF(-1f, -1f);
    public static float MAX_RADIUS = 0f;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.pointView) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i(TAG, "onTouch: ACTION_DOWN X:"+event.getRawX()+" Y:"+event.getRawY());
                    lastPoint.set(event.getRawX(), event.getRawY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.i(TAG, "onTouch: ACTION_MOVE X:"+event.getRawX()+" Y:"+event.getRawY());
                    PointF distance = new PointF();
                    distance.set(lastPoint);
                    distance.offset(-event.getRawX(), -event.getRawY());

                    float nextX = pointView.getX() - distance.x;
                    float nextY = pointView.getY() - distance.y;

                    Log.i(TAG, "onTouch: ACTION_MOVE ===========pointView.getX():"+pointView.getX()+"-distance.x:"+distance.x);
                    // limit the pointView in the circle
                    float lengthX = nextX - initPoint.x, lengthY = nextY - initPoint.y;
                    float sqLength = (float) Math.hypot(lengthX, lengthY);
                    if (sqLength > MAX_RADIUS) {
                        nextX = initPoint.x + MAX_RADIUS / sqLength * lengthX;
                        nextY = initPoint.y + MAX_RADIUS / sqLength * lengthY;
                        lastPoint.set(nextX + pointView.getWidth() / 2, nextY + pointView.getHeight() / 2);
                    } else {
                        lastPoint.set(event.getRawX(), event.getRawY());
                    }
                    animationMoveTo(nextX, nextY, 0);
                    break;
                case MotionEvent.ACTION_UP: {
                    Log.i(TAG, "onTouch: ACTION_UP X:"+event.getRawX()+" Y:"+event.getRawY());
                    // back to the center
                    animationMoveTo(initPoint, 150);
                }
                break;
                default:
                    break;
            }
        }
        return true;
    }

    private void animationMoveTo(PointF point, long duration) {
        animationMoveTo(point.x, point.y, duration);
    }
    private void animationMoveTo(float nextX, float nextY, long duration) {
        // move the animator
        ObjectAnimator x = ObjectAnimator.ofFloat(pointView, "x", pointView.getX(), nextX);
        ObjectAnimator y = ObjectAnimator.ofFloat(pointView, "y", pointView.getY(), nextY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(x, y);
        animatorSet.setDuration(duration);
        animatorSet.start();
    }

    // limit the speed of pointView
    int screenHeight, screenWidth;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // 这里来获取容器的宽和高
        if (hasFocus) {
            Point screen = new Point();
            getWindowManager().getDefaultDisplay().getSize(screen);
            screenHeight = screen.y;
            screenWidth = screen.x;
            MAX_RADIUS = screenHeight * 0.35f;
            if (initPoint.x < 0f && initPoint.y < 0f) {
                initPoint.set(pointView.getX(), pointView.getY());
                presChangeListener.onSharedPreferenceChanged(PreferenceManager.getDefaultSharedPreferences(this), getString(R.string.controlType));
            }
            Log.i(TAG, "onWindowFocusChanged: " + initPoint.toString());
        }
    }
}
