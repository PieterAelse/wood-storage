package com.jordylangen.woodstorage;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jordylangen.woodstorage.view.PresenterCache;
import com.jordylangen.woodstorage.view.WoodStorageContract;

public class WoodStorageViewActivity extends AppCompatActivity {

    private WoodStorageContract.Presenter woodStoragePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.woodstorage_overview_toolbar);
        setSupportActionBar(toolbar);

        woodStoragePresenter = (WoodStorageContract.Presenter) PresenterCache.get(R.id.view_wood_storage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        woodStoragePresenter.onOptionsItemSelected(item.getItemId());
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        woodStoragePresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
