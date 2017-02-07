package com.financial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.loadview.LoadStatelayout;
import com.presentation.SelecttPresentation;
import com.rg2.activity.BaseActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManageFinaicalSelect extends BaseActivity {

    @Bind(R.id.rb_benefith)
    RadioButton rbBenefith;
    @Bind(R.id.rb_worth)
    RadioButton rbWorth;
    @Bind(R.id.rb_commerce)
    RadioButton rbCommerce;
    @Bind(R.id.rg_finacial)
    RadioGroup rgFinacial;
    @Bind(R.id.rb_no_time_limit)
    RadioButton rbNoTimeLimit;
    @Bind(R.id.rb_Limt_30)
    RadioButton rbLimt30;
    @Bind(R.id.rb_limt_90)
    RadioButton rbLimt90;
    @Bind(R.id.rb_limt_181)
    RadioButton rbLimt181;
    @Bind(R.id.rb_limt_365)
    RadioButton rbLimt365;
    @Bind(R.id.rg_time)
    RadioGroup rgTime;
    @Bind(R.id.rb_no_moneykind_limt)
    RadioButton rbNoMoneykindLimt;
    @Bind(R.id.rb_rmb)
    RadioButton rbRmb;
    @Bind(R.id.rg_money)
    RadioGroup rgMoney;
    @Bind(R.id.rb_no_startpoint_limit)
    RadioButton rbNoStartpointLimit;
    @Bind(R.id.rb_equal)
    RadioButton rbEqual;
    @Bind(R.id.rb_more_equal)
    RadioButton rbMoreEqual;
    @Bind(R.id.rg_start)
    RadioGroup rgStart;
    @Bind(R.id.et_money)
    EditText etMoney;
    @Bind(R.id.rb_no_yearbenifit_limit)
    RadioButton rbNoYearbenifitLimit;
    @Bind(R.id.rb_before50)
    RadioButton rbBefore50;
    @Bind(R.id.rb_before30)
    RadioButton rbBefore30;
    @Bind(R.id.rb_before10)
    RadioButton rbBefore10;
    @Bind(R.id.rb_year_rate)
    RadioButton rbYearRate;
    @Bind(R.id.rg_year_benifit)
    RadioGroup rgYearBenifit;
    @Bind(R.id.et_rate)
    EditText etRate;
    @Bind(R.id.rb_no_danger_limit)
    RadioButton rbNoDangerLimit;
    @Bind(R.id.rb_care)
    RadioButton rbCare;
    @Bind(R.id.rb_smooth)
    RadioButton rbSmooth;
    @Bind(R.id.rb_balance)
    RadioButton rbBalance;
    @Bind(R.id.rb_progress)
    RadioButton rbProgress;
    @Bind(R.id.rb_more_benefit)
    RadioButton rbMoreBenefit;
    @Bind(R.id.rg_danger)
    RadioGroup rgDanger;
    @Bind(R.id.rb_no_basemoney_limit)
    RadioButton rbNoBasemoneyLimit;
    @Bind(R.id.rb_basemoney)
    RadioButton rbBasemoney;
    @Bind(R.id.rb_no_basemoney)
    RadioButton rbNoBasemoney;
    @Bind(R.id.rg_base_money)
    RadioGroup rgBaseMoney;
    @Bind(R.id.rb_no_saless_limit)
    RadioButton rbNoSalessLimit;
    @Bind(R.id.rb_will_saless)
    RadioButton rbWillSaless;
    @Bind(R.id.rb_doing_saless)
    RadioButton rbDoingSaless;
    @Bind(R.id.rg_saless)
    RadioGroup rgSaless;
    @Bind(R.id.activity_manage_finaical_list)
    LinearLayout activityManageFinaicalList;
    Button mErrorBtnRetry;
    @Bind(R.id.tv_Back)
    TextView tvBack;
    private SelecttPresentation mselecttPresentation;


    LoadStatelayout loadLayout;
  private Map map ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_manage_finaical_select);
        loadLayout  = new LoadStatelayout(this);

        // loadView();
    }

    public void loadView() {
        //初始化加载中可能出现的视图；
        Log.e("ManageFinaicalSelect", "............111");
      /*  View errorView = View.inflate(this, R.layout.layout_error, null);
        mErrorBtnRetry = (Button) errorView.findViewById(R.id.error_btn_retry);*/
         /* ProgressDialog progressDialog = new ProgressDialog(this);
          progressDialog.setCancelable(true);
          progressDialog.setMessage("正在加载中");
          progressDialog.show();*/

       /* loadLayout.setLoadingView(R.layout.layout_loading);
        loadLayout.setEmptyView(errorView);
        return loadLayout;*/
    }

    @Override
    protected void initData() {
    }
 //raduiobutton的点击记录事件；
    @Override
    protected void initEvent() {
        map = new HashMap();
        RadioGroup rgFinacial = (RadioGroup) findViewById(R.id.rg_finacial);
      rgFinacial.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(RadioGroup group, int checkedId) {
                map.remove("类型");
              switch (checkedId){
                  case R.id.rb_benefith:
                      map.put("类型","收益");
                      break;
                  case R.id.rb_worth:
                      map.put("类型","净值");
                      break;
                  case R.id.rb_commerce:
                      map.put("类型","商业");
                      break;
                }

          }
      });

    }

    //重置按钮；
    public void reset(View v){
        rgFinacial.check(R.id.rb_benefith);

        rgBaseMoney.check(R.id.rb_no_basemoney_limit);
        rgMoney.check(R.id.rb_no_moneykind_limt);
        rgDanger.check(R.id.rb_no_danger_limit);
        rgSaless.check(R.id.rb_no_saless_limit);
        rgStart.check(R.id.rb_no_startpoint_limit);
        rgYearBenifit.check(R.id.rb_no_yearbenifit_limit);
        rgTime.check(R.id.rb_no_time_limit);

      //  Log.e("ManageFinaicalSelect",".."+map.size());
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
        if (mselecttPresentation != null) {
            mselecttPresentation.dismiss();
            mselecttPresentation = null;
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
        if (mselecttPresentation != null && mselecttPresentation.getDisplay() != presentationDisplay) {
            mselecttPresentation.dismiss();
            mselecttPresentation = null;
        }
        if (mselecttPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mselecttPresentation = new SelecttPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            mPresentation = mselecttPresentation;
            mselecttPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mselecttPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mselecttPresentation = null;
            }
        }
    }

    /**
     * 在查询的方法里执行网络请求，筛选出需要的理财产品，并开启新的界面呈现筛选后的理财产品
     */
    public void query(View view) {
        //  loadLayout.setState(LoadStatelayout.STATE_LOADING);//默认加载中
        //执行网络请求
        initLoadData();
    }


    private void initLoadData() {

        new Thread() {
            @Override
            public void run() {
                //  super.run();
                SystemClock.sleep(2000);
            }
        }.start();
        //模拟网络请求
        //得到数据后传递给新的界面
        startActivity(new Intent(this, FinanceIntroduceList.class));

    }


    @OnClick(R.id.tv_Back)
    public void onClick() {
        finish();
    }

}
