package com.jordylangen.woodstorage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

        woodStoragePresenter = PresenterCache.get(R.id.view_wood_storage);
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
}
