package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.statement.IfStatement;
import com.pretz.compiler.compengine.statement.LetStatement;
import com.pretz.compiler.compengine.symboltable.Kind;
import com.pretz.compiler.compengine.symboltable.Symbol;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.pretz.compiler.compengine.terminal.TerminalType.INT_CONST;
import static com.pretz.compiler.compengine.terminal.TerminalType.KEYWORD_CONST;

public class VmCodeGenerationTest {

    private final ElementTestUtils $ = new ElementTestUtils();

    @Test
    public void shouldTranslateSimpleLetStatement() {
        String var = "x";
        SymbolTable symbolTable = symbolTable(new TestVariable("int", var));
        VmContext vmContext = new VmContext(symbolTable, null);

        LetStatement testLetStatement = new LetStatement(
                $.varUsageIdentifier(var),
                null,
                $.expression(
                        $.varNameTerm(var),
                        List.of($.opTerm("+", $.constantTerm("5", INT_CONST)))
                )
        );

        Assertions.assertThat(testLetStatement.toVm(vmContext)).isEqualTo(
                "push local 0\n" +
                        "push constant 5\n" +
                        "add\n" +
                        "pop local 0\n"
        );
    }

    @ParameterizedTest
    @MethodSource("operators")
    public void shouldTranslateLetStatementWithOperator(String op, String opVm, String type) {
        String var1 = "x";
        String var2 = "y";
        SymbolTable symbolTable = symbolTable(List.of(new TestVariable(type, var1),
                new TestVariable(type, var2)));
        VmContext vmContext = new VmContext(symbolTable, null);

        LetStatement testLetStatement = new LetStatement(
                $.varUsageIdentifier(var1),
                null,
                $.expression(
                        $.varNameTerm(var1),
                        List.of($.opTerm(op, $.varNameTerm(var2)))
                )
        );

        Assertions.assertThat(testLetStatement.toVm(vmContext)).isEqualTo(
                "push local 0\n" +
                        "push local 1\n" +
                        opVm + "\n" +
                        "pop local 0\n"
        );
    }

    @ParameterizedTest
    @MethodSource("unaryOperators")
    public void shouldTranslateLetStatementWithUnaryOp(String unaryOp, String unaryOpVm, String type) {
        String var = "x";
        SymbolTable symbolTable = symbolTable(new TestVariable(type, var));
        VmContext vmContext = new VmContext(symbolTable, null);

        LetStatement testLetStatement = new LetStatement(
                $.varUsageIdentifier(var),
                null,
                $.expression(
                        $.unaryOpTerm(var, unaryOp))
        );

        Assertions.assertThat(testLetStatement.toVm(vmContext)).isEqualTo(
                "push local 0\n" +
                        unaryOpVm + "\n" +
                        "pop local 0\n"
        );
    }


    //TODO(H) Follow the slides!

    //TODO(H) expression in brackets

    //TODO(H) string constant, keyword constant
    //TODO(H) array term (and array left-side)

    //TODO(H) test of using class scope symbol table in subroutines

    //TODO(H) implementation for this test does not work properly??

    @Test
    public void shouldTranslateLetStatementWithSubroutineCall() {
        String var = "x";
        String type = "int";
        SymbolTable symbolTable = symbolTable(new TestVariable(type, var));
        VmContext vmContext = new VmContext(symbolTable, null);

        LetStatement testLetStatement = new LetStatement(
                $.varUsageIdentifier(var),
                null,
                $.expression(
                        $.subroutineCallTerm("doAnotherStuff",
                                List.of(
                                        $.expression($.varNameTerm(var)),
                                        $.expression($.constantTerm("5", INT_CONST))
                                ))
                ));

        Assertions.assertThat(testLetStatement.toVm(vmContext)).isEqualTo(
                "push local 0\n" +
                        "push constant 5\n" +
                        "call doAnotherStuff 3\n" +
                        "pop local 0\n"
        );
    }

    @ParameterizedTest
    @MethodSource("compOperators")
    public void shouldTranslateSimpleIfStatement(String op, String opVm) {
        String var = "x";
        String type = "int";
        String label = "LtestClass.doStuff1_";
        SymbolTable symbolTable = symbolTable(new TestVariable(type, var));
        VmContext vmContext = new VmContext(symbolTable, label);

        IfStatement testIfStatement = new IfStatement(
                $.expression($.varNameTerm(var), List.of($.opTerm(op, $.constantTerm("2", INT_CONST)))),
                List.of($.returnStatement($.expression($.constantTerm("true", KEYWORD_CONST)))),
                List.of($.returnStatement($.expression($.constantTerm("false", KEYWORD_CONST))))
        );

        Assertions.assertThat(testIfStatement.toVm(vmContext)).isEqualTo(
                "push local 0\n" +
                        "push constant 2\n" +
                        opVm + "\n" +
                        "not\n" +
                        "if-goto " + elseLabel(label) +
                        "push constant 1\n" +
                        "return\n" +
                        "goto " + ifLabel(label) +
                        "label " + elseLabel(label) +
                        "push constant 0\n" +
                        "return\n" +
                        "label " + ifLabel(label)
        );
    }

    //TODO(H) while, do, statements

    private String elseLabel(String label) {
        return label + "b\n";
    }

    private String ifLabel(String label) {
        return label + "a\n";
    }

    private SymbolTable symbolTable(TestVariable var) {
        return new SymbolTable(
                HashMap.of(
                        $.varDefIdentifier(var.identifier),
                        new Symbol($.type(var.type), Kind.VAR, 0))
        );
    }

    private SymbolTable symbolTable(List<TestVariable> vars) {
        return new SymbolTable(
                vars.zipWithIndex().toMap(it -> Tuple.of($.varDefIdentifier(it._1().identifier),
                        new Symbol($.type(it._1().type), Kind.VAR, it._2()))
                ));
    }

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

    private static Stream<Arguments> compOperators() {
        return Stream.of(
                Arguments.of(">", "gt"),
                Arguments.of("<", "lt"),
                Arguments.of("=", "eq"));
    }


    class TestVariable {
        private final String type;
        private final String identifier;

        public TestVariable(String type, String identifier) {
            this.type = type;
            this.identifier = identifier;
        }
    }
}
