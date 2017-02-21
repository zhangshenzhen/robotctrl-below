package com.rg2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.brick.robotctrl.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.presentation.ActivityViewWrapper;
import com.presentation.presentionui.UserInfoPresentation;
import com.rg2.listener.MyOnClickListener;
import com.rg2.utils.DialogUtils;
import com.rg2.utils.SPUtils;


/**
 * 作者：王先云 on 2016/9/1 15:49
 * 邮箱：wangxianyun1@163.com
 * 描述：一句话简单描述
 */
public class FiveActivity extends BaseActivity {
    private static final String TAG = "CreadCarInfo";
    private Button mSubmitBtn;
    private EditText mCompanyNameEt;
    private TextView mCompanyPersonnelTv;
    private EditText mCompanyProvinceEt;
    private EditText mCompanyCityEt;
    private EditText mCompanyAddressEt;
    private TextView mCompanyTypeTv;
    private TextView mCompanyIndustryTv;
    private EditText mDepartmentEt;
    private EditText mPostEt;
    private TextView mPostLevelTv;
    private TextView mSalaryTv;
    private TextView mWorkYearsTv;
    private TextView mBackTv;
    private UserInfoPresentation mUserInfoPresentation;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;



    @Override
    protected void initData() {
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_five);
        mSubmitBtn = (Button) findViewById(R.id.btn_submit);
        mBackTv = (TextView) findViewById(R.id.tv_back);
        mCompanyNameEt = (EditText) findViewById(R.id.et_company_name);
        mCompanyPersonnelTv = (TextView) findViewById(R.id.tv_company_personnel);
        mCompanyProvinceEt = (EditText) findViewById(R.id.et_company_province);
        mCompanyCityEt = (EditText) findViewById(R.id.et_company_city);
        mCompanyAddressEt = (EditText) findViewById(R.id.et_company_address);
        mCompanyTypeTv = (TextView) findViewById(R.id.tv_company_type);
        mCompanyIndustryTv = (TextView) findViewById(R.id.tv_company_industry);
        mDepartmentEt = (EditText) findViewById(R.id.et_department);
        mPostEt = (EditText) findViewById(R.id.et_post);
        mPostLevelTv = (TextView) findViewById(R.id.tv_post_level);
        mSalaryTv = (TextView) findViewById(R.id.tv_salary);
        mWorkYearsTv = (TextView) findViewById(R.id.tv_work_years);

    }

    @Override
    protected void initEvent() {
        mSubmitBtn.setOnClickListener(this);
        mCompanyPersonnelTv.setOnClickListener(this);
        mCompanyTypeTv.setOnClickListener(this);
        mCompanyIndustryTv.setOnClickListener(this);
        mPostLevelTv.setOnClickListener(this);
        mSalaryTv.setOnClickListener(this);
        mWorkYearsTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
    }

    @Override
    protected void initViewData() {

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

    int Applyfor =0;
    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == mSubmitBtn) {
            String mCompanyName = mCompanyNameEt.getText().toString();
            String mCompanyPersonnel = mCompanyPersonnelTv.getText().toString();
            String mCompanyProvince = mCompanyProvinceEt.getText().toString();
            String mCompanyCity = mCompanyCityEt.getText().toString();
            String mCompanyAddress = mCompanyAddressEt.getText().toString();
            String mCompanyType = mCompanyTypeTv.getText().toString();
            String mCompanyIndustry = mCompanyIndustryTv.getText().toString();
            String mDepartment = mDepartmentEt.getText().toString();
            String mPost = mPostEt.getText().toString();
            String mPostLevel = mPostLevelTv.getText().toString();
            String mSalary = mSalaryTv.getText().toString();
            String mWorkYears = mWorkYearsTv.getText().toString();


//            if (StringUtils.stringIsEmpty(mCompanyName))
//            {
//                ToastUtil.show(FiveActivity.this, "请输入公司的名称");
//                return;
//            }
//
//            if (StringUtils.stringIsEmpty(mCompanyPersonnel))
//            {
//                ToastUtil.show(FiveActivity.this, "请选择员工人数");
//                return;
//            }
//            if (StringUtils.stringIsEmpty(mCompanyProvince))
//            {
//                ToastUtil.show(FiveActivity.this, "请选择公司所在省份");
//                return;
//            }
//            if (StringUtils.stringIsEmpty(mCompanyCity))
//            {
//                ToastUtil.show(FiveActivity.this, "请选择公司所在城市");
//                return;
//            }
//            if (StringUtils.stringIsEmpty(mCompanyAddress))
//            {
//                ToastUtil.show(FiveActivity.this, "请输入公司的详细地址");
//                return;
//            }
//            if (StringUtils.stringIsEmpty(mCompanyType))
//            {
//                ToastUtil.show(FiveActivity.this, "请选择单位性质");
//                return;
//            }
//            if (StringUtils.stringIsEmpty(mCompanyIndustry))
//            {
//                ToastUtil.show(FiveActivity.this, "请选择行业性质");
//                return;
//            }
//            if (StringUtils.stringIsEmpty(mDepartment))
//            {
//                ToastUtil.show(FiveActivity.this, "请输入任职部门");
//                return;
//            }
//            if (StringUtils.stringIsEmpty(mPost))
//            {
//                ToastUtil.show(FiveActivity.this, "请输入职位");
//                return;
//            }
//            if (StringUtils.stringIsEmpty(mPostLevel))
//            {
//                ToastUtil.show(FiveActivity.this, "请选择职位级别");
//                return;
//            }
//            if (StringUtils.stringIsEmpty(mSalary))
//            {
//                ToastUtil.show(FiveActivity.this, "请选择年薪");
//                return;
//            }
//            if (StringUtils.stringIsEmpty(mWorkYears))
//            {
//                ToastUtil.show(FiveActivity.this, "请选择工作年数");
//                return;
//            }

            //提交过之后就不能反回上一页,而是返回到信息采集的主页
            setResult(Activity.RESULT_OK);
            mBackTv.setText("反回卡片详情页 ");
            boolean b = false;

            b = (Boolean) SPUtils.get(mContext, "isCreadcard", false);
            Log.d(TAG, "激活信息采  ... 卡.." + b);
            if (b |Applyfor ==1) {
                Log.d(TAG, "激活信息采集软件 信用卡..2" + b);
                SPUtils.put(mContext, "isCreadcard", false);
                //开启网络请求,提交数据到服务器;
                Applyfor = 1 ;
                LoadData();
            } else {
                Log.d(TAG, "激活信息采集软件 借记卡..3" + b);
                //部分信息写入发卡机;
                WriterCard();
            }

            //  startActivity(new Intent(FiveActivity.this,GifActivity.class));
        } else if (v == mCompanyPersonnelTv) {
            DialogUtils.showListDialog("员工人数", getResources().getStringArray(R.array.company_personnel), FiveActivity.this, new MyOnClickListener() {
                @Override
                public void onClicked(String content) {
                    mCompanyPersonnelTv.setText(content);
                }
            });
        } else if (v == mCompanyTypeTv) {
            DialogUtils.showListDialog("单位性质", getResources().getStringArray(R.array.company_type), FiveActivity.this, new MyOnClickListener() {
                @Override
                public void onClicked(String content) {
                    mCompanyTypeTv.setText(content);
                }
            });
        } else if (v == mCompanyIndustryTv) {
            DialogUtils.showListDialog("行业性质", getResources().getStringArray(R.array.industry), FiveActivity.this, new MyOnClickListener() {
                @Override
                public void onClicked(String content) {
                    mCompanyIndustryTv.setText(content);
                }
            });
        } else if (v == mPostLevelTv) {
            DialogUtils.showListDialog("职位级别", getResources().getStringArray(R.array.user_post), FiveActivity.this, new MyOnClickListener() {
                @Override
                public void onClicked(String content) {
                    mPostLevelTv.setText(content);
                }
            });
        } else if (v == mSalaryTv) {
            DialogUtils.showListDialog("年薪", getResources().getStringArray(R.array.salary), FiveActivity.this, new MyOnClickListener() {
                @Override
                public void onClicked(String content) {
                    mSalaryTv.setText(content);
                }
            });
        } else if (v == mWorkYearsTv) {
            DialogUtils.showListDialog("工作年数", getResources().getStringArray(R.array.work_years), FiveActivity.this, new MyOnClickListener() {
                @Override
                public void onClicked(String content) {
                    mWorkYearsTv.setText(content);
                }
            });
        } else if (v == mBackTv) {
            finish();
         }

            // finish();
    }
    //写卡操作
    private void WriterCard() {
    }

    //  链接网络;
    private void LoadData() {
       ProgressDialog pd = new ProgressDialog(mContext);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
         pd.setMessage("正在加载中...");
         pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
         pd.show();//显示
        //上传数据;
   }
}
