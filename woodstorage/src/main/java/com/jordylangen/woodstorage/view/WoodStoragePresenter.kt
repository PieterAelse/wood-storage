package com.jordylangen.woodstorage.view

import com.jordylangen.woodstorage.R
import com.jordylangen.woodstorage.WoodStorageFactory
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

internal class WoodStoragePresenter : WoodStorageContract.Presenter {

    private var view: WoodStorageContract.View? = null
    private var logEntriesSubscription: Disposable? = null
    private var selectedTagsSubscription: Disposable? = null
    private var isSortOrderAscending: Boolean = false
    private var selectedTags: List<String>? = null

    init {
        isSortOrderAscending = true
        selectedTags = ArrayList()
    }

    override fun setup(view: WoodStorageContract.View) {
        this.view = view
        subscribe()
    }

    override fun teardown() {
        dispose(logEntriesSubscription)
        dispose(selectedTagsSubscription)
    }

    override fun onOptionsItemSelected(itemId: Int) {
        if (itemId == R.id.woodstorage_action_filter) {
            showTagFilterDialog()
            return
        }

        dispose(logEntriesSubscription)
        if (itemId == R.id.woodstorage_action_sort) {
            invertSortOrder()
        } else if (itemId == R.id.woodstorage_action_clear && WoodStorageFactory.worker != null) {
            WoodStorageFactory.worker?.storage?.clear()
        }

        view!!.clear()
        subscribe()
    }

    private fun dispose(subscription: Disposable?) {
        if (subscription != null && !subscription.isDisposed) {
            subscription.dispose()
        }
    }

    private fun subscribe() {
        val observable = WoodStorageFactory.worker?.storage?.load() ?: Flowable.empty()

        var logEntryObservable = observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        if (!selectedTags!!.isEmpty()) {
            logEntryObservable = logEntryObservable.filter { logEntry -> selectedTags!!.contains(logEntry.tag) }
        }

        logEntriesSubscription = logEntryObservable.subscribe { logEntry ->
            if (isSortOrderAscending) {
                view!!.add(logEntry)
            } else {
                view!!.addAt(logEntry, 0)
            }
        }
    }

    private fun invertSortOrder() {
        isSortOrderAscending = !isSortOrderAscending
    }

    private fun showTagFilterDialog() {
        view!!.showTagFilterDialog()

        val tagFilterPresenter = PresenterCache.get<TagFilterContract.Presenter>(R.id.dialog_tag_filter) ?: return

        selectedTagsSubscription = tagFilterPresenter.observeSelectedTags()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { selectableTags -> selectAndMapSelectedTags(selectableTags) }
            .subscribe { tags ->
                dispose(logEntriesSubscription)
                selectedTags = tags
                view!!.clear()
                subscribe()
            }
    }

    private fun selectAndMapSelectedTags(selectableTags: List<SelectableTag>): Observable<List<String>> {
        return Observable.fromIterable(selectableTags)
            .filter { (_, isSelected) -> isSelected }
            .map { (tag) -> tag }
            .toList()
            .toObservable()
    }
}
