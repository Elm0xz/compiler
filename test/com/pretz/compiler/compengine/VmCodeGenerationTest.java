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

public class VmCodeGenerationTest {

    private final ElementTestUtils $ = new ElementTestUtils();

    @Test
    public void shouldTranslateLetStatementAssignedExpression() {
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

    //TODO test on various operators (also check operation list)
}
