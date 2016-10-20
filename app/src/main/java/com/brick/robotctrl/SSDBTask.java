package com.brick.robotctrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.kjn.videoview.HttpAsk;
import com.udpwork.ssdb.SSDB;
import com.zhangyt.log.LogUtil;

import java.io.File;
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

    private Handler contextHandler = null;
    private Context context = null;
    private SSDB ssdbClient = null;
    public String serverIp = "60.171.108.192";
    public int serverPort = 11028;
    public String robotName = "r00004A";
    public String robotLocation = "江苏南大电子信息技术股份有限公司";
    public String videoPlayList = null;
    private final int serverSite = 222;
    private String serverSiteString = null;

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
//        connect();
        new Thread() {
            @Override
            public void run() {
                // 需要花时间计算的方法
                try {
                    String str = HttpAsk.posturl("http://60.171.108.151:8076/rb/t_loginbyrobot.aspx?action=login&robotcode=r00004&password=123456");
                    LogUtil.d(TAG, "HttpAsk: " + str);
                    String[] strArray = str.split(" ");
//					for(int i = 0; i < strArray.length; i++){
//						LogUtil.d(TAG, "strArray: " +strArray[i]);
//					}
                    String ans = HttpAsk.posturl("http://60.171.108.151:8076/rb/t_getproperty.aspx?action=queryall&"+strArray[1]+"&"+strArray[2]);
                    String[] strArray1 = ans.split(" ");
//					for(int i = 0; i < strArray1.length; i++){
//						LogUtil.d(TAG, "strArray: " +strArray1[i]);;
//					}
                    String[] strArray3 = strArray1[1].split("=");
//					for(int i = 0; i < strArray3.length; i++){
//						LogUtil.d(TAG, "strArray: " +strArray3[i]);;
//					}
                    robotLocation = strArray3[1];

                } catch (Exception e) {
                    // TODO: handle exception
                }
                LogUtil.d(TAG, "serverSite: " + robotLocation);
            }
        }.start();
