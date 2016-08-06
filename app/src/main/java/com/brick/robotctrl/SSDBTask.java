package com.brick.robotctrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.widget.Toast;

import com.udpwork.ssdb.SSDB;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class SSDBTask extends TimerTask {
    private static final String TAG = "SSDBTask";
    public static final int ENABLECTRL = 0x0001;            // enable control
    public static final int DIRCTRLWARNING = 0x0002;
    private static final int ACTION_CONNECT = 0x0001;
    private static final int ACTION_DISCONNECT = 0x0002;
    public static final int ACTION_HSET = 0x0004;
    public static final int ACTION_HGET = 0x0008;

    public static final int ACTION_CONNECT_FAILED = 0x000F;

    private Handler contextHandler = null;
    private Context context = null;
    private SSDB ssdbClient = null;
    public String serverIp = "60.171.108.155";
    public int serverPort = 11028;
    public String robotName = "r00004A";
    public String robotLocation = "江苏南大电子信息技术股份有限公司";

    public void setRobotName(@NonNull String robotName) {
        if (!TextUtils.isEmpty(robotName))
            this.robotName = robotName;
    }

    public void setRobotLocation(@NonNull String robotLocation) {
        if (!TextUtils.isEmpty(robotName))
            this.robotLocation = robotLocation;
    }

    public void setServerPort(int serverPort) {
        if (serverPort > 0 && serverPort < 65536) {
            this.serverPort = serverPort;
        }
    }

    public void setServerIP(@NonNull String serverIp) {
        if (!TextUtils.isEmpty(serverIp)) {
            this.serverIp = serverIp;
        }
    }

    Timer timer = new Timer();

    private static class CmdEntry<T, K, V> {
        public final T cmdType;
        public final K key;
        public final V val;

        private CmdEntry(T type, K key, V val) {
            this.cmdType = type;
            this.key = key;
            this.val = val;
        }

        public static <T, K, V> CmdEntry<T, K, V> create(T type, K key, V val) {
            return new CmdEntry<>(type, key, val);
        }

        @Override
        public int hashCode() {
            int hash = cmdType.hashCode();
            hash = hash * 31 + key.hashCode();
            hash = hash * 31 + val.hashCode();
            return hash;
        }

        @Override
        public String toString() {
            return String.format("type:%s\tkey:%s\tval:%s", cmdType, key, val);
        }
    }

    //保存命令列表 NOTE Queue用法
    Queue<CmdEntry<Integer, String, String>> cmdList = new LinkedList<>();

    public SSDBTask(Context context, Handler handler) {
        assert context != null;
        assert handler != null;
        this.context = context;
        this.contextHandler = handler;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        setRobotName(sp.getString(context.getString(R.string.robotName), robotName));
        setServerIP(sp.getString(context.getString(R.string.serverIp), serverIp));
        String port = sp.getString(context.getString(R.string.serverPort), String.valueOf(serverPort));
        try {
            setServerPort(Integer.parseInt(port));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        timer.schedule(this, 50, 50);
        connect();
        SSDBQuery(ACTION_HGET, "Location");
    }

    public void connect() {
        if (ssdbClient != null) {
            ssdbClient.close();
            ssdbClient = null;
        }
        stop = false;
        SSDBQuery(ACTION_CONNECT);
        contextHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "Connecting to " + serverIp + ":" + serverPort, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void disConnect() {
        if (ssdbClient != null) {
            SSDBQuery(ACTION_DISCONNECT);
        }
    }

    public boolean stop = false;


    public static final int Key_Event = 0;
    public static final int Key_DirCtrl = 1;////
    public static final int  Key_SetParam = 2;////
    public static final int Key_VideoPlay = 3;//
    public static final int Key_VideoInfo = 4;//
    public static final int Key_VideoPlayList = 5;//
    public static final int Key_RobotMsg = 6; //
    public static final int Key_BatteryVolt = 7;  //
    public static final int Key_NetworkDelay = 8;//
    public static final int Key_Location = 9;////
    public static final int Key_ChangeBrow = 10;////
    public static final int Key_CurrentTime = 11;//
    public static final int Key_DisableAudio = 12;
    public static final int Key_SetVolume = 13;
    public static final String[] event = new String[]{"event", "DirCtl", "param",
            "VideoPlay", "VideoInfo", "VideoPlayList", "RobotMsg", "BatteryVolt",
            "NetworkDelay", "Location", "Brow", "CurrentTime", "DisableAudio",
            "Volume"};
   ////////////////////////gaowei/////////////////////////////
    public static boolean enableForbidAudio=false;
    public static boolean enableCurrentTime=false;
    public static boolean enableLocation=false;
    public static boolean enableNetworkDelay=false;
    public static boolean enableBatteryVolt=false;
    public static boolean enableRobotMsg=false;             //
    public static boolean enableVideoPlayList=false;        //
    public static boolean enableVideoPlay=false;            //
    public static boolean enableVideoInfo=false;            //
    ////////////////////////gaowei////////////////////////////
    public static boolean enableDirCtl = false;
    public static boolean enableChangeBrow = false;
    public static boolean enableSetParameter = false;
    public static boolean enableSetVolume = false;

    private int iCount = 0;

    void sendMessageToMain(int Key_Type)     //ssdbtask对象从数据库取key_type所指定的键的值给mainactivity by gaowei                                               //
    {
        try {
            byte[] rlt = ssdbClient.hget(robotName, event[Key_Type]); // check event
            if (rlt != null) {
                Message message = new Message();
                message.what = Key_Type;
                message.obj = new String(rlt, "GBK");
                contextHandler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            SSDBQuery(ACTION_CONNECT);
        }
    }
    @Override
    public synchronized void run() {
//        Log.d(TAG, "run: stop:" + stop);
        if (stop) {
            return;
        }
        while (cmdList.size() > 0) {
            CmdEntry<Integer, String, String> cmd = null;
            try {
                cmd = cmdList.poll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (cmd == null) {
                return;
            }
//            Log.i(TAG, "run: " + cmd);
            switch (cmd.cmdType) {
                case ACTION_CONNECT:
                    try {
                        Log.d(TAG, "run: ACTION_CONNECT");
                        ssdbClient = new SSDB(serverIp, serverPort);
                        stop = false;
                    } catch (Exception e) {
                        Log.d(TAG, "run: ACTION_CONNECT_FAILED");
                        Message message = new Message();
                        message.what = ACTION_CONNECT_FAILED;
//                        message.obj = new String(, "GBK");
                        contextHandler.sendMessage(message);
                        stop = true;
                        e.printStackTrace();
                    }
                    break;
                case ACTION_DISCONNECT:
                    if (ssdbClient != null) {
                        ssdbClient.close();
                        ssdbClient = null;
                    }
                    break;
                case ACTION_HSET:
                    try {
                        ssdbClient.hset(robotName, cmd.key, cmd.val);
                    } catch (Exception e) {
                        e.printStackTrace();
                        SSDBQuery(ACTION_CONNECT);
                    }
                    break;
                case ACTION_HGET:
                    if (++iCount >= 5) {        // 1s check
                        iCount = 0;
                        try {
                            byte[] rlt = ssdbClient.hget(robotName, event[Key_Event]); // check event
                            if (rlt != null) {
                                Message message = new Message();
                                message.what = Key_Event;
                                message.obj = new String(rlt, "GBK");
                                contextHandler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            SSDBQuery(ACTION_CONNECT);
                        }
                    }
                    // by gaowei start
                    if(enableVideoInfo){
                        sendMessageToMain(Key_VideoInfo);
                    }else{
                        SSDBQuery(ACTION_HSET, event[Key_VideoInfo], "");
                    }
                    if(enableVideoPlay){
                        sendMessageToMain( Key_VideoPlay);
                    }else {
                        SSDBQuery(ACTION_HSET, event[Key_VideoPlay], "");
                    }
                    if(enableVideoPlayList){
                        sendMessageToMain(Key_VideoPlayList);
                    }else{
                        SSDBQuery(ACTION_HSET, event[Key_VideoPlayList], "");
                    }
                    if(enableRobotMsg){
                        sendMessageToMain(Key_RobotMsg);
                    }else{
                        SSDBQuery(ACTION_HSET, event[Key_RobotMsg], "");
                    }
                    if(enableBatteryVolt){
                        sendMessageToMain(Key_BatteryVolt);
                    }else{
                        SSDBQuery(ACTION_HSET, event[Key_BatteryVolt], "");
                    }
                    if(enableNetworkDelay){
                        sendMessageToMain(Key_NetworkDelay);
                    }else{
                        SSDBQuery(ACTION_HSET, event[Key_NetworkDelay], "");
                    }
                    if(enableLocation){
                        sendMessageToMain(Key_Location);
                    }else{
                        SSDBQuery(ACTION_HSET, event[Key_Location]);
                    }
                    if(enableCurrentTime){
                        sendMessageToMain(Key_CurrentTime);
                    }else{
                        SSDBQuery(ACTION_HSET, event[Key_CurrentTime]);
                    }
                    if(enableForbidAudio){
                        sendMessageToMain(Key_DisableAudio);
                    }else{
                        SSDBQuery(ACTION_HSET, event[Key_DisableAudio]);
                    }
                    // by gaowei end
                    ////////////////////////////////////////////////////////////// 200ms check
                    if (enableDirCtl) {             // check control move
                        try {
                            byte[] rlt = ssdbClient.hget(robotName, event[Key_DirCtrl]);          // check move control
                            if (rlt != null) {
                                Message message = new Message();
                                message.what = Key_DirCtrl;
                                message.obj = new String(rlt, "GBK");
                                contextHandler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            SSDBQuery(ACTION_CONNECT);
                        }
                    } else {
                        SSDBQuery(ACTION_HSET, event[Key_DirCtrl], "");
                    }
                    if ( enableSetParameter ) {     // check rate parameter
                        try {
                            byte[] rlt = ssdbClient.hget(robotName, event[Key_SetParam]);
                            if (rlt != null) {
                                Message message = new Message();
                                message.what = Key_SetParam;
                                message.obj = new String(rlt, "GBK");
                                contextHandler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            SSDBQuery(ACTION_CONNECT);
                        }
                    } else {
                        SSDBQuery(ACTION_HSET, event[Key_SetParam], "");
                    }
                    if ( enableChangeBrow ) {       // check emotion change
                        try {
                            byte[] rlt = ssdbClient.hget(robotName, event[Key_ChangeBrow]);
                            if (rlt != null) {
                                Message message = new Message();
                                message.what = Key_ChangeBrow;
                                message.obj = new String(rlt, "GBK");
                                contextHandler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            SSDBQuery(ACTION_CONNECT);
                        }
                    } else {
                        SSDBQuery(ACTION_HSET, event[Key_ChangeBrow], "");
                    }
                    if ( enableSetVolume ) {       // check volume change
                        try {
                            byte[] rlt = ssdbClient.hget(robotName, event[Key_SetVolume]);
                            if (rlt != null) {
                                Message message = new Message();
                                message.what = Key_SetVolume;
                                message.obj = new String(rlt, "GBK");
                                contextHandler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            SSDBQuery(ACTION_CONNECT);
                        }
                    }
//                    else {
//                        SSDBQuery(ACTION_HSET, event[Key_ChangeBrow], "");      // 后面不能清
//                    }
                default:
                    break;
            }
        }
    }

    public void SSDBQuery(int codeType) {
        SSDBQuery(codeType, null, null);
    }

    public void SSDBQuery(int codeType, String key) {
        SSDBQuery(codeType, key, null);
    }

    /**
     * @param codeType cmd type
     * @param key      key you want set or get
     * @param val      value for key
     */
    public synchronized void SSDBQuery(int codeType, String key, String val) {
        cmdList.add(CmdEntry.create(codeType, key, val));
    }
}
