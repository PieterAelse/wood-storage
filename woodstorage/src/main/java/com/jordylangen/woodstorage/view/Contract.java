package com.jordylangen.woodstorage.view;

import android.content.Context;

interface Contract {

    interface View {
        Context getContext();
    }

    interface Presenter<V extends View> {

        void setup(V view);

        void teardown();
    }
}
