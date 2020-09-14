package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.construct.ClassVarDec;
import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.construct.Parameter;
import com.pretz.compiler.compengine.construct.ParameterList;
import com.pretz.compiler.compengine.construct.SubroutineBody;
import com.pretz.compiler.compengine.construct.SubroutineDec;
import com.pretz.compiler.compengine.construct.VarDec;
import com.pretz.compiler.compengine.symboltable.ClassSymbolTableFactory;
import com.pretz.compiler.compengine.symboltable.Kind;
import com.pretz.compiler.compengine.symboltable.SubroutineSymbolTableFactory;
import com.pretz.compiler.compengine.symboltable.Symbol;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SymbolTableTest {

    private final ElementTestUtils $_ = new ElementTestUtils();

    private final ClassSymbolTableFactory classFactory = new ClassSymbolTableFactory();
    private final SubroutineSymbolTableFactory subroutineFactory = new SubroutineSymbolTableFactory();

    @Test
    public void shouldAddOneClassVariableIdentifier() {
        ClassVarDec classVarDec = $_.classVarDec("field", "int", $_.varNames("x"));

        SymbolTable symbolTable = classFactory.create($_.classUsageIdentifier("testClass"), List.of(classVarDec));

        Assertions.assertThat(symbolTable.size()).isEqualTo(1);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.classUsageIdentifier("testClass"),
                HashMap.of(
                        $_.varDefIdentifier("x"),
                        symbolId("int", Kind.FIELD, 0))
        ));
    }

    @Test
    public void shouldAddTwoClassVariableIdentifiers() {
        ClassVarDec classVarDec1 = $_.classVarDec("field", "int", $_.varNames("x"));
        ClassVarDec classVarDec2 = $_.classVarDec("static", "boolean", $_.varNames("y"));

        SymbolTable symbolTable = classFactory.create($_.classUsageIdentifier("testClass"), List.of(classVarDec1, classVarDec2));

        Assertions.assertThat(symbolTable.size()).isEqualTo(2);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.classUsageIdentifier("testClass"),
                HashMap.of(
                        $_.varDefIdentifier("x"),
                        symbolId("int", Kind.FIELD, 0),
                        $_.varDefIdentifier("y"),
                        symbolId("boolean", Kind.STATIC, 0))
        ));
    }

    @Test
    public void shouldAddTwoInlinedClassVariableIdentifiers() {
        ClassVarDec classVarDec = $_.classVarDec("field", "int", $_.varNames("x", "y"));

        SymbolTable symbolTable = classFactory.create($_.classUsageIdentifier("testClass"), List.of(classVarDec));

        Assertions.assertThat(symbolTable.size()).isEqualTo(2);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.classUsageIdentifier("testClass"),
                HashMap.of(
                        $_.varDefIdentifier("x"),
                        symbolId("int", Kind.FIELD, 0),
                        $_.varDefIdentifier("y"),
                        symbolId("int", Kind.FIELD, 1)
                )
        ));
    }

    @Test
    public void shouldAddTwoClassVariableIdentifiersWithSameType() {
        ClassVarDec classVarDec1 = $_.classVarDec("field", "int", $_.varNames("x"));
        ClassVarDec classVarDec2 = $_.classVarDec("field", "int", $_.varNames("y"));

        SymbolTable symbolTable = classFactory.create($_.classUsageIdentifier("testClass"), List.of(classVarDec1, classVarDec2));

        Assertions.assertThat(symbolTable.size()).isEqualTo(2);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.classUsageIdentifier("testClass"),
                HashMap.of(
                        $_.varDefIdentifier("x"),
                        symbolId("int", Kind.FIELD, 0),
                        $_.varDefIdentifier("y"),
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

        SymbolTable symbolTable = classFactory.create($_.classUsageIdentifier("testClass"), classVarDecs);

        Assertions.assertThat(symbolTable.size()).isEqualTo(5);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.classUsageIdentifier("testClass"),
                HashMap.of(
                        $_.varDefIdentifier("x"),
                        symbolId("int", Kind.FIELD, 0),
                        $_.varDefIdentifier("y"),
                        symbolId("int", Kind.FIELD, 1),
                        $_.varDefIdentifier("z"),
                        symbolId("int", Kind.FIELD, 2),
                        $_.varDefIdentifier("a"),
                        symbolId("int", Kind.STATIC, 0),
                        $_.varDefIdentifier("dog"),
                        symbolId("Dog", Kind.FIELD, 0))
        ));
    }

    @Test
    public void shouldAddMethodIdentifiersIncludingThisParameter() {
        List<Parameter> parameters = List.of(
                $_.parameter("int", "x"),
                $_.parameter("int", "a"));
        List<Construct> declarations = List.of($_.varDec("int", $_.varNames("y", "z")));
        SymbolTable symbolTable = $_.subroutineDec("method", "void", "doStuff", "Dog",
                new ParameterList(parameters), new SubroutineBody(declarations)).symbolTable();

        Assertions.assertThat(symbolTable.size()).isEqualTo(5);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.subroutineUsageIdentifier("doStuff"),
                HashMap.of(
                        $_.varDefIdentifier("this"),
                        symbolId("Dog", Kind.ARGUMENT, 0),
                        $_.varDefIdentifier("x"),
                        symbolId("int", Kind.ARGUMENT, 1),
                        $_.varDefIdentifier("a"),
                        symbolId("int", Kind.ARGUMENT, 2),
                        $_.varDefIdentifier("y"),
                        symbolId("int", Kind.VAR, 0),
                        $_.varDefIdentifier("z"),
                        symbolId("int", Kind.VAR, 1))
        ));
    }

    @Test
    public void shouldAddFunctionIdentifiersWithoutThisParameter() {
        List<Parameter> parameters = List.of(
                $_.parameter("int", "x"),
                $_.parameter("int", "a"));
        List<Construct> declarations = List.of($_.varDec("int", $_.varNames("y", "z")));
        SymbolTable symbolTable = $_.subroutineDec("function", "void", "doStuff", "Dog",
                new ParameterList(parameters), new SubroutineBody(declarations)).symbolTable();

        Assertions.assertThat(symbolTable.size()).isEqualTo(4);
        Assertions.assertThat(symbolTable).isEqualTo(new SymbolTable($_.subroutineUsageIdentifier("doStuff"),
                HashMap.of(
                        $_.varDefIdentifier("x"),
                        symbolId("int", Kind.ARGUMENT, 0),
                        $_.varDefIdentifier("a"),
                        symbolId("int", Kind.ARGUMENT, 1),
                        $_.varDefIdentifier("y"),
                        symbolId("int", Kind.VAR, 0),
                        $_.varDefIdentifier("z"),
                        symbolId("int", Kind.VAR, 1))
        ));
    }
}
