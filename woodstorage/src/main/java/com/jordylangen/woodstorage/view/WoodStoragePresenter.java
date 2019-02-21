package com.jordylangen.woodstorage.view;

import com.jordylangen.woodstorage.LogEntry;
import com.jordylangen.woodstorage.R;
import com.jordylangen.woodstorage.WoodStorageFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

class WoodStoragePresenter implements WoodStorageContract.Presenter {

    private WoodStorageContract.View view;
    private Disposable logEntriesSubscription;
    private Disposable selectedTagsSubscription;
    private boolean isSortOrderAscending;
    private List<String> selectedTags;

    WoodStoragePresenter() {
        isSortOrderAscending = true;
        selectedTags = new ArrayList<>();
    }

    @Override
    public void setup(final WoodStorageContract.View view) {
        this.view = view;
        subscribe();
    }

    @Override
    public void teardown() {
        dispose(logEntriesSubscription);
        dispose(selectedTagsSubscription);
    }

    @Override
    public void onOptionsItemSelected(int itemId) {
        if (itemId == R.id.woodstorage_action_filter) {
            showTagFilterDialog();
            return;
        }

        dispose(logEntriesSubscription);
        if (itemId == R.id.woodstorage_action_sort) {
            invertSortOrder();
        } else if (itemId == R.id.woodstorage_action_clear && WoodStorageFactory.getWorker() != null) {
            WoodStorageFactory.getWorker().getStorage().clear();
        }

        view.clear();
        subscribe();
    }

    private void dispose(Disposable subscription) {
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

    private void subscribe() {
        Flowable<LogEntry> observable = WoodStorageFactory.getWorker() != null
                ? WoodStorageFactory.getWorker().getStorage().load()
                : Flowable.<LogEntry>empty();

        Flowable<LogEntry> logEntryObservable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                ;

        if (!selectedTags.isEmpty()) {
            logEntryObservable = logEntryObservable.filter(new Predicate<LogEntry>() {
                @Override
                public boolean test(LogEntry logEntry) throws Exception {
                    return selectedTags.contains(logEntry.getTag());
                }
            });
        }

        logEntriesSubscription = logEntryObservable.subscribe(new Consumer<LogEntry>() {
            @Override
            public void accept(LogEntry logEntry) throws Exception {
                if (isSortOrderAscending) {
                    view.add(logEntry);
                } else {
                    view.addAt(logEntry, 0);
                }
            }
        });
    }

    private void invertSortOrder() {
        isSortOrderAscending = !isSortOrderAscending;
    }

    private void showTagFilterDialog() {
        view.showTagFilterDialog();

        TagFilterContract.Presenter tagFilterPresenter = PresenterCache.get(R.id.dialog_tag_filter);
        if (tagFilterPresenter == null) {
            return;
        }

        selectedTagsSubscription = tagFilterPresenter.observeSelectedTags()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<SelectableTag>, ObservableSource<List<String>>>() {
                    @Override
                    public ObservableSource<List<String>> apply(List<SelectableTag> selectableTags) throws Exception {
                        return selectAndMapSelectedTags(selectableTags);
                    }
                })
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> tags) throws Exception {
                        dispose(logEntriesSubscription);
                        selectedTags = tags;
                        view.clear();
                        subscribe();
                    }
                });
    }

    private Observable<List<String>> selectAndMapSelectedTags(List<SelectableTag> selectableTags) {
        return Observable.fromIterable(selectableTags)
                .filter(new Predicate<SelectableTag>() {
                    @Override
                    public boolean test(SelectableTag selectableTag) throws Exception {
                        return selectableTag.isSelected();
                    }
                })
                .map(new Function<SelectableTag, String>() {
                    @Override
                    public String apply(SelectableTag selectableTag) throws Exception {
                        return selectableTag.getTag();
                    }
                })
                .toList()
                .toObservable();
    }
}
