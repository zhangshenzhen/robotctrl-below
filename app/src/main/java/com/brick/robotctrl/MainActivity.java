package com.brick.robotctrl;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.jly.batteryView.BatteryView;
import com.kjn.videoview.ADVideo;
import com.zhangyt.log.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
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

    private String mp3Url = Environment.getExternalStorageDirectory().getPath() + "/Movies/qianqian.mp3";

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
                AboutActivity.startAction(MainActivity.this, ssdbTask.robotName);
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
                    LogUtil.i(TAG, "onSharedPreferenceChanged: " + key + " " + val);
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
//                    LogUtil.i(TAG, "onSharedPreferenceChanged: " + key + " " + val);
                }
            }
        };
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(presChangeListener);

//        PlayerService.startAction(this, mp3Url);

        // relative timer
        Timer timer = new Timer(true);
        timer.schedule(queryTask, 200, 200); //改指令执行后延时1000ms后执行run，之后每1000ms执行一次run
        // timer.cancel(); //退出计时器

        AboutActivity about = new AboutActivity();
        AboutActivity.MyThread tt = about.new MyThread(ssdbTask.robotName);

        tt.start();
    }

    private void threadToUiToast(final String message, final int toastLength) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, toastLength).show();
            }
        });
    }

    private int countForPlayer = 0;//播放计数器
    private int countForReconnectSSDB = 0;
    private int countForAlive = 0;//复活计数器
    private String strTimeFormat = null;
    private String disableAudio = "No";
    TimerTask queryTask = new TimerTask() {
        @Override
        public void run() {
//            LogUtil.d(TAG, "run: stop: " + ssdbTask.stop);
            if ( !ssdbTask.stop ) {                  // 发起读请求
                ssdbTask.SSDBQuery(SSDBTask.ACTION_HGET);////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!
            } else {
                countForReconnectSSDB++;
                if (countForReconnectSSDB % (1000/200) == 0) {
                    LogUtil.d(TAG, "run: "+ countForReconnectSSDB);
                    threadToUiToast("ssdb reconnect after " + (5-countForReconnectSSDB/(1000/200)) + "s", Toast.LENGTH_SHORT);
                    if (countForReconnectSSDB == 5*1000/200) {
                        threadToUiToast("ssdb reconnecting...", Toast.LENGTH_SHORT);
                        ssdbTask.connect();
                        countForReconnectSSDB = 0;
                    }
                }
            }

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
//            LogUtil.d(TAG, "pkg:"+cn.getPackageName());
//            LogUtil.d(TAG, "cls:"+cn.getClassName());

//            if ( cn.getClassName().equals("com.brick.robotctrl.MainActivity") ) {
//                countForPlayer++;
////                LogUtil.d(TAG, "run: countForPlayer:" + countForPlayer);
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
//            LogUtil.d(TAG, "run: " + getTimerCount());

//            if(getTimerCount() > (10*60*1000/200)) {
//                LogUtil.d(TAG, "Timeout to play video");
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
                    LogUtil.d(TAG, "handleMessage: videoInfo");
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
//                    LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                    if (rlt.equals("DirCtl")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableDirCtl = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                        ComponentName an = am.getRunningTasks(1).get(0).topActivity;//得到某一活动
                        if ( !an.getClassName().equals("com.brick.robotctrl.ExpressionActivity") ) {
                            ExpressionActivity.startAction(MainActivity.this, 0);
                        }
                    }
                    if (rlt.equals("EndDirCtl")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableDirCtl = false;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if (rlt.equals("Charge")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        //SSDBTask.enableCharge = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        //充电
                        serialCtrl.robotCharge();
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if (rlt.equals("setparam")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableSetParameter = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("Brow")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableChangeBrow = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("SetVolume")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableSetVolume = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                    }
					if (rlt.equals("EndVideo")){
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableVideoPlay=false;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    // by gaowei start
                    if(rlt.equals("VideoPlay")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableVideoPlay = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("VideoPlayList")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableVideoPlayList = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("RobotMsg")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableRobotMsg= true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("BatteryVolt")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableBatteryVolt= true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("NetworkDelay")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableNetworkDelay= true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("Location")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableLocation= true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("CurrentTime")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableCurrentTime= true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("DisableAudio")) {
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        SSDBTask.enableForbidAudio= true;//使能静音
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                    }
                    if(rlt.equals("reboot")){
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                        MainActivity.super.onReboot();
                    }
                    if(rlt.equals("shutdown")){
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
                        MainActivity.super.onShutdown();
                    }
                    if(rlt.equals("message")){
                        LogUtil.d(TAG, "handleMessage: Key:Event \tvalue:" + rlt);
                        ssdbTask.enableGetMessage = true;
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Event], "");
                        LogUtil.d(TAG, "handleMessage: clear Event");
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
                    LogUtil.d(TAG,"handleMessage: ------------------Key:Location \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        ssdbTask.SSDBQuery(SSDBTask.ACTION_HSET, SSDBTask.event[SSDBTask.Key_Location], ssdbTask.robotLocation);
                        SSDBTask.enableLocation=false;
                    }
                    break;
                case SSDBTask.Key_VideoPlay:
                    rlt=(String)msg.obj;
                    if(!rlt.equals("")){
                        LogUtil.d(TAG,"handleMessage: ------------------Key:VideoPlay \tvalue:" + rlt);
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
                                ActivityManager em = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                                ComponentName en = em.getRunningTasks(1).get(0).topActivity;//得到某一活动
                                if ( en.getClassName().equals("com.brick.robotctrl.ADActivity") ) {
                                    ADVideo.pause();
                                }

                                break;
                            case "Stop":
                                ActivityManager bm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                                ComponentName bn = bm.getRunningTasks(1).get(0).topActivity;//得到某一活动
                                if ( bn.getClassName().equals("com.brick.robotctrl.ADActivity") ) {
                                    ADVideo.stopPlayBack();
                                    ExpressionActivity.startAction(MainActivity.this, 0);
                                }else if(bn.getClassName().equals("com.brick.robotctrl.ImageActivity")){
                                    ExpressionActivity.startAction(MainActivity.this, 0);
                                }
                                break;
                            case "Single":
                                ActivityManager dm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                                ComponentName dn = dm.getRunningTasks(1).get(0).topActivity;//得到某一活动
                                if ( !dn.getClassName().equals("com.brick.robotctrl.ADActivity") ||
                                        !dn.getClassName().equals("com.brick.robotctrl.ImageActivity")) {
                                    if (strArray[1].endsWith(".jpg")) {
                                        ImageActivity.startAction(MainActivity.this,strArray[0], strArray[1]);
                                    }else {
                                        ADActivity.startAction(MainActivity.this, strArray[0], strArray[1]);
                                    }
                                }
                            case "Cycle":
                                ActivityManager cm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                                ComponentName cn = cm.getRunningTasks(1).get(0).topActivity;//得到某一活动
                                if ( !cn.getClassName().equals("com.brick.robotctrl.ADActivity") ||
                                        !cn.getClassName().equals("com.brick.robotctrl.ImageActivity")) {
                                    if (strArray[1].endsWith(".jpg")) {
                                        ImageActivity.startAction(MainActivity.this,strArray[0], strArray[1]);
                                    }else {
                                        ADActivity.startAction(MainActivity.this, strArray[0], strArray[1]);
                                    }
                                }
                                break;
                            case "SingleCycle":
                                ActivityManager fm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);//获得运行activity
                                ComponentName fn = fm.getRunningTasks(1).get(0).topActivity;//得到某一活动
                                if ( !fn.getClassName().equals("com.brick.robotctrl.ADActivity") ) {
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
                    LogUtil.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    ssdbTask.pushFileList();
//                    if(!rlt.equals(""))   {
//                        LogUtil.d(TAG, "videoplaylist: hehe");
//                        SSDBTask.enableVideoPlayList=false;
//                    }
                    break;
                case SSDBTask.Key_RobotMsg:
                    rlt=(String)msg.obj;
                    LogUtil.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        //!!!!!!!!!!!执行videorobotmsg操作
                        SSDBTask.enableRobotMsg=false;
                    }
                    break;
                case SSDBTask.Key_BatteryVolt:
                    rlt=(String)msg.obj;
                    LogUtil.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        //!!!!!!!!!!!执行BatteryVolt操作
                        SSDBTask.enableBatteryVolt=false;
                    }
                    break;
                case SSDBTask.Key_NetworkDelay:
                    rlt=(String)msg.obj;
                    LogUtil.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        //!!!!!!!!!!!执行NetworkDelay操作
                        SSDBTask.enableNetworkDelay=false;
                    }
                    break;
                case SSDBTask.Key_CurrentTime:
                    rlt=(String)msg.obj;
                    LogUtil.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        //!!!!!!!!!!!执行CurrentTime操作
                        SSDBTask.enableCurrentTime=false;
                    }
                    break;
                case SSDBTask.Key_DisableAudio:
                    rlt=(String)msg.obj;
                    LogUtil.d(TAG,"handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if(!rlt.equals(""))   {
                        //!!!!!!!!!!!执行ForbidAudio操作
                        SSDBTask.enableForbidAudio=false;
                    }
                    break;
                // by gaowie end
                case SSDBTask.Key_DirCtrl:
                    rlt = (String) msg.obj;
                    LogUtil.d(TAG, "handleMessage: ------------------Key:DirCtrl \tvalue:" + rlt);
                    if (rlt.equals("EndDirCtl")) {
                        SSDBTask.enableDirCtl = false;
                    } else if ( !rlt.equals("")) {
                        serialCtrl.robotMove(rlt);
                    }
                    break;
                case SSDBTask.Key_SetParam:
                    rlt = (String) msg.obj;
                    LogUtil.d(TAG, "handleMessage: ------------------Key:SetParam \tvalue:" + rlt);
                    if ( !rlt.equals("") ) {
                        serialCtrl.setRobotRate(rlt);
                        SSDBTask.enableSetParameter = false;
                    }
                    break;
                case SSDBTask.Key_ChangeBrow:
                    rlt = (String) msg.obj;
                    LogUtil.d(TAG, "handleMessage: ------------------Key:ChangeBrow \tvalue:" + rlt);
                    if ( !rlt.equals("") ) {
                        SSDBTask.enableChangeBrow = false;
                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                        if (cn.getClassName().equals("com.brick.robotctrl.ExpressionActivity")) {
                            ExpressionActivity.changeExpression(Integer.parseInt(rlt));
                            LogUtil.d(TAG, "handleMessage: changebrowed");
                        } else {
                            LogUtil.d(TAG, "handleMessage: change brow failure because of current activity is not ExpressionActivity");
                        }
                    }
                    break;
                case SSDBTask.Key_SetVolume:
                    rlt = (String) msg.obj;
                    LogUtil.d(TAG, "handleMessage: ------------------Key:SetVolume \tvalue:" + rlt);
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
                case SSDBTask.Key_Message:
                    rlt=(String)msg.obj;
                    LogUtil.d(TAG, "handleMessage: ------------------Key:Message \tvalue:" + rlt);
                    if ( !rlt.equals("") ) {
                        SpeechService.startAction(MainActivity.this, rlt);
                    }
                    break;
                case SSDBTask.key_ApkUpdate :
                    checkUpdateApk();
                default:
                    break;
            }
        }
    };

    private void checkUpdateApk() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String archiveFilePath = getInstallApkFullPath();
                if ( !(archiveFilePath == null) ) {                 // 判断字符串是否为空要用==， 不要用equals方法
                    LogUtil.d(TAG, "run: start to install apk: " + archiveFilePath);
//                    Intent intentA = new Intent(Intent.ACTION_VIEW);
//                    intentA.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intentA.setDataAndType(Uri.fromFile(new File(archiveFilePath)), "application/vnd.android.package-archive");
//                    startActivity(intentA);
//                        android.os.Process.killProcess(android.os.Process.myPid());
//                } else {
//                    LogUtil.d(TAG, "no apk need to install");
//                }
                    String[] args = { "pm", "install", "-r", archiveFilePath };
                    String result = "";
                    ProcessBuilder processBuilder = new ProcessBuilder(args);
                    Process process = null;
                    InputStream errIs = null;
                    InputStream inIs = null;
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int read = -1;
                        process = processBuilder.start();
                        errIs = process.getErrorStream();
                        while ((read = errIs.read()) != -1) {
                            baos.write(read);
                        }
                        baos.write('*');
                        inIs = process.getInputStream();
                        while ((read = inIs.read()) != -1) {
                            baos.write(read);
                        }
                        byte[] data = baos.toByteArray();
                        result = new String(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (errIs != null) {
                                errIs.close();
                            }
                            if (inIs != null) {
                                inIs.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (process != null) {
                            process.destroy();
                        }
                    }
                } else {
                    LogUtil.d(TAG, "no apk need to install");
                }
            }
        }).start();
    }

    private String getInstallApkFullPath() {
        String apkDirPath = Environment.getExternalStorageDirectory().getPath()+"/Download";
        File apkFile = new File(apkDirPath);
        File[] apkFiles = apkFile.listFiles();

        for (int i = 0; i < apkFiles.length; i++) {
            if (needUpdate(apkFiles[i].getAbsolutePath())) {
                LogUtil.d(TAG, "run: start to install apk: " + apkFiles[i].getAbsolutePath());
                return apkFiles[i].getAbsolutePath();
            }
        }
        return null;
    }


    // relative menu
    Menu menu = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LogUtil.i(TAG, "onCreateOptionsMenu: set menu UI");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.i(TAG, "onCreateOptionsMenu: "+item);
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
        LogUtil.d(TAG, "onActivityResult: resultCode:" + resultCode);
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
        LogUtil.i(TAG, "onStop");
