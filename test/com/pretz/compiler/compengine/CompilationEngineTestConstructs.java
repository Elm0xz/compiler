package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.construct.ParameterList;
import com.pretz.compiler.compengine.construct.SubroutineBody;
import com.pretz.compiler.compengine.statement.LetStatement;
import com.pretz.compiler.compengine.statement.ReturnStatement;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.TerminalType;
import com.pretz.compiler.tokenizer.token.TokenType;
import io.vavr.collection.List;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class CompilationEngineTestConstructs {

    private final ElementTestUtils $ = new ElementTestUtils();

    protected List<Construct> classConstructs() {
        return List.of(
                $.classVarDec("static", "int", $.oneVarName("testInt")),
                $.classVarDec("field", "boolean", $.varNames("testBool1", "testBool2")),
                $.subroutineDec("method", "void", "doStuff", subroutineParameterList(), subroutineBody())
        );
    }

    protected List<Construct> classVarDecConstructs() {
        return List.of(
                $.classVarDec("static", "int", $.oneVarName("testInt")),
                $.classVarDec("field", "boolean", $.varNames("testBool1", "testBool2"))
        );
    }

    protected List<Construct> classVarDecConstructsWithCustomType() {
        return List.of(
                $.classVarDec("static", "Cat", $.oneVarName("testCat")),
                $.classVarDec("field", "boolean", $.varNames("testBool1", "testBool2"))
        );
    }

    protected List<Construct> classSubroutineDecConstructs() {
        return List.of(
                $.subroutineDec("function", "int", "doSomeStuff", subroutineParameterList(), subroutineBody())
        );
    }

    protected ParameterList subroutineParameterList() {
        return new ParameterList(List.of(
                $.parameter("boolean", "booleanParameter"),
                $.parameter("int", "intParameter"),
                $.parameter("Dog", "objectParameter")
        ));
    }

    protected SubroutineBody subroutineBody() {
        return new SubroutineBody(
                List.of(
                        $.varDec("int", $.varNames("x", "y")),
                        $.varDec("Dog", $.oneVarName("dog")),
                        new LetStatement(
                                $.identifier("x", IdentifierMeaning.DEFINITION),
                                null,
                                $.expression($.constantTerm("5", TerminalType.INT_CONST))
                        ),
                        new ReturnStatement(
                                $.expression($.varNameTerm("x")))
                )
        );
    }

    static Stream<Arguments> invalidClassIdentifiers() {
        final ElementTestUtils $ = new ElementTestUtils();
        return Stream.of(
                Arguments.of($.token("NewClass", TokenType.STRING_CONST)),
                Arguments.of($.token("void", TokenType.KEYWORD)),
                Arguments.of($.token("53", TokenType.INT_CONST)),
                Arguments.of($.token("{", TokenType.SYMBOL)));
    }

    static Stream<Arguments> missingClassBracketSets() {
        final ElementTestUtils $ = new ElementTestUtils();
        return Stream.of(
                Arguments.of($.token("{", TokenType.SYMBOL), $.token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_A_CLOSING_CURLY_BRACKET),
                Arguments.of($.token("x", TokenType.UNKNOWN), $.token("}", TokenType.SYMBOL),
                        CompilationException.NOT_AN_OPENING_CURLY_BRACKET),
                Arguments.of($.token("y", TokenType.UNKNOWN), $.token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_AN_OPENING_CURLY_BRACKET));
    }

    static Stream<Arguments> missingSubroutineParametersBracketSets() {
        final ElementTestUtils $ = new ElementTestUtils();
        return Stream.of(
                Arguments.of($.token("(", TokenType.SYMBOL), $.token("x", TokenType.UNKNOWN),
                        CompilationException.MISSING_PARAMETER_COMMA_OR_CLOSING_ROUND_BRACKET),
                Arguments.of($.token("x", TokenType.UNKNOWN), $.token(")", TokenType.SYMBOL),
                        CompilationException.NOT_AN_OPENING_ROUND_BRACKET),
                Arguments.of($.token("y", TokenType.UNKNOWN), $.token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_AN_OPENING_ROUND_BRACKET));
    }

    static Stream<Arguments> missingSubroutineBodyBracketSets() {
        final ElementTestUtils $ = new ElementTestUtils();
        return Stream.of(
                Arguments.of($.token("{", TokenType.SYMBOL), $.token("x", TokenType.UNKNOWN),
                        CompilationException.INVALID_SUBROUTINE_BODY_KEYWORD),
                Arguments.of($.token("x", TokenType.UNKNOWN), $.token("}", TokenType.SYMBOL),
                        CompilationException.NOT_AN_OPENING_CURLY_BRACKET),
                Arguments.of($.token("y", TokenType.UNKNOWN), $.token("x", TokenType.UNKNOWN),
                        CompilationException.NOT_AN_OPENING_CURLY_BRACKET));
    }
}
