package com.pretz.compiler.tokenizing;

public class TokenizingFlags {
    private final boolean stringLiteral;

    public TokenizingFlags(boolean stringLiteral) {
        this.stringLiteral = stringLiteral;
    }

    public TokenizingFlags() {
        this.stringLiteral = false;
    }

    public boolean isStringConst() {
        return stringLiteral;
    }

    public TokenizingFlags stringConstStart() {
        return new TokenizingFlags(true);
    }

    public TokenizingFlags stringConstEnd() {
        return new TokenizingFlags(false);
    }
}
