package com.pretz.compiler.compengine.validator;

import com.pretz.compiler.tokenizer.token.Token;

public interface Validator {

    void validate(Token token);
}
