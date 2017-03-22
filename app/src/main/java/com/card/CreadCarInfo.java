package com.card;

import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bean.serialport.CardInfoBean;
import com.brick.robotctrl.R;
import com.jly.idcard.IDcardActivity;
import com.presentation.presentionui.CardinfoPresentation;
import com.rg2.activity.BaseActivity;
import com.rg2.utils.SPUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.brick.robotctrl.R.string.list;

/**
 * Created by shenzhen on 2017/2/10.
 */

public class CreadCarInfo extends BaseActivity {

    private static final String TAG ="CreadCarInfo";
    @Bind(R.id.tv_card_details)
    TextView tvCardDetails;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_next)
    Button btnNext;
    private CardinfoPresentation mCardinfoPresentation;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_creadcardinfo);
        ButterKnife.bind(this);
    }

    @Override
    protected void updatePresentation() {
        // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
        // 注释 : Dismiss the current presentation if the display has changed.
        if (mCardinfoPresentation != null && mCardinfoPresentation.getDisplay() != presentationDisplay) {
            mCardinfoPresentation.dismiss();
            mCardinfoPresentation = null;
        }
        if (mCardinfoPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mCardinfoPresentation = new CardinfoPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            mPresentation = mCardinfoPresentation;
            mCardinfoPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mCardinfoPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mCardinfoPresentation = null;
            }
        }
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
     /* CardInfoBean bean = (CardInfoBean) intent.getSerializableExtra("bean");
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl);
        for (String s : bean.body) {
           TextView tv1 = new TextView(mContext);
             tv1.setText(s);
            rl.addView(tv1);
         }
        for (String s2 : bean.foot) {
            TextView tv2 = new TextView(mContext);
            tv2.setText(s2);
            rl.addView(tv2);
        }
        TextView tv3 = new TextView(mContext);
           tv3.setText(bean.leg);
           rl.addView(tv3);

        TextView tv4 = new TextView(mContext);
                tv4.setText(bean.title);
            rl.addView(tv4);

        for (CardInfoBean.Waist waist : bean.waists) {
            TextView tv5 = new TextView(mContext);
            tv4.setText(waist.toString());
            rl.addView(tv5);
        }
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePresentation();
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initViewData() {
    }


    @OnClick({R.id.btn_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:
                //激活信息采集软件；
              //  Boolean  isCreadcard  = false;
              //  Log.d(TAG,"激活信息采集软件1.."+isCreadcard);
                  SPUtils.put(mContext,"isCreadcard",true);
                startActivity(new Intent(CreadCarInfo.this, IDcardActivity.class));
                break;
        }
    }
}
