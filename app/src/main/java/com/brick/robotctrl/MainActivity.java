package com.brick.robotctrl;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.jly.batteryView.BatteryView;
import com.kjn.videoview.ADVideo;

import java.util.Calendar;
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
    private boolean robotLocationChanged = false;

    private String mp3Url = "/sdcard/Movies/qianqian.mp3";

    Calendar currentTime = null;

    ADVideo adVideo1 = null;
    private final int videoInfo = 9999;
    private final int ssdbConn = 888;

    private IntentFilter intentFilter;
    private netWorkChangeReceiver netWorkChangeReceiver;

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

        ADActivity.setHandler(handler);
        AboutActivity.setHandler(handler);

        ssdbTask = new SSDBTask(MainActivity.this, handler);
        serialCtrl = new SerialCtrl(MainActivity.this, handler);

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        //创建NetWorkChangeReceiver的实例，并调用registerReceiver()方法进行注册
        netWorkChangeReceiver = new netWorkChangeReceiver();
        registerReceiver(netWorkChangeReceiver, intentFilter);


        DispQueue = new DispQueueThread();      //获取电压显示线程
        DispQueue.start();
        mBatteryView = (BatteryView) findViewById(R.id.battery_view);
        mBatteryView.setPower(SerialCtrl.batteryNum);

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
                startActivity(new Intent().setClass(MainActivity.this, AboutActivity.class));
            }
        });

        //NOTE OnSharedPreferenceChangeListener: listen settings changed
        presChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            private final String robotName = getString(R.string.robotName);
            private final String robotLocation = getString(R.string.robotLocation);
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
                        ssdbTask.setRobotName(val);     // deal it if val = null设置表名
                    } else if(key.equals(robotLocation) && val != null) {
                        robotLocationChanged = true;
                        ssdbTask.setRobotLocation(val);
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

//        PlayerService.startAction(this, mp3Url);

        // relative timer
        Timer timer = new Timer(true);
        timer.schedule(queryTask, 200, 200); //改指令执行后延时1000ms后执行run，之后每1000ms执行一次run
        // timer.cancel(); //退出计时器
    }

    private int countForPlayer = 0;//播放计数器
    private int countForAlive = 0;//复活计数器
    private String strTimeFormat = null;
    private String disableAudio = "No";
    TimerTask queryTask = new TimerTask() {
        @Override
        public void run() {
            if ( !ssdbTask.stop )                   // 发起读请求
                ssdbTask.SSDBQuery(SSDBTask.ACTION_HGET);////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!

            if ( countForAlive++ > 5*1000/200 ) {//显示时间
                currentTime = Calendar.getInstance();
                strTimeFormat = android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.TIME_12_24);
                if ( (strTimeFormat == null) || (strTimeFormat.equals("")) || strTimeFormat.equals("12") ) {     // 12HOUR
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

            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;//得到某一活动
//            Log.d(TAG, "pkg:"+cn.getPackageName());
//            Log.d(TAG, "cls:"+cn.getClassName());

//            if ( cn.getClassName().equals("com.brick.robotctrl.MainActivity") ) {
//                countForPlayer++;
////                Log.d(TAG, "run: countForPlayer:" + countForPlayer);
//                if ( countForPlayer == 30*1000/200 ) {
//                    PlayerService.startAction(MainActivity.this, mp3Url);
//                    countForPlayer = 0;
//                }
//            }
            if( cn.getClassName().equals("com.brick.robotctrl.ADActivity")) {//什么意思
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

//            if(getTimerCount() > (10*60*1000/200)) {
//                Log.d(TAG, "Timeout to play video");
//                startActivity(new Intent().setClass(MainActivity.this, ADActivity.class));
//                clearTimerCount();
////                serialCtrl.reOpenSerialCOM();
//            }
        }
    };

    // receive ssdb server info
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case videoInfo:
                    ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_VideoInfo], (String)msg.obj);
                    Log.d(TAG, "handleMessage: videoInfo");
                    break;
                case ssdbConn:
                    ssdbTask.connect();
                    break;
                case SSDBTask.Key_Event:
                    /**
                     * 处理event方法
                     * 1. 设置event事件使能，以获取事件内容；
                     * 2. 清除服务器中该event事件；
                     */
                    String rlt  = (String) msg.obj;
