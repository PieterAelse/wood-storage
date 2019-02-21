package com.jordylangen.woodstorage.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater

import com.jordylangen.woodstorage.LogEntry
import com.jordylangen.woodstorage.R

import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WoodStorageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseView<WoodStorageContract.View, WoodStorageContract.Presenter>(context, attrs, defStyleAttr),
    WoodStorageContract.View {

    private var adapter: LogEntryAdapter? = null

    override fun setup() {
        val recyclerView = findViewById<RecyclerView>(R.id.woodstorage_overview_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = LogEntryAdapter()
        recyclerView.adapter = adapter
    }

    override fun newPresenter(): WoodStorageContract.Presenter {
        return WoodStoragePresenter()
    }

    override fun add(logEntry: LogEntry) {
        adapter?.add(logEntry)
    }

    override fun addAt(logEntry: LogEntry, index: Int) {
        adapter?.add(logEntry, index)
    }

    override fun clear() {
        adapter?.clear()
    }

    override fun showTagFilterDialog() {
        val dialog = AlertDialog.Builder(context)
            .setView(LayoutInflater.from(context).inflate(R.layout.view_tag_filter, null))
            .create()

        dialog.show()
    }
}
