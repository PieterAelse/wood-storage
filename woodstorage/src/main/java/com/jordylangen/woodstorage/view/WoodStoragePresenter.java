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

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import com.jordylangen.woodstorage.LogEntry;
import com.jordylangen.woodstorage.R;
import com.jordylangen.woodstorage.WoodStorageFactory;

import java.util.ArrayList;
import java.util.List;

import java.io.File;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

class WoodStoragePresenter implements WoodStorageContract.Presenter {

    private static final int REQUEST_CODE_PERMISSIONS = 81;
    private static final boolean ASK_PERMISSIONS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    private static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private WoodStorageContract.View view;
    private Disposable logEntriesSubscription;
    private Disposable selectedTagsSubscription;
    private boolean isSortOrderAscending;
    private List<String> selectedTags;
    private int clickedItemId;

    WoodStoragePresenter() {
        isSortOrderAscending = true;
        selectedTags = new ArrayList<>();
    }

    @Override
    public void setup(final WoodStorageContract.View view) {
        this.view = view;
        subscribe();
    }

    @Override
    public void teardown() {
        dispose(logEntriesSubscription);
        dispose(selectedTagsSubscription);
    }

    @Override
    public void onOptionsItemSelected(int itemId) {
        if (itemId == R.id.woodstorage_action_filter) {
            showTagFilterDialog();
            return;
        }

        dispose(logEntriesSubscription);
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

    private void dispose(Disposable subscription) {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    private void subscribe() {
        Flowable<LogEntry> observable = WoodStorageFactory.getWorker() != null
                ? WoodStorageFactory.getWorker().getStorage().load()
                : Flowable.<LogEntry>empty();

        Flowable<LogEntry> logEntryObservable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                ;

        if (!selectedTags.isEmpty()) {
            logEntryObservable = logEntryObservable.filter(new Predicate<LogEntry>() {
                @Override
                public boolean test(LogEntry logEntry) throws Exception {
                    return selectedTags.contains(logEntry.getTag());
                }
            });
        }

        logEntriesSubscription = logEntryObservable.subscribe(new Consumer<LogEntry>() {
            @Override
            public void accept(LogEntry logEntry) throws Exception {
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

    private void showTagFilterDialog() {
        view.showTagFilterDialog();

        TagFilterContract.Presenter tagFilterPresenter = PresenterCache.get(R.id.dialog_tag_filter);
        if (tagFilterPresenter == null) {
            return;
        }

        selectedTagsSubscription = tagFilterPresenter.observeSelectedTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<SelectableTag>, ObservableSource<List<String>>>() {
                    @Override
                    public ObservableSource<List<String>> apply(List<SelectableTag> selectableTags) throws Exception {
                        return selectAndMapSelectedTags(selectableTags);
                    }
                })
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> tags) throws Exception {
                        dispose(logEntriesSubscription);
                        selectedTags = tags;
                        view.clear();
                        subscribe();
                    }
                });
    }

    private Observable<List<String>> selectAndMapSelectedTags(List<SelectableTag> selectableTags) {
        return Observable.fromIterable(selectableTags)
                .filter(new Predicate<SelectableTag>() {
                    @Override
                    public boolean test(SelectableTag selectableTag) throws Exception {
                        return selectableTag.isSelected();
                    }
                })
                .map(new Function<SelectableTag, String>() {
                    @Override
                    public String apply(SelectableTag selectableTag) throws Exception {
                        return selectableTag.getTag();
                    }
                })
                .toList();
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