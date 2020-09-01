package com.pretz.compiler.util;

public enum VmKeywords {
    FUNCTION("function");

    private final String keyword;

    public String keyword() {
        return keyword;
    }

    VmKeywords(String keyword) {
        this.keyword = keyword;
    }
}
