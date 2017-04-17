package com.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.brick.robotctrl.R;
import com.rg2.activity.BaseActivity;

import static android.R.attr.fragment;

/**
 * Created by lx on 2017/4/6.
 */

public class FunctionActivity extends BaseActivity {
    FragmentManager fm;
    android.app.FragmentTransaction ft;
    /**
     * 需要测试的功能*/
       String [] funtitems = new String[]{"界面白点","wifi","USB接口","摄像头","扬声器麦克风",
       "SIM卡状态"};
        Fragment[] fragments = { new ScreenFragment(),new WIFIFragment(), new USBFragment(), new CameraFragment()
                ,new SoundFragment(),new SIMStateFragment() };
       private ListView lv_funct;

    @Override
    protected void initViews(Bundle savedInstanceState) {
       setContentView(R.layout.function_activity);
        lv_funct = (ListView) findViewById(R.id.lv_funct);
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        lv_funct.setAdapter(new FunctionAdapter());
        initEventClazz();
    }
    @Override
    protected void updatePresentation() {
    }

    protected void initEventClazz() {
        lv_funct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id ) {
                fm = getFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.fg_contain, fragments[position]);
                ft.commit();
            }
        });
    }
    @Override
    protected void initViewData() {
    }

    private class FunctionAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return funtitems.length;
        }

        @Override
        public Object getItem(int position) {
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
             View  view;  ViewHolder holder  ;
            if(convertView !=null){
               view =convertView;
                holder = (ViewHolder) view.getTag();
            }else {
                holder = new ViewHolder();
                view = View.inflate(mContext, R.layout.function_item,null);
                holder.tv_functname = (TextView) view.findViewById(R.id.tv_functname);
                view.setTag(holder);
            }            //绑定数据
            holder.tv_functname.setText(funtitems[position]);
            return view;
        }

          class ViewHolder {
            TextView tv_functname;
        }

    }


}
