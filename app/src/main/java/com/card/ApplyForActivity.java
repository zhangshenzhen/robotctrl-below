package com.card;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaRouter;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
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


/*卡片申请*/

public class ApplyForActivity extends BaseActivity {

    private static final String TAG = "ApplyForActivity";
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_next)
    Button btnNext;

    private ApplyforPresentation mapplyforPresentation;
    private TextView tvxieyi;
    private TextView tvxieyi2;
    private TextView tvxieyi3;
    private TextView tvxieyi4;
    private TextView tvxieyi5;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_apply_for);
        tvxieyi = (TextView) findViewById(R.id.tv_xieyi);
        tvxieyi2 = (TextView) findViewById(R.id.tv_xieyi2);
        tvxieyi3 = (TextView) findViewById(R.id.tv_xieyi3);
        tvxieyi4 = (TextView) findViewById(R.id.tv_xieyi4);
        tvxieyi5 = (TextView) findViewById(R.id.tv_xieyi5);


        ButterKnife.bind(this);
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
    protected void initEvent() {
    }

    @Override
    protected void initViewData() {
    }
    @OnClick({R.id.cb_agree, R.id.btn_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cb_agree:
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:
                if (cbAgree.isChecked()) { //同意用户协议;
                    startActivity(new Intent(ApplyForActivity.this,
                            ApplyForSelectCardActivity.class));
                } else {
                    ToastUtil.show(ApplyForActivity.this, "请先阅读用户协议,并同意");
                    Log.d(TAG,"请先阅读用户协议,并同意");
                }
                break;
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
        if (mapplyforPresentation != null && mapplyforPresentation.getDisplay() != presentationDisplay) {
            mapplyforPresentation.dismiss();
            mapplyforPresentation = null;
        }
        if (mapplyforPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mapplyforPresentation = new ApplyforPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;

            mapplyforPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mapplyforPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mapplyforPresentation = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePresentation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updatePresentation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapplyforPresentation != null) {
            mapplyforPresentation.dismiss();
            mapplyforPresentation = null;
        }
    }

}
