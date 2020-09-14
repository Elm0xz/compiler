package com.pretz.compiler.compengine;

//TODO(H) refactor other vm keywords to this file
public enum VmKeyword {
    ADD("add"),
    SUB("sub"),
    AND("and"),
    OR("or"),
    NEG("neg"),
    NOT("not"),
    VAR("local"),
    ARGUMENT("argument"),
    STATIC("static"),
    FIELD("this"),
    CONSTANT("constant"),
    PUSH("push"),
    FUNCTION("function"),
    CALL("call");

    private final String keyword;

    VmKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return keyword;
    }
}
