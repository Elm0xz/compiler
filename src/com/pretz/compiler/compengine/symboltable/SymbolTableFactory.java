package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.ClassVarDec;
import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;

public class SymbolTableFactory {
    public SymbolTable create(Identifier identifier, List<Construct> declarations) {
        Map<SymbolId, Identifier> symbolTable =
                declarations.filter(it -> it instanceof ClassVarDec)
                        .map(it -> (ClassVarDec) it)
                        .flatMap(this::from)
                        .toMap(it -> it);

        return new SymbolTable(identifier, symbolTable);
    }

    public List<Tuple2<SymbolId, Identifier>> from(ClassVarDec classVarDec) {
        //TODO validation
        return classVarDec.varNames().varNames()
                .zipWithIndex()
                .map(it -> new Tuple2<>(symbolId(classVarDec, it._2), it._1));
    }

    private SymbolId symbolId(ClassVarDec classVarDec, int index) {
        return new SymbolId(classVarDec.type(), classVarDec.startingKeyword().keywordType(), index);
    }
}
