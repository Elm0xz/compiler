package com.pretz.compiler.tokenizer;

import com.pretz.compiler.tokenizer.Token;
import io.vavr.collection.List;

/**
 * Wrapper for immutable list of tokens used as tokenizer output type.
 */
public class Tokens {
    private static int index = 0;
    private final List<Token> tokens;

    public Tokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Token advance() {
        return tokens.get(index++);
    }
}
