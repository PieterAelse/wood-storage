package com.jordylangen.woodstorage.view

import android.content.Context
import android.util.AttributeSet

import com.jordylangen.woodstorage.R

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TagFilterView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseView<TagFilterContract.View, TagFilterContract.Presenter>(context, attrs, defStyleAttr), TagFilterContract.View,
    SelectableTagsAdapter.Callback {

    private var adapter: SelectableTagsAdapter? = null

    override fun setup() {
        val recyclerView = findViewById<RecyclerView>(R.id.dialog_tag_filter_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = SelectableTagsAdapter(this)
        recyclerView.adapter = adapter
    }

    override fun newPresenter(): TagFilterContract.Presenter {
        return TagFilterPresenter()
    }

    override fun add(selectableTag: SelectableTag) {
        adapter?.add(selectableTag)
    }

    override fun set(selectedTags: List<SelectableTag>) {
        adapter?.set(selectedTags)
    }

    override fun tagSelectedChanged(selectableTag: SelectableTag, isChecked: Boolean) {
        presenter?.tagSelectedChanged(selectableTag, isChecked)
    }
}
