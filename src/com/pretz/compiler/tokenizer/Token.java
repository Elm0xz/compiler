package com.pretz.compiler.tokenizer;

public class Token {
    private final TokenType type;
    private final KeywordType keywordType;
    private final String token;

    public Token(String token, TokenType type) {
        this.token = token;
        this.type = type;
        this.keywordType = KeywordType.NOT_A_KEYWORD;
    }

    /** this constructor is only used to update tokens that are keywords and set corresponding KeywordType.*/
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

    public Token add(Character ch) {
        return new Token(token + ch, type);
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
}
