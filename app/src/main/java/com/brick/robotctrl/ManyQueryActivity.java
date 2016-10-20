package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ant.liao.GifView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kjn.askquestion.Jason;
import com.kjn.askquestion.JsonBean;
import com.zhangyt.log.LogUtil;

import org.apache.commons.httpclient.HttpException;

import java.util.ArrayList;


public class ManyQueryActivity extends BaseActivity {
    String TAG ="ManyQueryActivity";
    private GifView showGf;
    String data;
    ArrayList<String> showItem = new ArrayList<>();
    ArrayList<Integer> showNum = new ArrayList<>();
    public String result;
    public String resultShow;
    String num = "no";
    private ListView queryListView;
    private Button humanButton;
    private Button askButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        Intent intent = getIntent();

        humanButton = (Button) findViewById(R.id.humanButton);
        humanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                humanButton.setClickable(false);
                clearTimerCount();
                ExpressionActivity.startAction(ManyQueryActivity.this, 0);
            }
        });

        askButton = (Button) findViewById(R.id.askButton);
        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askButton.setClickable(false);
                clearTimerCount();
                startActivity(new Intent().setClass(ManyQueryActivity.this, QuestTestActivity.class));
            }
        });


        showGf =(GifView)findViewById(R.id.gif3);
        showGf.setGifImage(R.drawable.think);
        showGf.setGifImageType(GifView.GifImageType.COVER);
      //  showGf.setShowDimension(640,400);

        showItem = intent.getStringArrayListExtra("extra_showItem");
        showNum = intent.getIntegerArrayListExtra("extra_showNum");
        queryListView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, showItem);
        queryListView.setAdapter(myArrayAdapter);

        queryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                queryListView.setEnabled(false);
                clearTimerCount();
                if (showNum.get(arg2).equals(0)) {
                    num = "0";
                    LogUtil.d(TAG, "点击成功");
                } else if (showNum.get(arg2).equals(1)) {
                    num = "1";
                    LogUtil.d(TAG, "点击成功");
                } else if (showNum.get(arg2).equals(2)) {
                    num = "2";
                    LogUtil.d(TAG, "点击成功");
                } else if (showNum.get(arg2).equals(3)) {
                    num = "3";
                    LogUtil.d(TAG, "点击成功");
                } else if (showNum.get(arg2).equals(4)) {
                    num = "4";
                    LogUtil.d(TAG, "点击成功");
                } else if (showNum.get(arg2).equals(5)) {
                    num = "5";
                    LogUtil.d(TAG, "点击成功");
                } else if (showNum.get(arg2).equals(6)) {
                    num = "6";
                    LogUtil.d(TAG, "点击成功");
                } else if (showNum.get(arg2).equals(7)) {
                    num = "7";
                    LogUtil.d(TAG, "点击成功");
                } else if (showNum.get(arg2).equals(8)) {
                    num = "8";
                    LogUtil.d(TAG, "点击成功");
                } else if (showNum.get(arg2).equals(9)) {
                    num = "9";
                    LogUtil.d(TAG, "点击成功");
                }
                queryListView.setEnabled(false);
                if(num.equals("no")) {
                    new Thread() {
                        @Override
                        public void run() {
                            Jason jts = new Jason();
                            LogUtil.i(TAG, "进入新线程edit");
                            try {
                                result = jts.ask(num);                               //把网络访问的代码放在这里
                                if (result != null) {
                                    LogUtil.i(TAG, "进入解析2");
                                    Gson gson = new Gson();
                                    java.lang.reflect.Type type = new TypeToken<JsonBean>() {
                                    }.getType();
                                    JsonBean jsonBean = gson.fromJson(result, type);
                                    System.out.println(jsonBean.getResult());
                                    resultShow = jsonBean.getSingleNode().getAnswerMsg();
                                    showItem.clear();
                                    showNum.clear();
                                    if (jsonBean.getVagueNode() != null) {
                                        for (int i = 0; i < jsonBean.getVagueNode().getItemList().size(); i++) {
                                            showItem.add(jsonBean.getVagueNode().getItemList().get(i).getQuestion());
                                            showNum.add(jsonBean.getVagueNode().getItemList().get(i).getNum());
                                        }
                                        if (resultShow != null) {
                                            Intent intent = new Intent(ManyQueryActivity.this, ManyQueryActivity.class);
                                            intent.putExtra("extra_showResult", resultShow);
                                            intent.putStringArrayListExtra("extra_showItem", showItem);
                                            intent.putIntegerArrayListExtra("extra_showNum", showNum);
                                            startActivity(intent);
                                        }
                                    } else {
                                        resultShow = jsonBean.getSingleNode().getAnswerMsg();
                                        LogUtil.d(TAG, resultShow);
                                        if (resultShow != null) {
                                            if (jsonBean.getAnswerTypeId() == 1) {
                                                Intent intent = new Intent(ManyQueryActivity.this, NoQueryActivity.class);
                                                resultShow = "请输入问题！";
                                                intent.putExtra("extra_showResult", resultShow);
                                                startActivity(intent);
                                            } else if (jsonBean.getAnswerTypeId() == 3) {
                                                Intent intent = new Intent(ManyQueryActivity.this, NoAnswerQueryActivity.class);
                                                resultShow = "抱歉，机器人无法理解您的意思,请转人工服务！";
                                                intent.putExtra("extra_showResult", resultShow);
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(ManyQueryActivity.this, ShowSureQueryActivity.class);
                                                intent.putExtra("extra_showResult", resultShow);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                }
                            } catch (HttpException e) {
                                System.out.println("heheda" + e);
                            }//把网络访问的代码放在这里
                        }
                    }.start();
                }
            }
        });
    }
    @Override
    protected void onRestart()
    {
        queryListView.setEnabled(true);
        humanButton.setClickable(true);
        askButton.setClickable(true);
        super.onRestart();
    }
}
