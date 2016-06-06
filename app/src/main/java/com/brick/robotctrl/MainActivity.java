package com.brick.robotctrl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // relative menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // remove text in toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

    // relative menu
    Menu menu = null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: set menu UI");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    boolean menuCtrl = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onCreateOptionsMenu: set menu option and relative function");
        switch (item.getItemId()) {
            // menu context
            case R.id.actionSettings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, 0);
                // do some thing else
                break;
            case R.id.actionSwitchCtrl:
                menuCtrl = !menuCtrl;
                menu.findItem(R.id.actionSwitchCtrl).setIcon( menuCtrl?
                        R.drawable.ic_action_changectrl :
                        R.drawable.ic_action_changectrl_disable);
                // do some thing else
                break;
            default:
                break;
        }
        return true;
    }
}
