package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.ClassVarDec;
import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.terminal.Terminal;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

//TODO finish implementation
public class SymbolTableFactory {
    public SymbolTable create(Terminal identifier, List<Construct> declarations) {
        Map<SymbolId, String> symbolTable = HashMap.empty();
        List<Construct> classVarDecs =
                declarations.filter(it -> it instanceof ClassVarDec)
                        .map(it -> (ClassVarDec) it)
                        .map(it -> it.type());

        return new SymbolTable();
    }

    //TODO implement
    public List<SymbolId> from(ClassVarDec classVarDec) {
        return List.empty();
    }
}
