package com.pretz.compiler.compengine.expression;

import com.pretz.compiler.compengine.Element;
import com.pretz.compiler.compengine.construct.Construct;
import io.vavr.collection.List;

import java.util.Objects;

public class Term implements Construct {
    private final List<Element> termParts;
    private final TermType termType;

    public Term(TermType termType, Element... elements) {
        this.termType = termType;
        this.termParts = List.of(elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return termParts.equals(term.termParts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termParts);
    }
}
