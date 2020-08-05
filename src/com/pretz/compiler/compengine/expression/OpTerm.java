package com.pretz.compiler.compengine.expression;

import com.pretz.compiler.compengine.construct.Construct;

import java.util.Objects;

public class OpTerm implements Construct {
    private final Op op;
    private final Term term;

    public OpTerm(Op op, Term term) {
        this.op = op;
        this.term = term;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpTerm opTerm = (OpTerm) o;
        return op.equals(opTerm.op) &&
                term.equals(opTerm.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, term);
    }
}
