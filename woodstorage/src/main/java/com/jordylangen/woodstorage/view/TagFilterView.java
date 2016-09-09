package com.jordylangen.woodstorage.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.jordylangen.woodstorage.R;

import java.util.List;

public class TagFilterView extends BaseView<TagFilterContract.View, TagFilterContract.Presenter> implements TagFilterContract.View, SelectableTagsAdapter.Callback {

    private SelectableTagsAdapter adapter;

    public TagFilterView(Context context) {
        this(context, null);
    }

    public TagFilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void setup() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.dialog_tag_filter_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SelectableTagsAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected TagFilterContract.Presenter newPresenter() {
        return new TagFilterPresenter();
    }

    @Override
    public void add(SelectableTag selectableTag) {
        adapter.add(selectableTag);
    }

    @Override
    public void set(List<SelectableTag> selectedTags) {
        adapter.set(selectedTags);
    }

    @Override
    public void tagSelectedChanged(SelectableTag selectableTag, boolean isChecked) {
        getPresenter().tagSelectedChanged(selectableTag, isChecked);
    }
}
