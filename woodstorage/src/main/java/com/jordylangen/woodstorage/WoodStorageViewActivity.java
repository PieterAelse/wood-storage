package com.jordylangen.woodstorage;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;

import com.jordylangen.woodstorage.view.PresenterCache;
import com.jordylangen.woodstorage.view.WoodStorageContract;

import java.io.File;

public class WoodStorageViewActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 81;
    private static final boolean ASK_PERMISSIONS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private WoodStorageContract.Presenter woodStoragePresenter;
    private MenuItem clickedMenuItem;

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
        if (item.getItemId() == R.id.woodstorage_action_save_on_sd) {
            if (!hasStoragePermission()) {
                clickedMenuItem = item;
                requestWriteStoragePermission();
                return true;
            }
            saveLogToSDCard();
        } else if (item.getItemId() == R.id.woodstorage_action_share) {
            if (!hasStoragePermission()) {
                clickedMenuItem = item;
                requestWriteStoragePermission();
                return true;
            }
            shareLogFile();
        } else {
            woodStoragePresenter.onOptionsItemSelected(item.getItemId());
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (clickedMenuItem != null) {
                onOptionsItemSelected(clickedMenuItem);
                clickedMenuItem = null;
            }
        }
    }

    private void saveLogToSDCard() {
        final File savedFile = copyLogFileToSD();
        if (savedFile == null) {
            showSnackbar(getString(R.string.woodstorage_message_log_saved_to_sd_failed));
        } else {
            showSnackbar(getString(R.string.woodstorage_message_log_saved_to_sd, savedFile.getAbsolutePath()));
        }
    }

    private void shareLogFile() {
        final File savedFile = copyLogFileToSD();
        if (savedFile == null) {
            showSnackbar(getString(R.string.woodstorage_message_log_saved_to_sd_failed));
            return;
        }

        final Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(savedFile));
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.woodstorage_title_share_file)));
        } catch (ActivityNotFoundException e) {
            showSnackbar(getString(R.string.woodstorage_error_no_app_for_file));
        }
    }

    @Nullable
    private File copyLogFileToSD() {
        final File savedFile = WoodStorageFactory.getWorker().getStorage().copyToSDCard();
        if (savedFile != null) {
            MediaScannerConnection.scanFile(this, new String[]{savedFile.getAbsolutePath()}, null, null);
        }
        return savedFile;
    }

    private boolean hasStoragePermission() {
        return !ASK_PERMISSIONS || ContextCompat.checkSelfPermission(this, PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestWriteStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{PERMISSION_STORAGE}, REQUEST_CODE_PERMISSIONS);
    }

    protected void showSnackbar(@NonNull final String text) {
        final ForegroundColorSpan whiteSpan = new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.white));
        final SpannableStringBuilder snackbarText = new SpannableStringBuilder(text);
        snackbarText.setSpan(whiteSpan, 0, snackbarText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), snackbarText, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.woodstorage_color_primary));
        snackbar.show();
    }
}
