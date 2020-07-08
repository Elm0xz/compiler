package com.pretz.compiler;

public class Token {
    private final TokenType type;
    private final String token;

    public Token(String token, TokenType type) {
        this.token = token;
        this.type = type;
    }

    public Token add(Character ch) {
        return new Token(token + ch, type);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", token='" + token + '\'' +
                '}';
    }
}
