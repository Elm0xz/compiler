package com.pretz.compiler.compengine.elements.expression;

import com.pretz.compiler.compengine.elements.construct.Construct;
import com.pretz.compiler.tokenizer.Token;
import io.vavr.collection.List;

import java.util.Objects;

public class Term implements Construct {
    private final List<Token> termParts;

    public Term(Token... tokens) {
        this.termParts = List.of(tokens);
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
