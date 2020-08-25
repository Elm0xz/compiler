package com.pretz.compiler.compengine.symboltable;

import io.vavr.collection.HashMap;
import io.vavr.collection.Map;

public class SymbolTable {
    private final Map<SymbolId, String> symbols;

    public SymbolTable() {
        this.symbols = HashMap.empty();
    }
}
