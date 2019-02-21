package com.jordylangen.woodstorage.view

import com.jordylangen.woodstorage.storage.LogEntry

interface WoodStorageContract {

    interface View : Contract.View {

        fun add(logEntry: LogEntry)

        fun addAt(logEntry: LogEntry, index: Int)

        fun clear()

        fun showTagFilterDialog()
    }

    interface Presenter : Contract.Presenter<View> {

        fun onOptionsItemSelected(itemId: Int)
    }
}
