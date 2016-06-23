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
            SSDBQuery(ACTION_DISCONNECT);
        }
    }

    public boolean stop = false;


    public static final int Key_Event = 0;
    public static final int Key_DirCtrl = 1;
    public static final int  Key_SetParam = 2;
    public static final int Key_VideoPlay = 3;
    public static final int Key_VideoInfo = 4;
    public static final int Key_VideoPlayList = 5;
    public static final int Key_RobotMsg = 6;
    public static final int Key_BatteryVolt = 7;
    public static final int Key_NetworkDelay = 8;
    public static final int Key_Location = 9;
    public static final int Key_ChangeEmotion = 10;
    public static final String[] event = new String[]{"event", "DirCtl", "param",
            "VideoPlay", "VideoInfo", "VideoPlayList", "RobotMsg", "BatteryVolt", "NetworkDelay", "Location", "ChangeEmotion"};
    public static boolean enableDirCtl = false;
    public static boolean enableChangeEmotion = false;
    public static boolean enableSetParameter = false;

    private int iCount = 0;
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
                                                    stop = true;
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
                    if (++iCount >= 5) {        // 1s check
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
                            if (ssdbClient == null) {
                                SSDBQuery(ACTION_CONNECT);
                            }
                        }
                    }
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
                            if (ssdbClient == null) {
                                SSDBQuery(ACTION_CONNECT);
                            }
                        }
                    }
                    if ( enableSetParameter ) {     // check rate parameter
                        enableSetParameter = false;
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
                            if (ssdbClient == null) {
                                SSDBQuery(ACTION_CONNECT);
                            }
                        }
                    }
                    if ( enableChangeEmotion ) {       // check emotion change
                        enableChangeEmotion = false;
                        try {
                            byte[] rlt = ssdbClient.hget(robotName, event[Key_ChangeEmotion]);
                            if (rlt != null) {
                                Message message = new Message();
                                message.what = Key_ChangeEmotion;
                                message.obj = new String(rlt, "GBK");
                                contextHandler.sendMessage(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (ssdbClient == null) {
                                SSDBQuery(ACTION_CONNECT);
                            }
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

    /**
     * @param codeType cmd type
     * @param key      key you want set or get
     * @param val      value for key
     */
    public synchronized void SSDBQuery(int codeType, String key, String val) {
        cmdList.add(CmdEntry.create(codeType, key, val));
    }
}