//                    Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                    if (rlt.equals("DirCtl")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableDirCtl = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                        ComponentName an = am.getRunningTasks(1).get(0).topActivity;//得到某一活动
                        if ( !an.getClassName().equals("com.brick.robotctrl.ExpressionActivity") ) {
                            ExpressionActivity.startAction(MainActivity.this, "12");
                        }
                    }
                    if (rlt.equals("EndDirCtl")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableDirCtl = false;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if (rlt.equals("Charge")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        //SSDBTask.enableCharge = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        //充电
                        serialCtrl.robotCharge();
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if (rlt.equals("setparam")) {
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
                    if(rlt.equals("SetVolume")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        ssdbTask.enableSetVolume = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                    }
					if (rlt.equals("EndVideo")){
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableVideoPlay=false;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    // by gaowei start
                    if(rlt.equals("VideoPlay")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableVideoPlay = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("VideoPlayList")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableVideoPlayList = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("RobotMsg")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableRobotMsg= true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("BatteryVolt")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableBatteryVolt= true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("NetworkDelay")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableNetworkDelay= true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("Location")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableLocation= true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("CurrentTime")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableCurrentTime= true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("DisableAudio")) {
                        Log.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableForbidAudio= true;//使能静音
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        Log.d(TAG, "handleMessage: clear Event");
                    }
                    break;
                /**
                 * 处理具体的event事件
                 * 1. 根据event事件的持续性修改event事件的使能开关；
                 * 2. 对获取的数据做一定的断言处理；
                 * 3. 根据事件类型及事件内容改变robot行为；
                 */
                case SSDBTask.Key_Location:
                    rlt=(String)msg.obj;
                    Log.d(TAG,"handleMessage: ------------------Key:Location \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Location], ssdbTask.robotLocation);
                        SSDBTask.enableLocation=false;
                    }
                    break;
                case SSDBTask.Key_VideoPlay:
                    rlt=(String)msg.obj;
                    if(!rlt.equals("")){
                        Log.d(TAG,"handleMessage: ------------------Key:VideoPlay \tvalue:" + rlt);
                        String[] strArray = rlt.split(" ");
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_VideoPlay], "");
                        switch (strArray[0]) {
                            case "Play":
                                startActivity(new Intent().setClass(MainActivity.this, ADActivity.class));
                                //singleTask 此Activity实例之上的其他Activity实例统统出栈，使此Activity实例成为栈顶对象，显示到幕前。   break;
                                break;
                            case "ContinuePlay":
                                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                                ComponentName an = am.getRunningTasks(1).get(0).topActivity;//得到某一活动
                                if ( !an.getClassName().equals("com.brick.robotctrl.ADActivity") ) {
                                    ADActivity.startAction(MainActivity.this, strArray[0], null);
                                }
//                                else{
//                                    ADVideo.start();
//                                }
                                break;
                            case "Pause":
                                ADVideo.pause();
                                break;
                            case "Stop":
                                ADVideo.stopPlayBack();
                                ExpressionActivity.startAction(MainActivity.this, "12");
                                break;
                            case "Single":
                                ActivityManager bm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                                ComponentName bn = bm.getRunningTasks(1).get(0).topActivity;//得到某一活动
                                if ( !bn.getClassName().equals("com.brick.robotctrl.ADActivity") ) {
                                    ADActivity.startAction(MainActivity.this, strArray[0], strArray[1]);
                                }
                            case "Cycle":
                                ActivityManager cm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                                ComponentName cn = cm.getRunningTasks(1).get(0).topActivity;//得到某一活动
                                if ( !cn.getClassName().equals("com.brick.robotctrl.ADActivity") ) {
                                    ADActivity.startAction(MainActivity.this, strArray[0], strArray[1]);
                                }
                                break;
                            case "SingleCycle":
                                ActivityManager dm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                                ComponentName dn = dm.getRunningTasks(1).get(0).topActivity;//得到某一活动
                                if ( !dn.getClassName().equals("com.brick.robotctrl.ADActivity") ) {
                                    ADActivity.startAction(MainActivity.this, strArray[0], strArray[1]);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case SSDBTask.Key_VideoPlayList:
                    rlt=(String)msg.obj;
                    Log.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    ssdbTask.pushFileList();
                    if(!rlt.equals(""))   {
                        SSDBTask.enableVideoPlayList=false;
                    }
                    break;
                case SSDBTask.Key_RobotMsg:
                    rlt=(String)msg.obj;
                    Log.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        //!!!!!!!!!!!执行videorobotmsg操作
                        SSDBTask.enableRobotMsg=false;
                    }
                    break;
                case SSDBTask.Key_BatteryVolt:
                    rlt=(String)msg.obj;
                    Log.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        //!!!!!!!!!!!执行BatteryVolt操作
                        SSDBTask.enableBatteryVolt=false;
                    }
                    break;
                case SSDBTask.Key_NetworkDelay:
                    rlt=(String)msg.obj;
                    Log.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        //!!!!!!!!!!!执行NetworkDelay操作
                        SSDBTask.enableNetworkDelay=false;
                    }
                    break;
                case SSDBTask.Key_CurrentTime:
                    rlt=(String)msg.obj;
                    Log.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        //!!!!!!!!!!!执行CurrentTime操作
                        SSDBTask.enableCurrentTime=false;
                    }
                    break;
                case SSDBTask.Key_DisableAudio:
                    rlt=(String)msg.obj;
                    Log.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        //!!!!!!!!!!!执行ForbidAudio操作
                        SSDBTask.enableForbidAudio=false;
                    }
                    break;
                // by gaowie end
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
                case SSDBTask.Key_SetVolume:
                    rlt = (String) msg.obj;
                    Log.d(TAG, "handleMessage: ------------------Key:SetVolume \tvalue:" + rlt);
                    if ( !rlt.equals("") ) {
                        int volume = Integer.parseInt(rlt);
                        if(volume > 100) {
                            volume = 100;
                        } else if( volume < 0 ) {
                            volume = 0;
                        }
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume/5, 0);
                        SSDBTask.enableSetVolume = false;
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
                if (serverChanged) {
                    serverChanged = false;
                    ssdbTask.connect();
                }
                if ( serialChanged ) {
                    serialChanged = false;
                    serialCtrl.openSerialCOM();
                }
            if ( robotLocationChanged ) {
                robotLocationChanged = false;
                ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Location], ssdbTask.robotLocation);
            }
