package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.constructs.Class;
import com.pretz.compiler.compengine.constructs.ClassVarDec;
import com.pretz.compiler.compengine.constructs.Construct;
import com.pretz.compiler.compengine.constructs.Type;
import com.pretz.compiler.compengine.constructs.VarNames;
import com.pretz.compiler.tokenizer.Token;
import com.pretz.compiler.tokenizer.TokenType;
import com.pretz.compiler.tokenizer.Tokens;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.pretz.compiler.compengine.CompilationException.NOT_A_CLASS;

public class CompilationEngineTest {

    private final CompilationEngine engine = new CompilationEngine(new CompilationValidator());

    @Test
    public void shouldCompileClassConstructSuccessfully() {
        Assertions.assertThat(true).isFalse();
    }

    @Test
    public void shouldThrowOnAbsentClassKeyword() {
        Tokens tokens = new Tokens(List.of(new Token("x", TokenType.UNKNOWN)));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(NOT_A_CLASS);
    }

    @Test
    public void shouldAcceptValidClassIdentifier() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = new Tokens(List.of(
                new Token("class", TokenType.KEYWORD),
                newClassToken,
                new Token("{", TokenType.SYMBOL),
                new Token("}", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                new Class(newClassToken, List.empty()));
    }

    @ParameterizedTest()
    @MethodSource("invalidClassIdentifiers")
    public void shouldThrowOnInvalidClassIdentifier(Token invalidClassIdentifier) {
        Tokens tokens = new Tokens(List.of(
                new Token("class", TokenType.KEYWORD),
                invalidClassIdentifier,
                new Token("{", TokenType.SYMBOL),
                new Token("}", TokenType.SYMBOL)));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_CLASS_IDENTIFIER);
    }

    @ParameterizedTest
    @MethodSource("missingBracketSets")
    public void shouldThrowOnMissingClassBrackets(Token openingBracket, Token closingBracket, String excMsg) {
        Tokens tokens = new Tokens(List.of(
                new Token("class", TokenType.KEYWORD),
                new Token("NewClass", TokenType.IDENTIFIER),
                openingBracket,
                closingBracket));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(excMsg);
    }

    @Test
    public void shouldCompileClassVarDecs() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = new Tokens(List.of(
                new Token("class", TokenType.KEYWORD),
                newClassToken,
                new Token("{", TokenType.SYMBOL))
                .appendAll(classVarDecTokensList())
                .append(new Token("}", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                new Class(newClassToken, classVarDecConstructs()));
    }

    @Test
    public void shouldCompileClassVarDecsWithCustomType() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = new Tokens(List.of(
                new Token("class", TokenType.KEYWORD),
                newClassToken,
                new Token("{", TokenType.SYMBOL))
                .appendAll(classVarDecTokensListWithCustomType())
                .append(new Token("}", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                new Class(newClassToken, classVarDecConstructsWithCustomType()));
    }

    @Test
    public void shouldThrowOnInvalidVarType() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = new Tokens(List.of(
                new Token("class", TokenType.KEYWORD),
                newClassToken,
                new Token("{", TokenType.SYMBOL))
                .appendAll(classVarDecTokensListWithInvalidVarType())
                .append(new Token("}", TokenType.SYMBOL)));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_TYPE);
    }

    @Test
    public void shouldThrowOnMissingCommaBetweenVars() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = new Tokens(List.of(
                new Token("class", TokenType.KEYWORD),
                newClassToken,
                new Token("{", TokenType.SYMBOL))
                .appendAll(classVarDecTokensListWithMissingComma())
                .append(new Token("}", TokenType.SYMBOL)));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.MISSING_COMMA);
    }

    private List<Token> classVarDecTokensList() {
        return List.of(
                new Token("static", TokenType.KEYWORD),
                new Token("int", TokenType.KEYWORD),
                new Token("testInt", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("field", TokenType.KEYWORD),
                new Token("boolean", TokenType.KEYWORD),
                new Token("testBool1", TokenType.IDENTIFIER),
                new Token(",", TokenType.SYMBOL),
                new Token("testBool2", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL)
        );
    }

    private List<Construct> classVarDecConstructs() {
        return List.of(
                new ClassVarDec(new Token("static", TokenType.KEYWORD),
                        new Type(new Token("int", TokenType.KEYWORD)),
                        new VarNames(List.of(new Token("testInt", TokenType.IDENTIFIER)))
                ),
                new ClassVarDec(new Token("field", TokenType.KEYWORD),
                        new Type(new Token("boolean", TokenType.KEYWORD)),
                        new VarNames(List.of(
                                new Token("testBool1", TokenType.IDENTIFIER),
                                new Token("testBool2", TokenType.IDENTIFIER)))
                )
        );
    }

    private List<Construct> classVarDecConstructsWithCustomType() {
        return List.of(
                new ClassVarDec(new Token("static", TokenType.KEYWORD),
                        new Type(new Token("Dog", TokenType.IDENTIFIER)),
                        new VarNames(List.of(new Token("testDog", TokenType.IDENTIFIER)))
                ),
                new ClassVarDec(new Token("field", TokenType.KEYWORD),
                        new Type(new Token("boolean", TokenType.KEYWORD)),
                        new VarNames(List.of(
                                new Token("testBool1", TokenType.IDENTIFIER),
                                new Token("testBool2", TokenType.IDENTIFIER)))
                )
        );
    }

    private List<Token> classVarDecTokensListWithCustomType() {
        return List.of(
                new Token("static", TokenType.KEYWORD),
                new Token("Dog", TokenType.IDENTIFIER),
                new Token("testDog", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("field", TokenType.KEYWORD),
                new Token("boolean", TokenType.KEYWORD),
                new Token("testBool1", TokenType.IDENTIFIER),
                new Token(",", TokenType.SYMBOL),
                new Token("testBool2", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL)
        );
    }

    private List<Token> classVarDecTokensListWithInvalidVarType() {
        return List.of(
                new Token("static", TokenType.KEYWORD),
                new Token("int", TokenType.KEYWORD),
                new Token("testInt", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("field", TokenType.KEYWORD),
                new Token("X", TokenType.STRING_CONST),
                new Token("testBool1", TokenType.IDENTIFIER),
                new Token(",", TokenType.SYMBOL),
                new Token("testBool2", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL)
        );
    }

    private List<Token> classVarDecTokensListWithMissingComma() {
        return List.of(
                new Token("static", TokenType.KEYWORD),
                new Token("int", TokenType.KEYWORD),
                new Token("testInt", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("field", TokenType.KEYWORD),
                new Token("boolean", TokenType.KEYWORD),
                new Token("testBool1", TokenType.IDENTIFIER),
                new Token("testBool2", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL)
        );
    }

    private static Stream<Arguments> invalidClassIdentifiers() {
        return Stream.of(
                Arguments.of(new Token("NewClass", TokenType.STRING_CONST)),
                Arguments.of(new Token("void", TokenType.KEYWORD)),
                Arguments.of(new Token("53", TokenType.INT_CONST)),
                Arguments.of(new Token("{", TokenType.SYMBOL)));
    }

    private static Stream<Arguments> missingBracketSets() {
        return Stream.of(
                Arguments.of(new Token("{", TokenType.SYMBOL), new Token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_A_CLOSING_BRACKET),
                Arguments.of(new Token("x", TokenType.UNKNOWN), new Token("}", TokenType.SYMBOL),
                        CompilationException.NOT_AN_OPENING_BRACKET),
                Arguments.of(new Token("y", TokenType.UNKNOWN), new Token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_AN_OPENING_BRACKET));
    }
}
