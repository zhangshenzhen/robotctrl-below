package com.rg2.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.brick.robotctrl.SettingsActivity;
import com.rg2.utils.DialogUtils;
import com.rg2.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：王先云 on 2016/12/17 18:44
 * 邮箱：wangxianyun1@163.com
 * 描述：一句话简单描述
 */
public class FingerprintActivity extends BaseActivity
{

    private TextView mBackTv;
    private Button   mFingerBtn;
    private Button   mSystemSettingBtn;
    private Timer    timer;

    @Override
    protected void updatePresentation() {

    }

    @Override
    protected void initData()
    {
        timer = new Timer(true);
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_finger);
        mBackTv = (TextView) findViewById(R.id.tv_back);
        mFingerBtn = (Button) findViewById(R.id.btn_finger);
        mSystemSettingBtn = (Button) findViewById(R.id.btn_system);
    }

    @Override
    protected void initEvent()
    {
        mBackTv.setOnClickListener(this);
        mFingerBtn.setOnClickListener(this);
        mSystemSettingBtn.setOnClickListener(this);
    }

    @Override
    protected void initViewData()
    {

    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (v == mBackTv)
        {
            finish();
        }
        else if (v == mFingerBtn)
        {
            fingerprint();
        }
        else if (v == mSystemSettingBtn)
        {
            startActivity(new Intent(this, SettingsActivity.class));
        }
    }


    private void fingerprint()
    {

        startAddCommand();
        //        timer.schedule(mFingerTask, 200, 1000); //改指令执行后延时1000ms后执行run，之后每1000ms执行�?次run
        mNoticeDialog = DialogUtils.showNoticeDialog(this, "请录入指纹", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            }
        });
        mNoticeDialog.show();


    }

    private Dialog mNoticeDialog;
    private TimerTask mFingerTask = new TimerTask()
    {
        @Override
        public void run()
        {
            String fingerprint = "echo low > /sys/class/gpio/gpio205/direction\n";
            List<String> commands = new ArrayList<String>();
            commands.add(fingerprint);

            ShellUtils.CommandResult result = ShellUtils.execCommand(commands, false, true);
            LogUtil.e("TAG", "result-->" + result.toString());
        }
    };

    private void startAddCommand() {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                List<String> commands = new ArrayList<String>();
                String fingerprint = "echo low > /sys/class/gpio/gpio205/direction\n";
                commands.add(fingerprint);

                ShellUtils.CommandResult result = ShellUtils.execCommand(commands, false, true);
                LogUtil.e("TAG", "result-->" + result.toString());

                try
                {
                    new Thread().sleep(200);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                fingerprint = "echo high > /sys/class/gpio/gpio205/direction\n";
                commands.clear();
                commands.add(fingerprint);

                LogUtil.e("TAG", "result-->" + ShellUtils.execCommand(commands, false, true));
            }
        }).start();
    }
    private void  stopCommand()
    {
        String end = "echo low > /sys/class/gpio/gpio36/direction\n";
        List<String> commands = new ArrayList<String>();
        commands.add(end);

        ShellUtils.CommandResult result = ShellUtils.execCommand(commands, false, true);
    }
}
