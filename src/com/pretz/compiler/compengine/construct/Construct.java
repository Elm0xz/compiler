package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.Element;
import com.pretz.compiler.compengine.symboltable.SymbolTable;

import static com.pretz.compiler.util.XmlUtils.indent;

public interface Construct extends Element {
    String CONSTRUCT_NAME = Construct.class.getSimpleName().toLowerCase();

    default String toXml(int indLvl) {
        return indent(indLvl) + "NOT YET IMPLEMENTED\n";
    }

    default String toVm(SymbolTable symbolTable) {
        return "NOT YET IMPLEMENTED\n";
    }
}
