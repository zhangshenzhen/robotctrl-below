package com.rg2.activity;

import android.media.MediaRouter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.brick.robotctrl.*;
import com.brick.robotctrl.BaseActivity;
import com.presentation.PrintPresentation;
import com.presentation.SamplePresentation;
import com.rg2.utils.StringUtils;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by Brick on 2016/12/10.
 */
public class PrintActivity extends com.rg2.activity.BaseActivity{
    private TextView   mBackTv;
    private  Button   mSubmitBnt1, mSubmitBnt2, mSubmitBnt3;
    SerialCtrl serialCtrlPrinter = null;
    //副屏
    private PrintPresentation mPrintPresentation;


    @Override
    protected void initData() {
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_print);
        mBackTv = (TextView) findViewById(R.id.tv_back);
        mSubmitBnt1 = (Button) findViewById(R.id.btn_submit1);
        mSubmitBnt2 = (Button) findViewById(R.id.btn_submit2);
        mSubmitBnt3 = (Button) findViewById(R.id.btn_submit3);


    }

    @Override
    protected void initEvent()
    {
        mSubmitBnt1.setOnClickListener(this);
        mSubmitBnt2.setOnClickListener(this);
        mSubmitBnt3.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    protected void initViewData()
    {
      serialCtrlPrinter = new SerialCtrl(PrintActivity.this, new Handler(), "ttyUSB1", 9600, "printer");
    }


    @Override
    protected void updatePresentation() {
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay =  route  !=  null ? route.getPresentationDisplay() : null;

        // 注释 : Dismiss the current presentation if the display has changed.
        if (mPrintPresentation != null && mPrintPresentation.getDisplay() !=  presentationDisplay) {
            mPrintPresentation.dismiss();
            mPrintPresentation = null;
        }
        if (mPrintPresentation == null &&  presentationDisplay != null) {
            mPrintPresentation = new PrintPresentation(this,  presentationDisplay);
            mPrintPresentation.setOnDismissListener(mOnDismissListener);
            // Try to show the presentation, this might fail if the display has
            // gone away in the mean time
            try {
                Log.e("SamplePresentation","................28");
                mPrintPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed

                mPrintPresentation = null;
            }
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_submit1:
                print1();
                break;
            case R.id.btn_submit2:
                print2();
                break;
            case R.id.btn_submit3:
                print3();
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }


    private void print1()
    {
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        //  String str="中华人民共和";
        String time = StringUtils.getDateToString(new Date());

        String str0 = "                                ";
        String str1 = "                      2016-12-12";
        String str2 = "                                ";
        String str3 = "                                ";
        String str4 = "               8号              ";
        String str5 = "                                ";
        String str6 = "                                ";
        String str7 = "对公业务    柜台032             ";
        String str8 = "                                ";
        String str9 = "                                ";
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
    }

    private void print2()
    {
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        String time = StringUtils.getDateToString(new Date());

        String str0 = "                                ";
        String str1 = "                      2016-12-12";
        String str2 = "                                ";
        String str3 = "                                ";
        String str4 = "               8号              ";
        String str5 = "                                ";
        String str6 = "                                ";
        String str7 = "对私业务    柜台032             ";
        String str8 = "                                ";
        String str9 = "                                ";
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
    }

    private void print3()
    {
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        String time = StringUtils.getDateToString(new Date());

        String str0 = "                                ";
        String str1 = "                      2016-12-12";
        String str2 = "                                ";
        String str3 = "                                ";
        String str4 = "               8号              ";
        String str5 = "                                ";
        String str6 = "                                ";
        String str7 = "理财业务    柜台032             ";
        String str8 = "                                ";
        String str9 = "                                ";
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
    }


    private void sendPortText(String content)
    {
        byte[] temp = null;
        try
        {
            temp = content.getBytes("gbk");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        serialCtrlPrinter.sendPortText(serialCtrlPrinter.ComA, temp);
    }

    @Override
    protected void onResume() {
        super.onResume();
      updatePresentation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPrintPresentation != null){
            mPrintPresentation.dismiss();
            mPrintPresentation = null;
        }
    }
}
