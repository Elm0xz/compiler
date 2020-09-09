package com.pretz.compiler.util;

//TODO(H) refactor other vm keywords to this file
public enum VmKeyword {
    FUNCTION("function");

    private final String keyword;

    public String keyword() {
        return keyword;
    }

    VmKeyword(String keyword) {
        this.keyword = keyword;
    }
}
