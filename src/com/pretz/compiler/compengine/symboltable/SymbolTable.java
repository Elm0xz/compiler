package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.NotImplementedException;
import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

public class SymbolTable {
    private final Map<SymbolId, Identifier> symbols;

    public SymbolTable() {
        this.symbols = HashMap.empty();
    }

    public int size() {
        throw new NotImplementedException("Not yet implemented");
    }

    public Tuple2<SymbolId, Identifier> get(int i) {
        throw new NotImplementedException("Not yet implemented");
    }
}
