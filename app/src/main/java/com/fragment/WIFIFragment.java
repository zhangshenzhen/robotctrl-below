package com.fragment;

import android.database.DataSetObserver;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brick.robotctrl.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lx on 2017/4/6.
 */

public class WIFIFragment extends BaseFragment {

    private ListView wifi;
    private ArrayList<ScanResult> list;                   //存放周围wifi热点对象的列表
    private WifiManager wifiManager;
    private DhcpInfo wifiInfo;
    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.wifi_fragment, null);
    }

    @Override
    public void initData() {
        wifi = (ListView) getActivity().findViewById(R.id.lv_wifi);
        String wifiService = getActivity().WIFI_SERVICE;
        wifiManager = (WifiManager) getActivity().getSystemService(wifiService);
        wifiInfo = wifiManager.getDhcpInfo();
        openWifi();
        list = (ArrayList<ScanResult>) wifiManager.getScanResults();
        if(list ==null){
          Toast.makeText(getActivity(), "wifi未打开！", Toast.LENGTH_LONG).show();
        }else {
            wifi.setAdapter(new MyAdapter(this,list));
        }
    }

    private void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    private class MyAdapter extends BaseAdapter {
        List<ScanResult> list;
        LayoutInflater inflater;
        private TextView wifiname;
        private TextView wifilevel;


        public MyAdapter(WIFIFragment wifiFragment, ArrayList<ScanResult> list) {
            this.inflater = LayoutInflater.from(getActivity());
            this.list = list;
        }

        @Override
        public int getCount() {
            return list==null? 0:list.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;//每次都是最新数据，
            if(convertView ==null) {
                view = inflater.inflate(R.layout.wifiitem, null);
                wifiname = (TextView) view.findViewById(R.id.textView);
                wifilevel = (TextView) view.findViewById(R.id.textView2);
            }else{
                view= convertView;
            }
            //绑定数据;list.get(position) = ScanResult;
            wifiname.setText(list.get(position).SSID);
             /*RSSI = level - NoiceFloor
                NoiceFloor一般取-96dBm
                这样如果 level 是 -60dBm, RSSI 就是 36
             * */
            wifilevel.setText("-"+String.valueOf(Math.abs((list.get(position).level))));
            return view;
        }
    }
}
