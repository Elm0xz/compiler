package com.pretz.compiler.compengine.expression;

import com.pretz.compiler.compengine.construct.Construct;
import io.vavr.collection.List;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.comma;

public class Expression implements Construct {
    private static final String CONSTRUCT_NAME = "expression";

    private final Term term;
    private final List<OpTerm> opTerms;

    public Expression(Term term, List<OpTerm> opTerms) {
        this.term = term;
        this.opTerms = opTerms;
    }

    public Expression(Term term) {
        this.term = term;
        this.opTerms = List.empty();
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                term.toXml(indLvl) +
                opTermsToXml(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    private String opTermsToXml(int indLvl) {
        return opTerms.map(it -> it.toXml(indLvl))
                .collect(Collectors.joining(comma(indLvl)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expression that = (Expression) o;
        return term.equals(that.term) &&
                opTerms.equals(that.opTerms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, opTerms);
    }

    @Override
    public String toString() {
        return "Expression{" +
                "term=" + term +
                ", opTerms=" + opTerms +
                '}';
    }
}
