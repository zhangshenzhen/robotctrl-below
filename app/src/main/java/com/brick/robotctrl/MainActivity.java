package com.brick.robotctrl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jly.expression.expression;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener{
    private static final String TAG = "MainActivity";

    SharedPreferences.OnSharedPreferenceChangeListener presChangeListener = null;

    ImageView leftEyeButton = null;
    ImageView rightEyeButton = null;
    SSDBTask ssdbTask = null;
    SerialCtrl serialCtrl = null;

    private boolean serverChanged = false;
    private boolean serialChanged = false;

    private RelativeLayout mainActivity = null;
    UserTimer userTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // remove text in toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        userTimer = new UserTimer();

        ssdbTask = new SSDBTask(MainActivity.this, handler);
        serialCtrl = new SerialCtrl(MainActivity.this, handler);

        leftEyeButton = (ImageView) findViewById(R.id.leftEyeButton);
        leftEyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTimer.clearTimerCount();
                startActivity(new Intent().setClass(MainActivity.this, expression.class));
            }
        });

        rightEyeButton = (ImageView) findViewById(R.id.rightEyeButton);
        rightEyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTimer.clearTimerCount();
                startActivity(new Intent().setClass(MainActivity.this, MenuActivity.class));
            }
        });

        mainActivity = (RelativeLayout) findViewById(R.id.mainActivity);
        mainActivity.setOnTouchListener(this);

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
//                    changeCtrlType(val);
                    Log.i(TAG, "onSharedPreferenceChanged: " + key + " " + val);
                } else {
                    String val = null;
                    try {
                        val = sharedPreferences.getString(key, "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (key.equals(robotName)) {
                        ssdbTask.setRobotName(val);     // deal it if val = null
                    } else if (key.equals(serverIp)) {
                        ssdbTask.setServerIP(val);
                        serverChanged = true;
                    } else if (key.equals(serverPort)) {
                        int serverPort = Integer.parseInt(val);
                        ssdbTask.setServerPort(serverPort);
                        serverChanged = true;
                    } else if(key.equals(serialCom)) {
                        // do some thing
                        serialCtrl.setSerialCOM(val);
                        serialChanged = true;
                    } else if(key.equals(serialBaud)) {
                        serialCtrl.setSerialBaud(val);
                        // do some thing
                        serialChanged = true;
                    }
//                    Log.i(TAG, "onSharedPreferenceChanged: " + key + " " + val);
                }
            }
        };
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(presChangeListener);

        // relative timer
        Timer timer = new Timer(true);
        timer.schedule(queryTask,200, 200); //延时1000ms后执行，1000ms执行一次
        // timer.cancel(); //退出计时器
    }

    TimerTask queryTask = new TimerTask() {
        @Override
        public void run() {
            ssdbTask.SSDBQuery(SSDBTask.ACTION_HGET);

            userTimer.addTimerCount();
            Log.d(TAG, "TimerTask: " + userTimer.getTimerCount());
            if(userTimer.getTimerCount() > (1*10*1000/200)) {
                startActivity(new Intent().setClass(MainActivity.this, ADActivity.class));
                userTimer.clearTimerCount();
            }
        }
    };

    private boolean enableCtrl = false;
    // receive ssdb server info
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Log.i(TAG, "handleMessage: msg.what: "+msg.what);
            switch (msg.what) {
                case SSDBTask.ENABLECTRL:
                    String rlt  = (String) msg.obj;
                    if( rlt.equals("") )
                            enableCtrl = true;
                    ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.Key_Event, "");
                    break;
                case SSDBTask.ACTION_HGET:
                    String rlt = (String) msg.obj;
//                    Log.d(TAG, "handleMessage: rlt:" + rlt + "\tenableCtrl:" + enableCtrl);
                    if( enableCtrl ) {
                        serialCtrl.robotMove(rlt);
                    }
                    break;
                case SSDBTask.DIRCTRLWARNING:
//                    notifyTextView.setText("open switch please");
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
            default:
                break;
        }
        return true;
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
                    serialCtrl.openSerialCOM();
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
        userTimer.clearTimerCount();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(presChangeListener);
        ssdbTask.disConnect();
        serialCtrl.closeSerialCOM();
        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        Log.d(TAG, "OnTouch: Touch Screen");
        userTimer.clearTimerCount();
        return true;
    }
}
