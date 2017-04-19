package com.financial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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

    /* @Bind(R.id.rb_benefith)
     RadioButton rbBenefith;
     @Bind(R.id.rb_worth)
     RadioButton rbWorth;
     @Bind(R.id.rb_commerce)
     RadioButton rbCommerce;*/
  /*  @Bind(R.id.rg_finacial)
    RadioGroup rgFinacial;*/
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

    @Bind(R.id.rg_start)
    RadioGroup rgStart;

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
    @Bind(R.id.rb_equal5w)
    RadioButton rbEqual5w;
    @Bind(R.id.rb_equal15w)
    RadioButton rbEqual15w;
    @Bind(R.id.rb_equa30w)
    RadioButton rbEqua30w;
    @Bind(R.id.rb_equa100w)
    RadioButton rbEqua100w;
    private SelecttPresentation mselecttPresentation;


    LoadStatelayout loadLayout;
    private Map<String, String> map;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
       // etRate.setFocusable(false);
       // Log.e("ManageFinaicalSelect", ".......编辑框失去焦点");

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_manage_finaical_select);
        loadLayout = new LoadStatelayout(this);
        map = new HashMap();
        // loadView();
    }

    public void loadView() {
        //初始化加载中可能出现的视图；
        Log.e("ManageFinaicalSelect", "............111");
    }

    @Override
    protected void initData() {
        //默认选中的;
        map.put("类型", "收益");
        map.put("time", "30以下");

    }

    //raduiobutton的点击记录事件；
    @Override
    protected void initEvent() {


        RadioGroup rgTime = (RadioGroup) findViewById(R.id.rg_time);
        rgTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_no_time_limit:
                        map.put("time", "不限");
                        break;
                    case R.id.rb_before30:
                        map.put("time", "30以下");
                        break;
                    case R.id.rb_Limt_30:
                        map.put("time", "30以上");
                        break;
                    case R.id.rb_limt_90:
                        map.put("time", "90以上");
                        break;
                    case R.id.rb_limt_181:
                        map.put("time", "181以上");
                        break;
                    case R.id.rb_limt_365:
                        map.put("time", "365以上");
                        break;
                }
                Log.e("ManageFinaicalSelect", ".." + map.size());
                //遍历map集合;
                Set<String> set = map.keySet();
                for (String key : set) {
                    Log.e("ManageFinaicalSelect", ".." + key + ":" + map.get(key));
                }
            }
        });
          RadioGroup rgRate= (RadioGroup) findViewById(R.id.rg_year_benifit);
         rgRate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
               switch (checkedId){
                   case R.id.rb_no_yearbenifit_limit:
                       map.put("收益","不限");
                       break;
                   case R.id.rb_before50:
                       map.put("收益","前50");
                       break;
                   case R.id.rb_before30:
                       map.put("收益","前30");
                       break;
                   case R.id.rb_before10:
                       map.put("收益","前10");
                       break;
                   case R.id.rb_year_rate:
                       Log.e("ManageFinaicalSelect", ".......编辑框获得焦点");
                       break;
                     }

                  }
             });

        }

    //重置按钮；
    public void reset(View v) {
//        rgFinacial.check(R.id.rb_benefith);
        rgBaseMoney.check(R.id.rb_no_basemoney_limit);
        rgMoney.check(R.id.rb_no_moneykind_limt);
        rgDanger.check(R.id.rb_no_danger_limit);
        rgSaless.check(R.id.rb_no_saless_limit);
        rgStart.check(R.id.rb_no_startpoint_limit);
        rgYearBenifit.check(R.id.rb_no_yearbenifit_limit);
        rgTime.check(R.id.rb_no_time_limit);
        map.clear();

        Log.e("ManageFinaicalSelect", ".." + map.size());
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
    protected void onStop() {
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
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("正在努力加载中");
        dialog.show();
        //执行网络请求
        initLoadData();
    }


    private void initLoadData() {

        //模拟网络请求
        //得到数据后传递给新的界面

         startActivity(new Intent(this, FinanceIntroduceList.class));
         dialog.dismiss();

    }


    @OnClick(R.id.tv_Back)
    public void onClick() {
        finish();
    }

      }
