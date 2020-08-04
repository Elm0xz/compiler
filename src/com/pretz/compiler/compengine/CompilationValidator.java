package com.pretz.compiler.compengine;

import com.pretz.compiler.tokenizer.KeywordType;
import com.pretz.compiler.tokenizer.Token;
import com.pretz.compiler.tokenizer.TokenType;

import java.util.function.Predicate;

public class CompilationValidator {
    public CompilationValidator() {
    }

    protected void validateClassKeyword(Token token) {
        if (token.isNot(TokenType.KEYWORD) && token.isNot(KeywordType.CLASS))
            throw new CompilationException(CompilationException.NOT_A_CLASS);
    }

    protected Token validateClassIdentifier(Token token) {
        if (token.isNot(TokenType.IDENTIFIER))
            throw new CompilationException(CompilationException.INVALID_CLASS_IDENTIFIER);
        return token;
    }

    protected void validateOpeningCurlyBracket(Token token) {
        if (isNotOpeningCurlyBracket(token))
            throw new CompilationException(CompilationException.NOT_AN_OPENING_CURLY_BRACKET);
    }

    private boolean isNotOpeningCurlyBracket(Token token) {
        return token.isNot("{") || token.isNot(TokenType.SYMBOL);
    }

    protected void validateClosingCurlyBracket(Token token) {
        if (isNotClosingCurlyBracket(token))
            throw new CompilationException(CompilationException.NOT_A_CLOSING_CURLY_BRACKET);
    }

    protected boolean isNotClosingCurlyBracket(Token token) {
        return token.isNot("}") || token.isNot(TokenType.SYMBOL);
    }

    protected Predicate<Token> isClassVarDec() {
        return it -> it.is(KeywordType.STATIC) || it.is(KeywordType.FIELD);
    }

    protected Predicate<Token> isSubroutineDec() {
        return it -> it.is(KeywordType.CONSTRUCTOR)
                || it.is(KeywordType.FUNCTION)
                || it.is(KeywordType.METHOD);
    }

    protected void validateVarName(Token token) {
        if (token.isNot(TokenType.IDENTIFIER))
            throw new CompilationException(CompilationException.INVALID_VARNAME);
    }

    protected void validateCommaOrSemicolon(Token token) {
        if (isNotComma(token) && isNotSemicolon(token))
            throw new CompilationException(CompilationException.MISSING_VAR_COMMA);
    }

    private boolean isNotComma(Token token) {
        return token.isNot(TokenType.SYMBOL) || token.isNot(",");
    }

    protected void validateCommaOrClosingRoundBracket(Token token) {
        if (isNotComma(token) && isNotClosingRoundBracket(token))
            throw new CompilationException(CompilationException.MISSING_PARAMETER_COMMA_OR_CLOSING_ROUND_BRACKET);
    }

    protected boolean isNotSemicolon(Token token) {
        return token.isNot(TokenType.SYMBOL) || token.isNot(";");
    }

    protected void validateType(Token token) {
        if (isNotPrimitiveType(token) && token.isNot(TokenType.IDENTIFIER))
            throw new CompilationException(CompilationException.INVALID_TYPE);
    }

    protected void validateTypeOrVoid(Token token) {
        if (isNotPrimitiveType(token) && token.isNot(TokenType.IDENTIFIER) && token.isNot(KeywordType.VOID))
            throw new CompilationException(CompilationException.INVALID_TYPE);
    }

    private boolean isNotPrimitiveType(Token token) {
        return token.isNot(TokenType.KEYWORD) ||
                (token.isNot(KeywordType.INT) &&
                        token.isNot(KeywordType.BOOLEAN) &&
                        token.isNot(KeywordType.CHAR));
    }

    protected void validateOpeningRoundBracket(Token token) {
        if (isNotOpeningRoundBracket(token))
            throw new CompilationException(CompilationException.NOT_AN_OPENING_ROUND_BRACKET);
    }

    private boolean isNotOpeningRoundBracket(Token token) {
        return token.isNot("(") || token.isNot(TokenType.SYMBOL);
    }

    protected void validateSubroutineName(Token token) {
        if (token.isNot(TokenType.IDENTIFIER))
            throw new CompilationException(CompilationException.INVALID_SUBROUTINE_NAME);
    }

    protected boolean isNotClosingRoundBracket(Token token) {
        return token.isNot(")") || token.isNot(TokenType.SYMBOL);
    }

    protected Predicate<Token> isVarDec() {
        return it -> it.is(KeywordType.VAR);
    }

    protected Predicate<Token> isStatement() {
        return this::isStatementKeyword;
    }

    private boolean isStatementKeyword(Token token) {
        return token.is(KeywordType.LET) ||
                token.is(KeywordType.IF) ||
                token.is(KeywordType.WHILE) ||
                token.is(KeywordType.DO) ||
                token.is(KeywordType.RETURN);
    }

    public Predicate<Token> isConstant() {
        return it -> it.is(TokenType.INT_CONST) || it.is(TokenType.STRING_CONST) || isKeywordConstant(it);
    }

    private boolean isKeywordConstant(Token token) {
        return token.is(KeywordType.TRUE) ||
                token.is(KeywordType.FALSE) ||
                token.is(KeywordType.NULL) ||
                token.is(KeywordType.THIS);
    }

    public Predicate<Token> isVarName() {
        return it -> it.is(TokenType.IDENTIFIER);
    }

    public boolean isNonOpSymbol(Token token) {
        return token.is(TokenType.SYMBOL) &&
                (token.is("{") ||
                        token.is("}") ||
                        token.is("(") ||
                        token.is(")") ||
                        token.is("[") ||
                        token.is("]") ||
                        token.is(".") ||
                        token.is(",") ||
                        token.is(";"));
    }

    public Predicate<Token> isUnaryOp() {
        return it -> it.is(TokenType.SYMBOL) && it.is("-");
    }
}
