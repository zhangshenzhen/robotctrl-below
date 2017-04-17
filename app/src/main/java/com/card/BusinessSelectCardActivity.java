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

import com.brick.robotctrl.R;
import com.presentation.presentionui.SelectCardPresentation;
import com.rg2.activity.BaseActivity;

import org.apache.commons.httpclient.HttpClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shenzhen on 2017/2/10.
 */

public class BusinessSelectCardActivity extends BaseActivity {
    private static final String TAG = "SelectCardActivity";
    @Bind(R.id.gv_card)
    GridView gvCard;
    @Bind(R.id.btn_back)
    Button btnBack;

    private CardAdapter adapter;

    private SelectCardPresentation mSelectCardPresentation;


    String[] details = new String []{"广发DIY卡","广发国行卡","广发南航明珠卡","广发淘宝潮女卡","广发淘宝型男卡"
            ,"广发携程卡","广发新聪明卡","广发易车联名卡","广发真情卡"};
    int [] pictures = new int []{R.drawable.gf_diy,R.drawable.gf_gh,R.drawable.gf_nhmz,R.drawable.gf_tbcn,R.drawable.gf_tbn
            ,R.drawable.gf_xc,R.drawable.gf_xcm,R.drawable.gf_ycl,R.drawable.gf_zq};


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_card);
        ButterKnife.bind(this);
    }

    @Override
    protected void initEvent() {
        gvCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //点击条目
                Intent intent = new Intent(BusinessSelectCardActivity.this, BusinessCarInfo.class);
                intent.putExtra("cardDetails", details[position]);
                startActivity(intent);
                //  Log.d(TAG ,"............."+position);
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
        updatePresentation();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        //得到当前route and its presentation display
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;
         Log.d(TAG, mSelectCardPresentation+"updatePresentation: "+presentationDisplay);
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
                Log.d(TAG, "updatePresentation: "+"again_Back");
                mSelectCardPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mSelectCardPresentation = null;
            }
        }
    }


    @OnClick({R.id.btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    private class CardAdapter extends BaseAdapter {

        @Override
        public int getCount() {
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
            ApplyForSelectCardActivity.ViewHolder holder;
            if (converView != null) {
                view = converView;
                holder = (ApplyForSelectCardActivity.ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.card_select_item, null);
                holder = new ApplyForSelectCardActivity.ViewHolder();
                holder.tv_carddetail = (TextView) view.findViewById(R.id.tv_carddetail);
                holder.iv_cardview = (ImageView) view.findViewById(R.id.iv_cardview);
                view.setTag(holder);
            }
            //绑定数据;
            holder.iv_cardview.setImageResource(pictures[position]);
            holder.tv_carddetail.setText(details[position]);
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_cardview;
        TextView tv_carddetail;
    }
}
