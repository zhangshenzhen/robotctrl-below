package com.brick.robotctrl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bean.serialport.ComBean;
import com.bean.serialport.SerialHelper;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by Brick on 2016/6/16.
 */
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
        openSerialCOM();
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
        Log.d(TAG, "openSerialCOM: serialBaud:" + serialBaud + "\tserialCOM:" + serialCOM);
//        ComA.setBaudRate(serialBaud);
//        ComA.setPort(serialCOM.toString());
        ComA.setBaudRate(9600);
        ComA.setPort("/dev/ttySAC2");
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
        if ( dir.equals("up") ) {
            Log.d(TAG, "handleMessage: it's up");
            sendPortData(ComA, "FF01FF01");
        }else if ( dir.equals("down") ) {
            Log.d(TAG, "handleMessage: it's down");
            sendPortData(ComA, "FF02FF02");

        }else if ( dir.equals("left") ) {
            Log.d(TAG, "handleMessage: it's left");
            sendPortData(ComA, "FF03FF03");
        }else if ( dir.equals("right") ) {
            Log.d(TAG, "handleMessage: it's right");
            sendPortData(ComA, "FF04FF04");
        }else if ( dir.equals("stop") ) {
            Log.d(TAG, "handleMessage: it's stop");
        }
    }


}
