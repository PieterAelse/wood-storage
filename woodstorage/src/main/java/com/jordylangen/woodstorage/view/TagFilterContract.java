package com.jordylangen.woodstorage.view;

import java.util.List;

import rx.Observable;

public interface TagFilterContract extends Contract {

    interface View extends Contract.View {

        void add(SelectableTag selectableTag);

        void addAll(List<SelectableTag> selectedTags);
    }

    interface Presenter extends Contract.Presenter<View> {

        void tagSelectedChanged(SelectableTag selectableTag, boolean isChecked);

        Observable<List<SelectableTag>> observeSelectedTags();
    }
}
