package com.pretz.compiler.compengine.elements.construct;

import com.pretz.compiler.compengine.elements.terminal.Terminal;
import io.vavr.collection.List;

import java.util.Objects;

public class VarNames implements Construct {
    private final List<Terminal> varNames;

    public VarNames(List<Terminal> varNames) {
        this.varNames = varNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarNames varNames1 = (VarNames) o;
        return varNames.equals(varNames1.varNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varNames);
    }
}