//        Intent stopIntent = new Intent();
//        stopIntent.putExtra("url", mp3Url);
//        LogUtil.d(TAG, "onCreate: stop PlayService");
//        stopIntent.setClass(MainActivity.this, PlayerService.class);
//        stopService(stopIntent);
        super.onPause();
    }

//    @Override
//    protected void onStop() {
//        LogUtil.i(TAG, "onStop");
//        Intent stopIntent = new Intent();
//        stopIntent.putExtra("url", mp3Url);
////        intent.putExtra("MSG", 0);
////        LogUtil.d(TAG, "onCreate: starting PlayService");
////        stopIntent.setClass(MainActivity.this, PlayerService.class);
////        stopService(stopIntent);
//        super.onStop();
//    }

    @Override
    protected void onRestart() {
        LogUtil.i(TAG, "onRestart");
        countForPlayer = 0;
//        PlayerService.startAction(MainActivity.this, mp3Url);
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "onDestroy");
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(presChangeListener);
        ssdbTask.disConnect();
        serialCtrl.closeSerialCOM();
        unregisterReceiver(netWorkChangeReceiver);
        Intent stopSpeechServiceIntent=new Intent(this,SpeechService.class);
        stopService(stopSpeechServiceIntent);
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
//                        LogUtil.d("abc", "run: batteryVoltVal = " + batteryVoltVal);
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
            LogUtil.d(TAG, "run: while over");
        }
    }

    class netWorkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //通过getSystemService()方法得到connectionManager这个系统服务类，专门用于管理网络连接
            ConnectivityManager connectionManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if((networkInfo != null && networkInfo.isAvailable()) && ssdbTask.stop) {
                Toast.makeText(context, "network is available, ssdb server haven't started, starting connect ssdb server",Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessage(ssdbConn);
            }else{
                Toast.makeText(context, "network is unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean needUpdate(String archiveFilePath) {
        PackageManager pm = getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo( archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            try {
                ApplicationInfo apkInfo = info.applicationInfo;
//                String appName = pm.getApplicationLabel(apkInfo).toString();
                String packageName = apkInfo.packageName;   //得到安装包名称
                String versionName = info.versionName;            //得到版本信息
                int versionCode = info.versionCode;            //得到版本信息

//              Drawable icon = pm.getApplicationIcon(appInfo);//得到图标信息
//              appName:RobotCtrl packagename: com.brick.robotctrl version: v1.27.31
                LogUtil.d(TAG, "apkInfo:packagename: " + packageName + " versionName: " + versionName + " versionCode: " + versionCode);

                String appVersionName = getVersion(packageName);
                LogUtil.d(TAG, "apkInstalled: appVersionName: " + appVersionName);


                if ( versionName.equals(null) ) {
                    threadToUiToast("尚未安装播放器，接下来安装", Toast.LENGTH_SHORT);
                    return true;
                } else if (!versionName.equals(appVersionName)) {
                    threadToUiToast("播放器需要更新，接下来更新", Toast.LENGTH_SHORT);
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    private String getVersion(String packageName) {
//        try {
//            PackageInfo info = packagemanager.getPackageInfo(this.getPackageName(), 0);
//            String version = info.versionName;
//            return version;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
        PackageManager pManager = MainActivity.this.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo appInfo = paklist.get(i);
            if ( appInfo.packageName.equals(packageName) ) {
                LogUtil.d(TAG, "getVersion: " + packageName + " version: " + appInfo.versionName);
                return appInfo.versionName;
            }
        }
        return null;
    }
}
