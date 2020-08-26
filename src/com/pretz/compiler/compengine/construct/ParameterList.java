package com.pretz.compiler.compengine.construct;

import io.vavr.collection.List;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.closingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.comma;
import static com.pretz.compiler.util.XmlUtils.openingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.semicolon;

public class ParameterList implements Construct {
    private static final String CONSTRUCT_NAME = "parameterList";

    private final List<Parameter> parameters;

    public ParameterList(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                parameterListToXml(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    private String parameterListToXml(int indLvl) {
        return parameters.map(it -> it.toXml(indLvl))
                .collect(Collectors.joining(comma(indLvl)));
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

    @Override
    public String toString() {
        return "ParameterList{" +
                "parameters=" + parameters +
                '}';
    }

    public List<Parameter> parameters() {
        return parameters;
    }
}
