package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.terminal.Terminal;
import io.vavr.collection.List;

import java.util.Objects;

public class Class implements Construct {
    private final Terminal identifier;
    private final List<Construct> declarations;

    public Class(Terminal identifier, List<Construct> declarations) {
        this.identifier = identifier;
        this.declarations = declarations;
    }

    @Override
    public String toString() { //TODO format this to return XML formatted output
        return "Class{" +
                "identifier='" + identifier + '\'' +
                ", declarations=" + declarations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class aClass = (Class) o;
        return identifier.equals(aClass.identifier) &&
                declarations.equals(aClass.declarations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, declarations);
    }
}
