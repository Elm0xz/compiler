package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.expression.Expression;
import com.pretz.compiler.compengine.expression.Op;
import com.pretz.compiler.compengine.expression.OpTerm;
import com.pretz.compiler.compengine.expression.Term;
import com.pretz.compiler.compengine.expression.TermType;
import com.pretz.compiler.compengine.statement.DoStatement;
import com.pretz.compiler.compengine.statement.ReturnStatement;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.terminal.TerminalType;
import com.pretz.compiler.compengine.validator.ValidatorFactory;
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

    private final StatementCompilationEngine engine = new StatementCompilationEngine(new CompilationMatcher(), new ValidatorFactory());

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
                        new Expression(new Term(TermType.CONSTANT, new Terminal("5", TerminalType.INT_CONST)), List.empty()))
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
                        new Expression(new Term(TermType.CONSTANT, new Terminal("Foo", TerminalType.STRING_CONST)), List.empty()))
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
                        new Expression(new Term(TermType.CONSTANT, new Terminal(keyword, TerminalType.KEYWORD)), List.empty()))
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
                        new Expression(new Term(TermType.CONSTANT, new Terminal("duck", TerminalType.IDENTIFIER)), List.empty()))
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
                                TermType.UNARY_OP, new Terminal("-", TerminalType.SYMBOL),
                                new Term(TermType.VAR, new Terminal("duck", TerminalType.IDENTIFIER))),
                                List.of(new OpTerm(
                                                new Op(new Terminal("+", TerminalType.SYMBOL)),
                                                new Term(TermType.VAR, new Terminal("dog", TerminalType.IDENTIFIER))),
                                        new OpTerm(
                                                new Op(new Terminal("+", TerminalType.SYMBOL)),
                                                new Term(TermType.CONSTANT, new Terminal("3", TerminalType.INT_CONST)))
                                )))
        );
    }

    @ParameterizedTest
    @MethodSource("arrayIndexTokens")
    public void shouldCompileReturnStatementWithArray(List<Token> arrayIndexTokens, Expression arrayIndexExpression) {
        Tokens tokens = new Tokens(List.of(
                new Token("return", TokenType.KEYWORD),
                new Token("duck", TokenType.IDENTIFIER),
                new Token("[", TokenType.SYMBOL))
                .appendAll(arrayIndexTokens)
                .appendAll(List.of(
                        new Token("]", TokenType.SYMBOL),
                        new Token(";", TokenType.SYMBOL))));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new ReturnStatement(
                        new Expression(new Term(TermType.VAR_ARRAY, new Terminal("duck", TerminalType.IDENTIFIER),
                                arrayIndexExpression), List.empty())
                ));
    }

    @Test
    public void shouldCompileReturnStatementWithAdditionalBrackets() {
        Tokens tokens = new Tokens(List.of(
                new Token("return", TokenType.KEYWORD),
                new Token("(", TokenType.SYMBOL),
                new Token("x", TokenType.IDENTIFIER),
                new Token("*", TokenType.SYMBOL),
                new Token("3", TokenType.INT_CONST),
                new Token(")", TokenType.SYMBOL),
                new Token("+", TokenType.SYMBOL),
                new Token("y", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new ReturnStatement(
                        new Expression(
                                new Term(
                                        TermType.EXPRESSION_IN_BRACKETS, new Expression(
                                                new Term(TermType.VAR, new Terminal("x", TerminalType.IDENTIFIER)),
                                                List.of(new OpTerm(
                                                        new Op(new Terminal("*", TerminalType.SYMBOL)),
                                                        new Term(TermType.CONSTANT, new Terminal("3", TerminalType.INT_CONST)))
                                                ))),
                                List.of(new OpTerm(
                                        new Op(new Terminal("+", TerminalType.SYMBOL)),
                                        new Term(TermType.VAR, new Terminal("y", TerminalType.IDENTIFIER)))
                                )))
        );
    }

    @Test
    public void shouldCompileReturnStatementWithSubroutineCall() {
        Tokens tokens = new Tokens(List.of(
                new Token("return", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("5", TokenType.INT_CONST),
                new Token(")", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new ReturnStatement(
                        new Expression(new Term(
                                TermType.SUBROUTINE_CALL, new Terminal("doStuff", TerminalType.IDENTIFIER),
                                new Expression(
                                        new Term(TermType.CONSTANT, new Terminal("5", TerminalType.INT_CONST)),
                                        List.empty())),
                                List.empty()
                        )));
    }

    @Test
    public void shouldCompileReturnStatementWithClassSubroutineCall() {
        Tokens tokens = new Tokens(List.of(
                new Token("return", TokenType.KEYWORD),
                new Token("stuff", TokenType.IDENTIFIER),
                new Token(".", TokenType.SYMBOL),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("5", TokenType.INT_CONST),
                new Token(")", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new ReturnStatement(
                        new Expression(new Term(
                                TermType.SUBROUTINE_CALL, new Terminal("stuff", TerminalType.IDENTIFIER),
                                new Terminal("doStuff", TerminalType.IDENTIFIER),
                                new Expression(
                                        new Term(TermType.CONSTANT, new Terminal("5", TerminalType.INT_CONST)),
                                        List.empty())),
                                List.empty()
                        )));
    }

    @Test
    public void shouldCompileDoStatement() {
        Tokens tokens = new Tokens(List.of(
                new Token("do", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("5", TokenType.INT_CONST),
                new Token(")", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new DoStatement(
                        new Term(
                                TermType.SUBROUTINE_CALL, new Terminal("doStuff", TerminalType.IDENTIFIER),
                                new Expression(
                                        new Term(TermType.CONSTANT, new Terminal("5", TerminalType.INT_CONST)),
                                        List.empty()))
                ));
    }

    @Test
    public void shouldCompileDoStatementWithClassSubroutineCall() {
        Tokens tokens = new Tokens(List.of(
                new Token("do", TokenType.KEYWORD),
                new Token("stuff", TokenType.IDENTIFIER),
                new Token(".", TokenType.SYMBOL),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("5", TokenType.INT_CONST),
                new Token(")", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo(
                new DoStatement(
                        new Term(
                                TermType.SUBROUTINE_CALL, new Terminal("stuff", TerminalType.IDENTIFIER),
                                new Terminal("doStuff", TerminalType.IDENTIFIER),
                                new Expression(
                                        new Term(TermType.CONSTANT, new Terminal("5", TerminalType.INT_CONST)),
                                        List.empty()))
                ));
    }

    @Test
    public void shouldThrowOnReturnStatementWithInvalidExpression() {

    }

    private static Stream<Arguments> keywordConstants() {
        return Stream.of(Arguments.of("true"),
                Arguments.of("false"),
                Arguments.of("this"),
                Arguments.of("null")
        );
    }

    private static Stream<Arguments> arrayIndexTokens() {
        return Stream.of(
                Arguments.of(
                        List.of(new Token("x", TokenType.IDENTIFIER)),
                        new Expression(new Term(TermType.VAR, new Terminal("x", TerminalType.IDENTIFIER)), List.empty())),
                Arguments.of(
                        List.of(new Token("5", TokenType.INT_CONST)),
                        new Expression(new Term(TermType.CONSTANT, new Terminal("5", TerminalType.INT_CONST)), List.empty())),
                Arguments.of(
                        List.of(new Token("Foo", TokenType.STRING_CONST)),
                        new Expression(new Term(TermType.CONSTANT, new Terminal("Foo", TerminalType.STRING_CONST)), List.empty())),
                Arguments.of(
                        List.of(new Token("-", TokenType.SYMBOL),
                                new Token("a", TokenType.IDENTIFIER)),
                        new Expression(new Term(
                                TermType.UNARY_OP, new Terminal("-", TerminalType.SYMBOL),
                                new Term(TermType.VAR, new Terminal("a", TerminalType.IDENTIFIER))), List.empty())),
                Arguments.of(
                        List.of(new Token("y", TokenType.IDENTIFIER),
                                new Token("[", TokenType.SYMBOL),
                                new Token("x", TokenType.IDENTIFIER),
                                new Token("]", TokenType.SYMBOL)),
                        new Expression(new Term(
                                TermType.VAR_ARRAY, new Terminal("y", TerminalType.IDENTIFIER),
                                new Expression(new Term(
                                        TermType.VAR, new Terminal("x", TerminalType.IDENTIFIER)), List.empty())), List.empty()))
        );
    }
}
