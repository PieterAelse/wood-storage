package com.jordylangen.woodstorage.example;

import android.content.Intent;
import android.os.PersistableBundle;
import android.os.Bundle;
import android.view.View;

import com.jordylangen.woodstorage.WoodStorageViewActivity;

import androidx.appcompat.app.AppCompatActivity;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_open_overview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WoodStorageViewActivity.class));
            }
        });

        findViewById(R.id.button_log_exception).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.e(new Throwable("Something horrible happened!"), "oh noes, an exception!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("onResume");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Timber.d("onSaveInstanceState");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Timber.d("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy");
    }
}
