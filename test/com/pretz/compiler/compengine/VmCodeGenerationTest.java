package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.statement.LetStatement;
import com.pretz.compiler.compengine.symboltable.Kind;
import com.pretz.compiler.compengine.symboltable.Symbol;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
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
                $.identifier("x", IdentifierMeaning.DEFINITION),
                null,
                $.expression(
                        $.varNameTerm("x"),
                        List.of($.opTerm("+", $.constantTerm("5", TerminalType.INT_CONST)))
                )
        );

        SymbolTable symbolTable = new SymbolTable($.identifier("doStuff", IdentifierMeaning.DEFINITION),
                HashMap.of(
                        $.identifier("x", IdentifierMeaning.DEFINITION),
                        new Symbol($.type("int"), Kind.VAR, 0))
        );

        Assertions.assertThat(testLetStatement.toVm(symbolTable)).isEqualTo(
                "push local 0\n" +
                        "push constant 5\n" +
                        "add\n" +
                        "pop local 0\n"
        );
    }

    @ParameterizedTest()
    @MethodSource("operators")
    public void shouldTranslateLetStatementWithOperand(String op, String opVm, String type) {
        LetStatement testLetStatement = new LetStatement(
                $.identifier("x", IdentifierMeaning.DEFINITION),
                null,
                $.expression(
                        $.varNameTerm("x"),
                        List.of($.opTerm(op, $.varNameTerm("y")))
                )
        );

        SymbolTable symbolTable = new SymbolTable($.identifier("doStuff", IdentifierMeaning.DEFINITION),
                HashMap.of(
                        $.identifier("x", IdentifierMeaning.DEFINITION),
                        new Symbol($.type(type), Kind.VAR, 0),
                        $.identifier("y", IdentifierMeaning.DEFINITION),
                        new Symbol($.type(type), Kind.VAR, 1))
        );

        Assertions.assertThat(testLetStatement.toVm(symbolTable)).isEqualTo(
                "push local 0\n" +
                        "push local 1\n" +
                        opVm + "\n" +
                        "pop local 0\n"
        );
    }

    //TODO test for unary ops (both types)

    //TODO test for comparison operators (with if statement, probably)
    //TODO string constant, keyword constant
    //TODO array term
    //TODO subroutine call
    //TODO expression in brackets

    //TODO if, while, do, return statements

    private static Stream<Arguments> operators() {
        return Stream.of(
                Arguments.of("+", "add", "int"),
                Arguments.of("-", "sub", "int"),
                Arguments.of("&", "and", "boolean"),
                Arguments.of("|", "or", "boolean"));
    }
}
