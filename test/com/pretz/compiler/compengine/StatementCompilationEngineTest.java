package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.expression.Expression;
import com.pretz.compiler.compengine.expression.Op;
import com.pretz.compiler.compengine.expression.OpTerm;
import com.pretz.compiler.compengine.expression.Term;
import com.pretz.compiler.compengine.statement.ReturnStatement;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.terminal.TerminalType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.tokenizer.token.Tokens;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class StatementCompilationEngineTest {

    private final StatementCompilationEngine engine = new StatementCompilationEngine(new CompilationMatcher());

    @Test
    public void shouldCompileEmptyReturnStatement() {
        Tokens tokens = new Tokens(List.of(new Token("return", TokenType.KEYWORD),
                new Token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new ReturnStatement(null));
    }

    @Test
    public void shouldCompileReturnStatementWithIntegerConstant() {
        Tokens tokens = new Tokens(List.of(
                new Token("return", TokenType.KEYWORD),
                new Token("5", TokenType.INT_CONST),
                new Token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new ReturnStatement(
                        new Expression(new Term(new Terminal("5", TerminalType.INT_CONST)), List.empty()))
        );
    }

    @Test
    public void shouldCompileReturnStatementWithStringConstant() {
        Tokens tokens = new Tokens(List.of(
                new Token("return", TokenType.KEYWORD),
                new Token("Foo", TokenType.STRING_CONST),
                new Token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new ReturnStatement(
                        new Expression(new Term(new Terminal("Foo", TerminalType.STRING_CONST)), List.empty()))
        );
    }

    @ParameterizedTest
    @MethodSource("keywordConstants")
    public void shouldCompileReturnStatementWithKeywordConstant(String keyword) {
        Tokens tokens = new Tokens(List.of(
                new Token("return", TokenType.KEYWORD),
                new Token(keyword, TokenType.KEYWORD),
                new Token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new ReturnStatement(
                        new Expression(new Term(new Terminal(keyword, TerminalType.KEYWORD)), List.empty()))
        );
    }

    @Test
    public void shouldCompileReturnStatementWithVarName() {
        Tokens tokens = new Tokens(List.of(
                new Token("return", TokenType.KEYWORD),
                new Token("duck", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new ReturnStatement(
                        new Expression(new Term(new Terminal("duck", TerminalType.IDENTIFIER)), List.empty()))
        );
    }

    @Test
    public void shouldCompileReturnStatementWithOperators() {
        Tokens tokens = new Tokens(List.of(
                new Token("return", TokenType.KEYWORD),
                new Token("-", TokenType.SYMBOL),
                new Token("duck", TokenType.IDENTIFIER),
                new Token("+", TokenType.SYMBOL),
                new Token("dog", TokenType.IDENTIFIER),
                new Token("+", TokenType.SYMBOL),
                new Token("3", TokenType.INT_CONST),
                new Token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new ReturnStatement(
                        new Expression(new Term(
                                new Terminal("-", TerminalType.SYMBOL),
                                new Terminal("duck", TerminalType.IDENTIFIER)),
                                List.of(new OpTerm(
                                                new Op(new Terminal("+", TerminalType.SYMBOL)),
                                                new Term(new Terminal("dog", TerminalType.IDENTIFIER))),
                                        new OpTerm(
                                                new Op(new Terminal("+", TerminalType.SYMBOL)),
                                                new Term(new Terminal("3", TerminalType.INT_CONST)))
                                )))
        );
    }

    private static Stream<Arguments> keywordConstants() {
        return Stream.of(Arguments.of("true"),
                Arguments.of("false"),
                Arguments.of("this"),
                Arguments.of("null")
        );
    }
}
