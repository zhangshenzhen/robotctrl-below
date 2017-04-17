package com.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brick.robotctrl.R;

/**
 * Created by lx on 2017/4/11.
 */

public class USBFragment extends BaseFragment {
    private static final String TAG ="USBFragment" ;
    private TextView textView;
    private UsbManager usbManager;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private LinearLayout linearLayout;
    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.usb_fragment,null);
    }

    @Override
    public void initData() {
        textView = (TextView)getActivity().findViewById(R.id.tv_usb);
       // usbManager = (UsbManager)getActivity().getSystemService(getActivity().USB_SERVICE);
        usbManager = (UsbManager)getActivity().getSystemService(Context.USB_SERVICE);
        Receiver();
    }
  /*广播接收者
  **/
    private void Receiver() {
        Log.d(TAG,"Receiver");
         receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"onReceive");
                String action = intent.getAction();//动作意图;
                  switch (action){
                      case MyUsbManager.ACTION_USB_STATE:
                          boolean connected = intent.getBooleanExtra(MyUsbManager.USB_CONNECTED, false);
                          if(connected){
                              Log.d(TAG,"链接");
                              addText("connected : " + "已连接");
                          } else {
                              Log.d(TAG,"断开");
                              addText("connected : " + "已断开");
                       }
                 }
             }
        };
        Log.d(TAG,"Receiver2");
        intentFilter = new IntentFilter();
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(MyUsbManager.ACTION_USB_STATE);
        Log.d(TAG,"Receiver3:"+receiver);
        getActivity().registerReceiver(receiver,intentFilter);
        Log.d(TAG,"Receiver4:"+getActivity().registerReceiver(receiver,intentFilter));
    }


    @Override
    public void onDestroy() {
        if (receiver !=null) {
            getActivity().unregisterReceiver(receiver);
        }
        super.onDestroy();
    }
    private void addText(String str) {
        textView.setText(str+"\n");
    }
}
