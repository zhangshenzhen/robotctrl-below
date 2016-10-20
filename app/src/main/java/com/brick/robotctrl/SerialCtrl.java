package com.brick.robotctrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

import com.bean.serialport.ComBean;
import com.bean.serialport.SerialHelper;
import com.zhangyt.log.LogUtil;

import java.io.IOException;
import java.security.InvalidParameterException;

public class SerialCtrl {
    public final String TAG = "SerialCtrl";
    public String serialCOM = "ttySAC2";
    public int serialBaud = 9600;
    SerialControl ComA = null;

    private Handler contextHandler = null;
    private Context context = null;

    public static ComBean ComRecDatatmp = null;    //暂存电池电压的值
    public static int batteryNum = 0;         //最终电压值
    public static int loop=0;                 //用于for循环的循环变量
    public static int [] RmShake ={0,0,0,0,0,0,0};              //去除电压值的抖动

    public SerialCtrl(Context context, Handler handler) {
        assert context != null;
        assert handler != null;
        this.context = context;
        this.contextHandler = handler;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        setSerialCOM(sp.getString(context.getString(R.string.serialCOM), serialCOM));
        setSerialBaud(sp.getString(context.getString(R.string.serialBaud), String.valueOf(serialBaud)));

        ComA = new SerialControl();
        openSerialCOM();
//        sendPortData(ComA, "FF10FF10");
    }

    public void setSerialCOM(@NonNull String serialCOM) {
        if (!TextUtils.isEmpty(serialCOM))
            this.serialCOM = "/dev/" + serialCOM;
    }

    public void setSerialBaud(int serialBaud) {
        if (serialBaud > 0 && serialBaud <= 115200) {
            this.serialBaud = serialBaud;
        }
    }

    public void setSerialBaud(@NonNull String serialBaud) {
        if (!TextUtils.isEmpty(serialBaud)) {
            this.serialBaud = Integer.parseInt(serialBaud);
        }
    }

    public void openSerialCOM() {
        LogUtil.d(TAG, "openSerialCOM: serialBaud:" + serialBaud + "\tserialCOM:" + serialCOM);
        ShowMessage("Open " + serialCOM + " successful, the BaudRate is " + serialBaud);
        ComA.setBaudRate(serialBaud);
        ComA.setPort(serialCOM);
        openComPort(ComA);
    }

    public void reOpenSerialCOM() {
        closeSerialCOM();
        serialCOM = "/dev/" + serialCOM;
        ComA.setBaudRate(serialBaud);
        ComA.setPort(serialCOM);
        openComPort(ComA);
    }

    public void closeSerialCOM() {
        closeComPort(ComA);
    }

    // serial control class
    private class SerialControl extends SerialHelper {
        public SerialControl(){
        }

