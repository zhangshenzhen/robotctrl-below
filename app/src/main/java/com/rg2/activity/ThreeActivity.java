package com.rg2.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.presentation.presentionui.UserInfoPresentation;
import com.rg2.listener.MyOnClickListener;
import com.rg2.utils.CityDialog;
import com.rg2.utils.DialogUtils;
import com.rg2.utils.StringUtils;
import com.rg2.utils.ToastUtil;


/**
 * 作者：王先云 on 2016/9/1 15:49
 * 邮箱：wangxianyun1@163.com
 * 描述：一句话简单描述
 */
public class ThreeActivity extends BaseActivity
{
    private EditText mEmailEt;
    private EditText mProvinceEt;
    private EditText mCityEt;
    private EditText mCardedAddressEt;//身份证地址
    private RadioGroup mMaritalStatusRg;
    private TextView mEducationTv;//学历
    private TextView mResidentialAddressTv;//住宅地址
    private EditText mDescAddressEt;//住宅具体门牌号
    private TextView mResidentialTypeTv;//住宅类型
    private TextView mResidenceTimeTv;//住宅年限
    private Button mBackTv;
    private Button mSubmitBtn;
    private UserInfoPresentation mUserInfoPresentation;

    @Override
    protected void initData()
    {

    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_three);
        mSubmitBtn = (Button) findViewById(R.id.btn_submit);
        mEmailEt = (EditText) findViewById(R.id.et_email);
        mProvinceEt = (EditText) findViewById(R.id.et_province);
        mCityEt = (EditText) findViewById(R.id.et_city);
        mCardedAddressEt = (EditText) findViewById(R.id.et_carded_address);
        mMaritalStatusRg = (RadioGroup) findViewById(R.id.rg_marital_status);
        mEducationTv = (TextView) findViewById(R.id.tv_education);
        mResidentialAddressTv = (TextView) findViewById(R.id.tv_residential_address);
        mDescAddressEt = (EditText) findViewById(R.id.et_desc_address);
        mResidentialTypeTv = (TextView) findViewById(R.id.tv_residential_type);
        mResidenceTimeTv = (TextView) findViewById(R.id.tv_residence_time);
        mBackTv=(Button)findViewById(R.id.tv_back);
    }

    @Override
    protected void initEvent()
    {
        mSubmitBtn.setOnClickListener(this);
        mEducationTv.setOnClickListener(this);
        mResidentialAddressTv.setOnClickListener(this);
        mResidentialTypeTv.setOnClickListener(this);
        mResidenceTimeTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    protected void initViewData()
    {

    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePresentation();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserInfoPresentation != null){
            mUserInfoPresentation.dismiss();
            mUserInfoPresentation= null;
        }
    }

    @Override
    protected void updatePresentation() {
        // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay =  route  !=  null ? route.getPresentationDisplay() : null;
        // 注释 : Dismiss the current presentation if the display has changed.
        if (mUserInfoPresentation != null && mUserInfoPresentation.getDisplay() !=  presentationDisplay) {
            mUserInfoPresentation.dismiss();
            mUserInfoPresentation = null;
        }
        if (mUserInfoPresentation == null &&  presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mUserInfoPresentation = new UserInfoPresentation(this,  presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            mPresentation  =  mUserInfoPresentation  ;
            // Log.d(TAG, "updatePresentation: this: "+ this.toString());
            mUserInfoPresentation.setOnDismissListener(mOnDismissListener);

            // Try to show the presentation, this might fail if the display has
            // gone away in the mean time
            try {
                mUserInfoPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed
                // Log.d(TAG, "updatePresentation: failed");
                mUserInfoPresentation = null;
            }
        }
    }
    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (v == mSubmitBtn)
        {

            String mEmail = mEmailEt.getText().toString();
            String mProvince = mProvinceEt.getText().toString();
            String mCity = mCityEt.getText().toString();
            String mCardedAddress = mCardedAddressEt.getText().toString();
            String mMaritalStatus = mMaritalStatusRg.getCheckedRadioButtonId() + "";
            String mEducation = mEducationTv.getText().toString();
            String mResidentialAddress = mResidentialAddressTv.getText().toString();
            String mDescAddress = mDescAddressEt.getText().toString();

            String mResidentialType = mResidentialTypeTv.getText().toString();
            String mResidenceTime = mResidenceTimeTv.getText().toString();

//                        if (!StringUtils.checkEmail(mEmail))
//                        {
//                            ToastUtil.show(ThreeActivity.this, "请输入正确的邮箱");
//                            return;
//                        }

//                        if (StringUtils.stringIsEmpty(mProvince))
//                        {
//                            ToastUtil.show(ThreeActivity.this, "请输入身份证所在省份");
//                            return;
//                        }
//
//                        if (StringUtils.stringIsEmpty(mCity))
//                        {
//                            ToastUtil.show(ThreeActivity.this, "请输入身份证所在城市");
//                            return;
//                        }
//                        if (StringUtils.stringIsEmpty(mCardedAddress))
//                        {
//                            ToastUtil.show(ThreeActivity.this, "请与身份证上地址保持一致");
//                            return;
//                        }
//                        if (StringUtils.stringIsEmpty(mEducation))
//                        {
//                            ToastUtil.show(ThreeActivity.this, "请选择学历");
//                            return;
//                        }
//                        if (StringUtils.stringIsEmpty(mResidentialAddress))
//                        {
//                            ToastUtil.show(ThreeActivity.this, "请选择学历");
//                            return;
//                        }
//                        if (StringUtils.stringIsEmpty(mDescAddress))
//                        {
//                            ToastUtil.show(ThreeActivity.this, "请输入详细街道门牌号");
//                            return;
//                        }
//                        if (StringUtils.stringIsEmpty(mResidentialType))
//                        {
//                            ToastUtil.show(ThreeActivity.this, "请选择住宅类型");
//                            return;
//                        }

//                        if (StringUtils.stringIsEmpty(mResidenceTime))
//                        {
//                            ToastUtil.show(ThreeActivity.this, "请选择住宅年薪");
//                            return;
//                        }

            startActivityForResult(new Intent(this,FourActivity.class),1);
        }
        else if (v == mEducationTv)
        {
            DialogUtils.showListDialog("选择学历", getResources().getStringArray(R.array.education), ThreeActivity.this, new MyOnClickListener()
            {
                @Override
                public void onClicked(String content)
                {
                    mEducationTv.setText(content);
                }
            });
        }
        //居住地址;
        else if (v == mResidentialAddressTv)
        {
            CityDialog mCityDialog = new CityDialog();
            mCityDialog.showCityDialog(ThreeActivity.this, new MyOnClickListener()
            {
                @Override
                public void onClicked(String content)
                {
                    mResidentialAddressTv.setText(content);
                }
            });
        }
        else if (v == mResidentialTypeTv)
        {
            DialogUtils.showListDialog("选择住宅类型", getResources().getStringArray(R.array.residential_type), ThreeActivity.this, new MyOnClickListener()
            {
                @Override
                public void onClicked(String content)
                {
                    mResidentialTypeTv.setText(content);
                }
            });
        }
        else if (v == mResidenceTimeTv)
        {
            String[] arr = new String[70];

            for (int i = 0; i < 70; i++)
            {
                arr[i] = (i + 1) + "年";
            }
            DialogUtils.showListDialog("选择住宅年限", arr, ThreeActivity.this, new MyOnClickListener()
            {
                @Override
                public void onClicked(String content)
                {
                    mResidenceTimeTv.setText(content);
                }
            });
        }
        else if(v == mBackTv)
        {
            finish();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 &&resultCode== Activity.RESULT_OK)
        {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
