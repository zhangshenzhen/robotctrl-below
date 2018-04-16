package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rg2.activity.PrintActivity;

public class FunctionSelectActivity extends BaseActivity {
    private static final String TAG ="FunctionSelectActivity" ;
    private Button jiaohao;
    private Button fuwu;
    private TextView back;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_function_select);
     }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        jiaohao = (Button) findViewById(R.id.btn_jiaohao);
        fuwu = (Button) findViewById(R.id.btn_fuwu);
        back = (TextView) findViewById(R.id.tv_back);
    }

    @Override
    protected void initViewData() {
        jiaohao.setOnClickListener(this);
        fuwu.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.btn_jiaohao:
                //进入叫号界面;
                startActivity(new Intent(FunctionSelectActivity.this, PrintActivity.class));
                break;
            case R.id.btn_fuwu:
                //进入表情界面;
               ExpressionActivity.startAction(FunctionSelectActivity.this,1);
                break;
            case R.id.tv_back:
                finish();
                Log.i(TAG, "onClick: 返回了");
                break;
        }
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
