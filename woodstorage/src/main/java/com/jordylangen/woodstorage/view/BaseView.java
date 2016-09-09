package com.jordylangen.woodstorage.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class BaseView<V extends Contract.View, P extends Contract.Presenter<V>> extends LinearLayout {

    private P presenter;

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setup();

        presenter = PresenterCache.get(getId());
        if (presenter == null) {
            presenter = newPresenter();
            PresenterCache.put(getId(), presenter);
        }

        presenter.setup((V) this);
    }

    protected abstract void setup();

    protected abstract P newPresenter();

    public P getPresenter() {
        return presenter;
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.teardown();
        super.onDetachedFromWindow();
    }
}
