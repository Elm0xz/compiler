package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.expression.Expression;
import com.pretz.compiler.compengine.validator.ValidatorFactory;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.tokenizer.token.Tokens;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class StatementCompilationEngineTest {

    private final ElementTestUtils $_ = new ElementTestUtils();
    private final StatementCompilationEngineTestTokens $$ = new StatementCompilationEngineTestTokens();
    private final StatementCompilationEngineTestConstructs $ = new StatementCompilationEngineTestConstructs();

    private final StatementCompilationEngine engine = new StatementCompilationEngine(new CompilationMatcher(), new ValidatorFactory());

    @Test
    public void shouldCompileEmptyReturnStatement() {
        Tokens tokens = new Tokens(List.of(
                $_.token("return", TokenType.KEYWORD),
                $_.token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.emptyReturnStatement());
    }

    @Test
    public void shouldCompileReturnStatementWithIntegerConstant() {
        Tokens tokens = new Tokens(List.of(
                $_.token("return", TokenType.KEYWORD),
                $_.token("5", TokenType.INT_CONST),
                $_.token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.returnStatementWithIntegerConstant());
    }

    @Test
    public void shouldCompileReturnStatementWithStringConstant() {
        Tokens tokens = new Tokens(List.of(
                $_.token("return", TokenType.KEYWORD),
                $_.token("Foo", TokenType.STRING_CONST),
                $_.token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.returnStatementWithStringConstant());
    }

    @ParameterizedTest
    @MethodSource("com.pretz.compiler.compengine.StatementCompilationEngineTestTokens#keywordConstants")
    public void shouldCompileReturnStatementWithKeywordConstant(String keyword) {
        Tokens tokens = new Tokens(List.of(
                $_.token("return", TokenType.KEYWORD),
                $_.token(keyword, TokenType.KEYWORD),
                $_.token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.returnStatementWithKeywordConstant(keyword));
    }

    @Test
    public void shouldCompileReturnStatementWithVarName() {
        Tokens tokens = new Tokens(List.of(
                $_.token("return", TokenType.KEYWORD),
                $_.token("duck", TokenType.IDENTIFIER),
                $_.token(";", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.returnStatementWithVarName());
    }

    @Test
    public void shouldCompileReturnStatementWithOperators() {
        Tokens tokens = $$.returnStatementWithOperatorsTokens();

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.returnStatementWithOperators());
    }

    @ParameterizedTest
    @MethodSource("com.pretz.compiler.compengine.StatementCompilationEngineTestConstructs#arrayIndexTokensExpressions")
    public void shouldCompileReturnStatementWithArray(List<Token> arrayIndexTokens, Expression arrayIndexExpression) {
        Tokens tokens = new Tokens(List.of(
                $_.token("return", TokenType.KEYWORD),
                $_.token("duck", TokenType.IDENTIFIER),
                $_.token("[", TokenType.SYMBOL))
                .appendAll(arrayIndexTokens)
                .appendAll(List.of(
                        $_.token("]", TokenType.SYMBOL),
                        $_.token(";", TokenType.SYMBOL))));

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.returnStatementWithArray(arrayIndexExpression));
    }

    @Test
    public void shouldCompileReturnStatementWithAdditionalBrackets() {
        Tokens tokens = $$.returnStatementWithAdditionalBracketsTokens();

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.returnStatementWithAdditionalBrackets());
    }

    @Test
    public void shouldCompileReturnStatementWithSubroutineCall() {
        Tokens tokens = $$.returnStatementWithSubroutineCallTokens();

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.returnStatementWithSubroutineCall());
    }

    @Test
    public void shouldCompileReturnStatementWithClassSubroutineCall() {
        Tokens tokens = $$.returnStatementWithClassSubroutineCallTokens();

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.returnStatementWithClassSubroutineCall());
    }

    //TODO(L) not done, I don't know why
    @Test
    public void shouldThrowOnReturnStatementWithInvalidExpression() {

    }

    @Test
    public void shouldCompileDoStatement() {
        Tokens tokens = $$.doStatementTokens();

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.doStatementSimple());
    }

    @Test
    public void shouldCompileDoStatementWithClassSubroutineCall() {
        Tokens tokens = $$.doStatementWithClassSubroutineCallTokens();

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.doStatementWithClassSubroutineCall());
    }

    @Test
    public void shouldCompileWhileStatement() {
        Tokens tokens = $$.whileStatementTokens();

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.whileStatement());
    }

    @Test
    public void shouldCompileIfStatement() {
        Tokens tokens = $$.ifStatementTokens();

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.ifStatement());
    }

    @Test
    public void shouldCompileLetStatement() {
        Tokens tokens = $$.letStatementTokens();

        Assertions.assertThat(engine.compileStatement(tokens)).isEqualTo($.letStatement());
    }
}
