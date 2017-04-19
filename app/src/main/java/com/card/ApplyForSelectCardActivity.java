package com.card;

import android.content.Intent;
import android.media.MediaRouter;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bean.serialport.CardInfoBean;
import com.brick.robotctrl.R;
import com.presentation.presentionui.SelectCardPresentation;
import com.rg2.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by shenzhen on 2017/2/10.
 */

public class ApplyForSelectCardActivity extends BaseActivity {
    private static final String TAG = "SelectCardActivity";
    @Bind(R.id.gv_card)
    GridView gvCard;
    @Bind(R.id.btn_back)
    Button btnBack;

    private CardAdapter adapter;

    private SelectCardPresentation mSelectCardPresentation;


    String[] names = new String []{"广发DIY卡","广发国行卡","广发南航明珠卡","广发淘宝潮女卡","广发淘宝型男卡"
      ,"广发携程卡","广发新聪明卡","广发易车联名卡","广发真情卡","广发国行卡","广发南航明珠卡","广发淘宝潮女卡"};
     String[][] details =new String [][] {{"自选商户类型3倍积分","积分自由抵消费或换礼"},{"刷卡消费，最高可获得100万个","集分宝（价值¥10000元）奖励！"},
           {"刷卡消费，最高可获得100万个","集分宝（价值¥10000元）奖励！"},{"透现/分期5倍积分 ","积分折抵消费余额"},{"网购五折任买","三倍积分任送","高额保险任享"}
           ,{"刷卡2元积1携程积分","异地提现免手续费","商旅预定有奖励"},{"商旅消费双倍奖励","里程宝”额外6%里程奖励","兑换机票兑一送一"}};
      String[] foods = new String []{"","免费二手车估值及置换服务，赠送高额驾驶员意外险及道路救援服务","",
      "新开卡透现/分期优惠：首笔12期及以上分期免3期手续费","","免费二手车估值及置换服务，","" };


    int [] pictures = new int []{R.drawable.gf_diy,R.drawable.gf_gh,R.drawable.gf_nhmz,R.drawable.gf_tbcn,R.drawable.gf_tbn
            ,R.drawable.gf_xc,R.drawable.gf_xcm,R.drawable.gf_ycl,R.drawable.gf_zq,R.drawable.gf_gh,R.drawable.gf_nhmz,R.drawable.gf_tbcn};
private ArrayList<CardInfoBean> list;
    private CardInfoBean bean;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_card);
        ButterKnife.bind(this);

      /* list = new ArrayList<>();
        for (int i = 0; i<details.length; i++){
            bean = new CardInfoBean();
            bean.setTitle(names[i]);
            bean.setBody(Arrays.asList(details[i]));
            bean.setWaist("年费");
            ArrayList<CardInfoBean.Waist> waists = new ArrayList<>();
            waists.add(new CardInfoBean.Waist(1,50));
            waists.add(new CardInfoBean.Waist(2,50));
            bean.setWaists(waists);
            bean.setLeg("新开卡客户免首年年费，刷卡消费6次或以上滚动免次年年费");
            bean.setFoot(Arrays.asList(foods[i]));
            for (String s : bean.body) {
                TextView textView = new TextView(this);
                textView.setText(s);
            }
            list.add(bean);
        }*/

    }
    @Override
    protected void initEvent() {


        gvCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //点击条目,打开新的内容
                Intent intent = new Intent(ApplyForSelectCardActivity.this, CreadCarInfo.class);
                // intent.putExtra("bean",list.get(position));
                 startActivity(intent);
                Log.d(TAG, "............." + position);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        adapter = new CardAdapter();
        gvCard.setAdapter(adapter);


    }

    @Override
    protected void initViewData() {


    }

    @Override
    protected void onResume() {
        super.onResume();
       // updatePresentation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSelectCardPresentation != null) {
            mSelectCardPresentation.dismiss();
            mSelectCardPresentation = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSelectCardPresentation != null) {
            mSelectCardPresentation.dismiss();
            mSelectCardPresentation = null;
        }
    }

    @Override
    protected void updatePresentation() {
        // Log.d(TAG, "updatePresentation: ");
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
        Log.d(TAG, (presentationDisplay!=null)+"/updatePresentation: "+mSelectCardPresentation);
        // 注释 : Dismiss the current presentation if the display has changed.
        if (mSelectCardPresentation != null && mSelectCardPresentation.getDisplay() != presentationDisplay) {
            mSelectCardPresentation.dismiss();
            mSelectCardPresentation = null;
        }
        if (mSelectCardPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mSelectCardPresentation = new SelectCardPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;

            mSelectCardPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mSelectCardPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mSelectCardPresentation = null;
            }
        }
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//    }


    private class CardAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Log.d(TAG, "...........长度为.." + pictures.length);
            return pictures.length != 0 ? pictures.length : 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View converView, ViewGroup viewGroup) {
            View view;
            ViewHolder holder;
            if (converView != null) {
                view = converView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.card_select_item, null);
                holder = new ViewHolder();
                holder.tv_carddetail = (TextView) view.findViewById(R.id.tv_carddetail);
                holder.iv_cardview = (ImageView) view.findViewById(R.id.iv_cardview);
                view.setTag(holder);
            }
            //绑定数据;
            holder.iv_cardview.setImageResource(pictures[position]);
            holder.tv_carddetail.setText(names[position]);
            return view;
        }
    }
    static class ViewHolder {
        ImageView iv_cardview;
        TextView tv_carddetail;
    }
}
