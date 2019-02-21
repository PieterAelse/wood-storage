package com.jordylangen.woodstorage.view

import io.reactivex.Observable

interface TagFilterContract : Contract {

    interface View : Contract.View {

        fun add(selectableTag: SelectableTag)

        fun set(selectedTags: List<SelectableTag>)
    }

    interface Presenter : Contract.Presenter<View> {

        fun tagSelectedChanged(selectableTag: SelectableTag, isChecked: Boolean)

        fun observeSelectedTags(): Observable<List<SelectableTag>>
    }
}
