package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.terminal.Terminal;
import io.vavr.collection.List;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.pretz.compiler.util.XmlUtils.comma;

public class VarNames implements Construct {
    private final List<Terminal> varNames;

    public VarNames(List<Terminal> varNames) {
        this.varNames = varNames;
    }

    @Override
    public String toXml(int indLvl) {
        return varNames.map(it -> it.toXml(indLvl))
                .collect(Collectors.joining(comma(indLvl)));
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
