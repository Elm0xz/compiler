package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.elements.terminal.TerminalMapper;

public class CompilationEngineFactory {

    public CompilationEngine create() {
        return new CompilationEngine(new CompilationValidator(),
                new StatementCompilationEngine(new CompilationValidator(), new TerminalMapper()),
                new TerminalMapper());
    }
}
