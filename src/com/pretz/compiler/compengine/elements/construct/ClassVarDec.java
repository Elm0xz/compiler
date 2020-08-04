package com.pretz.compiler.compengine.elements.construct;

import com.pretz.compiler.tokenizer.Token;

import java.util.Objects;

public class ClassVarDec implements Construct {
    private final Token startingKeyword;
    private final Type type;
    private final VarNames varNames;

    public ClassVarDec(Token startingKeyword, Type type, VarNames varNames) {
        this.startingKeyword = startingKeyword;
        this.type = type;
        this.varNames = varNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassVarDec that = (ClassVarDec) o;
        return startingKeyword.equals(that.startingKeyword) &&
                type.equals(that.type) &&
                varNames.equals(that.varNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingKeyword, type, varNames);
    }
}
