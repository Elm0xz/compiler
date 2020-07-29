package com.pretz.compiler.compengine;

import com.pretz.compiler.tokenizer.KeywordType;
import com.pretz.compiler.tokenizer.Token;
import com.pretz.compiler.tokenizer.TokenType;

import java.util.function.Predicate;

public class CompilationValidator {
    public CompilationValidator() {
    }

    protected void validateClassKeyword(Token token) {
        if (!token.type().equals(TokenType.KEYWORD) && !token.keyword().equals(KeywordType.CLASS))
            throw new CompilationException(CompilationException.NOT_A_CLASS);
    }

    protected Token validateClassIdentifier(Token token) {
        if (!token.type().equals(TokenType.IDENTIFIER))
            throw new CompilationException(CompilationException.INVALID_CLASS_IDENTIFIER);
        return token;
    }

    protected void validateOpeningBracket(Token token) {
        if (isNotOpeningBracket(token))
            throw new CompilationException(CompilationException.NOT_AN_OPENING_BRACKET);
    }

    private boolean isNotOpeningBracket(Token token) {
        return !token.token().equals("{") || !token.type().equals(TokenType.SYMBOL);
    }

    protected void validateClosingBracket(Token token) {
        if (isNotClosingBracket(token))
            throw new CompilationException(CompilationException.NOT_A_CLOSING_BRACKET);
    }

    private boolean isNotClosingBracket(Token token) {
        return !token.token().equals("}") || !token.type().equals(TokenType.SYMBOL);
    }

    protected Predicate<Token> isClassVarDec() {
        return it -> it.keyword().equals(KeywordType.STATIC) || it.keyword().equals(KeywordType.FIELD);
    }

    protected Predicate<Token> isSubroutineDec() {
        return it -> false;
    }

    protected void validateCommaOrSemicolon(Token token) {
        if (!token.type().equals(TokenType.SYMBOL) && !token.token().equals(",") && !token.token().equals(";"))
            throw new CompilationException(CompilationException.MISSING_COMMA);
    }

    protected boolean isNotSemicolon(Token token) {
        return !token.type().equals(TokenType.SYMBOL) || !token.token().equals(";");
    }

    protected void validateType(Token token) {
        if (!isPrimitiveType(token) && !token.type().equals(TokenType.IDENTIFIER))
            throw new CompilationException(CompilationException.INVALID_TYPE);
    }

    private boolean isPrimitiveType(Token token) {
        return token.type().equals(TokenType.KEYWORD) &&
                (token.keyword().equals(KeywordType.INT) ||
                token.keyword().equals(KeywordType.BOOLEAN) ||
                token.keyword().equals(KeywordType.CHAR));
    }
}
