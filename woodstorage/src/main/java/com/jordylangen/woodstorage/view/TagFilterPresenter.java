package com.jordylangen.woodstorage.view;

import com.jordylangen.woodstorage.LogEntry;
import com.jordylangen.woodstorage.WoodStorageFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class TagFilterPresenter implements TagFilterContract.Presenter {

    private TagFilterContract.View view;
    private Disposable logEntriesSubscription;
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
        selectedTagsPublishSubject.onComplete();
        dispose();
    }

    private void dispose() {
        if (logEntriesSubscription != null && !logEntriesSubscription.isDisposed()) {
            logEntriesSubscription.dispose();
        }
    }

    private void subscribe() {
        Flowable<LogEntry> observable = WoodStorageFactory.getWorker() != null
                ? WoodStorageFactory.getWorker().getStorage().load()
                : Flowable.<LogEntry>empty();

        logEntriesSubscription = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<LogEntry>() {
                    @Override
                    public boolean test(LogEntry logEntry) throws Exception {
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
                .subscribe(new Consumer<LogEntry>() {
                    @Override
                    public void accept(LogEntry logEntry) throws Exception {
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
        return selectedTagsPublishSubject;
    }
}
