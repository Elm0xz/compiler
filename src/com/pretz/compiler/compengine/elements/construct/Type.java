package com.pretz.compiler.compengine.elements.construct;

import com.pretz.compiler.compengine.elements.terminal.Terminal;

import java.util.Objects;

public class Type implements Construct {
    private final Terminal type;

    public Type(Terminal type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type1 = (Type) o;
        return type.equals(type1.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
