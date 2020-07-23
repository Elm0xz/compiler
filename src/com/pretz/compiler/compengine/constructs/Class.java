package com.pretz.compiler.compengine.constructs;

import io.vavr.collection.List;

import java.util.Objects;

public class Class implements Construct {
    private final String identifier;
    private final List<ClassVarDec> variableDeclarations;
    private final List<SubroutineDec> subroutineDeclarations;

    public Class(String identifier, List<ClassVarDec> variableDeclarations, List<SubroutineDec> subroutineDeclarations) {
        this.identifier = identifier;
        this.variableDeclarations = variableDeclarations;
        this.subroutineDeclarations = subroutineDeclarations;
    }

    @Override
    public String toString() { //TODO format this to return XML formatted output
        return "Class{" +
                "identifier='" + identifier + '\'' +
                ", variableDeclarations=" + variableDeclarations +
                ", subroutineDeclarations=" + subroutineDeclarations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class aClass = (Class) o;
        return identifier.equals(aClass.identifier) &&
                variableDeclarations.equals(aClass.variableDeclarations) &&
                subroutineDeclarations.equals(aClass.subroutineDeclarations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, variableDeclarations, subroutineDeclarations);
    }
}
