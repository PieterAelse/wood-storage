package com.jordylangen.woodstorage.view

interface Contract {

    interface View

    interface Presenter<V : Contract.View> {

        fun setup(view: V)

        fun teardown()
    }
}
