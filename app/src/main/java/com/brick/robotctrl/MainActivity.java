package com.brick.robotctrl;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bean.serialport.ComBean;
import com.jly.batteryView.BatteryView;
import com.udpwork.ssdb.SSDB;

import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    SharedPreferences.OnSharedPreferenceChangeListener presChangeListener = null;

    ImageView leftEyeButton = null;
    ImageView rightEyeButton = null;
    SSDBTask ssdbTask = null;
    SerialCtrl serialCtrl = null;

    DispQueueThread DispQueue=null;//刷新电压显示线程
    public BatteryView mBatteryView = null;

    private boolean serverChanged = false;
    private boolean serialChanged = false;

    private RelativeLayout mainActivity = null;

    private String mp3Url = "/sdcard/Movies/qianqian.mp3";

    Calendar currentTime = null;
    Calendar previousTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // remove text in toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ssdbTask = new SSDBTask(MainActivity.this, handler);
        serialCtrl = new SerialCtrl(MainActivity.this, handler);

        DispQueue = new DispQueueThread();      //获取电压显示线程
        DispQueue.start();
        mBatteryView = (BatteryView) findViewById(R.id.battery_view);
        mBatteryView.setPower(SerialCtrl.BatteryNum);

        leftEyeButton = (ImageView) findViewById(R.id.leftEyeButton);
        leftEyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTimerCount();
                startActivity(new Intent().setClass(MainActivity.this, QuestTestActivity.class));
            }
        });

        rightEyeButton = (ImageView) findViewById(R.id.rightEyeButton);
        rightEyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTimerCount();
                startActivity(new Intent().setClass(MainActivity.this, MenuActivity.class));
            }
        });

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
                    if (key.equals(robotName) && val != null) {
                        ssdbTask.setRobotName(val);     // deal it if val = null
                    } else if (key.equals(serverIp) && val != null) {
                        ssdbTask.setServerIP(val);
                        serverChanged = true;
                    } else if (key.equals(serverPort)) {
                        int serverPort = Integer.parseInt(val);
                        ssdbTask.setServerPort(serverPort);
                        serverChanged = true;
                    } else if(key.equals(serialCom) && val != null) {
                        // do some thing
                        serialCtrl.setSerialCOM(val);
                        serialChanged = true;
                    } else if(key.equals(serialBaud) && val != null) {
                        serialCtrl.setSerialBaud(val);
                        // do some thing
                        serialChanged = true;
                    }
//                    Log.i(TAG, "onSharedPreferenceChanged: " + key + " " + val);
                }
            }
        };
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(presChangeListener);

        PlayerService.startAction(this, mp3Url);

        // relative timer
        Timer timer = new Timer(true);
        timer.schedule(queryTask, 200, 200); //延时1000ms后执行，1000ms执行一次
        // timer.cancel(); //退出计时器
    }

    private int countForPlayer = 0;
    private int countForAlive = 0;
    private String strTimeFormat = null;
    private String disableAudio = "No";
    TimerTask queryTask = new TimerTask() {
        @Override
        public void run() {
            if ( !ssdbTask.stop )                   // 发起读请求
                ssdbTask.SSDBQuery(SSDBTask.ACTION_HGET);

            if ( countForAlive++ > 5*1000/200 ) {
                currentTime = Calendar.getInstance();
                strTimeFormat = android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.TIME_12_24);
                if (strTimeFormat.equals(null) || strTimeFormat.equals("12")) {     // 12HOUR
                    if (Calendar.getInstance().get(Calendar.AM_PM) == Calendar.AM) {      // AM
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_CurrentTime], String.valueOf(currentTime.get(Calendar.HOUR)) +
                                ":" + String.valueOf(currentTime.get(Calendar.MINUTE)) + ":" + String.valueOf(currentTime.get(Calendar.SECOND)));
                    } else {    // PM
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_CurrentTime], String.valueOf(currentTime.get(Calendar.HOUR) + 12) +
                                ":" + String.valueOf(currentTime.get(Calendar.MINUTE)) + ":" + String.valueOf(currentTime.get(Calendar.SECOND)));
                    }
                }else {        // 24HOUR
                    ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_CurrentTime], String.valueOf(currentTime.get(Calendar.HOUR)) +
                            ":" + String.valueOf(currentTime.get(Calendar.MINUTE)) + ":" + String.valueOf(currentTime.get(Calendar.SECOND)));
                }
                countForAlive = 0;
            }

            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//            Log.d(TAG, "pkg:"+cn.getPackageName());
