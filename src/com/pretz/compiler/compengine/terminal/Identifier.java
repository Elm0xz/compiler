package com.pretz.compiler.compengine.terminal;

import com.pretz.compiler.compengine.VmContext;
import com.pretz.compiler.compengine.VmKeyword;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.validator.Validator;
import com.pretz.compiler.tokenizer.token.Token;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class Identifier extends Terminal {
    private final IdentifierMeaning identifierMeaning;
    private final IdentifierType identifierType;

    public Identifier(Token token, Validator validator, IdentifierMeaning identifierMeaning, IdentifierType identifierType) {
        super(token, validator);
        this.identifierMeaning = identifierMeaning;
        this.identifierType = identifierType;
    }

    public Identifier(Token token, IdentifierMeaning identifierMeaning, IdentifierType identifierType) {
        super(token);
        this.identifierMeaning = identifierMeaning;
        this.identifierType = identifierType;
    }

    //TODO(L) this constructor is used only for tests
    public Identifier(String token, TerminalType type, IdentifierMeaning identifierMeaning, IdentifierType identifierType) {
        super(token, type);
        this.identifierMeaning = identifierMeaning;
        this.identifierType = identifierType;
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "identifierMeaning=" + identifierMeaning +
                "} " + super.toString();
    }

    @Override
    public String toVm(VmContext vmContext) {
        return Match(identifierType).of(
                Case($(IdentifierType.VAR), () -> varToVm(vmContext.symbolTable())),
                Case($(IdentifierType.SUBROUTINE), this::subroutineToVm),
                Case($(), () -> "NOT YET IMPLEMENTED!")
        );
    }

    private String varToVm(SymbolTable symbolTable) {
        var symbol = symbolTable.get(this);
        return symbol.toVm();
    }

    private String subroutineToVm() {
        return VmKeyword.CALL + " " + token + " ";
    }
}
