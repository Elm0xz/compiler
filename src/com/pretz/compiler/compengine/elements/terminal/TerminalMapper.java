package com.pretz.compiler.compengine.elements.terminal;

import com.pretz.compiler.tokenizer.token.KeywordType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;

public class TerminalMapper {

    public Terminal from(Token token) {
        return new Terminal(token.token(), map(token.type()), map(token.keyword()));
    }

    private TerminalType map(TokenType type) {
        return Enum.valueOf(TerminalType.class, type.toString().toUpperCase());
    }

    private TerminalKeywordType map(KeywordType type) {
        return Enum.valueOf(TerminalKeywordType.class, type.toString().toUpperCase());
    }
}
