package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.VmKeyword;

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
                Case($(VAR), () -> VmKeyword.VAR),
                Case($(ARGUMENT), () -> VmKeyword.ARGUMENT),
                Case($(STATIC), () -> VmKeyword.STATIC),
                Case($(FIELD), () -> VmKeyword.FIELD)).toString();
    }
}
