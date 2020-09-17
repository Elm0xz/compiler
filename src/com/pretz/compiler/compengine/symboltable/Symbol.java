package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.Type;
import com.pretz.compiler.compengine.terminal.TerminalKeywordType;

import java.util.Objects;

//TODO(M) what about arrays?
public class Symbol {
    private final Type type;
    private final Kind kind;
    private final int id;

    public Symbol(Type type, Kind kind, int id) {
        this.type = type;
        this.kind = kind;
        this.id = id;
    }

    public Symbol(Type type, TerminalKeywordType kind, int id) {
        this.type = type;
        this.kind = map(kind);
        this.id = id;
    }

    //TODO(L) some validation of possible mappings?
    private Kind map(TerminalKeywordType kind) {
        return Kind.valueOf(kind.name());
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "type=" + type +
                ", kind=" + kind +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return id == symbol.id &&
                type.equals(symbol.type) &&
                kind == symbol.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, kind, id);
    }

    public Kind kind() {
        return kind;
    }

    public String toVm() {
        return kind.toVm() + " " + id + "\n";
    }

    public String type() {
        return type.type();
    }
}
