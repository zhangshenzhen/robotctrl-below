package com.brick.robotctrl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";

    SharedPreferences.OnSharedPreferenceChangeListener presChangeListener = null;

    TextView gravityTextView = null;
    TextView notifyTextView = null;
    ImageView pointView = null;
    CheckBox dirCtrlSwitch = null;
    SSDBTask ssdbTask = null;

    private boolean serverChanged = false;
    private boolean serialChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pointView = (ImageView) findViewById(R.id.pointView);
        pointView.setOnTouchListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // remove text in toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ssdbTask = new SSDBTask(MainActivity.this, handler);

        notifyTextView = (TextView) findViewById(R.id.notifyTextView);
        pointView = (ImageView) findViewById(R.id.pointView);
        pointView.setOnTouchListener(this);
        dirCtrlSwitch = (CheckBox) findViewById(R.id.dirCtrlCheckBox);
        dirCtrlSwitch.setOnCheckedChangeListener(this);

        //NOTE OnSharedPreferenceChangeListener: listen settings changed
        presChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            private final String robotName = getString(R.string.robotName);
            private final String serverIp = getString(R.string.serverIp);
            private final String serverPort = getString(R.string.serverPort);
            private final String controlType = getString(R.string.controlType);

            private final String serialBaud = getString(R.string.serialBaud);
            private final String serialCom = getString(R.string.serialCOM);

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
                        ssdbTask.setRobotName(val);
                    } else if (key.equals(serverIp)) {
                        ssdbTask.setServerIP(val);
                        serverChanged = true;
                    } else if (key.equals(serverPort)) {
                        int serverPort = Integer.parseInt(val);
                        ssdbTask.setServerPort(serverPort);
                        serverChanged = true;
                    } else if(key.equals(serialCom)) {
                        // do some thing
                        serialChanged = true;
                    } else if(key.equals(serialBaud)) {
                        // do some thing
                        serialChanged = true;
                    }
                    Log.i(TAG, "onSharedPreferenceChanged: " + key + " " + val);
                }
            }
        };
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(presChangeListener);
    }

    // receive ssdb server info
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SSDBTask.ENABLECTRL:
                    dirCtrlSwitch.setChecked(true);
                    break;
                case SSDBTask.ACTION_HGET:
                    String rlt = (String) msg.obj;
                    gravityTextView.setText(rlt);
                    break;
                case SSDBTask.DIRCTRLWARNING:
                    notifyTextView.setText("open switch please");
                    break;
                default:
                    break;
            }
        }
    };

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
    long lastTime = 0, curTime = 0;
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

                    //50ms check once，do not WR ssdb too quick
                    curTime = System.currentTimeMillis();
                    if (curTime - lastTime > 50) {
                        lastTime = curTime;
                        int dir = getMoveDirection(nextX, nextY);
                        ssdbTask.robotMove(dir);
                        notifyTextView.setText(SSDBTask.DirCtrlVals[dir]);
                    }

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
                    ssdbTask.robotMove(SSDBTask.DIR_STOP);
                    notifyTextView.setText(SSDBTask.DirCtrlVals[SSDBTask.DIR_STOP]);
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

    private int getMoveDirection(float x, float y) {
        float deltaX = x - initPoint.x;
        float deltaY = y - initPoint.y;
        float deltaAbsX = Math.abs(deltaX);
        float deltaAbsY = Math.abs(deltaY);
        if (deltaAbsX < MAX_RADIUS / 2 && deltaAbsY < MAX_RADIUS / 2) {
            return SSDBTask.DIR_STOP;
        } else {
            if (deltaAbsY > deltaAbsX) {
                if (deltaY < 0f) {
                    return SSDBTask.DIR_UP;
                } else {
                    return SSDBTask.DIR_DOWN;
                }
            } else {
                if (deltaX < 0f) {
                    return SSDBTask.DIR_LEFT;
                } else {
                    return SSDBTask.DIR_RIGHT;
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.dirCtrlCheckBox) {
            ssdbTask.setDirCtrlEnable(isChecked);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "onActivityResult: " + data.getBooleanExtra("data", false));
                if (serverChanged) {
                    serverChanged = false;
                    ssdbTask.connect();
                }
                if ( serialChanged ) {
                    serialChanged = false;
                    // do some thing
                }
            }
        }
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(presChangeListener);
        ssdbTask.disConnect();
        super.onDestroy();
    }
}
