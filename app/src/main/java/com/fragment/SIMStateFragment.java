package com.fragment;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.brick.robotctrl.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lx on 2017/4/14.
 */

public class SIMStateFragment extends  BaseFragment {
    private TelephonyManager tManager;
    ListView ShowView ;
    // 声明代表状态名的数组
    String[] statusNames;
    // 声明代表手机状态的集合
    ArrayList<String> statusValues = new ArrayList<String>();
    @Override
    public View initView() {
        return View.inflate(getActivity(), R.layout.sim_state,null);
    }

    @Override
    public void initData() {
        //获取系统的Telephonery管理器；
        tManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        //获取各种状态的名称数组
        //获取各种状态的名称数组
        statusNames = getResources().getStringArray(R.array.statusNames);
        // 获取代表SIM卡状态的数组
        String[] simState = getResources().getStringArray(R.array.simState);
        // 获取代表电话网络类型的数组
        String[] phoneType = getResources().getStringArray(R.array.phoneType);
        // 获取设备编号
        statusValues.add(tManager.getDeviceId());
        // 获取系统平台的版本
        statusValues.add(tManager.getDeviceSoftwareVersion() != null ? tManager
                .getDeviceSoftwareVersion() : "未知");
        // 获取网络运营商代号
        statusValues.add(tManager.getNetworkOperator());
        // 获取网络运营商名称
        statusValues.add(tManager.getNetworkOperatorName());
        // 获取手机网络类型
        statusValues.add(phoneType[tManager.getPhoneType()]);
        // 获取设备所在位置
        statusValues.add(tManager.getCellLocation() != null ? tManager
                .getCellLocation().toString() : "未知位置");
        // 获取SIM卡的国别
        statusValues.add(tManager.getSimCountryIso());
        // 获取SIM卡的序列号
        statusValues.add(tManager.getSimSerialNumber());
        // 获取SIM卡的状态
        statusValues.add(simState[tManager.getSimState()]);
        // 获取ListView对象
        ShowView = (ListView)getActivity().findViewById(R.id.show);
        ArrayList<Map<String, String>> status = new ArrayList<Map<String, String>>();
        // 遍历statusValues集合,将statusNames,statusValues
        // 的数据封装到List<Map<String,String>>集合中
        for (int i = 0; i < statusValues.size(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", statusNames[i]);
            map.put("value", statusValues.get(i));
            status.add(map);
        }
        // 使用SimpleAdapter封装List数据
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), status, R.layout.line,
                new String[] { "name", "value" }, new int[] { R.id.name,
                R.id.value });
        ShowView.setAdapter(adapter);
    }
}
