package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.collection.List;

public interface SymbolTableFactory {

    SymbolTable create(Identifier identifier, List<Construct> declarations);
}
