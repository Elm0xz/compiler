package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.terminal.Terminal;

import java.util.Objects;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.semicolon;

public class VarDec implements Construct {
    private static final String CONSTRUCT_NAME = "varDec";

    private final Terminal startingKeyword;
    private final Type type;
    private final VarNames varNames;

    public VarDec(Terminal startingKeyword, Type type, VarNames varNames) {
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
        VarDec varDec = (VarDec) o;
        return startingKeyword.equals(varDec.startingKeyword) &&
                type.equals(varDec.type) &&
                varNames.equals(varDec.varNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingKeyword, type, varNames);
    }

    @Override
    public String toString() {
        return "VarDec{" +
                "startingKeyword=" + startingKeyword +
                ", type=" + type +
                ", varNames=" + varNames +
                '}';
    }

    public VarNames varNames() {
        return varNames;
    }

    public Terminal startingKeyword() {
        return startingKeyword;
    }

    public Type type() {
        return type;
    }
}
