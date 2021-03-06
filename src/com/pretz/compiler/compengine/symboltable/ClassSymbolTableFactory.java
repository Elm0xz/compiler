package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.ClassVarDec;
import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.function.Predicate;

public class ClassSymbolTableFactory implements SymbolTableFactory {

    @Override
    public SymbolTable create(List<Construct> declarations) {
        var classVarDeclarations = asClassVarDec(declarations);
        Map<Identifier, Symbol> symbolTable =
                classVarDeclarations
                        .flatMap(it -> from(it, classVarDeclarations))
                        .toMap(it -> it);
        return new SymbolTable(symbolTable);
    }

    private List<ClassVarDec> asClassVarDec(List<Construct> declarations) {
        return declarations.map(it -> (ClassVarDec) it);
    }

    private List<Tuple2<Identifier, Symbol>> from(ClassVarDec classVarDec, List<ClassVarDec> declarations) {
        //TODO(L) validation
        return classVarDec.varNames().varNames()
                .zipWithIndex()
                .map(it -> new Tuple2<>(it._1, symbolId(classVarDec, nextId(it, classVarDec, declarations))));
    }

    //TODO(L) maybe it could be simplified by first grouping variable declarations by type and kind and then indexing
    private Integer nextId(Tuple2<Identifier, Integer> varNameWithIndex, ClassVarDec classVarDec, List<ClassVarDec> declarations) {
        return declarations
                .filter(isSameKind(classVarDec))
                .takeUntil(isCurrent(classVarDec))
                .flatMap(it -> it.varNames().varNames())
                .length() + varNameWithIndex._2;
    }

    private Predicate<ClassVarDec> isSameKind(ClassVarDec classVarDec) {
        return it -> it.startingKeyword().equals(classVarDec.startingKeyword());
    }

    private Predicate<ClassVarDec> isCurrent(ClassVarDec classVarDec) {
        return it -> it.equals(classVarDec);
    }

    private Symbol symbolId(ClassVarDec classVarDec, int index) {
        return new Symbol(classVarDec.type(), classVarDec.startingKeyword().keywordType(), index);
    }
}
