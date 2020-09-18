package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.expression.Term;
import com.pretz.compiler.compengine.statement.DoStatement;
import com.pretz.compiler.compengine.statement.IfStatement;
import com.pretz.compiler.compengine.statement.LetStatement;
import com.pretz.compiler.compengine.statement.WhileStatement;
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

        LetStatement testLetStatement = $.letStatement(var, $.expression(
                $.varNameTerm(var),
                List.of($.opTerm("+", $.constantTerm("5", INT_CONST)))
        ));

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

        LetStatement testLetStatement = $.letStatement(var1,
                $.expression(
                        $.varNameTerm(var1),
                        List.of($.opTerm(op, $.varNameTerm(var2)))
                ));

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

        LetStatement testLetStatement = $.letStatement(var, $.expression(
                $.unaryOpTerm(var, unaryOp)));

        Assertions.assertThat(testLetStatement.toVm(vmContext)).isEqualTo(
                "push local 0\n" +
                        unaryOpVm + "\n" +
                        "pop local 0\n"
        );
    }

    //TODO(H) expressions: expression in brackets, string constant, keyword constant

    @Test
    public void shouldTranslateLetStatementWithSubroutineCall() {
        String var = "x";
        String type = "int";
        SymbolTable symbolTable = symbolTable(new TestVariable(type, var));
        VmContext vmContext = new VmContext(symbolTable, null);

        LetStatement testLetStatement = $.letStatement(var, $.expression(
                $.subroutineCallTerm("doAnotherStuff",
                        List.of(
                                $.expression($.varNameTerm(var)),
                                $.expression($.constantTerm("5", INT_CONST))
                        ))
        ));

        Assertions.assertThat(testLetStatement.toVm(vmContext)).isEqualTo(
                "push pointer 0\n" +
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
                        "if-goto " + falseLabel(label) +
                        "push constant 1\n" +
                        "return\n" +
                        "goto " + trueLabel(label) +
                        "label " + falseLabel(label) +
                        "push constant 0\n" +
                        "return\n" +
                        "label " + trueLabel(label)
        );
    }

    @Test
    public void shouldTranslateSimpleWhileStatement() {
        String var = "x";
        String type = "int";
        String label = "LtestClass.doStuff1_";
        SymbolTable symbolTable = symbolTable(new TestVariable(type, var));
        VmContext vmContext = new VmContext(symbolTable, label);

        WhileStatement testWhileStatement = new WhileStatement(
                $.expression($.varNameTerm(var), List.of($.opTerm("<", $.constantTerm("10", INT_CONST)))),
                List.of($.letStatement(var,
                        $.expression($.varNameTerm(var), List.of($.opTerm("+", $.constantTerm("1", INT_CONST))))
                )));

        Assertions.assertThat(testWhileStatement.toVm(vmContext)).isEqualTo(
                "label " + trueLabel(label) +
                        "push local 0\n" +
                        "push constant 10\n" +
                        "lt\n" +
                        "not\n" +
                        "if-goto " + falseLabel(label) +
                        "push local 0\n" +
                        "push constant 1\n" +
                        "add\n" +
                        "pop local 0\n" +
                        "goto " + trueLabel(label) +
                        "label " + falseLabel(label)
        );
    }

    @Test
    public void shouldTranslateDoStatement() {
        String var = "x";
        String type = "int";
        String label = "testClass.doStuff";
        SymbolTable symbolTable = symbolTable(new TestVariable(type, var));
        VmContext vmContext = new VmContext(symbolTable, null);

        DoStatement testDoStatement = $.doStatement($.subroutineCallTerm("doAnotherStuff", $.expression($.varNameTerm(var))));

        Assertions.assertThat(testDoStatement.toVm(vmContext)).isEqualTo(
                "push pointer 0\n" +
                        "push local 0\n" +
                        "call TestClass.doAnotherStuff 1\n" +
                        "pop temp 0\n"
        );
    }

    //1. function call (ThisClass.doStuff(x,y,z)) -> call Class.doStuff 3
    //2. method call on this object (doStuff(x,y,z)) -> call ThisClass.doStuff 4
    //3. method call on other object (otherClass.doStuff(x,y,z)) -> call otherClass.doStuff(x,y,z))

    @Test
    public void shouldTranslateFunctionCall() {
        String type = "int";
        SymbolTable symbolTable = argSymbolTable(List.of(
                new TestVariable(type, "x"),
                new TestVariable(type, "y"),
                new TestVariable(type, "z")
        ));
        VmContext vmContext = new VmContext(symbolTable, null);

        Term functionCall = $.classSubroutineCallTerm("Class", "doStuff",
                List.of($.expression($.varNameTerm("x")),
                        $.expression($.varNameTerm("y")),
                        $.expression($.varNameTerm("z"))
                )
        );

        Assertions.assertThat(functionCall.toVm(vmContext)).isEqualTo(
                "push argument 0\n" +
                        "push argument 1\n" +
                        "push argument 2\n" +
                        "call Class.doStuff 3\n"
        );
    }

    @Test
    public void shouldTranslateThisObjectMethodCall() {
        String type = "int";
        SymbolTable symbolTable = argSymbolTable(List.of(
                new TestVariable(type, "x"),
                new TestVariable(type, "y"),
                new TestVariable(type, "z"),
                new TestVariable("Class", "this")
        ));
        VmContext vmContext = new VmContext(symbolTable, null);

        Term functionCall = $.subroutineCallTerm("doStuff",
                List.of($.expression($.varNameTerm("x")),
                        $.expression($.varNameTerm("y")),
                        $.expression($.varNameTerm("z"))
                )
        );

        Assertions.assertThat(functionCall.toVm(vmContext)).isEqualTo(
                "push pointer 0\n" +
                        "push argument 0\n" +
                        "push argument 1\n" +
                        "push argument 2\n" +
                        "call Class.doStuff 4\n"
        );
    }

    @Test
    public void shouldTranslateAnotherObjectMethodCall() {
        String type = "int";
        SymbolTable symbolTable = argSymbolTable(List.of(
                new TestVariable(type, "x"),
                new TestVariable(type, "y"),
                new TestVariable(type, "z")
        )).merge(fieldSymbolTable(
                new TestVariable("AnotherClass", "anotherClass")
        ));
        VmContext vmContext = new VmContext(symbolTable, null);

        Term functionCall = $.classSubroutineCallTerm("anotherClass", "doStuff",
                List.of($.expression($.varNameTerm("x")),
                        $.expression($.varNameTerm("y")),
                        $.expression($.varNameTerm("z"))
                )
        );

        Assertions.assertThat(functionCall.toVm(vmContext)).isEqualTo(
                        "push this 0\n" +
                        "push argument 0\n" +
                        "push argument 1\n" +
                        "push argument 2\n" +
                        "call AnotherClass.doStuff 4\n"
        );
    }

    private String falseLabel(String label) {
        return label + "false\n";
    }

    private String trueLabel(String label) {
        return label + "true\n";
    }

    private SymbolTable symbolTable(TestVariable var) {
        return new SymbolTable(
                HashMap.of(
                        $.varDefIdentifier(var.identifier),
                        new Symbol($.type(var.type), Kind.VAR, 0))
        );
    }

    private SymbolTable fieldSymbolTable(TestVariable var) {
        return new SymbolTable(
                HashMap.of(
                        $.varDefIdentifier(var.identifier),
                        new Symbol($.type(var.type), Kind.FIELD, 0))
        );
    }

    private SymbolTable symbolTable(List<TestVariable> vars) {
        return new SymbolTable(
                vars.zipWithIndex().toMap(it -> Tuple.of($.varDefIdentifier(it._1().identifier),
                        new Symbol($.type(it._1().type), Kind.VAR, it._2()))
                ));
    }

    private SymbolTable argSymbolTable(List<TestVariable> vars) {
        return new SymbolTable(
                vars.zipWithIndex().toMap(it -> Tuple.of($.varDefIdentifier(it._1().identifier),
                        new Symbol($.type(it._1().type), Kind.ARGUMENT, it._2()))
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
