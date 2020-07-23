package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.constructs.Class;
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

    private final CompilationEngine engine = new CompilationEngine();

    @Test
    public void shouldParseClassConstructSuccessfully() {
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
                new Class(newClassToken, List.empty(), List.empty()));
    }

    @ParameterizedTest()
    @MethodSource("provideInvalidClassIdentifiers")
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
    @MethodSource("provideMissingBracketSets")
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

    private static Stream<Arguments> provideInvalidClassIdentifiers() {
        return Stream.of(
                Arguments.of(new Token("NewClass", TokenType.STRING_CONST)),
                Arguments.of(new Token("void", TokenType.KEYWORD)),
                Arguments.of(new Token("53", TokenType.INT_CONST)),
                Arguments.of(new Token("{", TokenType.SYMBOL)));
    }

    private static Stream<Arguments> provideMissingBracketSets() {
        return Stream.of(
                Arguments.of(new Token("{", TokenType.SYMBOL), new Token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_A_CLOSING_BRACKET),
                Arguments.of(new Token("x", TokenType.UNKNOWN), new Token("}", TokenType.SYMBOL),
                        CompilationException.NOT_AN_OPENING_BRACKET),
                Arguments.of(new Token("y", TokenType.UNKNOWN), new Token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_AN_OPENING_BRACKET));
    }
}
