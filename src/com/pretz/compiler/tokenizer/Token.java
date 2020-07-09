package com.pretz.compiler.tokenizer;

public class Token {
    private final TokenType type;

    private final String token;

    public Token(String token, TokenType type) {
        this.token = token;
        this.type = type;
    }

    public String token() {
        return token;
    }

    public TokenType type() {
        return type;
    }

    public Token add(Character ch) {
        return new Token(token + ch, type);
    }

    public Token changeType(TokenType newType) {
        return new Token(token, newType);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", token='" + token + '\'' +
                '}';
    }
}
