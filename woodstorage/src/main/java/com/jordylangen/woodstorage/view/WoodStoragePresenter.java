package com.jordylangen.woodstorage.view;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.jordylangen.woodstorage.LogEntry;
import com.jordylangen.woodstorage.R;
import com.jordylangen.woodstorage.WoodStorageFactory;

import java.io.File;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

class WoodStoragePresenter implements WoodStorageContract.Presenter {

    private static final int REQUEST_CODE_PERMISSIONS = 81;
    private static final boolean ASK_PERMISSIONS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private WoodStorageContract.View view;
    private Subscription subscription;
    private boolean isSortOrderAscending;
    private int clickedItemId;

    WoodStoragePresenter() {
        isSortOrderAscending = true;
    }

    @Override
    public void setup(final WoodStorageContract.View view) {
        this.view = view;
        subscribe();
    }

    @Override
    public void teardown() {
        subscription.unsubscribe();
    }

    @Override
    public void onOptionsItemSelected(int itemId) {
        unsubscribe();
        if (itemId == R.id.woodstorage_action_sort) {
            invertSortOrder();
        } else if (itemId == R.id.woodstorage_action_save_on_sd) {
            if (!hasStoragePermission()) {
                clickedItemId = itemId;
                requestWriteStoragePermission();
                return;
            }
            saveLogFileToSDCard();
        } else if (itemId == R.id.woodstorage_action_share) {
            if (!hasStoragePermission()) {
                clickedItemId = itemId;
                requestWriteStoragePermission();
                return;
            }
            shareLogFile();
        } else if (itemId == R.id.woodstorage_action_clear && WoodStorageFactory.getWorker() != null) {
            WoodStorageFactory.getWorker().getStorage().clear();
        }
        view.clear();
        subscribe();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (clickedItemId != 0) {
                onOptionsItemSelected(clickedItemId);
                clickedItemId = 0;
            }
        }
    }

    private void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void subscribe() {
        Observable<LogEntry> observable = WoodStorageFactory.getWorker() != null
                ? WoodStorageFactory.getWorker().getStorage().load()
                : Observable.<LogEntry>empty();

        subscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LogEntry>() {
                    @Override
                    public void call(LogEntry logEntry) {
                        if (isSortOrderAscending) {
                            view.add(logEntry);
                        } else {
                            view.addAt(logEntry, 0);
                        }
                    }
                });
    }

    private void invertSortOrder() {
        isSortOrderAscending = !isSortOrderAscending;
    }

    protected boolean hasStoragePermission() {
        return !ASK_PERMISSIONS || ContextCompat.checkSelfPermission(view.getContext(), PERMISSION_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestWriteStoragePermission() {
        final Context context = view.getContext();
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{PERMISSION_STORAGE}, REQUEST_CODE_PERMISSIONS);
        } else {
            throw new RuntimeException("The context of the view should always be an Activity");
        }
    }

    private void saveLogFileToSDCard() {
        final File savedFile = copyLogFileToSD();
        if (savedFile == null) {
            showSaveToSDError();
        } else {
            view.showSnackbar(R.string.woodstorage_message_log_saved_to_sd, savedFile.getAbsolutePath());
        }
    }

    private void shareLogFile() {
        final File savedFile = copyLogFileToSD();
        if (savedFile == null) {
            showSaveToSDError();
            return;
        }

        final Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(savedFile));
        try {
            final Context context = view.getContext();
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.woodstorage_title_share_file)));
        } catch (ActivityNotFoundException e) {
            view.showSnackbar(R.string.woodstorage_error_no_app_for_file);
        }
    }

    @Nullable
    private File copyLogFileToSD() {
        final File savedFile = WoodStorageFactory.getWorker().getStorage().copyToSDCard();
        if (savedFile != null) {
            MediaScannerConnection.scanFile(view.getContext(), new String[]{savedFile.getAbsolutePath()}, null, null);
        }
        return savedFile;
    }

    private void showSaveToSDError() {
        view.showSnackbar(R.string.woodstorage_message_log_saved_to_sd_failed);
    }
}