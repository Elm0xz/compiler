package com.pretz.compiler.compengine.terminal;

import com.pretz.compiler.compengine.validator.Validator;
import com.pretz.compiler.tokenizer.token.Token;

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

    public Identifier(String token, TerminalType type, IdentifierMeaning identifierMeaning) {
        super(token, type);
        this.identifierMeaning = identifierMeaning;
    }
}
