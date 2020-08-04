package com.pretz.compiler.compengine.elements.expression;

import com.pretz.compiler.compengine.CompilationException;
import com.pretz.compiler.compengine.elements.construct.Construct;
import com.pretz.compiler.tokenizer.Token;
import com.pretz.compiler.tokenizer.TokenType;

import java.util.Objects;

public class Op implements Construct {
    private final Token op;

    public Op(Token token) {
        validate(token);
        this.op = token;
    }

    private void validate(Token token) { //TODO test this
        if (!token.is(TokenType.SYMBOL) ||
                (token.isNot("+") && token.isNot("+") &&
                        token.isNot("-") && token.isNot("*") &&
                        token.isNot("/") && token.isNot("&") &&
                        token.isNot("|") && token.isNot("<") &&
                        token.isNot(">") && token.isNot("=")))
            throw new CompilationException(CompilationException.INVALID_OPERATOR);
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
