package com.pretz.compiler.compengine.expression;

import com.pretz.compiler.compengine.construct.Construct;
import io.vavr.collection.List;

import java.util.Objects;

public class Expression implements Construct {
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
}
