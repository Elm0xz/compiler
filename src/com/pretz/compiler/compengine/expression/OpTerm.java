package com.pretz.compiler.compengine.expression;

import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import io.vavr.collection.List;

import java.util.Objects;

public class OpTerm implements Construct {
    private final Op op;
    private final Term term;

    public OpTerm(Op op, Term term) {
        this.op = op;
        this.term = term;
    }

    @Override
    public String toXml(int indLvl) {
        return op.toXml(indLvl) +
                term.toXml(indLvl);
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

    @Override
    public String toString() {
        return "OpTerm{" +
                "op=" + op +
                ", term=" + term +
                '}';
    }

    @Override
    public String toVm(SymbolTable symbolTable) {
        return List.of(term.toVm(symbolTable),
                op.toVm(symbolTable))
                .mkString();
    }
}
