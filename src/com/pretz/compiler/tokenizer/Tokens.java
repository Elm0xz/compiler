package com.pretz.compiler.tokenizer;

import io.vavr.collection.List;

/**
 * Wrapper for immutable list of tokens used as tokenizer output type. It is not immutable itself,
 * because index is used to make iteration through token list more flexible.
 */
public class Tokens {
    private int index;
    private final List<Token> tokens;

    public Tokens(List<Token> tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    public void advance() {
        index++;
    }

    public Token current() {
        return tokens.get(index);
    }
}
