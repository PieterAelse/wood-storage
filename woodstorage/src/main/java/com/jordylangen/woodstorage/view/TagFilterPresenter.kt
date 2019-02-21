package com.jordylangen.woodstorage.view

import com.jordylangen.woodstorage.WoodStorageFactory
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*

class TagFilterPresenter internal constructor() : TagFilterContract.Presenter {

    private var view: TagFilterContract.View? = null
    private var logEntriesSubscription: Disposable? = null
    private val selectableTags: MutableList<SelectableTag>
    private var selectedTagsPublishSubject: PublishSubject<List<SelectableTag>> = PublishSubject.create()

    init {
        selectableTags = ArrayList()
    }

    override fun setup(view: TagFilterContract.View) {
        this.view = view

        if (!selectableTags.isEmpty()) {
            view.set(selectableTags)
        }

        subscribe()
    }

    override fun teardown() {
        selectedTagsPublishSubject.onComplete()
        dispose()
    }

    private fun dispose() {
        if (logEntriesSubscription?.isDisposed != true) {
            logEntriesSubscription?.dispose()
        }
    }

    private fun subscribe() {
        val observable = WoodStorageFactory.worker?.storage?.load() ?: Flowable.empty()

        logEntriesSubscription = observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter(Predicate { logEntry ->
                if (logEntry.tag == null) {
                    return@Predicate false
                }

                for ((tag) in selectableTags) {
                    if (tag == logEntry.tag) {
                        return@Predicate false
                    }
                }

                true
            })
            .subscribe { logEntry ->
                val selectableTag = SelectableTag(logEntry.tag!!, true)
                selectableTags.add(selectableTag)
                view?.add(selectableTag)
            }
    }

    override fun tagSelectedChanged(viewModel: SelectableTag, isChecked: Boolean) {
        for (selectableTag in selectableTags) {
            if (selectableTag.tag == viewModel.tag) {
                selectableTag.isSelected = isChecked
            }
        }

        selectedTagsPublishSubject.onNext(selectableTags)
    }

    override fun observeSelectedTags(): Observable<List<SelectableTag>> = selectedTagsPublishSubject
}
