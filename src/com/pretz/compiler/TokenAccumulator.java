package com.pretz.compiler;

public class TokenAccumulator {
    private final String tokens;
    private final TokenizingCommentFlags tFlags;

    protected TokenAccumulator(String tokens, TokenizingCommentFlags tFlags) {
        this.tokens = tokens;
        this.tFlags = tFlags;
    }

    protected TokenAccumulator() {
        this.tokens = "";
        this.tFlags = new TokenizingCommentFlags();
    }

    protected String tokens() {
        return tokens;
    }

    protected TokenizingCommentFlags pFlags() {
        return tFlags;
    }

    protected TokenAccumulator potentialCommentStart(Character ch) {
        return new TokenAccumulator(tokens + ch, tFlags.potentialCommentStart());
    }

    protected TokenAccumulator notCommentStart(Character ch) {
        return new TokenAccumulator(tokens + ch, tFlags.notCommentStart());
    }

    protected TokenAccumulator blockCommentStart() {
        return new TokenAccumulator(StringUtils.removeLastChar(tokens), tFlags.blockCommentStart());
    }

    protected TokenAccumulator lineCommentStart() {
        return new TokenAccumulator(StringUtils.removeLastChar(tokens), tFlags.lineCommentStart());
    }

    protected TokenAccumulator lineCommentEnd(Character ch) {
        return new TokenAccumulator(tokens + ch, tFlags.lineCommentEnd());
    }

    protected TokenAccumulator potentialBlockCommentEnd(Character ch) {
        return new TokenAccumulator(tokens + ch, tFlags.potentialBlockCommentEnd());
    }

    protected TokenAccumulator blockCommentEnd() {
        return new TokenAccumulator(StringUtils.removeLastChar(tokens), tFlags.blockCommentEnd());
    }

    protected TokenAccumulator notBlockCommentEnd() {
        return new TokenAccumulator(StringUtils.removeLastChar(tokens), tFlags.notBlockCommentEnd());
    }

    protected TokenAccumulator token(Character ch) {
        return new TokenAccumulator(tokens + ch, tFlags);
    }
}
