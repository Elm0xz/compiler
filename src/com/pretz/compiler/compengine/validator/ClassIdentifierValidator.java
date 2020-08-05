package com.pretz.compiler.compengine.validator;

import com.pretz.compiler.compengine.CompilationException;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;

public class ClassIdentifierValidator implements Validator {

    @Override
    public void validate(Token token) {
        if (token.isNot(TokenType.IDENTIFIER))
            throw new CompilationException(CompilationException.INVALID_CLASS_IDENTIFIER);
    }
}
