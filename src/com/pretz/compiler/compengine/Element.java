package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.symboltable.SymbolTable;

public interface Element {

    String toXml(int indLvl);
    String toVm(SymbolTable symbolTable);
}
