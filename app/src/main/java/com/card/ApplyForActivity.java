package com.card;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;

/*卡片激活*/

public class ApplyForActivity extends BaseActivity {
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
    String port = "/dev/ttyUSB0";
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_apply_for);
    }


    @Override
    protected void initData() {

    }


    @Override
    protected void initEvent() {

    }

    @Override
    protected void initViewData() {

    }

    @Override
    protected void updatePresentation() {

    }

}