        @Override
        protected void onDataReceived(final ComBean ComRecData)
        {
            // receive data
            ComRecDatatmp = ComRecData;
            try {
                if(Integer.parseInt(String.format("%02x", ComRecDatatmp.bRec[1]).toUpperCase(), 16) == 16) {
                    RmShake[loop++] = Integer.parseInt(String.format("%02x", ComRecDatatmp.bRec[2]).toUpperCase(), 16);
                    if(loop==7) {
                        batteryNum = GetMid(RmShake, 7);                  //更新电池值
                        loop=0;
//                        LogUtil.d("onDataReceived", "getbattery: " + batteryNum);
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
                LogUtil.d(TAG, "onDataReceived: ");
            }
        }
        public int GetMid( int r[], int n) {      //冒泡排序,取中间值，去除最大值和最小值
            int i= n -1;                          //初始时,最后位置保持不变
            while ( i> 0) {
                int pos= 0;                       //每趟开始时,无记录交换
                for (int j= 0; j< i; j++)
                    if (r[j]> r[j+1]) {
                        pos= j;                   //记录交换的位置
                        int tmp = r[j]; r[j]=r[j+1];r[j+1]=tmp;
                    }
                i= pos;                           //为下一趟排序作准备
            }
            return r[n/2];
        }
    }

    // send
    private void sendPortData(SerialHelper ComPort,String sOut){
        if (ComPort!=null && ComPort.isOpen())
        {
            ComPort.sendHex(sOut);
        }
    }
    // close serial
    private void closeComPort(SerialHelper ComPort){
        if (ComPort!=null){
            ComPort.stopSend();
            ComPort.close();
        }
    }

    // open serial
    private void openComPort(SerialHelper ComPort){
        try
        {
            ComPort.open();
        } catch (SecurityException e) {
            ShowMessage("open serial failure: permission denied!");
        } catch (IOException e) {
            ShowMessage("open serial failure: unknow why!");
        } catch (InvalidParameterException e) {
            ShowMessage("open serial failure: parameter error!");
        }
    }

    private void ShowMessage(final String sMsg)
    {
//        Toast.makeText(this, sMsg, Toast.LENGTH_SHORT).show();
        contextHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, sMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

// relative robot
    public void robotMove(String dir) {
        LogUtil.d(TAG, "robotMove: ");
        switch (dir) {
            case "up":
                sendPortData(ComA, "FF01FF01");
                break;
            case "down":
                sendPortData(ComA, "FF02FF02");
                break;
            case "left":
                sendPortData(ComA, "FF03FF03");
                break;
            case "right":
                sendPortData(ComA, "FF04FF04");
                break;
            case "stop":
                sendPortData(ComA, "FF05FF05");
                break;
            case "headup":
                sendPortData(ComA, "FF11FF11");
                break;
            case "headdown":
                sendPortData(ComA, "FF12FF12");
                break;
            case "headleft":
                sendPortData(ComA, "FF13FF13");
                break;
            case "headright":
                sendPortData(ComA, "FF14FF14");
                break;
            case "headmid":
                sendPortData(ComA, "FF15FF15");
                break;
            default:
        }
    }
    public int getBattery()      //发送获取电压命令
    {
        sendPortData(ComA, "FF10FF10");
        return batteryNum;
    }

    public void robotCharge(){
        sendPortData(ComA, "FF20FF20");
    }

//    //----------------------------------------------------显示接收数据
//    public void DispRecData(ComBean ComRecData){
//        // LogUtil.d("getbattery", "getbattery: "+String.format("%02x", ComRecData.bRec[2]).toUpperCase());
//
//    }
    public void setRobotRate(String rate) {
        LogUtil.d(TAG, "setRobotRate: ");
        String[] splitRate = rate.split(" ");
        int exeRate, exeRateBCC, turnRate, turnRateBCC, headRate, headRateBCC, timeoutTimeBCC;
        exeRate = Integer.parseInt(splitRate[0])*2;
        turnRate = Integer.parseInt(splitRate[1])*2;
        headRate = Integer.parseInt(splitRate[2])*2;
        exeRateBCC = (0xFF ^ 0x06 )^exeRate;
        turnRateBCC = ( 0xFF ^ 0x07) ^ turnRate;
        headRateBCC = ( 0xFF ^ 0x08) ^ headRate;
//        timeoutTimeBCC =  0xFF & 0x16 & (Integer.parseInt(splitRate[3]));

        for (int i=0; i < 4; i++) {
            LogUtil.d(TAG, "setRobotRate: "+ splitRate[i]);
        }
        sendPortData(ComA, "FF06" + Integer.toHexString(exeRate) + Integer.toHexString(exeRateBCC));
        //LogUtil.d(TAG, "setRobotRate: " +Integer.toHexString(exeRate));
        LogUtil.d(TAG, "setRobotRate: " + "FF06" +Integer.toHexString(exeRate) + Integer.toHexString(exeRateBCC));
        sendPortData(ComA, "FF07" + Integer.toHexString(turnRate) +Integer.toHexString(turnRateBCC));
        LogUtil.d(TAG, "setRobotRate: " + "FF07" +Integer.toHexString(turnRate) +Integer.toHexString(turnRateBCC));
        sendPortData(ComA, "FF08" + Integer.toHexString(headRate) +Integer.toHexString(headRateBCC));
        LogUtil.d(TAG, "setRobotRate: " + "FF08" + Integer.toHexString(headRate) +Integer.toHexString(headRateBCC));
//        sendPortData(ComA, "FF16"+splitRate[3]+String.valueOf(timeoutTimeBCC));
    }
}
