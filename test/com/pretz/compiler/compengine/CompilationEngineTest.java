package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.constructs.Class;
import com.pretz.compiler.compengine.constructs.ClassVarDec;
import com.pretz.compiler.compengine.constructs.Construct;
import com.pretz.compiler.compengine.constructs.Parameter;
import com.pretz.compiler.compengine.constructs.ParameterList;
import com.pretz.compiler.compengine.constructs.Statement;
import com.pretz.compiler.compengine.constructs.SubroutineBody;
import com.pretz.compiler.compengine.constructs.SubroutineDec;
import com.pretz.compiler.compengine.constructs.Type;
import com.pretz.compiler.compengine.constructs.VarDec;
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
    public void shouldCompileClass() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classTokensList());

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                new Class(newClassToken, classConstructs()));
    }

    @Test
    public void shouldThrowOnAbsentClassKeyword() {
        Tokens tokens = new Tokens(List.of(new Token("x", TokenType.UNKNOWN)));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(NOT_A_CLASS);
    }

    @Test
    public void shouldCompileValidClassIdentifier() {
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
    @MethodSource("missingClassBracketSets")
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
        Tokens tokens = classWithDeclarations(newClassToken, classVarDecTokensList());

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                new Class(newClassToken, classVarDecConstructs()));
    }

    @Test
    public void shouldCompileClassVarDecsWithCustomType() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classVarDecTokensListWithCustomType());

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                new Class(newClassToken, classVarDecConstructsWithCustomType()));
    }

    @Test
    public void shouldThrowOnInvalidVarType() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classVarDecTokensListWithInvalidVarType());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_TYPE);
    }

    @Test
    public void shouldThrowOnInvalidVarName() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classVarDecTokensListWithInvalidVarName());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_VARNAME);
    }

    @Test
    public void shouldThrowOnMissingCommaBetweenVars() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classVarDecTokensListWithMissingComma());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.MISSING_VAR_COMMA);
    }

    @Test
    public void shouldCompileSubroutineDec() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classSubroutineDecTokensList());

        Assertions.assertThat(engine.compileClass(tokens)).isEqualTo(
                new Class(newClassToken, classSubroutineDecConstructs()));
    }

    @Test
    public void shouldThrowOnInvalidSubroutineName() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classSubroutineDecTokensListWithInvalidSubroutineName());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_SUBROUTINE_NAME);
    }

    @ParameterizedTest
    @MethodSource("missingSubroutineParametersBracketSets")
    public void shouldThrowOnMissingSubroutineParametersBrackets(Token openingBracket, Token closingBracket, String excMsg) {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classSubroutineDecTokensListWithoutBrackets()
                .append(openingBracket)
                .append(new Token("boolean", TokenType.KEYWORD))
                .append(new Token("flag", TokenType.IDENTIFIER))
                .append(closingBracket));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(excMsg);
    }

    @Test
    public void shouldThrowOnInvalidParameterType() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classSubroutineDecTokensListWithInvalidParameterType());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_TYPE);
    }

    @Test
    public void shouldThrowOnInvalidParameterName() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classSubroutineDecTokensListWithInvalidParameterName());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_VARNAME);
    }

    //TODO tests: 1. missing subroutinebody bracket, 3. missing var

    @Test
    public void shouldThrowOnMissingSubroutineBodyVarDecCommas() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken,
                classSubroutineDecTokensListWithMissingSubroutineBodyVarDecCommas());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.MISSING_VAR_COMMA);
    }

    @Test
    public void shouldThrowOnMissingSubroutineBodyVarKeyword() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken,
                classSubroutineDecTokensListWithMissingSubroutineBodyVarDecKeyword());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_SUBROUTINE_BODY);
        //TODO this should be checked by statement validator instead as we assume that
        // if beginning token does not have "var" keyword then it's statement (maybe refactor to pattern matching?)
    }

    @ParameterizedTest
    @MethodSource("missingSubroutineBodyBracketSets")
    public void shouldThrowOnMissingSubroutineBodyBrackets(Token openingBracket, Token closingBracket, String excMsg) {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken, classSubroutineDecTokensListWithoutSubroutineBodyBrackets()
                .append(openingBracket)
                .append(new Token("var", TokenType.KEYWORD))
                .append(new Token("int", TokenType.KEYWORD))
                .append(new Token("x", TokenType.IDENTIFIER))
                .append(new Token(";", TokenType.SYMBOL))
                .append(new Token("return", TokenType.KEYWORD))
                .append(new Token("x", TokenType.IDENTIFIER))
                .append(new Token(";", TokenType.SYMBOL))
                .append(closingBracket));

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(excMsg);
    }

    @Test
    public void shouldThrowOnInvalidSubroutineBodyVarDecType() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken,
                classSubroutineDecTokensListWithInvalidSubroutineBodyVarDecType());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_TYPE);
    }

    //TODO test with both var dec and subroutine dec
    @Test
    public void shouldThrowOnInvalidSubroutineBodyVarDecName() {
        Token newClassToken = new Token("NewClass", TokenType.IDENTIFIER);
        Tokens tokens = classWithDeclarations(newClassToken,
                classSubroutineDecTokensListWithInvalidSubroutineBodyVarDecName());

        Assertions.assertThatThrownBy(() -> engine.compileClass(tokens))
                .isInstanceOf(CompilationException.class)
                .hasMessage(CompilationException.INVALID_VARNAME);
    }

    private List<Token> classTokensList() {
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
                new Token(";", TokenType.SYMBOL),
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("boolean", TokenType.KEYWORD),
                new Token("booleanParameter", TokenType.IDENTIFIER),
                new Token(",", TokenType.SYMBOL),
                new Token("int", TokenType.KEYWORD),
                new Token("intParameter", TokenType.IDENTIFIER),
                new Token(",", TokenType.SYMBOL),
                new Token("Dog", TokenType.IDENTIFIER),
                new Token("objectParameter", TokenType.IDENTIFIER),
                new Token(")", TokenType.SYMBOL),
                new Token("{", TokenType.SYMBOL),
                new Token("var", TokenType.KEYWORD),
                new Token("int", TokenType.KEYWORD),
                new Token("x", TokenType.IDENTIFIER),
                new Token(",", TokenType.SYMBOL),
                new Token("y", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("var", TokenType.KEYWORD),
                new Token("Dog", TokenType.IDENTIFIER),
                new Token("dog", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("return", TokenType.KEYWORD),
                new Token("x", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("}", TokenType.SYMBOL)
        );
    }

    private List<Construct> classConstructs() {
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
                ),
                new SubroutineDec(new Token("method", TokenType.KEYWORD),
                        new Type(new Token("void", TokenType.KEYWORD)),
                        new Token("doStuff", TokenType.IDENTIFIER),
                        new ParameterList(subroutineParameterList()), subroutineBody())
        );
    }

    private Tokens classWithDeclarations(Token className, List<Token> declarationTokens) {
        return new Tokens(List.of(
                new Token("class", TokenType.KEYWORD),
                className,
                new Token("{", TokenType.SYMBOL))
                .appendAll(declarationTokens)
                .append(new Token("}", TokenType.SYMBOL)));
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

    private List<Token> classVarDecTokensListWithInvalidVarName() {
        return List.of(
                new Token("static", TokenType.KEYWORD),
                new Token("int", TokenType.KEYWORD),
                new Token("testInt", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("field", TokenType.KEYWORD),
                new Token("boolean", TokenType.KEYWORD),
                new Token("(", TokenType.SYMBOL),
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

    private List<Token> classSubroutineDecTokensList() {
        return List.of(
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("boolean", TokenType.KEYWORD),
                new Token("booleanParameter", TokenType.IDENTIFIER),
                new Token(",", TokenType.SYMBOL),
                new Token("int", TokenType.KEYWORD),
                new Token("intParameter", TokenType.IDENTIFIER),
                new Token(",", TokenType.SYMBOL),
                new Token("Dog", TokenType.IDENTIFIER),
                new Token("objectParameter", TokenType.IDENTIFIER),
                new Token(")", TokenType.SYMBOL),
                new Token("{", TokenType.SYMBOL),
                new Token("var", TokenType.KEYWORD),
                new Token("int", TokenType.KEYWORD),
                new Token("x", TokenType.IDENTIFIER),
                new Token(",", TokenType.SYMBOL),
                new Token("y", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("var", TokenType.KEYWORD),
                new Token("Dog", TokenType.IDENTIFIER),
                new Token("dog", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("return", TokenType.KEYWORD),
                new Token("x", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("}", TokenType.SYMBOL)
        );
    }

    private List<Token> classSubroutineDecTokensListWithInvalidSubroutineName() {
        return List.of(
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("boolean", TokenType.KEYWORD),
                new Token("(", TokenType.SYMBOL),
                new Token(")", TokenType.SYMBOL)
        );
    }

    private List<Token> classSubroutineDecTokensListWithInvalidParameterType() {
        return List.of(
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("$", TokenType.SYMBOL),
                new Token("flag", TokenType.IDENTIFIER),
                new Token(",", TokenType.SYMBOL),
                new Token(")", TokenType.SYMBOL)
        );
    }

    private List<Token> classSubroutineDecTokensListWithInvalidParameterName() {
        return List.of(
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("boolean", TokenType.KEYWORD),
                new Token("$", TokenType.SYMBOL),
                new Token(",", TokenType.SYMBOL),
                new Token(")", TokenType.SYMBOL)
        );
    }

    private List<Token> classSubroutineDecTokensListWithoutBrackets() {
        return List.of(
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER)
        );
    }

    private List<Token> classSubroutineDecTokensListWithMissingSubroutineBodyVarDecKeyword() {
        return List.of(
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("boolean", TokenType.KEYWORD),
                new Token("booleanParameter", TokenType.IDENTIFIER),
                new Token(")", TokenType.SYMBOL),
                new Token("{", TokenType.SYMBOL),
                new Token("field", TokenType.KEYWORD),
                new Token("int", TokenType.KEYWORD),
                new Token("x", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("return", TokenType.KEYWORD),
                new Token("x", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("}", TokenType.SYMBOL)
        );
    }

    private List<Token> classSubroutineDecTokensListWithMissingSubroutineBodyVarDecCommas() {
        return List.of(
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("boolean", TokenType.KEYWORD),
                new Token("booleanParameter", TokenType.IDENTIFIER),
                new Token(")", TokenType.SYMBOL),
                new Token("{", TokenType.SYMBOL),
                new Token("var", TokenType.KEYWORD),
                new Token("int", TokenType.KEYWORD),
                new Token("x", TokenType.IDENTIFIER),
                new Token("y", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("return", TokenType.KEYWORD),
                new Token("x", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("}", TokenType.SYMBOL)
        );
    }

    private List<Token> classSubroutineDecTokensListWithInvalidSubroutineBodyVarDecType() {
        return List.of(
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("boolean", TokenType.KEYWORD),
                new Token("booleanParameter", TokenType.IDENTIFIER),
                new Token(")", TokenType.SYMBOL),
                new Token("{", TokenType.SYMBOL),
                new Token("var", TokenType.KEYWORD),
                new Token("$", TokenType.SYMBOL),
                new Token("x", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("return", TokenType.KEYWORD),
                new Token("x", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("}", TokenType.SYMBOL)
        );
    }

    private List<Token> classSubroutineDecTokensListWithInvalidSubroutineBodyVarDecName() {
        return List.of(
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("boolean", TokenType.KEYWORD),
                new Token("booleanParameter", TokenType.IDENTIFIER),
                new Token(")", TokenType.SYMBOL),
                new Token("{", TokenType.SYMBOL),
                new Token("var", TokenType.KEYWORD),
                new Token("int", TokenType.KEYWORD),
                new Token("$", TokenType.SYMBOL),
                new Token(";", TokenType.SYMBOL),
                new Token("return", TokenType.KEYWORD),
                new Token("x", TokenType.IDENTIFIER),
                new Token(";", TokenType.SYMBOL),
                new Token("}", TokenType.SYMBOL)
        );
    }

    private List<Token> classSubroutineDecTokensListWithoutSubroutineBodyBrackets() {
        return List.of(
                new Token("method", TokenType.KEYWORD),
                new Token("void", TokenType.KEYWORD),
                new Token("doStuff", TokenType.IDENTIFIER),
                new Token("(", TokenType.SYMBOL),
                new Token("boolean", TokenType.KEYWORD),
                new Token("booleanParameter", TokenType.IDENTIFIER),
                new Token(")", TokenType.SYMBOL)
        );
    }

    private List<Construct> classSubroutineDecConstructs() {
        return List.of(
                new SubroutineDec(new Token("method", TokenType.KEYWORD),
                        new Type(new Token("void", TokenType.KEYWORD)),
                        new Token("doStuff", TokenType.IDENTIFIER),
                        new ParameterList(subroutineParameterList()), subroutineBody())
        );
    }

    private List<Parameter> subroutineParameterList() {
        return List.of(
                new Parameter(new Type(new Token("boolean", TokenType.KEYWORD)),
                        new Token("booleanParameter", TokenType.IDENTIFIER)),
                new Parameter(new Type(new Token("int", TokenType.KEYWORD)),
                        new Token("intParameter", TokenType.IDENTIFIER)),
                new Parameter(new Type(new Token("Dog", TokenType.IDENTIFIER)),
                        new Token("objectParameter", TokenType.IDENTIFIER))
        );
    }

    private SubroutineBody subroutineBody() {
        return new SubroutineBody(
                List.of(
                        new VarDec(
                                new Token("var", TokenType.KEYWORD),
                                new Type(new Token("int", TokenType.KEYWORD)),
                                new VarNames(
                                        List.of(new Token("x", TokenType.IDENTIFIER),
                                                new Token("y", TokenType.IDENTIFIER))
                                )),
                        new VarDec(
                                new Token("var", TokenType.KEYWORD),
                                new Type(new Token("Dog", TokenType.IDENTIFIER)),
                                new VarNames(
                                        List.of(new Token("dog", TokenType.IDENTIFIER))
                                )),
                        new Statement()
                )
        );
    }

    private static Stream<Arguments> invalidClassIdentifiers() {
        return Stream.of(
                Arguments.of(new Token("NewClass", TokenType.STRING_CONST)),
                Arguments.of(new Token("void", TokenType.KEYWORD)),
                Arguments.of(new Token("53", TokenType.INT_CONST)),
                Arguments.of(new Token("{", TokenType.SYMBOL)));
    }

    private static Stream<Arguments> missingClassBracketSets() {
        return Stream.of(
                Arguments.of(new Token("{", TokenType.SYMBOL), new Token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_A_CLOSING_CURLY_BRACKET),
                Arguments.of(new Token("x", TokenType.UNKNOWN), new Token("}", TokenType.SYMBOL),
                        CompilationException.NOT_AN_OPENING_CURLY_BRACKET),
                Arguments.of(new Token("y", TokenType.UNKNOWN), new Token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_AN_OPENING_CURLY_BRACKET));
    }

    private static Stream<Arguments> missingSubroutineParametersBracketSets() {
        return Stream.of(
                Arguments.of(new Token("(", TokenType.SYMBOL), new Token("x", TokenType.UNKNOWN),
                        CompilationException.MISSING_PARAMETER_COMMA_OR_CLOSING_ROUND_BRACKET),
                Arguments.of(new Token("x", TokenType.UNKNOWN), new Token(")", TokenType.SYMBOL),
                        CompilationException.NOT_AN_OPENING_ROUND_BRACKET),
                Arguments.of(new Token("y", TokenType.UNKNOWN), new Token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_AN_OPENING_ROUND_BRACKET));
    }

    private static Stream<Arguments> missingSubroutineBodyBracketSets() {
        return Stream.of(
                Arguments.of(new Token("{", TokenType.SYMBOL), new Token("x", TokenType.UNKNOWN),
                        CompilationException.INVALID_SUBROUTINE_BODY),
                Arguments.of(new Token("x", TokenType.UNKNOWN), new Token("}", TokenType.SYMBOL),
                        CompilationException.NOT_AN_OPENING_CURLY_BRACKET),
                Arguments.of(new Token("y", TokenType.UNKNOWN), new Token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_AN_OPENING_CURLY_BRACKET));
    }
}