//            }
        }
    }

    @Override
    protected void onPause(){
        Log.i(TAG, "onStop");
//        Intent stopIntent = new Intent();
//        stopIntent.putExtra("url", mp3Url);
//        Log.d(TAG, "onCreate: stop PlayService");
//        stopIntent.setClass(MainActivity.this, PlayerService.class);
//        stopService(stopIntent);
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
//        PlayerService.startAction(MainActivity.this, mp3Url);
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(presChangeListener);
        ssdbTask.disConnect();
        serialCtrl.closeSerialCOM();
        unregisterReceiver(netWorkChangeReceiver);
        super.onDestroy();
    }

    //----------------------------------------------------电池电压刷新显示线程
    private int batteryVoltVal = 0;
    public class DispQueueThread extends Thread{
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                try {
                    while( true ) {
                        batteryVoltVal = serialCtrl.getBattery();
//                        Log.d(TAG, "run: batteryVoltVal = " + batteryVoltVal);
                        if ( batteryVoltVal != 0) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    mBatteryView.setPower(batteryVoltVal);
                                }
                            });
                        }
                        Thread.sleep(1000);//显示性能高的话，可以把此数值调小。
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            Log.d(TAG, "run: while over");
        }
    }

    class netWorkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //通过getSystemService()方法得到connectionManager这个系统服务类，专门用于管理网络连接
            ConnectivityManager connectionManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                Toast.makeText(context, "network is available",Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessage(ssdbConn);
            }else{
                Toast.makeText(context, "network is unavailable", Toast.LENGTH_SHORT).show();
                SettingsActivity.activityStart(MainActivity.this);
            }
        }
    }


}
