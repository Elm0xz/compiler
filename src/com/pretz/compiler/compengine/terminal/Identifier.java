package com.pretz.compiler.compengine.terminal;

import com.pretz.compiler.compengine.validator.Validator;
import com.pretz.compiler.tokenizer.token.Token;

import java.util.Objects;

public class Identifier extends Terminal {
    private final IdentifierMeaning identifierMeaning;

    public Identifier(Token token, Validator validator, IdentifierMeaning identifierMeaning) {
        super(token, validator);
        this.identifierMeaning = identifierMeaning;
    }

    public Identifier(Token token, IdentifierMeaning identifierMeaning) {
        super(token);
        this.identifierMeaning = identifierMeaning;
    }

    //TODO this constructor is used only for tests
    public Identifier(String token, TerminalType type, IdentifierMeaning identifierMeaning) {
        super(token, type);
        this.identifierMeaning = identifierMeaning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Identifier that = (Identifier) o;
        return identifierMeaning == that.identifierMeaning;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifierMeaning);
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "identifierMeaning=" + identifierMeaning +
                "} " + super.toString();
    }
}
