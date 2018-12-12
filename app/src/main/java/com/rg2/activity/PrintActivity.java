package com.rg2.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.brick.robotctrl.SerialCtrl;
import com.rg2.utils.LogUtil;
import com.rg2.utils.SPUtils;
import com.rg2.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Brick on 2016/12/10.
 */
public class PrintActivity extends com.brick.robotctrl.BaseActivity{
    private static final String TAG = "PrintActivity";
    private TextView   mBackTv;
    private  Button   mSubmitBnt1, mSubmitBnt2, mSubmitBnt3;
    SerialCtrl serialCtrlPrinter = null;
    //副屏

    @Override
    protected void initData() {
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_print);

        LogUtil.e(TAG, "..initViews.currentTimeMillis()"+System.currentTimeMillis());

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
    {                                                                       //串口1
    // serialCtrlPrinter = new SerialCtrl(PrintActivity.this, new Handler(), "ttyUSB1", 9600, "printer");
       serialCtrlPrinter = new SerialCtrl(PrintActivity.this, new Handler(), "ttyS1", 9600, "printer");
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_submit1:
                CountPtint();
                print12();
               // sentPicture(Environment.getExternalStorageDirectory().getPath()+"/Pictures/"+"d.png");
                break;
            case R.id.btn_submit2:
                CountPtint();
                print2();
                break;
            case R.id.btn_submit3:
                CountPtint();
                print3();

                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }


    //计数器
     private  int countNum ;

    public void CountPtint(){
        //采用sp进行物理存储，打开退出关闭程序不影响计数器的数值;
        countNum = (int) SPUtils.get(mContext, "countNum",0);
         Log.d(TAG,"countNum= 取出"+countNum);
        if (countNum<9){
            ++countNum;
            Log.d(TAG,"countNum=  打印"+countNum);
            SPUtils.put(mContext,"countNum", countNum);//存储变量

          }else {
            ++countNum;
            Log.d(TAG,"countNum="+countNum+"还剩"+(10-countNum)+"张纸");
            countNum = 0;//复原变量到初始值；
            SPUtils.put(mContext,"countNum", countNum);
            Log.d(TAG,"countNum="+countNum);

        }

    }
    Date date = new Date();
    String time = DateFormat.getInstance().format(date);
    private void print1(){
        //  String str ="1234567890ABCDEFGHIJ中华人民共和";
        //  String str="中华人民共和";

           //每行24个字符
        String str0 = " \n";
        String str1 = "对公业务    柜台032\n";
        String str2 = " \n";
        String str3 = "时间:"+time+"\n";
        String str4 = " \n";
        String str5 = "        A"+8+"号\n";
        String str6 = " \n";
        String str7 = " \n";
        String str8 = " \n";
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
    }


/*
*       String str1 = "\n 恭喜发财   年年有余   岁岁平安";
        String str2 = "\n 财源广进   财源滚滚   财源滚滚";
        String str3 = "\n 事事如意   招财进宝   吉祥如意";
        String str4 = "\n 万事如意   一路发财   心想事成";
        String str5 = "\n 合家平安   步步高升   一元复始";
        String str6 = "\n祝您:万事如意,事业有成,合家平安";
        String str7 = "\n清晨曙光初现,幸福在你身边;中午艳";
        String str8 = "\n阳高照,微笑在你心间;傍晚日落西山";
        String str9 = "\n欢乐随你满天;狗年吉祥，大吉大利!";
        String str10 ="\n愿你一年天天天开心,小时时时快乐,";
        String str11 ="\n分分分精彩,秒秒秒幸福。 ";*/

    private void print12(){
        String str0="                                ";
        String str5="             赐如意符           ";
        String str6 = "\n云篆太虚,浩劫之初,乍遐乍迩,或沉";
        String str7 = "\n或浮!";
        String str8 = "\n五方徘徊,一丈之余,天真皇人,按笔";
        String str9 = "\n乃书!";
        String str10 ="\n以演洞章,次书灵符,元始下降,真文";
        String str11 ="\n诞敷!";
        String str12 ="\n昭昭其有，冥冥其无!";
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str0);
        sendPortText(str12);
        sendPortText(str11);
        sendPortText(str10);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
        sendPortText(str0);
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
        String str11 = "             赐吉祥符           ";
        String str1 = "\n 恭喜发财   年年有余   岁岁平安";
        String str2 = "\n 财源广进   财源滚滚   财源滚滚";
        String str3 = "\n 事事如意   招财进宝   吉祥如意";
        String str4 = "\n 万事如意   一路发财   心想事成";
        String str5 = "\n 合家平安   步步高升   一元复始";
      /* String str6 = "\n祝您:万事如意,事业有成,合家平安";
        String str7 = "\n清晨曙光初现,幸福在你身边;中午艳";
        String str8 = "\n阳高照,微笑在你心间;傍晚日落西山";
        String str9 = "\n欢乐随你满天;狗年吉祥，大吉大利!";
        String str10 ="\n愿你一年天天天开心,小时时时快乐,";
        String str111 ="\n分分分精彩,秒秒秒幸福。 ";*/
        String str12 ="\n";
        sendPortText(str0);
        sendPortText(str12);
     /*   sendPortText(str111);
        sendPortText(str10);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);*/
        sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);
        sendPortText(str11);
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
      /*  String str1 = "\n 恭喜发财   年年有余   岁岁平安";
        String str2 = "\n 财源广进   财源滚滚   财源滚滚";
        String str3 = "\n 事事如意   招财进宝   吉祥如意";
        String str4 = "\n 万事如意   一路发财   心想事成";
        String str5 = "\n 合家平安   步步高升   一元复始";*/
        String str5 = "            赐平安符            ";
        String str6 = "\n祝您:万事如意,事业有成,合家平安";
        String str7 = "\n清晨曙光初现,幸福在你身边;中午艳";
        String str8 = "\n阳高照,微笑在你心间;傍晚日落西山";
        String str9 = "\n欢乐随你满天;狗年吉祥，大吉大利!";
        String str10 ="\n愿你一年天天天开心,小时时时快乐,";
        String str11 ="\n分分分精彩,秒秒秒幸福。 ";
        String str12 ="";
        sendPortText(str0);
        sendPortText(str12);
        sendPortText(str11);
        sendPortText(str10);
        sendPortText(str9);
        sendPortText(str8);
        sendPortText(str7);
        sendPortText(str6);
        sendPortText(str5);
       /* sendPortText(str5);
        sendPortText(str4);
        sendPortText(str3);
        sendPortText(str2);
        sendPortText(str1);*/
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



    /*图片到字节数组*/
    public byte[] image2byte(String path){
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        }
        catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"打印的Activity");
        LogUtil.e(TAG, "..System.currentTimeMillis()"+System.currentTimeMillis());
       // updatePresentation();//在父类中已经被调用了


    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.i(TAG, "onStop: 停止了么？");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
