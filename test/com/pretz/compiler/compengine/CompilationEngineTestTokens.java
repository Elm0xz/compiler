package com.pretz.compiler.compengine;

import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.tokenizer.token.Tokens;
import io.vavr.collection.List;

public class CompilationEngineTestTokens {

    private final ElementTestUtils $ = new ElementTestUtils();

    protected Tokens classWithDeclarations(Token className, List<Token> declarationTokens) {
        return new Tokens(List.of(
                $.token("class", TokenType.KEYWORD),
                className,
                $.token("{", TokenType.SYMBOL))
                .appendAll(declarationTokens)
                .append($.token("}", TokenType.SYMBOL)));
    }

    protected List<Token> classTokensList() {
        return List.of(
                $.token("static", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("testInt", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("field", TokenType.KEYWORD),
                $.token("boolean", TokenType.KEYWORD),
                $.token("testBool1", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token("testBool2", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("method", TokenType.KEYWORD),
                $.token("void", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("boolean", TokenType.KEYWORD),
                $.token("booleanParameter", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token("int", TokenType.KEYWORD),
                $.token("intParameter", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token("Dog", TokenType.IDENTIFIER),
                $.token("objectParameter", TokenType.IDENTIFIER),
                $.token(")", TokenType.SYMBOL),
                $.token("{", TokenType.SYMBOL),
                $.token("var", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token("y", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("var", TokenType.KEYWORD),
                $.token("Dog", TokenType.IDENTIFIER),
                $.token("dog", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("let", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token("=", TokenType.SYMBOL),
                $.token("5", TokenType.INT_CONST),
                $.token(";", TokenType.SYMBOL),
                $.token("return", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("}", TokenType.SYMBOL)
        );
    }

    protected List<Token> classVarDecTokensList() {
        return List.of(
                $.token("static", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("testInt", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("field", TokenType.KEYWORD),
                $.token("boolean", TokenType.KEYWORD),
                $.token("testBool1", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token("testBool2", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL)
        );
    }

    protected List<Token> classVarDecTokensListWithCustomType() {
        return List.of(
                $.token("static", TokenType.KEYWORD),
                $.token("Cat", TokenType.IDENTIFIER),
                $.token("testCat", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("field", TokenType.KEYWORD),
                $.token("boolean", TokenType.KEYWORD),
                $.token("testBool1", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token("testBool2", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL)
        );
    }

    protected List<Token> classVarDecTokensListWithInvalidVarType() {
        return List.of(
                $.token("static", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("testInt", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("field", TokenType.KEYWORD),
                $.token("X", TokenType.STRING_CONST),
                $.token("testBool1", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token("testBool2", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL)
        );
    }

    protected List<Token> classVarDecTokensListWithInvalidVarName() {
        return List.of(
                $.token("static", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("testInt", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("field", TokenType.KEYWORD),
                $.token("boolean", TokenType.KEYWORD),
                $.token("(", TokenType.SYMBOL),
                $.token(",", TokenType.SYMBOL),
                $.token("testBool2", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL)
        );
    }

    protected List<Token> classVarDecTokensListWithMissingComma() {
        return List.of(
                $.token("static", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("testInt", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("field", TokenType.KEYWORD),
                $.token("boolean", TokenType.KEYWORD),
                $.token("testBool1", TokenType.IDENTIFIER),
                $.token("testBool2", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL)
        );
    }

    protected List<Token> classSubroutineDecTokensList() {
        return List.of(
                $.token("function", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("doSomeStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("boolean", TokenType.KEYWORD),
                $.token("booleanParameter", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token("int", TokenType.KEYWORD),
                $.token("intParameter", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token("Dog", TokenType.IDENTIFIER),
                $.token("objectParameter", TokenType.IDENTIFIER),
                $.token(")", TokenType.SYMBOL),
                $.token("{", TokenType.SYMBOL),
                $.token("var", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token("y", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("var", TokenType.KEYWORD),
                $.token("Dog", TokenType.IDENTIFIER),
                $.token("dog", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("let", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token("=", TokenType.SYMBOL),
                $.token("5", TokenType.INT_CONST),
                $.token(";", TokenType.SYMBOL),
                $.token("return", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("}", TokenType.SYMBOL)
        );
    }

    protected List<Token> classSubroutineDecTokensListWithInvalidSubroutineName() {
        return List.of(
                $.token("method", TokenType.KEYWORD),
                $.token("void", TokenType.KEYWORD),
                $.token("boolean", TokenType.KEYWORD),
                $.token("(", TokenType.SYMBOL),
                $.token(")", TokenType.SYMBOL)
        );
    }

    protected List<Token> classSubroutineDecTokensListWithInvalidSubroutineType() {
        return List.of(
                $.token("method", TokenType.KEYWORD),
                $.token("$", TokenType.SYMBOL),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token(")", TokenType.SYMBOL)
        );
    }

    protected List<Token> classSubroutineDecTokensListWithInvalidParameterType() {
        return List.of(
                $.token("method", TokenType.KEYWORD),
                $.token("void", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("$", TokenType.SYMBOL),
                $.token("flag", TokenType.IDENTIFIER),
                $.token(",", TokenType.SYMBOL),
                $.token(")", TokenType.SYMBOL)
        );
    }

    protected List<Token> classSubroutineDecTokensListWithInvalidParameterName() {
        return List.of(
                $.token("method", TokenType.KEYWORD),
                $.token("void", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("boolean", TokenType.KEYWORD),
                $.token("$", TokenType.SYMBOL),
                $.token(",", TokenType.SYMBOL),
                $.token(")", TokenType.SYMBOL)
        );
    }

    protected List<Token> classSubroutineDecTokensListWithoutBrackets() {
        return List.of(
                $.token("method", TokenType.KEYWORD),
                $.token("void", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER)
        );
    }

    protected List<Token> classSubroutineDecTokensListWithMissingSubroutineBodyVarDecKeyword() {
        return List.of(
                $.token("method", TokenType.KEYWORD),
                $.token("void", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("boolean", TokenType.KEYWORD),
                $.token("booleanParameter", TokenType.IDENTIFIER),
                $.token(")", TokenType.SYMBOL),
                $.token("{", TokenType.SYMBOL),
                $.token("field", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("return", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("}", TokenType.SYMBOL)
        );
    }

    protected List<Token> classSubroutineDecTokensListWithMissingSubroutineBodyVarDecCommas() {
        return List.of(
                $.token("method", TokenType.KEYWORD),
                $.token("void", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("boolean", TokenType.KEYWORD),
                $.token("booleanParameter", TokenType.IDENTIFIER),
                $.token(")", TokenType.SYMBOL),
                $.token("{", TokenType.SYMBOL),
                $.token("var", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token("y", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("return", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("}", TokenType.SYMBOL)
        );
    }

    protected List<Token> classSubroutineDecTokensListWithInvalidSubroutineBodyVarDecType() {
        return List.of(
                $.token("method", TokenType.KEYWORD),
                $.token("void", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("boolean", TokenType.KEYWORD),
                $.token("booleanParameter", TokenType.IDENTIFIER),
                $.token(")", TokenType.SYMBOL),
                $.token("{", TokenType.SYMBOL),
                $.token("var", TokenType.KEYWORD),
                $.token("$", TokenType.SYMBOL),
                $.token("x", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("return", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("}", TokenType.SYMBOL)
        );
    }

    protected List<Token> classSubroutineDecTokensListWithInvalidSubroutineBodyVarDecName() {
        return List.of(
                $.token("method", TokenType.KEYWORD),
                $.token("void", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("boolean", TokenType.KEYWORD),
                $.token("booleanParameter", TokenType.IDENTIFIER),
                $.token(")", TokenType.SYMBOL),
                $.token("{", TokenType.SYMBOL),
                $.token("var", TokenType.KEYWORD),
                $.token("int", TokenType.KEYWORD),
                $.token("$", TokenType.SYMBOL),
                $.token(";", TokenType.SYMBOL),
                $.token("return", TokenType.KEYWORD),
                $.token("x", TokenType.IDENTIFIER),
                $.token(";", TokenType.SYMBOL),
                $.token("}", TokenType.SYMBOL)
        );
    }

    protected List<Token> classSubroutineDecTokensListWithoutSubroutineBodyBrackets() {
        return List.of(
                $.token("method", TokenType.KEYWORD),
                $.token("void", TokenType.KEYWORD),
                $.token("doStuff", TokenType.IDENTIFIER),
                $.token("(", TokenType.SYMBOL),
                $.token("boolean", TokenType.KEYWORD),
                $.token("booleanParameter", TokenType.IDENTIFIER),
                $.token(")", TokenType.SYMBOL)
        );
    }
}
