package com.card;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shenzhen on 2017/2/10.
 */

public class SelectCardActivity extends BaseActivity {
    private static final String TAG = "SelectCardActivity";
    @Bind(R.id.gv_card)
     GridView gridcard;
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_next)
    Button btnNext;
    private CardAdapter adapter;
   String []  cards = new String[]{"白金卡","会员卡","Vip金卡","超级卡","普通卡","黑卡"};


    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_card);
        ButterKnife.bind(this);
    }

    @Override
    protected void initEvent() {
        gridcard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               //点击条目,打开新的内容
             startActivity(new Intent(SelectCardActivity.this,BusinessCarInfo.class));
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
    protected void updatePresentation() {

    }

    @OnClick({R.id.btn_back, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                break;
            case R.id.btn_next:
                Intent intent = new Intent();
                intent.putExtra("..",  2);//模拟传递数据；
                break;
        }
    }



    private class CardAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            Log.d(TAG ,"...........长度为.."+cards.length);
            return cards.length !=0 ? cards.length:0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            Log.d(TAG ,"............."+position);
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
