package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.Tuple2;
import io.vavr.collection.Map;

public class SymbolTable {
    private final Identifier scope;
    private final Map<SymbolId, Identifier> symbols;

    public SymbolTable(Identifier scope, Map<SymbolId, Identifier> symbols) {
        this.scope = scope;
        this.symbols = symbols;
    }

    public int size() {
        return symbols.size();
    }

    public Tuple2<SymbolId, Identifier> get(int i) {
        return symbols.get();
    }
}
