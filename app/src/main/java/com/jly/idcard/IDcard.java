package com.jly.idcard;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brick.robotctrl.BaseActivity;
import com.brick.robotctrl.R;
import com.hdos.idCardUartDevice.JniReturnData;
import com.hdos.idCardUartDevice.publicSecurityIDCardLib;

import java.io.UnsupportedEncodingException;

/**
 * Created by jiangly on 2016/6/22.
 */

public class IDcard extends BaseActivity {

    private byte[] name = new byte[32];
    private byte[] sex = new byte[6];
    private byte[] birth = new byte[18];
    private byte[] nation = new byte[12];
    private byte[] address = new byte[72];
    private byte[] Department = new byte[32];
    private byte[] IDNo = new byte[38];
    private byte[] EffectDate = new byte[18];
    private byte[] ExpireDate = new byte[18];
    private byte[] pErrMsg = new byte[20];
    private byte[] BmpFile = new byte[38556];
    String port="/dev/ttySAC3";

    private LinearLayout llGroup;
    private publicSecurityIDCardLib iDCardDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idcard);

        iDCardDevice = new publicSecurityIDCardLib();
        llGroup=(LinearLayout) findViewById(R.id.scrollView1);
    }

    /*身份证选卡*/
    public void click4(View view) {
        byte[]cmdSelect= new byte[]{(byte) 0xAA,(byte) 0xAA,(byte) 0xAA,(byte) 0x96,0x69,0x00,0x03,0x20,0x02,0x21};
        byte[] response = null;
        returnData=iDCardDevice.idSamDataExchange(returnData,port, cmdSelect);
        if(returnData.result>0) {
            response=iDCardDevice.strToHex(returnData.iDCardData,returnData.result);
            showString("选成功");
            showString(response,returnData.result);
        } else {
            showString("选卡失败");
        }
    }
    /*身份证寻卡*/
    JniReturnData returnData = new JniReturnData();
    public void click6(View view) {
        byte[]cmdRequst= new byte[]{(byte) 0xAA,(byte) 0xAA,(byte) 0xAA,(byte) 0x96,0x69,0x00,0x03,0x20,0x01,0x22};
        byte[] response = null;
        returnData=iDCardDevice.idSamDataExchange(returnData,port,cmdRequst);
        if(returnData.result>0) {
            response=iDCardDevice.strToHex(returnData.iDCardData,returnData.result);
            showString("寻卡成功");
            showString(response,returnData.result);
        } else {
            showString("寻卡失败");
        }
    }

    /*读身份证信息*/
    public void click5(View view) {

        int retval;
        llGroup.removeAllViews();// 清空
        String pkName;
        pkName=this.getPackageName();
        pkName="/data/data/"+pkName+"/lib/libwlt2bmp.so";
        try {
            retval = iDCardDevice.readBaseMsg(port,pkName,BmpFile, name, sex, nation, birth, address, IDNo, Department,
                    EffectDate, ExpireDate,pErrMsg);

            if (retval < 0) {
                showString("读卡错误，原因：" + new String(pErrMsg, "Unicode"));
            } else {
                int []colors = iDCardDevice.convertByteToColor(BmpFile);
                Bitmap bm = Bitmap.createBitmap(colors, 102, 126, Bitmap.Config.ARGB_8888);
                Bitmap bm1=Bitmap.createScaledBitmap(bm, (int)(102*1),(int)(126*1), false);
                ImageView imageView = new ImageView(this);
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                imageView.setImageBitmap(bm);
                llGroup.addView(imageView);

                showString("");
                showString("名字=" + new String(name, "Unicode"));
                showString("性别=" + new String(sex, "Unicode"));
                showString("民族=" + new String(nation, "Unicode"));
                showString("生日=" + new String(birth, "Unicode"));
                showString("地址=" + new String(address, "Unicode"));
                showString("身份证号=" + new String(IDNo, "Unicode"));
                showString("发卡机构=" + new String(Department, "Unicode"));
                showString("有效日期=" + new String(EffectDate, "Unicode") + "至"+ new String(ExpireDate, "Unicode"));

            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void click2(View view) {
        llGroup.removeAllViews();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
//		commonApi.setGpioOut(125, 0);
        super.onDestroy();
    }

    //Display
    public void showString(String RecvBuffer) {
        TextView tv = new TextView(this);
        tv.setText(RecvBuffer);
        llGroup.addView(tv);
    }
    public void showString(byte[] RecvBuffer, int length) {
        String logOut = new String();
        for (int i = 0; i < length; i++) {
            logOut = logOut + String.format("%02x ", RecvBuffer[i] & 0xFF);
        }
        TextView tv = new TextView(this);
        tv.setText(logOut);
        llGroup.addView(tv);
    }
}


