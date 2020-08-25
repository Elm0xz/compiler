package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.terminal.Terminal;
import io.vavr.collection.List;

public class SymbolTableFactory {
    public SymbolTable create(Terminal identifier, List<Construct> declarations) {
        return new SymbolTable();
    }
}
