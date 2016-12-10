package com.jly.idcard;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brick.robotctrl.BaseActivity;
import com.brick.robotctrl.R;
import com.brick.robotctrl.SSDBTask;
import com.hdos.idCardUartDevice.JniReturnData;
import com.kjn.msgabout.Msg;
import com.hdos.idCardUartDevice.publicSecurityIDCardLib;
import com.rg2.activity.TwoActivity;
import com.rg2.listener.MyOnClickListener;
import com.rg2.utils.CityDialog;
import com.rg2.utils.LogUtil;
import com.rg2.utils.StringUtils;
import com.rg2.utils.ToastUtil;

import java.io.UnsupportedEncodingException;

/**
 * Created by jiangly on 2016/6/22.
 */

public class IDcard extends BaseActivity implements View.OnClickListener {

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
    String port="/dev/ttyUSB0";

    public LinearLayout llGroup;
    private publicSecurityIDCardLib iDCardDevice;
    public static boolean IDflag = false;
    private TextView mUserNameTv;
    private TextView mIdNumberTv;
    private TextView mAddressTv;
    private Button mSubmitBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rg2);

        iDCardDevice = new publicSecurityIDCardLib();
        //llGroup=(LinearLayout) findViewById(R.id.scrollView1);
//        Thread tt = new Thread(new IDcard());
//        tt.start();

        mUserNameTv = (TextView) findViewById(R.id.tv_userName);
        mIdNumberTv = (TextView) findViewById(R.id.tv_idNumber);
        mAddressTv = (TextView) findViewById(R.id.tv_address);
        mSubmitBtn = (Button) findViewById(R.id.btn_submit);
        mAddressTv.setOnClickListener(this);
        mSubmitBtn.setOnClickListener(this);
        hdler.sendEmptyMessage(1);
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

    Handler hdler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getCard();
                        }
                    }).start();


                        break;
            }
        }
    };






    private void getCard()
    {
        int retval;
       //  llGroup.removeAllViews();// 清空
        String pkName;
        pkName = getPackageName();
        pkName = "/data/data/" + pkName + "/lib/libwlt2bmp.so";
        try {
            retval = iDCardDevice.readBaseMsg(port, pkName, BmpFile, name, sex, nation, birth, address, IDNo, Department,
                    EffectDate, ExpireDate, pErrMsg);

            if (retval < 0) {
               // showString("读卡错误，原因：" + new String(pErrMsg, "Unicode"));
                LogUtil.e("TAG","读卡错误1111");
                hdler.sendEmptyMessageDelayed(1,1000);

            } else {
                LogUtil.e("TAG","读卡正确111");

                LogUtil.e("TAG", new String(name, "Unicode"));
                LogUtil.e("TAG", new String(IDNo, "Unicode"));
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            mUserNameTv.setText(new String(name, "Unicode"));
                            mIdNumberTv.setText(new String(IDNo, "Unicode"));
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }




//    public void run()
//    {


//        while(true) {
//            int retval;
//          //  llGroup.removeAllViews();// 清空
//            String pkName;
//            pkName = getPackageName();
//            pkName = "/data/data/" + pkName + "/lib/libwlt2bmp.so";
//            try {
//                retval = iDCardDevice.readBaseMsg(port, pkName, BmpFile, name, sex, nation, birth, address, IDNo, Department,
//                        EffectDate, ExpireDate, pErrMsg);
//
//                if (retval < 0) {
//                    showString("读卡错误，原因：" + new String(pErrMsg, "Unicode"));
//                } else {
//                    Message mg = new Message();
//                    mg.what =SSDBTask.key_ApkUpdate;    ////////////////////////////////////////////////////////////这里17
//                    hdler.sendMessage(mg);
//
//                    if(IDflag) {
//                        IDflag = false;
//                        int[] colors = iDCardDevice.convertByteToColor(BmpFile);
//                        Bitmap bm = Bitmap.createBitmap(colors, 102, 126, Bitmap.Config.ARGB_8888);
//                        Bitmap bm1 = Bitmap.createScaledBitmap(bm, (int) (102 * 1), (int) (126 * 1), false);
//                        ImageView imageView = new ImageView(this);
//                        imageView.setScaleType(ImageView.ScaleType.MATRIX);
//                        imageView.setImageBitmap(bm);
//                        llGroup.addView(imageView);
//
//                        showString("");
//                        showString("名字=" + new String(name, "Unicode"));
//                        showString("性别=" + new String(sex, "Unicode"));
//                        showString("民族=" + new String(nation, "Unicode"));
//                        showString("生日=" + new String(birth, "Unicode"));
//                        showString("地址=" + new String(address, "Unicode"));
//                        showString("身份证号=" + new String(IDNo, "Unicode"));
//                        showString("发卡机构=" + new String(Department, "Unicode"));
//                        showString("有效日期=" + new String(EffectDate, "Unicode") + "至" + new String(ExpireDate, "Unicode"));
//                    }
//                }
//            } catch (UnsupportedEncodingException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }

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

    @Override
    public void onClick(View v) {
        if (v == mAddressTv) {
            CityDialog mCityDialog = new CityDialog();
            mCityDialog.showCityDialog(IDcard.this, new MyOnClickListener() {
                @Override
                public void onClicked(String content) {
                    mAddressTv.setText(content);
                }
            });
        } else if (v == mSubmitBtn) {

            String mUserName = mUserNameTv.getText().toString();
            String mIdNumber = mIdNumberTv.getText().toString();
            String mAddress = mAddressTv.getText().toString();


            if(StringUtils.stringIsEmpty(mUserName) || StringUtils.stringIsEmpty(mIdNumber))
            {
                ToastUtil.show(IDcard.this,"请刷身份证");
                return;
            }


            if(StringUtils.stringIsEmpty(mAddress))
            {
                ToastUtil.show(IDcard.this,"请选择公司所在区域");
                return;
            }

            startActivityForResult(new Intent(IDcard.this,TwoActivity.class),1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 &&resultCode==Activity.RESULT_OK)
        {
            finish();
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x,y;
        x = event.getX();
        //250
        y=event.getY();
        Log.d("IDcard", "dispatchTouchEvent: "+x+":"+" "+y);
        event.setLocation(x*1280/1024,y*750/768);

        return super.dispatchTouchEvent(event);
    }
}


