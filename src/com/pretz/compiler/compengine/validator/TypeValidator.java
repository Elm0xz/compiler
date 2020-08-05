package com.pretz.compiler.compengine.validator;

import com.pretz.compiler.compengine.CompilationException;
import com.pretz.compiler.tokenizer.token.KeywordType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;

public class TypeValidator implements Validator {

    @Override
    public void validate(Token token) {
        if (isNotPrimitiveType(token) && token.isNot(TokenType.IDENTIFIER))
            throw new CompilationException(CompilationException.INVALID_TYPE);
    }

    private boolean isNotPrimitiveType(Token token) {
        return token.isNot(TokenType.KEYWORD) ||
                (token.isNot(KeywordType.INT) &&
                        token.isNot(KeywordType.BOOLEAN) &&
                        token.isNot(KeywordType.CHAR));
    }
}
