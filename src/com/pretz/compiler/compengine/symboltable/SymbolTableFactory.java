package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.ClassVarDec;
import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.function.Predicate;

public class SymbolTableFactory {
    public SymbolTable create(Identifier identifier, List<ClassVarDec> declarations) {
        Map<SymbolId, Identifier> symbolTable =
                declarations
                        .flatMap(it -> from(it, declarations))
                        .toMap(it -> it);

        return new SymbolTable(identifier, symbolTable);
    }

    public List<Tuple2<SymbolId, Identifier>> from(ClassVarDec classVarDec, List<ClassVarDec> declarations) {
        //TODO validation
        return classVarDec.varNames().varNames()
                .zipWithIndex()
                .map(it -> new Tuple2<>(symbolId(classVarDec, nextId(it, classVarDec, declarations)), it._1));
    }

    //TODO maybe it could be simplified by first grouping variable declarations by type and kind and then indexing
    private Integer nextId(Tuple2<Identifier, Integer> varNameWithIndex, ClassVarDec classVarDec, List<ClassVarDec> declarations) {
        return declarations
                .filter(isSameTypeAndKind(classVarDec))
                .takeUntil(isCurrent(classVarDec))
                .flatMap(it -> it.varNames().varNames())
                .length() + varNameWithIndex._2;
    }

    private Predicate<ClassVarDec> isSameTypeAndKind(ClassVarDec classVarDec) {
        return it -> it.type().equals(classVarDec.type()) && it.startingKeyword().equals(classVarDec.startingKeyword());
    }

    private Predicate<ClassVarDec> isCurrent(ClassVarDec classVarDec) {
        return it -> it.equals(classVarDec);
    }

    private SymbolId symbolId(ClassVarDec classVarDec, int index) {
        return new SymbolId(classVarDec.type(), classVarDec.startingKeyword().keywordType(), index);
    }
}
