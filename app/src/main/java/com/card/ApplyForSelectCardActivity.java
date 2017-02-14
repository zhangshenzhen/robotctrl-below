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
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.presentation.presentionui.ApplyforPresentation;
import com.presentation.presentionui.SelectCardPresentation;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shenzhen on 2017/2/10.
 */

public class ApplyForSelectCardActivity extends BaseActivity {
    private static final String TAG = "SelectCardActivity";
    @Bind(R.id.gv_card)
     GridView gridcard;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_next)
    Button btnNext;
    private CardAdapter adapter;

    private SelectCardPresentation mSelectCardPresentation;

   String []  cards = new String[]{"白金卡","会员卡","Vip金卡","超级卡","普通卡","黑卡"
           ,"会员卡","Vip金卡","超级卡","普通卡","黑卡","会员卡"};


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_card);
        ButterKnife.bind(this);
    }

    @Override
    protected void initEvent() {
        gridcard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
               //点击条目,打开新的内容
            startActivity(new Intent(ApplyForSelectCardActivity.this,CreadCarInfo.class));
              Log.d(TAG ,"............."+position);
            }
        });


    }
    @Override
    protected void initData() {
        adapter = new CardAdapter();
        gridcard.setAdapter(adapter);

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
        if (mSelectCardPresentation != null){
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
        // 注释 : Dismiss the current presentation if the display has changed.
        if (mSelectCardPresentation != null && mSelectCardPresentation.getDisplay() != presentationDisplay) {
            mSelectCardPresentation.dismiss();
            mSelectCardPresentation = null;
        }
        if (mSelectCardPresentation == null && presentationDisplay != null) {
            // Initialise a new Presentation for the Display
            mSelectCardPresentation = new SelectCardPresentation(this, presentationDisplay);
            //把当前的对象引用赋值给BaseActivity中的引用;
            mPresentation = mSelectCardPresentation;
            mSelectCardPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mSelectCardPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                mSelectCardPresentation = null;
            }
        }
    }

    @OnClick({R.id.btn_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next:
           /*     Intent intent = new Intent();
                intent.putExtra("..",  2);//模拟传递数据；*/
                break;
        }
    }



    private class CardAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            Log.d(TAG,"...........长度为.."+cards.length);
            return cards.length !=0 ? cards.length:0;
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
             View view ; ViewHolder holder;
            if (converView != null){
                view = converView;
               holder = (ViewHolder) view.getTag();
            }else{
                view = View.inflate(getApplicationContext(),R.layout.card_select_item,null);
                holder = new ViewHolder();
                holder.tv_cardNum = (TextView) view.findViewById(R.id.tv_cardNum);
                view.setTag(holder);
            }
            //绑定数据;
            holder.tv_cardNum.setText(cards[position]);
            return view;
        }
    }
       static class ViewHolder{
           TextView tv_cardNum;
      }
}
