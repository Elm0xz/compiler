package com.pretz.compiler.compengine.elements.construct;

import com.pretz.compiler.tokenizer.token.Token;

import java.util.Objects;

public class Parameter implements Construct {
    private final Type type;
    private final Token varName;

    public Parameter(Type type, Token varName) {
        this.type = type;
        this.varName = varName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter = (Parameter) o;
        return type.equals(parameter.type) &&
                varName.equals(parameter.varName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, varName);
    }
}
