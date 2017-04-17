package com.card;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaRouter;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.presentation.presentionui.ApplyforPresentation;
import com.rg2.activity.BaseActivity;
import com.rg2.utils.ToastUtil;

import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CardBusinessActivity extends BaseActivity {
    public final static String TAG = "CardBusinessActivity";
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_next)
    Button btnNext;


    private ApplyforPresentation mApplyforPresentation;
    private TextView tvxieyi;
    private TextView tvxieyi2;
    private TextView tvxieyi3;
    private TextView tvxieyi4;
    private TextView tvxieyi5;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_business_card);
        tvxieyi = (TextView) findViewById(R.id.tv_xieyi);
        tvxieyi2 = (TextView) findViewById(R.id.tv_xieyi2);
        tvxieyi3 = (TextView) findViewById(R.id.tv_xieyi3);
        tvxieyi4 = (TextView) findViewById(R.id.tv_xieyi4);
        tvxieyi5 = (TextView) findViewById(R.id.tv_xieyi5);

        ButterKnife.bind(this);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initViewData() {

    }

    @Override
    protected void initData() {
        String html = "<html><body>&nbsp&nbsp&nbsp&nbsp<font color=\"#123\" size=\"8\">最高可获得100万个集分宝（价值¥10000元）奖励" +
                "年费 人民币卡普卡：RMB 40 人民币卡金卡：RMB 80 新开卡客户免首年年费" +
                "年费 人民币卡普卡：RMB 40 人民币卡金卡：RMB 80 新开卡客户免首年年费" +
                "年费 人民币卡普卡：RMB 40 人民币卡金卡：RMB 80 新开卡客户免首年年费" +
                "刷卡消费6次或以上滚动免次年年费。首次使用广发淘宝卡快捷支付，送5000集分宝" +
                "刷卡消费6次或以上滚动免次年年费。首次使用广发淘宝卡快捷支付，送5000集分宝" +
                "刷卡消费6次或以上滚动免次年年费。首次使用广发淘宝卡快捷支付，送5000集分宝" +
                "刷卡消费6次或以上滚动免次年年费。首次使用广发淘宝卡快捷支付，送5000集分宝" +
                "年费 人民币卡普卡：RMB 40 人民币卡金卡：RMB 80 新开卡客户免首年年费，" +
                "年费 人民币卡普卡：RMB 40 人民币卡金卡：RMB 80 新开卡客户免首年年费，" +
                "币卡普卡：RMB 40 人民币卡金卡：RMB 80 新开卡客户免首年年费，刷卡消费6次或以上滚动免次" +
                "年年费。首次使用广发淘宝卡快捷支付，送5000集分宝 "+
                "最高可获得100万个 </font><body><html>";
        // tvxieyi.setMovementMethod(ScrollingMovementMethod.getInstance());// 设置可滚动
        // tvxieyi.setMovementMethod(LinkMovementMethod.getInstance());//设置超链接可以打开网页
        tvxieyi.setText(Html.fromHtml(html, imgGetter, null));
        tvxieyi2.setText(Html.fromHtml(html, imgGetter, null));
        tvxieyi3.setText(Html.fromHtml(html, imgGetter, null));
        tvxieyi4.setText(Html.fromHtml(html, imgGetter, null));
        tvxieyi5.setText(Html.fromHtml(html, imgGetter, null));

    }
    Html.ImageGetter imgGetter = new Html.ImageGetter() {
        public Drawable getDrawable(String source) {
            Drawable drawable = null;
            URL url;
            try {
                url = new URL(source);
                drawable = Drawable.createFromStream(url.openStream(), ""); // 获取网路图片
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            return drawable;
        }
    };


    @Override
    protected void updatePresentation() {
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
          Log.d(TAG, mApplyforPresentation+"updatePresentation: "+presentationDisplay);
        // 注释 : Dismiss the current presentation if the display has changed.
        if (mApplyforPresentation != null && mApplyforPresentation.getDisplay() != presentationDisplay) {
            mApplyforPresentation.dismiss();
            mApplyforPresentation = null;
        }
        if (mApplyforPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mApplyforPresentation = new ApplyforPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;

            mApplyforPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mApplyforPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mApplyforPresentation = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePresentation();
     }
    @Override
    protected void onStop() {
        super.onStop();
        if (mApplyforPresentation != null){
            mApplyforPresentation.dismiss();
            mApplyforPresentation = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mApplyforPresentation != null){
            mApplyforPresentation.dismiss();
            mApplyforPresentation = null;
        }
    }

    @OnClick({R.id.btn_back,R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                Log.e(TAG, "退出卡片办理业务");
                finish();
                break;
            case R.id.btn_next:
                if (cbAgree.isChecked()) { //同意用户协议;
                    Log.e(TAG, "同意用户协议，进入下一页");
                    startActivity(new Intent(CardBusinessActivity.this,
                            BusinessSelectCardActivity.class));
                } else {
                    ToastUtil.show(CardBusinessActivity.this, "请先阅读用户协议,并同意");
                }
                break;
        }
    }

}
