package com.jordylangen.woodstorage.view;

import com.jordylangen.woodstorage.LogEntry;
import com.jordylangen.woodstorage.WoodStorageFactory;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class TagFilterPresenter implements TagFilterContract.Presenter {

    private TagFilterContract.View view;
    private Subscription logEntriesSubscription;
    private List<SelectableTag> selectableTags;
    private PublishSubject<List<SelectableTag>> selectedTagsPublishSubject;

    TagFilterPresenter() {
        selectableTags = new ArrayList<>();
    }

    @Override
    public void setup(TagFilterContract.View view) {
        this.view = view;

        selectedTagsPublishSubject = PublishSubject.create();

        if (selectableTags != null && !selectableTags.isEmpty()) {
            view.set(selectableTags);
        }

        subscribe();
    }

    @Override
    public void teardown() {
        selectedTagsPublishSubject.onCompleted();
        unsubscribe();
    }

    private void unsubscribe() {
        if (logEntriesSubscription != null && !logEntriesSubscription.isUnsubscribed()) {
            logEntriesSubscription.unsubscribe();
        }
    }

    private void subscribe() {
        Observable<LogEntry> observable = WoodStorageFactory.getWorker() != null
                ? WoodStorageFactory.getWorker().getStorage().load()
                : Observable.<LogEntry>empty();

        logEntriesSubscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<LogEntry, Boolean>() {
                    @Override
                    public Boolean call(LogEntry logEntry) {
                        if (logEntry.getTag() == null) {
                            return false;
                        }

                        for (SelectableTag selectableTag : selectableTags) {
                            if (selectableTag.getTag().equals(logEntry.getTag())) {
                                return false;
                            }
                        }

                        return true;
                    }
                })
                .subscribe(new Action1<LogEntry>() {
                    @Override
                    public void call(LogEntry logEntry) {
                        SelectableTag selectableTag = new SelectableTag(logEntry.getTag(), true);
                        selectableTags.add(selectableTag);
                        view.add(selectableTag);
                    }
                });
    }

    @Override
    public void tagSelectedChanged(SelectableTag viewModel, boolean isSelected) {
        for (SelectableTag selectableTag : selectableTags) {
            if (selectableTag.getTag().equals(viewModel.getTag())) {
                selectableTag.setIsSelected(isSelected);
            }
        }

        selectedTagsPublishSubject.onNext(selectableTags);
    }

    @Override
    public Observable<List<SelectableTag>> observeSelectedTags() {
        return selectedTagsPublishSubject.asObservable();
    }
}
