package com.jordylangen.woodstorage.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.jordylangen.woodstorage.LogEntry;

public interface WoodStorageContract {

    interface View extends Contract.View {

        void add(LogEntry logEntry);

        void addAt(LogEntry logEntry, int index);

        void clear();

        void showSnackbar(@StringRes int resId, Object... formatArgs);
    }

    interface Presenter extends Contract.Presenter<View> {

        void onOptionsItemSelected(int itemId);

        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }
}
