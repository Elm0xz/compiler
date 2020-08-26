package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.Type;
import com.pretz.compiler.compengine.terminal.TerminalKeywordType;

import java.util.Objects;

public class SymbolId {
    private final Type type;
    private final TerminalKeywordType kind;
    private final int id;

    public SymbolId(Type type, TerminalKeywordType kind, int id) {
        this.type = type;
        this.kind = kind;
        this.id = id;
    }

    @Override
    public String toString() {
        return "SymbolId{" +
                "type=" + type +
                ", kind=" + kind +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolId symbolId = (SymbolId) o;
        return id == symbolId.id &&
                type.equals(symbolId.type) &&
                kind == symbolId.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, kind, id);
    }
}
