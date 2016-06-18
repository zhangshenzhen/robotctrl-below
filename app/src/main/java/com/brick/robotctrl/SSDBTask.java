package com.brick.robotctrl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
    private static final int ACTION_DISCONNECT = 0x0010;
    public static final int ACTION_HSET = 0x0100;
    public static final int ACTION_HGET = 0x1000;

    public static final int DIR_UP = 0;
    public static final int DIR_DOWN = 1;
    public static final int DIR_LEFT = 2;
    public static final int DIR_RIGHT = 3;
    public static final int DIR_STOP = 4;

    private Handler contextHandler = null;
    private Context context = null;
    private SSDB ssdbClient = null;
    public String serverIp = "60.171.108.155";
    public int serverPort = 11028;
    public String robotName = "Robot";

    public void setRobotName(@NonNull String robotName) {
        if (!TextUtils.isEmpty(robotName))
            this.robotName = robotName;
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

    private Vibrator vibrator = null;

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
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null) {
            throw new NullPointerException("vibrator is null");
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
            if (enableDirCtrl) {
                setDirCtrlEnable(false);
            }
            SSDBQuery(ACTION_DISCONNECT);
        }
    }

    public boolean stop = false;

    //----event-->DirCtrl-->
    public static final String[] event = new String[]{"event", "DirCtl", "left", "right", "stop"};
    public static final String Key_Event = "event";
    public static final String Key_DirCtrl = "DirCtl";
    public static final String Key_EndDirCtrl = "EndDirCtl";
    public static boolean enableDirCtrl = false;
    public static final String Key_SetParam = "param";
    public static boolean enableSetParam = false;
    public static final String Key_Charge = "Charge";
    public static final String Key_VideoPlay = "VideoPlay";
    public static boolean enableVideoPlay = false;
    public static final String Key_VideoInfo = "VideoInfo";
    public static final String Key_VideoPlayList = "VideoPlayList";
    public static final String Key_RobotMsg = "RobotMsg";
    public static final String Key_BatteryVolt = "BatteryVolt";
    public static final String Key_NetworkDelay = "NetworkDelay";
    public static final String Key_Location = "Location";
    public static final String[] DirCtrlVals = new String[]{"up", "down", "left", "right", "stop"};

    @Override
    public synchronized void run() {
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
                        ssdbClient = new SSDB(serverIp, serverPort);
                    } catch (Exception e) {
                        if (ssdbClient == null) {
                            stop = true;
                            contextHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(context).setIcon(R.mipmap.ic_launcher)
                                            .setTitle("can not connect to server: " + serverIp + " " + serverPort)
                                            .setCancelable(false)
                                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    stop = false;
                                                }
                                            })
                                            .setPositiveButton("retry", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    stop = false;
                                                    SSDBQuery(ACTION_CONNECT);
                                                    run();
                                                }
                                            })
                                            .create().show();
                                }
                            });
                        }
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
                        if (ssdbClient == null) {
                            SSDBQuery(ACTION_CONNECT);
                        }
                    }
                    break;
                case ACTION_HGET:
                    try {
                        byte[] rlt = ssdbClient.hget(robotName, Key_Event);     // check event
                        if (rlt != null) {
                            Message message = new Message();
                            message.what = Key_Event;
                            message.obj = new String(rlt, "GBK");
                            contextHandler.sendMessage(message);
                        }
                        rlt = ssdbClient.hget(robotName, Key_DirCtrl);          // check move control
                        if (rlt != null) {
                            Message message = new Message();
                            message.what = ACTION_HGET;
                            message.obj = new String(rlt, "GBK");
                            contextHandler.sendMessage(message);
                        }
                        rlt = ssdbClient.hget(robotName, Key_SetParam);
                        if (rlt != null) {
                            Message message = new Message();
                            message.what = ACTION_HGET;
                            message.obj = new String(rlt, "GBK");
                            contextHandler.sendMessage(message);
                        }
                        rlt = ssdbClient.hget(robotName, Key_VideoPlay);
                        if (rlt != null) {
                            Message message = new Message();
                            message.what = ACTION_HGET;
                            message.obj = new String(rlt, "GBK");
                            contextHandler.sendMessage(message);
                        }
//                        rlt = ssdbClient.hget(robotName, Key_VideoInfo);
//                        if (rlt != null) {
//                            Message message = new Message();
//                            message.what = ACTION_HGET;
//                            message.obj = new String(rlt, "GBK");
//                            contextHandler.sendMessage(message);
//                        }
//                        rlt = ssdbClient.hget(robotName, Key_VideoPlayList);
//                        if (rlt != null) {
//                            Message message = new Message();
//                            message.what = ACTION_HGET;
//                            message.obj = new String(rlt, "GBK");
//                            contextHandler.sendMessage(message);
//                        }
//                        rlt = ssdbClient.hget(robotName, Key_RobotMsg);
//                        if (rlt != null) {
//                            Message message = new Message();
//                            message.what = ACTION_HGET;
//                            message.obj = new String(rlt, "GBK");
//                            contextHandler.sendMessage(message);
//                        }
//                        rlt = ssdbClient.hget(robotName, Key_BatteryVolt);
//                        if (rlt != null) {
//                            Message message = new Message();
//                            message.what = ACTION_HGET;
//                            message.obj = new String(rlt, "GBK");
//                            contextHandler.sendMessage(message);
//                        }
//                        rlt = ssdbClient.hget(robotName, Key_NetworkDelay);
//                        if (rlt != null) {
//                            Message message = new Message();
//                            message.what = ACTION_HGET;
//                            message.obj = new String(rlt, "GBK");
//                            contextHandler.sendMessage(message);
//                        }
//                        rlt = ssdbClient.hget(robotName, Key_Location);
//                        if (rlt != null) {
//                            Message message = new Message();
//                            message.what = ACTION_HGET;
//                            message.obj = new String(rlt, "GBK");
//                            contextHandler.sendMessage(message);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (ssdbClient == null) {
                            SSDBQuery(ACTION_CONNECT);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setDirCtrlEnable(boolean enable) {
        if (!enable) {
            SSDBQuery(ACTION_HSET, Key_DirCtrl, Key_EndDirCtrl);
        }
        SSDBQuery(ACTION_HSET, "event", enable ? Key_DirCtrl : Key_EndDirCtrl);
        enableDirCtrl = enable;
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
