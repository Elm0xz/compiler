package com.pretz.compiler.compengine.elements.expression;

import com.pretz.compiler.compengine.elements.Element;
import com.pretz.compiler.compengine.elements.construct.Construct;
import io.vavr.collection.List;

import java.util.Objects;

public class Term implements Construct {
    private final List<Element> termParts;

    public Term(Element... elements) {
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
