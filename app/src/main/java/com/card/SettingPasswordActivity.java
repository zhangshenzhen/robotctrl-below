package com.card;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.presentation.presentionui.InserCardPresentation;
import com.presentation.presentionui.PasswordPresentation;
import com.rg2.activity.BaseActivity;
import com.rg2.activity.FiveActivity;
import com.rg2.utils.StringUtils;
import com.rg2.utils.ToastUtil;
import com.userinfo.InforCompleteActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.commons.httpclient.HttpClient;

import java.security.KeyStore;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

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
    private PasswordPresentation mPasswordPresentation;

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
         settingRight.setVisibility(View.GONE);//隐藏
        if (TextUtils.isEmpty(password) || password.length() != 6) {
            ToastUtil.show(mContext, "请检查密码是否符合要求");
            Log.d(TAG, "请检查密码是否符合要求password");
            mPasswordPresentation.passWordError();//调用方法;
            return;
        } else if (TextUtils.isEmpty(repassword) || repassword.length() != 6) {
            Log.d(TAG, "请检查密码是否符合要求password");
            mPasswordPresentation.passWordError();//调用方法;
            return;
        } else if (!repassword.equals(password)) {
            ToastUtil.show(mContext, "两次密码不一致,请重新设置");
            Log.d(TAG, "两次密码不一致,请重新设置");
            return;
        }
        //网络请求. 提交密码数据到服务器;
        LoadData(password, repassword);
    }

    private void LoadData(String password, String repassword) {
        final ProgressDialog pd = new ProgressDialog(mContext);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("正在提交您设置的密码....");
        pd.show();
        //上传数据;
        String url = "https://www.baidu.com";
        OkHttpUtils.get().url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        SystemClock.sleep(1000);
                        pd.dismiss();
                        Log.d(TAG, "Exception:"+call+"......"+e);
                    }
                    @Override
                    public void onResponse(Call call, String s) {
                        pd.dismiss();
                        Log.d(TAG, "String:"+call+"......"+s);
                        settingRight.setVisibility(View.VISIBLE);
                    }
                });
        //显示出设置正确的图标；
        mPasswordPresentation.passWord();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePresentation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPasswordPresentation != null){
            mPasswordPresentation.dismiss();
            mPasswordPresentation= null;
        }
    }

    @Override
    protected void updatePresentation() {
        // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
        // 注释 : Dismiss the current presentation if the display has changed.
        if (mPasswordPresentation != null && mPasswordPresentation.getDisplay() != presentationDisplay) {
            mPasswordPresentation.dismiss();
            mPasswordPresentation = null;
        }
        if (mPasswordPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mPasswordPresentation = new PasswordPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;

            mPasswordPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mPasswordPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mPasswordPresentation = null;
            }
        }
    }
}