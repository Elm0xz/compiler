package com.pretz.compiler.compengine.terminal;

import com.pretz.compiler.compengine.symboltable.SymbolTable;
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

    //TODO this constructor is used only for tests
    public Identifier(String token, TerminalType type, IdentifierMeaning identifierMeaning) {
        super(token, type);
        this.identifierMeaning = identifierMeaning;
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "identifierMeaning=" + identifierMeaning +
                "} " + super.toString();
    }

    @Override
    public String toVm(SymbolTable symbolTable) {
        var symbol = symbolTable.get(this);
        return symbol.toVm();
    }
}
