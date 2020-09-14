package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.collection.Map;

import java.util.Objects;

public class SymbolTable {
    private final Map<Identifier, Symbol> symbols;

    public SymbolTable(Map<Identifier, Symbol> symbols) {
        this.symbols = symbols;
    }

    public int size() {
        return symbols.size();
    }

    public Integer numberByKind(Kind kind) {
        return symbols.toList()
                .count(it -> it._2.kind().equals(kind));
    }

    public Symbol get(Identifier identifier) {
        return symbols.get(identifier)
                .getOrElseThrow(() -> new NonExistentSymbolException("Nonexistent symbol!"));
    }

    /**
     * Used to merge class symbol table with subroutine symbol table when needed
     * @param other subroutine symbol table to be merged
     * @return merged symbol table
     */
    public SymbolTable merge(SymbolTable other) {
        return new SymbolTable(this.symbols.merge(other.symbols));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SymbolTable that = (SymbolTable) o;
        return Objects.equals(symbols, that.symbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbols);
    }

    @Override
    public String toString() {
        return "SymbolTable{" +
                "symbols=" + symbols +
                '}';
    }

    class NonExistentSymbolException extends RuntimeException {
        public NonExistentSymbolException(String msg) {
            super(msg);
        }
    }
}
