package com.brick.robotctrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bean.serialport.ComBean;
import com.bean.serialport.SerialHelper;

import java.io.IOException;
import java.security.InvalidParameterException;

public class SerialCtrl {
    public final String TAG = "SerialCtrl";
    public String serialCOM = "ttySAC2";
    public int serialBaud = 9600;
    SerialControl ComA = null;

    private Handler contextHandler = null;
    private Context context = null;

    public SerialCtrl(Context context, Handler handler) {
        assert context != null;
        assert handler != null;
        this.context = context;
        this.contextHandler = handler;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        setSerialCOM(sp.getString(context.getString(R.string.serialCOM), serialCOM));
        setSerialBaud(sp.getString(context.getString(R.string.serialBaud), String.valueOf(serialBaud)));

        ComA = new SerialControl();
//        openSerialCOM();
    }

    public void setSerialCOM(@NonNull String serialCOM) {
        if (!TextUtils.isEmpty(serialCOM))
            this.serialCOM = serialCOM;
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
        serialCOM = "/dev/" + serialCOM;
        Log.d(TAG, "openSerialCOM: serialBaud:" + serialBaud + "\tserialCOM:" + serialCOM);
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
            ShowMessage("open serial failure: parameeter error!");
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
        Log.d(TAG, "robotMove: ");
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
    public void setRobotRate(String rate) {
        Log.d(TAG, "setRobotRate: ");
        String[] splitRate = rate.split(" ");
        int exeRate, exeRateBCC, turnRate, turnRateBCC, headRate, headRateBCC, timeoutTimeBCC;
        exeRate = Integer.parseInt(splitRate[0])/2;
        turnRate = Integer.parseInt(splitRate[1])/2;
        headRate = Integer.parseInt(splitRate[2])/2;
        exeRateBCC = 0xFF & 0x16 & exeRate;
        turnRateBCC =  0xFF & 0x16 & turnRate;
        headRateBCC =  0xFF & 0x16 & headRate;
//        timeoutTimeBCC =  0xFF & 0x16 & (Integer.parseInt(splitRate[3]));

        for (int i=0; i < 4; i++) {
            Log.d(TAG, "setRobotRate: "+ splitRate[i]);
        }
        sendPortData(ComA, "FF16" + String.valueOf(exeRate) + String.valueOf(exeRateBCC));
        sendPortData(ComA, "FF17" + String.valueOf(turnRate) + String.valueOf(turnRateBCC));
        sendPortData(ComA, "FF18" + String.valueOf(headRate) + String.valueOf(headRateBCC));
//        sendPortData(ComA, "FF16"+splitRate[3]+String.valueOf(timeoutTimeBCC));
    }
}
