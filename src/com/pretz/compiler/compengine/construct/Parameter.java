package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.terminal.Terminal;

import java.util.Objects;

public class Parameter implements Construct {
    private final Type type;
    private final Terminal varName;

    public Parameter(Type type, Terminal varName) {
        this.type = type;
        this.varName = varName;
    }

    @Override
    public String toXml(int indLvl) {
        return type.toXml(indLvl) +
                varName.toXml(indLvl);
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
