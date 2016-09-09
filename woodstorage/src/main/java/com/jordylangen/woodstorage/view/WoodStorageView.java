package com.jordylangen.woodstorage.view;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.jordylangen.woodstorage.LogEntry;
import com.jordylangen.woodstorage.R;

public class WoodStorageView extends BaseView<WoodStorageContract.View, WoodStorageContract.Presenter> implements WoodStorageContract.View {

    private LogEntryAdapter adapter;

    public WoodStorageView(Context context) {
        this(context, null);
    }

    public WoodStorageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WoodStorageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setup() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.woodstorage_overview_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LogEntryAdapter();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected WoodStorageContract.Presenter newPresenter() {
        return new WoodStoragePresenter();
    }

    @Override
    public void add(LogEntry logEntry) {
        adapter.add(logEntry);
    }

    @Override
    public void addAt(LogEntry logEntry, int index) {
        adapter.add(logEntry, index);
    }

    @Override
    public void clear() {
        adapter.clear();
    }

    @Override
    public void showTagFilterDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(LayoutInflater.from(getContext()).inflate(R.layout.view_tag_filter, null))
                .create();

        dialog.show();
    }

    @Override
    public void showSnackbar(@StringRes final int resId, Object... formatArgs) {
        final ForegroundColorSpan whiteSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), android.R.color.white));
        final SpannableStringBuilder snackbarText = new SpannableStringBuilder(getContext().getString(resId, formatArgs));
        snackbarText.setSpan(whiteSpan, 0, snackbarText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        final Snackbar snackbar = Snackbar.make(this, snackbarText, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.woodstorage_color_primary));
        snackbar.show();
    }
}
