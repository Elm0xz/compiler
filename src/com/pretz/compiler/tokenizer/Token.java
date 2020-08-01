package com.pretz.compiler.tokenizer;

import java.util.Objects;

public class Token {
    private final TokenType type;
    private final KeywordType keywordType;
    private final String token;

    public Token(String token, TokenType type) {
        this.token = token;
        this.type = type;
        if (type != TokenType.KEYWORD)
            this.keywordType = KeywordType.NOT_A_KEYWORD;
        else
            this.keywordType = setKeywordType(token);
    }

    /**
     * this constructor is only used to update tokens that are keywords and set corresponding KeywordType.
     */
    private Token(String token, TokenType type, KeywordType keywordType) {
        this.token = token;
        this.type = type;
        this.keywordType = keywordType;
    }

    public String token() {
        return token;
    }

    public TokenType type() {
        return type;
    }

    public KeywordType keyword() {
        return keywordType;
    }

    public Token add(Character ch) {
        return new Token(token + ch, type);
    }

    public boolean is(TokenType tokenType) {
        return type().equals(tokenType);
    }

    public boolean isNot(TokenType tokenType) {
        return !is(tokenType);
    }

    public boolean is(KeywordType keywordType) {
        return keyword().equals(keywordType);
    }

    public boolean isNot(KeywordType keywordType) {
        return !is(keywordType);
    }

    public boolean isNot(String token) {
        return !token().equals(token);
    }

    private KeywordType setKeywordType(String token) {
        if (Lexicals.keywords().contains(token))
            return Enum.valueOf(KeywordType.class, token.toUpperCase());
        else throw new IllegalArgumentException("Invalid keyword type");
    }

    public Token changeType(TokenType newType) {
        if (Lexicals.keywords().contains(token)) {
            return new Token(token, newType, Enum.valueOf(KeywordType.class, token.toUpperCase()));
        }
        return new Token(token, newType);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", keywordType=" + keywordType +
                ", token='" + token + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token1 = (Token) o;
        return type == token1.type &&
                keywordType == token1.keywordType &&
                token.equals(token1.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, keywordType, token);
    }
}
