package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.validator.ValidatorFactory;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.tokenizer.token.Tokens;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.pretz.compiler.compengine.CompilationException.NOT_A_CLASS;

//TODO(L) maybe split "integration" (using statement) and unit (basic test cases) tests
public class CompilationEngineTest {

    private final ElementTestUtils $_ = new ElementTestUtils();
    private final CompilationEngineTestTokens $$ = new CompilationEngineTestTokens();
    private final CompilationEngineTestConstructs $ = new CompilationEngineTestConstructs();

    private final CompilationEngine engine = new CompilationEngine(new ValidatorFactory(),
            new StatementCompilationEngine(new CompilationMatcher(), new ValidatorFactory()),
            new CompilationMatcher());

    @Test
    public void shouldCompileClass() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classTokensList());

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                $_.classC(newClassToken, $.classConstructs()));
    }

    @Test
    public void shouldThrowOnAbsentClassKeyword() {
        Tokens tokens = new Tokens(List.of($_.token("x", TokenType.UNKNOWN)));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(NOT_A_CLASS);
    }

    @Test
    public void shouldCompileValidClassIdentifier() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = new Tokens(List.of(
                $_.token("class", TokenType.KEYWORD),
                newClassToken,
                $_.token("{", TokenType.SYMBOL),
                $_.token("}", TokenType.SYMBOL)));

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                $_.classC(newClassToken, List.empty()));
    }

    @ParameterizedTest()
    @MethodSource("com.pretz.compiler.compengine.CompilationEngineTestConstructs#invalidClassIdentifiers")
    public void shouldThrowOnInvalidClassIdentifier(Token invalidClassIdentifier) {
        Tokens tokens = new Tokens(List.of(
                $_.token("class", TokenType.KEYWORD),
                invalidClassIdentifier,
                $_.token("{", TokenType.SYMBOL),
                $_.token("}", TokenType.SYMBOL)));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_CLASS_IDENTIFIER);
    }

    @ParameterizedTest
    @MethodSource("com.pretz.compiler.compengine.CompilationEngineTestConstructs#missingClassBracketSets")
    public void shouldThrowOnMissingClassBrackets(Token openingBracket, Token closingBracket, String excMsg) {
        Tokens tokens = new Tokens(List.of(
                $_.token("class", TokenType.KEYWORD),
                $_.token("NewClass", TokenType.IDENTIFIER),
                openingBracket,
                closingBracket));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(excMsg);
    }

    @Test
    public void shouldCompileClassVarDecs() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classVarDecTokensList());

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                $_.classC(newClassToken, $.classVarDecConstructs()));
    }

    @Test
    public void shouldCompileClassVarDecsWithCustomType() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classVarDecTokensListWithCustomType());

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                $_.classC(newClassToken, $.classVarDecConstructsWithCustomType()));
    }

    @Test
    public void shouldThrowOnInvalidVarType() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classVarDecTokensListWithInvalidVarType());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_TYPE);
    }

    @Test
    public void shouldThrowOnInvalidVarName() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classVarDecTokensListWithInvalidVarName());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_VARNAME);
    }

    @Test
    public void shouldThrowOnMissingCommaBetweenVars() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classVarDecTokensListWithMissingComma());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.MISSING_VAR_COMMA);
    }

    @Test
    public void shouldCompileSubroutineDec() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classSubroutineDecTokensList());

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                $_.classC(newClassToken, $.classSubroutineDecConstructs()));
    }

    @Test
    public void shouldThrowOnInvalidSubroutineName() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classSubroutineDecTokensListWithInvalidSubroutineName());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_SUBROUTINE_NAME);
    }

    @Test
    public void shouldThrowOnInvalidSubroutineType() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classSubroutineDecTokensListWithInvalidSubroutineType());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_SUBROUTINE_TYPE);
    }

    @ParameterizedTest
    @MethodSource("com.pretz.compiler.compengine.CompilationEngineTestConstructs#missingSubroutineParametersBracketSets")
    public void shouldThrowOnMissingSubroutineParametersBrackets(Token openingBracket, Token closingBracket, String excMsg) {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classSubroutineDecTokensListWithoutBrackets()
                .append(openingBracket)
                .append($_.token("boolean", TokenType.KEYWORD))
                .append($_.token("flag", TokenType.IDENTIFIER))
                .append(closingBracket));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(excMsg);
    }

    @Test
    public void shouldThrowOnInvalidParameterType() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classSubroutineDecTokensListWithInvalidParameterType());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_TYPE);
    }

    @Test
    public void shouldThrowOnInvalidParameterName() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classSubroutineDecTokensListWithInvalidParameterName());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_VARNAME);
    }

    @Test
    public void shouldThrowOnMissingSubroutineBodyVarDecCommas() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken,
                $$.classSubroutineDecTokensListWithMissingSubroutineBodyVarDecCommas());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.MISSING_VAR_COMMA);
    }

    @Test
    public void shouldThrowOnMissingSubroutineBodyVarKeyword() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken,
                $$.classSubroutineDecTokensListWithMissingSubroutineBodyVarDecKeyword());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_SUBROUTINE_BODY_KEYWORD);
        //TODO(L) this should be checked by statement validator instead as we assume that
        // if beginning token does not have "var" keyword then it's statement (maybe refactor to pattern matching?)
    }

    @ParameterizedTest
    @MethodSource("com.pretz.compiler.compengine.CompilationEngineTestConstructs#missingSubroutineBodyBracketSets")
    public void shouldThrowOnMissingSubroutineBodyBrackets(Token openingBracket, Token closingBracket, String excMsg) {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken, $$.classSubroutineDecTokensListWithoutSubroutineBodyBrackets()
                .append(openingBracket)
                .append($_.token("var", TokenType.KEYWORD))
                .append($_.token("int", TokenType.KEYWORD))
                .append($_.token("x", TokenType.IDENTIFIER))
                .append($_.token(";", TokenType.SYMBOL))
                .append($_.token("return", TokenType.KEYWORD))
                .append($_.token("x", TokenType.IDENTIFIER))
                .append($_.token(";", TokenType.SYMBOL))
                .append(closingBracket));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(excMsg);
    }

    @Test
    public void shouldThrowOnInvalidSubroutineBodyVarDecType() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken,
                $$.classSubroutineDecTokensListWithInvalidSubroutineBodyVarDecType());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_TYPE);
    }

    @Test
    public void shouldThrowOnInvalidSubroutineBodyVarDecName() {
        Token newClassToken = $_.token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = $$.classWithDeclarations(newClassToken,
                $$.classSubroutineDecTokensListWithInvalidSubroutineBodyVarDecName());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_VARNAME);
    }
}