//        pushFileList();
    }
    public void pushFileList(){
        try {
            File file = new File(Environment.getExternalStorageDirectory()
                    .getPath()+"/Movies");
            File[] files = file.listFiles();
            if (files.length == 0){
                LogUtil.d(TAG, "pushFileList: 为空");
            }
            if (files.length > 1) {
                videoPlayList = null;
                for (int i = 0; i < files.length - 1; i++) {
                    if (
                            files[i].getAbsolutePath().endsWith(".avi") ||
                                    files[i].getAbsolutePath().endsWith(".mp4") ||
                                    files[i].getAbsolutePath().endsWith(".3gp") ||
                                    files[i].getAbsolutePath().endsWith(".jpg")
                        //  files[i].getAbsolutePath().endsWith(".flv")
                        // files[i].getAbsolutePath().endsWith(".gif")||
                        // files[i].getAbsolutePath().endsWith(".mkv")||
                        //files[i].getAbsolutePath().endsWith(".mov")||
                        // files[i].getAbsolutePath().endsWith(".mpg")||
                        // files[i].getAbsolutePath().endsWith(".rmvb")
                        // files[i].getAbsolutePath().endsWith(".swf")||
                        //  files[i].getAbsolutePath().endsWith(".vob")
                        // files[i].getAbsolutePath().endsWith(".wmv")
                            ) {
                        LogUtil.d(TAG, "getFiles: " + files[i].getAbsolutePath().substring(files[i].getAbsolutePath().lastIndexOf("/") + 1));
                        if (videoPlayList != null) {
                            videoPlayList = videoPlayList + files[i].getAbsolutePath().substring(files[i].getAbsolutePath().lastIndexOf("/") + 1) + " ";
                        } else {
                            videoPlayList = files[i].getAbsolutePath().substring(files[i].getAbsolutePath().lastIndexOf("/") + 1) + " ";
                        }
                    }
                }
                videoPlayList = videoPlayList + files[files.length-1].getAbsolutePath().substring(files[files.length-1].getAbsolutePath().lastIndexOf("/") + 1);
            }
            if(files.length == 1){
                videoPlayList = files[0].getAbsolutePath().substring(files[0].getAbsolutePath().lastIndexOf("/") + 1);
            }
            LogUtil.d(TAG, "SSDBTask: "  + videoPlayList);
            SSDBQuery(ACTION_HSET, event[Key_VideoPlayList], videoPlayList);
        } catch (Exception e) {
            LogUtil.d("getfile", "查找异常!");
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
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
        pushFileList();
        SSDBQuery(ACTION_HSET, "appVersion", context.getString(R.string.appVersion));
        SSDBQuery(ACTION_HSET, event[Key_Location], robotLocation);
    }

    public void disConnect() {
        if (ssdbClient != null) {
            SSDBQuery(ACTION_DISCONNECT);
            stop = true;
        }
    }

    public boolean stop = true;


    public static final int Key_Event = 0;
    public static final int Key_DirCtrl = 1;
    public static final int Key_SetParam = 2;
    public static final int Key_VideoPlay = 3;
    public static final int Key_VideoInfo = 4;        // 视频播放时1s上传一次info，不需要接收该关键字
    public static final int Key_VideoPlayList = 5;
    public static final int Key_RobotMsg = 6;
    public static final int Key_BatteryVolt = 7;
    public static final int Key_NetworkDelay = 8;
    public static final int Key_Location = 9;
    public static final int Key_ChangeBrow = 10;
    public static final int Key_CurrentTime = 11;
    public static final int Key_DisableAudio = 12;
    public static final int Key_SetVolume = 13;
	public static final int Key_EndVideo=14;
    public static final int key_ApkUpdate=15;               //用来更新apk的，跟ssdb没关系，放在这里只是为了统一,15被谁用了
    public static final int Key_Message=16;

    public static final String[] event = new String[]{
            "event", "DirCtl", "param", "VideoPlay", "VideoInfo", "VideoPlayList", "RobotMsg", "BatteryVolt", "NetworkDelay", "Location",
            "Brow", "CurrentTime", "DisableAudio", "Volume","EndVideo", "", "Message"};
   ////////////////////////gaowei/////////////////////////////
    public static boolean enableForbidAudio=false;
    public static boolean enableCurrentTime=false;
    public static boolean enableLocation=false;
    public static boolean enableNetworkDelay=false;
    public static boolean enableBatteryVolt=false;
    public static boolean enableRobotMsg=false;             //
    public static boolean enableVideoPlayList=false;        //
    public static boolean enableVideoPlay=false;            //
//    public static boolean enableVideoInfo=false;            //
    ////////////////////////gaowei////////////////////////////
    public static boolean enableDirCtl = false;
    public static boolean enableChangeBrow = false;
    public static boolean enableSetParameter = false;
    public static boolean enableSetVolume = false;
	// public static boolean enableEndVideo = false;
    public static boolean enableGetMessage = false;

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
//            SSDBQuery(ACTION_CONNECT);

        }
    }
    @Override
    public synchronized void run() {
//        LogUtil.d(TAG, "run: stop:" + stop);

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
//            LogUtil.i(TAG, "run: " + cmd);
            switch (cmd.cmdType) {
                case ACTION_CONNECT:
                    try {
                        LogUtil.d(TAG, "run: ACTION_CONNECT");
                        ssdbClient = new SSDB(serverIp, serverPort);
                        stop = false;
                    } catch (Exception e) {
                        LogUtil.d(TAG, "run: ACTION_CONNECT_FAILED");
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
//                        SSDBQuery(ACTION_CONNECT);
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
                            iCount = 5;                     // 快速重新获取
                            stop = true;
//                            SSDBQuery(ACTION_CONNECT);    // 异常处理不应该是重新连接
                        }
                    }
                    // by gaowei start
                    if(enableVideoPlay){
                        sendMessageToMain(Key_VideoPlay);
                    }else {
//                        SSDBQuery(ACTION_HSET, event[Key_VideoPlay], "");
                    }
                    if(enableVideoPlayList){
                        sendMessageToMain(Key_VideoPlayList);
                    }else{
//                        SSDBQuery(ACTION_HSET, event[Key_VideoPlayList], "");
                    }
                    if(enableRobotMsg){
                        sendMessageToMain(Key_RobotMsg);
                    }else{
//                        SSDBQuery(ACTION_HSET, event[Key_RobotMsg], "");
                    }
                    if(enableBatteryVolt){
                        sendMessageToMain(Key_BatteryVolt);
                    }else{
//                        SSDBQuery(ACTION_HSET, event[Key_BatteryVolt], "");
                    }
                    if(enableNetworkDelay){
                        sendMessageToMain(Key_NetworkDelay);
                    }else{
//                        SSDBQuery(ACTION_HSET, event[Key_NetworkDelay], "");
                    }
                    if(enableLocation){
                        sendMessageToMain(Key_Location);
                    }else{
//                        SSDBQuery(ACTION_HSET, event[Key_Location]);
                    }
                    if(enableCurrentTime){
                        sendMessageToMain(Key_CurrentTime);
                    }else{
//                        SSDBQuery(ACTION_HSET, event[Key_CurrentTime]);
                    }
                    if(enableForbidAudio){
                        sendMessageToMain(Key_DisableAudio);
                    }else{
//                        SSDBQuery(ACTION_HSET, event[Key_DisableAudio]);
                    }
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
//                            SSDBQuery(ACTION_CONNECT);
                        }
                    } else {
//                        SSDBQuery(ACTION_HSET, event[Key_DirCtrl], "");
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
//                            SSDBQuery(ACTION_CONNECT);
                        }
                    } else {
//                        SSDBQuery(ACTION_HSET, event[Key_SetParam], "");
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
//                            SSDBQuery(ACTION_CONNECT);
                        }
                    } else {
//                        SSDBQuery(ACTION_HSET, event[Key_ChangeBrow], "");
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
//                            SSDBQuery(ACTION_CONNECT);
                        }
                    }
//                    else {
//                        SSDBQuery(ACTION_HSET, event[Key_ChangeBrow], "");      // 后面不能清
//                    }
                    if( enableGetMessage ) {
                        try {
                            byte[] rlt = ssdbClient.hget(robotName, event[Key_Message]);
                            ssdbClient.hset(robotName, event[Key_Message], "");
                            if (rlt != null) {
                                Message message = new Message();
                                message.what = Key_Message;
                                message.obj = new String(rlt, "GBK");
                                contextHandler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
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

    public synchronized void SSDBQuery(int codeType, String key, String val) {
        cmdList.add(CmdEntry.create(codeType, key, val));
    }


}
