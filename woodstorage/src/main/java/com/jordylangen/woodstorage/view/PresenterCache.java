package com.jordylangen.woodstorage.view;

import java.util.HashMap;
import java.util.Map;

public class PresenterCache {

    private final static Map<Integer, Contract.Presenter> PRESENTERS = new HashMap<>();

    static void put(int id, Contract.Presenter presenter) {
        PRESENTERS.put(id, presenter);
    }

    public static <P extends Contract.Presenter> P get(int id) {
        if (PRESENTERS.containsKey(id)) {
            return (P) PRESENTERS.get(id);
        }

        return null;
    }
}
