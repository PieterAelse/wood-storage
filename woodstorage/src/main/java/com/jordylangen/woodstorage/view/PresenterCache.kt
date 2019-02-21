package com.jordylangen.woodstorage.view

import java.util.HashMap

class PresenterCache {

    companion object {
        private val PRESENTERS = HashMap<Int, Contract.Presenter<*>>()

        @JvmStatic
        fun put(id: Int, presenter: Contract.Presenter<*>) {
            PRESENTERS[id] = presenter
        }

        @JvmStatic
        operator fun <P : Contract.Presenter<*>> get(id: Int): P? {
            return if (PRESENTERS.containsKey(id)) {
                PRESENTERS[id] as P?
            } else null

        }
    }

}
