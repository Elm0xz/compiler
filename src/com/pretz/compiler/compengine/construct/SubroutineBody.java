package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.statement.Statement;
import io.vavr.collection.List;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.closingCurlyBracket;
import static com.pretz.compiler.util.XmlUtils.openingCurlyBracket;

public class SubroutineBody implements Construct {
    private static final String CONSTRUCT_NAME = "subroutineBody";
    private static final String STATEMENTS = "statements";

    private final List<Construct> subroutineBody;

    public SubroutineBody(List<Construct> subroutineBody) {
        this.subroutineBody = subroutineBody;
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                openingCurlyBracket(indLvl) +
                varDecToXml(indLvl) +
                basicOpeningTag(indLvl, STATEMENTS) +
                statementsToXml(indLvl + 1) +
                basicClosingTag(indLvl, STATEMENTS) +
                closingCurlyBracket(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    public String varDecToXml(int indLvl) {
        return subroutineBody.filter(it -> it instanceof VarDec)
                .map(it -> it.toXml(indLvl))
                .collect(Collectors.joining());
    }

    private String statementsToXml(int indLvl) {
        return subroutineBody.filter(it -> it instanceof Statement)
                .map(it -> it.toXml(indLvl))
                .collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubroutineBody that = (SubroutineBody) o;
        return subroutineBody.equals(that.subroutineBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subroutineBody);
    }

    @Override
    public String toString() {
        return "SubroutineBody{" +
                "subroutineBody=" + subroutineBody +
                '}';
    }
}
