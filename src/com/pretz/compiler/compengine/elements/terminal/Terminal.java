package com.pretz.compiler.compengine.elements.terminal;

import com.pretz.compiler.compengine.elements.Element;
import com.pretz.compiler.util.Lexicals;

import java.util.Objects;

public class Terminal implements Element {
    private final String token;
    private final TerminalType type;
    private final TerminalKeywordType keywordType;

    public Terminal(String token, TerminalType type) {
        this.token = token;
        this.type = type;
        if (type != TerminalType.KEYWORD)
            this.keywordType = TerminalKeywordType.NOT_A_KEYWORD;
        else
            this.keywordType = setKeywordType(token);
    }

    public Terminal(String token, TerminalType type, TerminalKeywordType keywordType) {
        this.token = token;
        this.type = type;
        this.keywordType = keywordType;
    }

    private TerminalKeywordType setKeywordType(String token) {
        if (Lexicals.keywords().contains(token))
            return TerminalKeywordType.valueOf(token.toUpperCase());
        else throw new IllegalArgumentException("Invalid keyword type");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Terminal terminal = (Terminal) o;
        return token.equals(terminal.token) &&
                type == terminal.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, type);
    }
}
