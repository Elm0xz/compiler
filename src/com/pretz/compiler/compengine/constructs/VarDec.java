package com.pretz.compiler.compengine.constructs;

import com.pretz.compiler.tokenizer.Token;

import java.util.Objects;

public class VarDec implements Construct {
    private final Token startingKeyword;
    private final Type type;
    private final VarNames varNames;

    public VarDec(Token startingKeyword, Type type, VarNames varNames) {
        this.startingKeyword = startingKeyword;
        this.type = type;
        this.varNames = varNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarDec varDec = (VarDec) o;
        return startingKeyword.equals(varDec.startingKeyword) &&
                type.equals(varDec.type) &&
                varNames.equals(varDec.varNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingKeyword, type, varNames);
    }
}
