package com.pretz.compiler.compengine.expression;

import com.pretz.compiler.compengine.CompilationException;
import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.validator.Validator;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.util.Lexicals;

import java.util.Objects;

public class Op implements Construct, Validator {
    private final Terminal op;

    public Op(Token token) {
        this.op = new Terminal(token, this);
    }

    public Op(Terminal terminal) {
        this.op = terminal;
    }

    @Override
    public void validate(Token token) { //TODO test this
        if (!token.is(TokenType.SYMBOL) ||
                (!Lexicals.ops().contains(token.token().charAt(0))))
            throw new CompilationException(CompilationException.INVALID_OPERATOR);
    }

    @Override
    public String toXml(int indLvl) {
        return op.toXml(indLvl);
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
