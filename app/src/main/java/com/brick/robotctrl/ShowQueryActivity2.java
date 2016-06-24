package com.brick.robotctrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kjn.askquestion.Jason;
import com.kjn.askquestion.JsonBean;

import org.apache.commons.httpclient.HttpException;

import java.util.ArrayList;

/**
 * Created by ${kang} on 2016/6/20.
 */

public class ShowQueryActivity2 extends BaseActivity  {
    String TAG ="showQueryActivity2";
    String data;
    private TextView showqueryText;
    public String result;
    public String resultShow;
    String num;
    ArrayList<String> showItem = new ArrayList<String>();
    ArrayList<Integer> showNum = new ArrayList<Integer>();
    private ListView queryListView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showquery);
        Intent intent = getIntent();
        data = intent.getStringExtra("extra_showResult");
//      showqueryText = (TextView) findViewById(R.id.textView);
//      showqueryText.setText(data);
//      Log.d("extra_showResult",data);

        showItem = intent.getStringArrayListExtra("extra_showItem");
        showNum = intent.getIntegerArrayListExtra("extra_showNum");
        queryListView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,showItem);
        queryListView.setAdapter(myArrayAdapter);
//        for (int i = 0;i < showItem.size(); i++){
//            Log.d(TAG,showItem.get(i));
//        }
//        for (int i = 0;i < showNum.size(); i++){
//            Log.d(TAG,showNum.get(i).toString());
//        }
        queryListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(showNum.get(arg2).equals(0)) {
                    num = "0";
                    Log.d(TAG, "点击成功");
                }
                if(showNum.get(arg2).equals(1)) {
                    num = "1";
                    Log.d(TAG, "点击成功");
                }
                if(showNum.get(arg2).equals(2)) {
                    num = "2";
                    Log.d(TAG, "点击成功");
                }
                if(showNum.get(arg2).equals(3)) {
                    num = "3";
                    Log.d(TAG, "点击成功");
                }
                if(showNum.get(arg2).equals(4)) {
                    num = "4";
                    Log.d(TAG, "点击成功");
                }
                if(showNum.get(arg2).equals(5)) {
                    num = "5";
                    Log.d(TAG, "点击成功");
                }
                if(showNum.get(arg2).equals(6)) {
                    num = "6";
                    Log.d(TAG, "点击成功");
                }
                if(showNum.get(arg2).equals(7)) {
                    num = "7";
                    Log.d(TAG, "点击成功");
                }
                if(showNum.get(arg2).equals(8)) {
                    num = "8";
                    Log.d(TAG, "点击成功");
                }
                if(showNum.get(arg2).equals(9)) {
                    num = "9";
                    Log.d(TAG, "点击成功");
                }
                new Thread(){
                    @Override
                    public void run() {
                        Jason jts = new Jason();
                        Log.i(TAG,"进入新线程edit");
                        try {
                            result = jts.ask(num);                               //把网络访问的代码放在这里
                            if (result != null) {
                                Log.i(TAG,"进入解析2");
                                Gson gson = new Gson();
                                java.lang.reflect.Type type = new TypeToken<JsonBean>() {}.getType();
                                JsonBean jsonBean = gson.fromJson(result, type);
                                System.out.println(jsonBean.getResult());
                                resultShow = jsonBean.getSingleNode().getAnswerMsg();
                                if (jsonBean.getVagueNode() != null) {
                                    for (int i = 0;i < jsonBean.getVagueNode().getItemList().size(); i++){
                                        resultShow += jsonBean.getVagueNode().getItemList().get(i).getNum() + jsonBean.getVagueNode().getItemList().get(i).getQuestion();
                                    }
                                    if(resultShow != null) {
                                        Intent intent = new Intent(ShowQueryActivity2.this, ShowQueryActivity.class);
                                        intent.putExtra("extra_showResult",resultShow);
                                        startActivity(intent);
                                    }
                                }else{
                                    resultShow = jsonBean.getSingleNode().getAnswerMsg();
                                    if(resultShow != null) {
                                        Intent intent = new Intent(ShowQueryActivity2.this, ShowSureQueryActivity.class);
                                        intent.putExtra("extra_showResult",resultShow);
                                        startActivity(intent);
                                    }
                                }
                            }
                        } catch (HttpException e) {
                            System.out.println("heheda" + e);
                        }//把网络访问的代码放在这里
                    }
                }.start();
            }
        });

//        getValueButton=(Button)findViewById(R.id.button2);
//        getValueButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                num = editText.getText().toString();
//                new Thread(){
//                    @Override
//                    public void run() {
//                        Jason jts = new Jason();
//                        Log.i(TAG,"进入新线程edit");
//                        try {
//                            result = jts.ask(num);                               //把网络访问的代码放在这里
//                            if (result != null) {
//                                Log.i(TAG,"进入解析2");
//                                Gson gson = new Gson();
//                                java.lang.reflect.Type type = new TypeToken<JsonBean>() {
//                                }.getType();
//                                JsonBean jsonBean = gson.fromJson(result, type);
//                                System.out.println(jsonBean.getResult());
//                                resultShow = jsonBean.getSingleNode().getAnswerMsg();
//                                if (jsonBean.getVagueNode() != null) {
//                                    for (int i = 0;i < jsonBean.getVagueNode().getItemList().size(); i++){
//                                        resultShow += jsonBean.getVagueNode().getItemList().get(i).getNum() + jsonBean.getVagueNode().getItemList().get(i).getQuestion();
//                                    }
//                                    if(resultShow != null) {
//                                        Intent intent = new Intent(ShowQueryActivity2.this, ShowQueryActivity.class);
//                                        intent.putExtra("extra_showResult",resultShow);
//                                        startActivity(intent);
//                                    }
//
//                                }else{
//                                    resultShow = jsonBean.getSingleNode().getAnswerMsg();
//                                    if(resultShow != null) {
//                                        Intent intent = new Intent(ShowQueryActivity2.this, ShowSureQueryActivity.class);
//                                        intent.putExtra("extra_showResult",resultShow);
//                                        startActivity(intent);
//                                    }
//                                }
//                            }
//                        } catch (HttpException e) {
//                            System.out.println("heheda" + e);
//                        }
//                    }
//                }.start();
//            }
//        });

    }
}
