package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.statement.LetStatement;
import com.pretz.compiler.compengine.symboltable.Kind;
import com.pretz.compiler.compengine.symboltable.Symbol;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.terminal.TerminalType;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class VmCodeGenerationTest {

    private final ElementTestUtils $ = new ElementTestUtils();

    @Test
    public void shouldTranslateSimpleLetStatement() {
        LetStatement testLetStatement = new LetStatement(
                $.varUsageIdentifier("x"),
                null,
                $.expression(
                        $.varNameTerm("x"),
                        List.of($.opTerm("+", $.constantTerm("5", TerminalType.INT_CONST)))
                )
        );

        SymbolTable symbolTable = new SymbolTable($.subroutineUsageIdentifier("doStuff"),
                HashMap.of(
                        $.varDefIdentifier("x"),
                        new Symbol($.type("int"), Kind.VAR, 0))
        );

        Assertions.assertThat(testLetStatement.toVm(symbolTable)).isEqualTo(
                "push local 0\n" +
                        "push constant 5\n" +
                        "add\n" +
                        "pop local 0\n"
        );
    }

    @ParameterizedTest
    @MethodSource("operators")
    public void shouldTranslateLetStatementWithOperator(String op, String opVm, String type) {
        LetStatement testLetStatement = new LetStatement(
                $.varUsageIdentifier("x"),
                null,
                $.expression(
                        $.varNameTerm("x"),
                        List.of($.opTerm(op, $.varNameTerm("y")))
                )
        );

        SymbolTable symbolTable = new SymbolTable($.subroutineUsageIdentifier("doStuff"),
                HashMap.of(
                        $.varDefIdentifier("x"),
                        new Symbol($.type(type), Kind.VAR, 0),
                        $.varDefIdentifier("y"),
                        new Symbol($.type(type), Kind.VAR, 1))
        );

        Assertions.assertThat(testLetStatement.toVm(symbolTable)).isEqualTo(
                "push local 0\n" +
                        "push local 1\n" +
                        opVm + "\n" +
                        "pop local 0\n"
        );
    }

    @ParameterizedTest
    @MethodSource("unaryOperators")
    public void shouldTranslateLetStatementWithUnaryOp(String unaryOp, String unaryOpVm, String type) {
        LetStatement testLetStatement = new LetStatement(
                $.varUsageIdentifier("x"),
                null,
                $.expression(
                        $.unaryOpTerm("x", unaryOp))
        );

        SymbolTable symbolTable = new SymbolTable($.subroutineUsageIdentifier("doStuff"),
                HashMap.of(
                        $.varDefIdentifier("x"),
                        new Symbol($.type(type), Kind.VAR, 0))
        );

        Assertions.assertThat(testLetStatement.toVm(symbolTable)).isEqualTo(
                "push local 0\n" +
                        unaryOpVm + "\n" +
                        "pop local 0\n"
        );
    }

    //TODO(H) we should differentiate between various types of subroutines!
    //TODO(H) subroutine call with function instead of method
    //TODO(H) implementation for this test does not work properly
    @Test
    public void shouldTranslateLetStatementWithSubroutineCall() {
        LetStatement testLetStatement = new LetStatement(
                $.varUsageIdentifier("x"),
                null,
                $.expression(
                        $.subroutineCallTerm("doAnotherStuff",
                                List.of(
                                        $.expression($.varNameTerm("x")),
                                        $.expression($.constantTerm("5", TerminalType.INT_CONST))
                                ))
                ));

        SymbolTable symbolTable = new SymbolTable($.subroutineUsageIdentifier("doStuff"),
                HashMap.of(
                        $.varDefIdentifier("x"),
                        new Symbol($.type("int"), Kind.VAR, 0))
        );

        Assertions.assertThat(testLetStatement.toVm(symbolTable)).isEqualTo(
                "push local 0\n" +
                        "push constant 5\n" +
                        "call doAnotherStuff 3\n" +
                        "pop local 0\n"
        );
    }

    //TODO(H) expression in brackets

    //TODO(H) test for comparison operators (with if statement, probably)
    //TODO(H) string constant, keyword constant
    //TODO(H) array term (and array left-side)

    //TODO(H) test of using class scope symbol table in subroutines

    //TODO(H) if, while, do, return statements
    private static Stream<Arguments> operators() {
        return Stream.of(
                Arguments.of("+", "add", "int"),
                Arguments.of("-", "sub", "int"),
                Arguments.of("&", "and", "boolean"),
                Arguments.of("|", "or", "boolean"));
    }

    private static Stream<Arguments> unaryOperators() {
        return Stream.of(
                Arguments.of("-", "neg", "int"),
                Arguments.of("~", "not", "boolean"));
    }
}
