package com.jordylangen.woodstorage.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

abstract class BaseView<V : Contract.View, P : Contract.Presenter<V>> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var presenter: P? = null
        private set

    override fun onFinishInflate() {
        super.onFinishInflate()
        setup()

        presenter = PresenterCache.get(id)
        if (presenter == null) {
            presenter = newPresenter()
            PresenterCache.put(id, presenter!!)
        }

        @Suppress("UNCHECKED_CAST")
        presenter?.setup(this as V)
    }

    protected abstract fun setup()

    protected abstract fun newPresenter(): P

    override fun onDetachedFromWindow() {
        presenter?.teardown()
        super.onDetachedFromWindow()
    }
}
