package com.brick.robotctrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kjn.askquestion.Jason;
import com.kjn.askquestion.JsonBean;

import org.apache.commons.httpclient.HttpException;

/**
 * Created by ${kang} on 2016/6/20.
 */

public class ShowQueryActivity extends Activity {
    String TAG ="showQueryActivity";
    String data;
    private Button showqueryButton;
    private Button getValueButton;
    public String result;
    public String resultShow;
    private EditText editText;
    String num;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showquery);
        Intent intent = getIntent();
        data = intent.getStringExtra("extra_showResult");
        Log.d("extra_showResult",data);
        showqueryButton = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        showqueryButton.setText(data);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Toast.makeText(ShowQueryActivity.this, String.valueOf(actionId), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        getValueButton=(Button)findViewById(R.id.button2);
        getValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = editText.getText().toString();
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
                                java.lang.reflect.Type type = new TypeToken<JsonBean>() {
                                }.getType();
                                JsonBean jsonBean = gson.fromJson(result, type);
                                System.out.println(jsonBean.getResult());
                                resultShow = jsonBean.getSingleNode().getAnswerMsg();
                                System.out.println(resultShow);
                                if(resultShow != null) {
                                    Intent intent = new Intent(ShowQueryActivity.this, ShowQueryActivity2.class);
                                    intent.putExtra("extra_showResult2",resultShow);
                                    startActivity(intent);
                                }
                            }

                        } catch (HttpException e) {
                            System.out.println("heheda" + e);
                        }//把网络访问的代码放在这里
                    }
                }.start();
            }
        });

    }
}
