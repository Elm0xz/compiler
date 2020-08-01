package com.pretz.compiler.compengine.constructs;

import io.vavr.collection.List;

import java.util.Objects;

public class ParameterList implements Construct {
    private final List<Parameter> parameters;

    public ParameterList(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterList that = (ParameterList) o;
        return parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameters);
    }
}
