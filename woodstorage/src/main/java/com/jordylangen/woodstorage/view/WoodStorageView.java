package com.jordylangen.woodstorage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.jordylangen.woodstorage.LogEntry;
import com.jordylangen.woodstorage.R;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        RecyclerView recyclerView = findViewById(R.id.woodstorage_overview_recyclerview);
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
}
