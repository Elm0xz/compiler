package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.collection.Map;

import java.util.Objects;

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

    public String scope() {
        return scope.token();
    }

    public Integer numberByKind(Kind kind) {
        return symbols.toList()
                .count(it -> it._1.kind().equals(kind));
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "scope=" + scope +
                ", symbols=" + symbols +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolTable that = (SymbolTable) o;
        return scope.equals(that.scope) &&
                symbols.equals(that.symbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, symbols);
    }

    /**
     * Used to merge class symbol table with subroutine symbol table when needed
     * @param other subroutine symbol table to be merged
     * @return merged symbol table
     */
    public SymbolTable merge(SymbolTable other) {
        return new SymbolTable(this.scope, this.symbols.merge(other.symbols));
    }
}