//            Log.d(TAG, "cls:"+cn.getClassName());

            if ( cn.getClassName().equals("com.brick.robotctrl.MainActivity") ) {
                countForPlayer++;
//                Log.d(TAG, "run: countForPlayer:" + countForPlayer);
                if ( countForPlayer == 30*1000/200 ) {
                    PlayerService.startAction(MainActivity.this, mp3Url);
                    countForPlayer = 0;
                }
            }
            if( cn.getClassName().equals("com.brick.robotctrl.ADActivity")) {
                if ( disableAudio.equals("No") ) {
                    disableAudio = "Yes";
                    ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_DisableAudio], disableAudio);
                }
            } else {
                if ( disableAudio.equals("Yes") ) {
                    disableAudio = "No";
                    ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_DisableAudio], disableAudio);
                }
            }

            addTimerCount();
//            Log.d(TAG, "run: " + getTimerCount());

            if(getTimerCount() > (10*60*1000/200)) {
                Log.d(TAG, "Timeout to play video");
                startActivity(new Intent().setClass(MainActivity.this, ADActivity.class));
                clearTimerCount();
//                serialCtrl.reOpenSerialCOM();
            }
        }
    };

    // receive ssdb server info
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SSDBTask.Key_Event:
                    String rlt  = (String) msg.obj;
//                    Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                    if (rlt.equals("DirCtl")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableDirCtl = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if (rlt.equals("param")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableSetParameter = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("Brow")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableChangeBrow = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    break;
                case SSDBTask.Key_DirCtrl:
                    rlt = (String) msg.obj;
                    Log.d(TAG, "handleMessage: ------------------Key:DirCtrl \tvalue:" + rlt);
                    if (rlt.equals("EndDirCtl")) {
                        SSDBTask.enableDirCtl = false;
                    } else if ( !rlt.equals("")) {
                        serialCtrl.robotMove(rlt);
                    }
                    break;
                case SSDBTask.Key_SetParam:
                    rlt = (String) msg.obj;
                    Log.d(TAG, "handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if ( !rlt.equals("") ) {
                        serialCtrl.setRobotRate(rlt);
                        SSDBTask.enableSetParameter = false;
                    }
                    break;
                case SSDBTask.Key_ChangeBrow:
                    rlt = (String) msg.obj;
                    Log.d(TAG, "handleMessage: ------------------Key:ChangeBrow \tvalue:" + rlt);
                    if ( !rlt.equals("") ) {
                        SSDBTask.enableChangeBrow = false;
                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                        if (cn.getClassName().equals("com.brick.robotctrl.ExpressionActivity")) {
                            ExpressionActivity.changeExpression(Integer.parseInt(rlt));
                            Log.d(TAG, "handleMessage: changebrowed");
                        } else {
                            Log.d(TAG, "handleMessage: change brow failure because of current activity is not ExpressionActivity");
                        }
                    }
                    break;
                case SSDBTask.ACTION_CONNECT_FAILED:
                    Log.d(TAG, "handleMessage: connect ssdb failure!");
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivityForResult(intent, 0);
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
        Log.d(TAG, "onActivityResult: resultCode:" + resultCode);
        if (requestCode == 0) {
//            if (resultCode == RESULT_OK) {        // left top back resultCode = 0
//                Log.i(TAG, "onActivityResult: " + data.getBooleanExtra("data", false));
//                Log.d(TAG, "onActivityResult: serverChanged:" + serverChanged);
//                Log.d(TAG, "onActivityResult: serialChanged:" + serialChanged);
//                if (serverChanged) {
//                    serverChanged = false;
                    ssdbTask.connect();
//                }
//                if ( serialChanged ) {
//                    serialChanged = false;
                    serialCtrl.openSerialCOM();
//                    // do some thing
//                }
//            }
        }
    }

    @Override
    protected void onPause(){
        Log.i(TAG, "onStop");
        Intent stopIntent = new Intent();
        stopIntent.putExtra("url", mp3Url);
        Log.d(TAG, "onCreate: stop PlayService");
        stopIntent.setClass(MainActivity.this, PlayerService.class);
        stopService(stopIntent);
        super.onPause();
    }

//    @Override
//    protected void onStop() {
//        Log.i(TAG, "onStop");
//        Intent stopIntent = new Intent();
//        stopIntent.putExtra("url", mp3Url);
////        intent.putExtra("MSG", 0);
////        Log.d(TAG, "onCreate: starting PlayService");
////        stopIntent.setClass(MainActivity.this, PlayerService.class);
////        stopService(stopIntent);
//        super.onStop();
//    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart");
        countForPlayer = 0;
        PlayerService.startAction(MainActivity.this, mp3Url);
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

    //----------------------------------------------------电池电压刷新显示线程
    private int batteryVoltVal = -1;
    public class DispQueueThread extends Thread{
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                if ((batteryVoltVal = serialCtrl.getBattery())!=-1) {
                    Log.d(TAG, "run: batteryVoltVal = " + batteryVoltVal);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mBatteryView.setPower(batteryVoltVal);
                        }
                    });

                    try {
                        Thread.sleep(1000);//显示性能高的话，可以把此数值调小。
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}
