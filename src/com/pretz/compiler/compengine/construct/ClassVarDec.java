package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.terminal.Terminal;

import java.util.Objects;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.semicolon;

public class ClassVarDec implements Construct {
    private static final String CONSTRUCT_NAME = "classVarDec";

    private final Terminal startingKeyword;
    private final Type type;
    private final VarNames varNames;

    public ClassVarDec(Terminal startingKeyword, Type type, VarNames varNames) {
        this.startingKeyword = startingKeyword;
        this.type = type;
        this.varNames = varNames;
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                startingKeyword.toXml(indLvl) +
                type.toXml(indLvl) +
                varNames.toXml(indLvl) +
                semicolon(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
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
