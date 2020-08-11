package com.pretz.compiler.compengine;

import com.pretz.compiler.tokenizer.token.KeywordType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.tokenizer.token.Tokens;
import com.pretz.compiler.util.Lexicals;

import java.util.function.Predicate;

public class CompilationMatcher {

    protected Predicate<Token> isClassVarDec() {
        return it -> it.is(KeywordType.STATIC) || it.is(KeywordType.FIELD);
    }

    protected Predicate<Token> isSubroutineDec() {
        return it -> it.is(KeywordType.CONSTRUCTOR)
                || it.is(KeywordType.FUNCTION)
                || it.is(KeywordType.METHOD);
    }

    protected boolean isNotSemicolon(Token token) {
        return token.isNot(TokenType.SYMBOL) || token.isNot(";");
    }

    protected boolean isNotClosingRoundBracket(Token token) {
        return token.isNot(")") || token.isNot(TokenType.SYMBOL);
    }

    protected boolean isNotClosingCurlyBracket(Token token) {
        return token.isNot("}") || token.isNot(TokenType.SYMBOL);
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

    Predicate<Token> isReturnStatement() {
        return it -> it.is(KeywordType.RETURN);
    }

    public Predicate<Token> isDoStatement() {
        return it -> it.is(KeywordType.DO);
    }

    public Predicate<Token> isWhileStatement() {
        return it -> it.is(KeywordType.WHILE);
    }

    public Predicate<Token> isIfStatement() {
        return it -> it.is(KeywordType.IF);
    }
    public Predicate<Token> isLetStatement() {
        return it -> it.is(KeywordType.LET);
    }


    public boolean isNonOpSymbol(Token token) {
        return token.is(TokenType.SYMBOL) &&
                Lexicals.nonOps().contains(token.token().charAt(0));
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

    public Predicate<Token> isVarNameOrSubroutineCall() {
        return it -> it.is(TokenType.IDENTIFIER);
    }

    public Predicate<Token> isUnaryOp() {
        return it -> it.is(TokenType.SYMBOL) && it.is("-");
    }

    public Predicate<Token> isExpressionInBrackets() {
        return it -> it.is(TokenType.SYMBOL) && it.is("(");
    }

    public Predicate<Tokens> isVarNameArray() {
        return it -> it.ll1().is(TokenType.SYMBOL) && it.ll1().is("[");
    }

    public Predicate<Tokens> isSubroutineCall() {
        return it -> it.ll1().is(TokenType.SYMBOL) && (it.ll1().is("(") || it.ll1().is("."));
    }

    public boolean isDot(Token token) {
        return token.is(TokenType.SYMBOL) && token.is(".");
    }

    public boolean isElseKeyword(Token token) {
        return token.is(TokenType.KEYWORD) && token.is(KeywordType.ELSE);
    }
}
