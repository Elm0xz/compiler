package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.symboltable.SymbolTable;

public class VmContext {
    private final SymbolTable symbolTable;
    private final String label;

    public VmContext(SymbolTable symbolTable, String label) {
        this.symbolTable = symbolTable;
        this.label = label;
    }

    public SymbolTable symbolTable() {
        return symbolTable;
    }

    public String label() {
        return label;
    }

    /**Merges class symbol table with subroutine symbol table and creates label used for if/while statements.*/
    public VmContext mergeTablesAddingStatementId(VmContext subroutineVmContext, Integer statementId) {
        return new VmContext(this.symbolTable().merge(subroutineVmContext.symbolTable()), "L" + subroutineVmContext.label() + statementId(statementId));
    }

    public VmContext addStatementId(Integer statementId) {
        return new VmContext(this.symbolTable(), this.label + statementId(statementId));
    }

    private String statementId(Integer statementId) {
        return statementId + "_";
    }
}
