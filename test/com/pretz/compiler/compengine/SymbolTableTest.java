package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.construct.ClassVarDec;
import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.symboltable.ClassSymbolTableFactory;
import com.pretz.compiler.compengine.symboltable.Kind;
import com.pretz.compiler.compengine.symboltable.SubroutineSymbolTableFactory;
import com.pretz.compiler.compengine.symboltable.Symbol;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SymbolTableTest {

    private final ElementTestUtils $_ = new ElementTestUtils();

    private final ClassSymbolTableFactory classFactory = new ClassSymbolTableFactory();
    private final SubroutineSymbolTableFactory subroutineFactory = new SubroutineSymbolTableFactory();

    @Test
    public void shouldAddOneClassVariableIdentifier() {
        ClassVarDec classVarDec = $_.classVarDec("field", "int", $_.varNames("x"));

        SymbolTable symbolTable = classFactory.create($_.defIdentifier("testClass"), List.of(classVarDec));

        Assertions.assertThat(symbolTable.size()).isEqualTo(1);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        $_.defIdentifier("x"),
                        symbolId("int", Kind.FIELD, 0))
        ));
    }

    @Test
    public void shouldAddTwoClassVariableIdentifiers() {
        ClassVarDec classVarDec1 = $_.classVarDec("field", "int", $_.varNames("x"));
        ClassVarDec classVarDec2 = $_.classVarDec("static", "boolean", $_.varNames("y"));

        SymbolTable symbolTable = classFactory.create($_.defIdentifier("testClass"), List.of(classVarDec1, classVarDec2));

        Assertions.assertThat(symbolTable.size()).isEqualTo(2);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        $_.defIdentifier("x"),
                        symbolId("int", Kind.FIELD, 0),
                        $_.defIdentifier("y"),
                        symbolId("boolean", Kind.STATIC, 0))
        ));
    }

    @Test
    public void shouldAddTwoInlinedClassVariableIdentifiers() {
        ClassVarDec classVarDec = $_.classVarDec("field", "int", $_.varNames("x", "y"));

        SymbolTable symbolTable = classFactory.create($_.defIdentifier("testClass"), List.of(classVarDec));

        Assertions.assertThat(symbolTable.size()).isEqualTo(2);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        $_.defIdentifier("x"),
                        symbolId("int", Kind.FIELD, 0),
                        $_.defIdentifier("y"),
                        symbolId("int", Kind.FIELD, 1)
                )
        ));
    }

    @Test
    public void shouldAddTwoClassVariableIdentifiersWithSameType() {
        ClassVarDec classVarDec1 = $_.classVarDec("field", "int", $_.varNames("x"));
        ClassVarDec classVarDec2 = $_.classVarDec("field", "int", $_.varNames("y"));

        SymbolTable symbolTable = classFactory.create($_.defIdentifier("testClass"), List.of(classVarDec1, classVarDec2));

        Assertions.assertThat(symbolTable.size()).isEqualTo(2);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        $_.defIdentifier("x"),
                        symbolId("int", Kind.FIELD, 0),
                        $_.defIdentifier("y"),
                        symbolId("int", Kind.FIELD, 1))
        ));
    }

    private Symbol symbolId(String type, Kind kind, int id) {
        return new Symbol($_.type(type), kind, id);
    }

    @Test
    public void shouldAddSeveralClassVariableIdentifiersWithDifferentTypesAndKinds() {
        List<Construct> classVarDecs = List.of(
                $_.classVarDec("field", "int", $_.varNames("x", "y")),
                $_.classVarDec("field", "int", $_.varNames("z")),
                $_.classVarDec("field", "Dog", $_.varNames("dog")),
                $_.classVarDec("static", "int", $_.varNames("a")));

        SymbolTable symbolTable = classFactory.create($_.defIdentifier("testClass"), classVarDecs);

        Assertions.assertThat(symbolTable.size()).isEqualTo(5);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        $_.defIdentifier("x"),
                        symbolId("int", Kind.FIELD, 0),
                        $_.defIdentifier("y"),
                        symbolId("int", Kind.FIELD, 1),
                        $_.defIdentifier("z"),
                        symbolId("int", Kind.FIELD, 2),
                        $_.defIdentifier("a"),
                        symbolId("int", Kind.STATIC, 0),
                        $_.defIdentifier("dog"),
                        symbolId("Dog", Kind.FIELD, 0))
        ));
    }

    @Test
    public void shouldAddSubroutineIdentifiers() {
        List<Construct> declarations = List.of(
                $_.parameter("int", "x"),
                $_.parameter("int", "a"),
                $_.varDec("int", $_.varNames("y", "z")));

        SymbolTable symbolTable = subroutineFactory.create($_.defIdentifier("testClass"), declarations);

        Assertions.assertThat(symbolTable.size()).isEqualTo(4);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.defIdentifier("testClass"),
                HashMap.of(
                        $_.defIdentifier("x"),
                        symbolId("int", Kind.ARGUMENT, 0),
                        $_.defIdentifier("a"),
                        symbolId("int", Kind.ARGUMENT, 1),
                        $_.defIdentifier("y"),
                        symbolId("int", Kind.VAR, 0),
                        $_.defIdentifier("z"),
                        symbolId("int", Kind.VAR, 1))
        ));
    }
}
