package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.construct.ClassVarDec;
import com.pretz.compiler.compengine.symboltable.SymbolId;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.symboltable.SymbolTableFactory;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.TerminalKeywordType;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SymbolTableTest {

    private final ElementTestUtils $_ = new ElementTestUtils();

    private final SymbolTableFactory factory = new SymbolTableFactory();

    @Test
    public void shouldAddOneClassVariableIdentifier() {
        ClassVarDec classVarDec = $_.classVarDec("field", "int", $_.varNames("x"));

        SymbolTable symbolTable = factory.create($_.identifier("testClass", IdentifierMeaning.DEFINITION), List.of(classVarDec));

        Assertions.assertThat(symbolTable.size()).isEqualTo(1);
        Assertions.assertThat(symbolTable.get(1)).isEqualTo(
                new Tuple2<>(
                        new SymbolId(
                                $_.type("int"),
                                TerminalKeywordType.FIELD, 0),
                        $_.identifier("x", IdentifierMeaning.DEFINITION)));
    }
}
