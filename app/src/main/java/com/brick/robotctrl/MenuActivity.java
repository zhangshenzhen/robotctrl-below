package com.brick.robotctrl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    private final String TAG = "MenuActivity";

    Button IDButton = null;
    Button ADButton = null;
    Button testButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        IDButton = (Button) findViewById(R.id.IDButton);
        IDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent().setClass(MenuActivity.this, ...));
                Toast.makeText(MenuActivity.this, "No ID Detector", Toast.LENGTH_SHORT).show();
            }
        });

        ADButton = (Button) findViewById(R.id.ADButton);
        ADButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent().setClass(MenuActivity.this, ADActivity.class));
            }
        });

        testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    startActivity(new Intent().setClass(MenuActivity.this, ...));
                }
            }
        );
    }
}
