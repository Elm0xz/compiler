package com.pretz.compiler.compengine.expression;

import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.terminal.Terminal;

import java.util.Objects;

public class Op implements Construct {
    private final Terminal op;

    public Op(Terminal token) {
        validate(token);
        this.op = token;
    }

    //TODO refactor this
    private void validate(Terminal token) { //TODO test this
        /*if (!token.is(TerminalType.SYMBOL) ||
                (token.isNot("+") && token.isNot("+") &&
                        token.isNot("-") && token.isNot("*") &&
                        token.isNot("/") && token.isNot("&") &&
                        token.isNot("|") && token.isNot("<") &&
                        token.isNot(">") && token.isNot("=")))
            throw new CompilationException(CompilationException.INVALID_OPERATOR);*/
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Op op1 = (Op) o;
        return op.equals(op1.op);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op);
    }
}
