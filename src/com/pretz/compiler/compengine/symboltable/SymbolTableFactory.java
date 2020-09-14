package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.Construct;
import io.vavr.collection.List;

public interface SymbolTableFactory {

    SymbolTable create(List<Construct> declarations);
}
