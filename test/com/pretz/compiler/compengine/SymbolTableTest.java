package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.construct.ClassVarDec;
import com.pretz.compiler.compengine.symboltable.SymbolId;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.symboltable.SymbolTableFactory;
import com.pretz.compiler.compengine.terminal.TerminalKeywordType;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SymbolTableTest {

    private final ElementTestUtils $_ = new ElementTestUtils();

    private final SymbolTableFactory factory = new SymbolTableFactory();

    @Test
    public void shouldAddOneClassVariableIdentifier() {
        ClassVarDec classVarDec = $_.classVarDec("field", "int", $_.varNames("x"));

        SymbolTable symbolTable = factory.create($_.defIdentifier("testClass"), List.of(classVarDec));

        Assertions.assertThat(symbolTable.size()).isEqualTo(1);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        symbolId("int", TerminalKeywordType.FIELD, 0),
                        $_.defIdentifier("x"))));
    }

    @Test
    public void shouldAddTwoClassVariableIdentifiers() {
        ClassVarDec classVarDec1 = $_.classVarDec("field", "int", $_.varNames("x"));
        ClassVarDec classVarDec2 = $_.classVarDec("static", "boolean", $_.varNames("y"));

        SymbolTable symbolTable = factory.create($_.defIdentifier("testClass"), List.of(classVarDec1, classVarDec2));

        Assertions.assertThat(symbolTable.size()).isEqualTo(2);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        symbolId("int", TerminalKeywordType.FIELD, 0),
                        $_.defIdentifier("x"),
                        symbolId("boolean", TerminalKeywordType.STATIC, 0),
                        $_.defIdentifier("y"))));
    }

    @Test
    public void shouldAddTwoInlinedClassVariableIdentifiers() {
        ClassVarDec classVarDec = $_.classVarDec("field", "int", $_.varNames("x", "y"));

        SymbolTable symbolTable = factory.create($_.defIdentifier("testClass"), List.of(classVarDec));

        Assertions.assertThat(symbolTable.size()).isEqualTo(2);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        symbolId("int", TerminalKeywordType.FIELD, 0),
                        $_.defIdentifier("x"),
                        symbolId("int", TerminalKeywordType.FIELD, 1),
                        $_.defIdentifier("y"))));
    }

    @Test
    public void shouldAddTwoClassVariableIdentifiersWithSameType() {
        ClassVarDec classVarDec1 = $_.classVarDec("field", "int", $_.varNames("x"));
        ClassVarDec classVarDec2 = $_.classVarDec("field", "int", $_.varNames("y"));

        SymbolTable symbolTable = factory.create($_.defIdentifier("testClass"), List.of(classVarDec1, classVarDec2));

        Assertions.assertThat(symbolTable.size()).isEqualTo(2);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        symbolId("int", TerminalKeywordType.FIELD, 0),
                        $_.defIdentifier("x"),
                        symbolId("int", TerminalKeywordType.FIELD, 1),
                        $_.defIdentifier("y"))));
    }

    private SymbolId symbolId(String type, TerminalKeywordType kind, int id) {
        return new SymbolId($_.type(type), kind, id);
    }

    @Test
    public void shouldAddSeveralClassVariableIdentifiersWithDifferentTypesAndKinds() {
        List<ClassVarDec> classVarDecs = List.of(
                $_.classVarDec("field", "int", $_.varNames("x", "y")),
                $_.classVarDec("field", "int", $_.varNames("z")),
                $_.classVarDec("field", "Dog", $_.varNames("dog")),
                $_.classVarDec("static", "int", $_.varNames("a")));

        SymbolTable symbolTable = factory.create($_.defIdentifier("testClass"), classVarDecs);

        Assertions.assertThat(symbolTable.size()).isEqualTo(5);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        symbolId("int", TerminalKeywordType.FIELD, 0),
                        $_.defIdentifier("x"),
                        symbolId("int", TerminalKeywordType.FIELD, 1),
                        $_.defIdentifier("y"),
                        symbolId("int", TerminalKeywordType.FIELD, 2),
                        $_.defIdentifier("z"),
                        symbolId("int", TerminalKeywordType.STATIC, 0),
                        $_.defIdentifier("a"),
                        symbolId("Dog", TerminalKeywordType.FIELD, 0),
                        $_.defIdentifier("dog"))));
    }
}
