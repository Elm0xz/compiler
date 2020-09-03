package com.pretz.compiler.compengine.symboltable;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public enum Kind {
    VAR,
    ARGUMENT,
    STATIC,
    FIELD;

    public String toVm() {
        return Match(this).of(
                Case($(VAR), () -> "local"),
                Case($(ARGUMENT), () -> "argument"),
                Case($(STATIC), () -> "static"),
                Case($(FIELD), () -> "this"));
    }
}
