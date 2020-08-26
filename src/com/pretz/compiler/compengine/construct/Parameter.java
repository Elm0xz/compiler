package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.terminal.Identifier;
import com.pretz.compiler.compengine.terminal.Terminal;

import java.util.Objects;

public class Parameter implements Construct {
    private final Type type;
    private final Identifier varName;

    public Parameter(Type type, Identifier varName) {
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

    @Override
    public String toString() {
        return "Parameter{" +
                "type=" + type +
                ", varName=" + varName +
                '}';
    }

    public Type type() {
        return type;
    }

    public Identifier varName() {
        return varName;
    }
}
