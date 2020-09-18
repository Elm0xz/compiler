package com.pretz.compiler.compengine;

public enum VmKeyword {
    ADD("add"),
    SUB("sub"),
    AND("and"),
    OR("or"),
    NEG("neg"),
    NOT("not"),
    EQ("eq"),
    GT("gt"),
    LT("lt"),
    VAR("local"),
    ARGUMENT("argument"),
    STATIC("static"),
    FIELD("this"),
    CONSTANT("constant"),
    POINTER("pointer"),
    ARRAY("that"),
    TEMP("temp"),
    PUSH("push"),
    POP("pop"),
    IF_GOTO("if-goto"),
    GOTO("goto"),
    LABEL("label"),
    FUNCTION("function"),
    CALL("call"),
    RETURN("return"),
    ALLOC("Memory.alloc"),
    STRING_NEW("String.new"),
    STRING_APPEND("String.appendChar"),
    MULT("Math.multiply 2"),
    DIV("Math.divide 2");

    private final String keyword;

    VmKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return keyword;
    }
}
