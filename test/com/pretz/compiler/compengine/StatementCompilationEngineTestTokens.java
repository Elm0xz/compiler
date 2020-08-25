package com.pretz.compiler.compengine;

import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.tokenizer.token.Tokens;
import io.vavr.collection.List;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class StatementCompilationEngineTestTokens {

    private final ElementTestUtils $ = new ElementTestUtils();

    protected Tokens returnStatementWithOperatorsTokens() {
        return new Tokens(List.of(
                $.token("return", TokenType.KEYWORD),
                $.token("-", TokenType.SYMBOL),
                $.token("duck", TokenType.IDENTIFIER),
                $.token("+", TokenType.SYMBOL),
                $.token("dog", TokenType.IDENTIFIER),
                $.token("+", TokenType.SYMBOL),
                $.token("3", TokenType.INT_CONST),
                $.token(";", TokenType.SYMBOL)));
    }

    protected Tokens returnStatementWithAdditionalBracketsTokens() {
        return new Tokens(List.of(
                $.token("return", TokenType.KEYWORD),
                $.token("(", TokenType.SYMBOL),
                $.token("x", TokenType.IDENTIFIER),
                $.token("*", TokenType.SYMBOL),
                $.token("3", TokenType.INT_CONST),
                $.token(")", TokenType.SYMBOL),
                $.token("+", TokenType.SYMBOL),
                $.token("y", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL)));
    }

    protected Tokens returnStatementWithSubroutineCallTokens() {
        return new Tokens(List.of(
                $.token("return", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("5", TokenType.INT_CONST),
                $.token(")", TokenType.SYMBOL),
                $.token(";", TokenType.SYMBOL)));
    }

    protected Tokens returnStatementWithClassSubroutineCallTokens() {
        return new Tokens(List.of(
                $.token("return", TokenType.KEYWORD),
                $.token("stuff", TokenType.IDENTIFIER),
                $.token(".", TokenType.SYMBOL),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("5", TokenType.INT_CONST),
                $.token(")", TokenType.SYMBOL),
                $.token(";", TokenType.SYMBOL)));
    }

    protected Tokens doStatementTokens() {
        return new Tokens(List.of(
                $.token("do", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("5", TokenType.INT_CONST),
                $.token(")", TokenType.SYMBOL),
                $.token(";", TokenType.SYMBOL)));
    }

    protected Tokens doStatementWithClassSubroutineCallTokens() {
        return new Tokens(List.of(
                $.token("do", TokenType.KEYWORD),
                $.token("stuff", TokenType.IDENTIFIER),
                $.token(".", TokenType.SYMBOL),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("5", TokenType.INT_CONST),
                $.token(")", TokenType.SYMBOL),
                $.token(";", TokenType.SYMBOL)));
    }

    protected Tokens whileStatementTokens() {
        return new Tokens(List.of(
                $.token("while", TokenType.KEYWORD),
                $.token("(", TokenType.SYMBOL),
                $.token("true", TokenType.KEYWORD),
                $.token(")", TokenType.SYMBOL),
                $.token("{", TokenType.SYMBOL),
                $.token("do", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("3", TokenType.INT_CONST),
                $.token(")", TokenType.SYMBOL),
                $.token(";", TokenType.SYMBOL),
                $.token("do", TokenType.KEYWORD),
                $.token("doAnotherStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("false", TokenType.KEYWORD),
                $.token(")", TokenType.SYMBOL),
                $.token(";", TokenType.SYMBOL),
                $.token("}", TokenType.SYMBOL)
        ));
    }

    protected Tokens ifStatementTokens() {
        return new Tokens(List.of(
                $.token("if", TokenType.KEYWORD),
                $.token("(", TokenType.SYMBOL),
                $.token("x", TokenType.IDENTIFIER),
                $.token("=", TokenType.SYMBOL),
                $.token("3", TokenType.INT_CONST),
                $.token(")", TokenType.SYMBOL),
                $.token("{", TokenType.SYMBOL),
                $.token("do", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("3", TokenType.INT_CONST),
                $.token(")", TokenType.SYMBOL),
                $.token(";", TokenType.SYMBOL),
                $.token("}", TokenType.SYMBOL),
                $.token("else", TokenType.KEYWORD),
                $.token("{", TokenType.SYMBOL),
                $.token("do", TokenType.KEYWORD),
                $.token("doAnotherStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("false", TokenType.KEYWORD),
                $.token(")", TokenType.SYMBOL),
                $.token(";", TokenType.SYMBOL),
                $.token("}", TokenType.SYMBOL),
                $.token("}", TokenType.SYMBOL)
        ));
    }

    protected Tokens letStatementTokens() {
        return new Tokens(List.of(
                $.token("let", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token("[", TokenType.SYMBOL),
                $.token("5", TokenType.INT_CONST),
                $.token("]", TokenType.SYMBOL),
                $.token("=", TokenType.INT_CONST),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("3", TokenType.INT_CONST),
                $.token(")", TokenType.SYMBOL),
                $.token(";", TokenType.SYMBOL)
        ));
    }

    static Stream<Arguments> keywordConstants() {
        return Stream.of(Arguments.of("true"),
                Arguments.of("false"),
                Arguments.of("this"),
                Arguments.of("null")
        );
    }
}
