package com.pretz.compiler.compengine.terminal;

import com.pretz.compiler.compengine.CompilationException;
import com.pretz.compiler.tokenizer.token.TokenType;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public enum TerminalType {
    KEYWORD_CONST,
    OP,
    UNARY_OP,
    INT_CONST,
    STRING_CONST,
    IDENTIFIER
}

class TerminalTypeMapper {

    public TerminalType from(TokenType tokenType) {
        return Match(tokenType).of(
                Case($(TokenType.KEYWORD), TerminalType.KEYWORD_CONST),
                Case($(TokenType.IDENTIFIER), TerminalType.IDENTIFIER),
                Case($(TokenType.SYMBOL), TerminalType.OP),
                Case($(TokenType.INT_CONST), TerminalType.INT_CONST),
                Case($(TokenType.STRING_CONST), TerminalType.STRING_CONST),
                Case($(), this::throwMappingException));
    }

    private TerminalType throwMappingException() {
        throw new CompilationException(CompilationException.INVALID_TOKEN_MAPPING);
    }
}
