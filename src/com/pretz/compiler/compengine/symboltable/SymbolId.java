package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.Type;

public class SymbolId {
    private final Type type;
    private final Kind kind;
    private final int id;

    public SymbolId(Type type, Kind kind, int id) {
        this.type = type;
        this.kind = kind;
        this.id = id;
    }
}
