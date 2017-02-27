package com.financial;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.card.ApplyForSelectCardActivity;
import com.rg2.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FinanceIntroduceList extends BaseActivity {


    @Bind(R.id.tv_back_select)
    TextView tvBackSelect;
    @Bind(R.id.activity_finance_introduce_list)
    LinearLayout activityFinanceIntroduceList;
    String[] names = {"广发银行薪满益足日薪月益(个人版)","广发银行薪满益足 170213版私人版","广发安福薪满益足170223版私行专属",
              "广发银行薪满益足日薪月益(个人版)","广发银行薪满益足 170213版私人版","广发安福薪满益足170223版私行专属" };
     String[] times ={"长期","97天","244天","110天","69天","73天"};
     String[] moneys = {"50000.00","300000.00","300000.00","300000.00","360000.00","800000.00"};
     String[] rates = {"以官网公布为准","4.6%","4.6%","4.55%","4.6%","4.5%"};
    private ListView list;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_finance_list);
        list = (ListView) findViewById(R.id.finance_list);
        ButterKnife.bind(this);

    }
    @Override
    protected void initEvent() {
        tvBackSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
       list.setAdapter(new FinanceAdapter());
    }

    @Override
    protected void updatePresentation() {

    }

   @Override
    protected void initViewData() {

    }

    private class FinanceAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return names.length==0 ? 0:names.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View converView, ViewGroup viewGroup) {
            View view;
            ViewHolder holder;
            if (converView != null) {
                view = converView;
                holder = (ViewHolder) view.getTag();
            }else{
                 view = View.inflate(mContext, R.layout.finance_item,null);
                  holder = new ViewHolder();
                holder.tvdanger = (TextView) view.findViewById(R.id.tv_danger);
                holder.tvmoney = (TextView) view.findViewById(R.id.tv_money);
                holder.tvtime = (TextView) view.findViewById(R.id.tv_time);
                holder.tvrate = (TextView) view.findViewById(R.id.tv_rate);
                holder.tvkind = (TextView) view.findViewById(R.id.tv_kind);
                holder.tvname = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(holder);
            }
            //绑定数据;
               holder.tvdanger.setText("PR2稳健型");
               holder.tvkind.setText("人民币");
               holder.tvtime.setText(times[position]);
               holder.tvname.setText(names[position]);
               holder.tvrate.setText(rates[position]);
            return view;
        }
    }


    static class ViewHolder {
        TextView   tvdanger;
        TextView   tvrate;
        TextView   tvmoney;
        TextView   tvtime;
        TextView   tvkind;
        TextView   tvname;
    }
}
