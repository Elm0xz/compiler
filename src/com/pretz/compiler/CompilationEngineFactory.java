package com.pretz.compiler;

import com.pretz.compiler.compengine.CompilationEngine;
import com.pretz.compiler.compengine.CompilationValidator;
import com.pretz.compiler.compengine.StatementCompilationEngine;
import com.pretz.compiler.compengine.elements.terminal.TerminalMapper;

public class CompilationEngineFactory {

    public CompilationEngine create() {
        return new CompilationEngine(new CompilationValidator(),
                new StatementCompilationEngine(new CompilationValidator()),
                new TerminalMapper());
    }
}
