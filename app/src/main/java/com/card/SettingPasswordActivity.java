package com.card;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;
import com.rg2.utils.StringUtils;
import com.rg2.utils.ToastUtil;

import org.apache.commons.httpclient.HttpClient;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lx on 2017/2/20.
 */

public class SettingPasswordActivity extends BaseActivity {

    private static final String TAG = "SettingPasswordActivity";
    private EditText etpassword;
    private EditText etrepassrord;
    private ImageView settingRight;
    private Button submit;
    private TextView tvBack;
    private TextView tvNext;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
    }

    @Override
    protected void initViewData() {
        etpassword = (EditText) findViewById(R.id.et_password);
        etrepassrord = (EditText) findViewById(R.id.et_repassword);
        settingRight = (ImageView) findViewById(R.id.iv_right);
        submit = (Button) findViewById(R.id.btn_submit);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvNext = (TextView) findViewById(R.id.tv_next);
    }

    @Override
    protected void initEvent() {


    }

    @Override
    protected void initData() {

    }

    @Override
    protected void updatePresentation() {

    }

    @OnClick({R.id.btn_submit, R.id.tv_back, R.id.tv_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                Submit();
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_next:
                //开启下一页
                break;
        }
    }

    private void Submit() {
        String password = etpassword.getText().toString().trim();
        String repassword = etrepassrord.getText().toString().trim();
        //密码不能为空， 长度
        if (TextUtils.isEmpty(password)||password.length() !=6){
            ToastUtil.show(mContext,"请检查密码是否符合要求");
            Log.d(TAG,"请检查密码是否符合要求password");
            return;
        }else if(TextUtils.isEmpty(repassword)||repassword.length() !=6){
            ToastUtil.show(mContext,"两次密码不一致,请重新设置");
            Log.d(TAG,"请检查密码是否符合要求password");
            return;
        }else if( !repassword.equals(password)){
            ToastUtil.show(mContext,"两次密码不一致,请重新设置");
            Log.d(TAG,"两次密码不一致,请重新设置");
            return;
        }
        //网络请求. 提交密码数据到服务器;
             LoadData(password,repassword);
    }

    private void LoadData(String password, String repassword) {
        ProgressDialog pd = new ProgressDialog(mContext);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("正在提交数据到服务器....");
        pd.show();
    }
}
