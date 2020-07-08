package com.pretz.compiler;

import io.vavr.collection.List;

public class TokenAccumulator {
    private final List<Character> tokens;
    private final TokenizingFlags tFlags;

    protected TokenAccumulator(List<Character> tokens, TokenizingFlags tFlags) {
        this.tokens = tokens;
        this.tFlags = tFlags;
    }

    protected TokenAccumulator() {
        this.tokens = List.of();
        this.tFlags = new TokenizingFlags();
    }

    protected List<Character> tokens() {
        return tokens;
    }

    protected TokenizingFlags pFlags() {
        return tFlags;
    }

    protected TokenAccumulator potentialCommentStart(Character ch) {
        return new TokenAccumulator(appendTo(tokens, ch), tFlags.potentialCommentStart());
    }

    protected TokenAccumulator notCommentStart(Character ch) {
        return new TokenAccumulator(appendTo(tokens, ch), tFlags.notCommentStart());
    }

    protected TokenAccumulator blockCommentStart() {
        return new TokenAccumulator(removeFrom(tokens), tFlags.blockCommentStart());
    }

    protected TokenAccumulator lineCommentStart() {
        return new TokenAccumulator(removeFrom(tokens), tFlags.lineCommentStart());
    }

    protected TokenAccumulator lineCommentEnd(Character ch) {
        return new TokenAccumulator(appendTo(tokens, ch), tFlags.lineCommentEnd());
    }

    protected TokenAccumulator potentialBlockCommentEnd(Character ch) {
        return new TokenAccumulator(appendTo(tokens, ch), tFlags.potentialBlockCommentEnd());
    }

    protected TokenAccumulator blockCommentEnd() {
        return new TokenAccumulator(removeFrom(tokens), tFlags.blockCommentEnd());
    }

    protected TokenAccumulator notBlockCommentEnd() {
        return new TokenAccumulator(removeFrom(tokens), tFlags.notBlockCommentEnd());
    }

    protected TokenAccumulator token(Character ch) {
        return new TokenAccumulator(appendTo(tokens, ch), tFlags);
    }

    private List<Character> appendTo(List<Character> tokens, Character ch) {
            return tokens.append(ch);
    }

    private List<Character> removeFrom(List<Character> tokens) {
        return tokens.subSequence(0, tokens.size() - 1);
    }

    private String getLastToken(List<String> tokens) {
        return tokens.get(tokens.size() - 1);
    }
}
