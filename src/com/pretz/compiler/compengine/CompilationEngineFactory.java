package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.validator.ValidatorFactory;

public class CompilationEngineFactory {

    public CompilationEngine create() {
        return new CompilationEngine(new ValidatorFactory(),
                new StatementCompilationEngine(new CompilationMatcher(), new ValidatorFactory()),
                new CompilationMatcher());
    }
}
