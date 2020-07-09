package com.pretz.compiler;

import io.vavr.collection.List;

public class ListUtils {

    public static <T> List<T> appendTo(List<T> list, T el) {
        return list.append(el);
    }

    public static <T> List<T> removeLastFrom(List<T> list) {
        if (list.length() == 0) return list;
        return list.subSequence(0, list.size() - 1);
    }
}
