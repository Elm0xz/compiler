package com.pretz.compiler;

class TokenizingFlags {
    private final boolean potentialCommentStart;
    private final boolean lineComment;
    private final boolean blockComment;
    private final boolean potentialBlockCommentEnd;
    private final boolean stringValue;

    protected TokenizingFlags(boolean potentialCommentStart, boolean lineComment, boolean blockComment, boolean potentialBlockCommentEnd, boolean stringValue) {
        this.potentialCommentStart = potentialCommentStart;
        this.lineComment = lineComment;
        this.blockComment = blockComment;
        this.potentialBlockCommentEnd = potentialBlockCommentEnd;
        this.stringValue = stringValue;
    }

    protected TokenizingFlags() {
        this(false, false, false, false, false);
    }

    protected boolean isPotentialCommentStart() {
        return potentialCommentStart;
    }

    protected boolean isLineComment() {
        return lineComment;
    }

    protected boolean isBlockComment() {
        return blockComment;
    }

    protected boolean isPotentialBlockCommentEnd() {
        return potentialBlockCommentEnd;
    }

    protected TokenizingFlags potentialCommentStart() {
        return new TokenizingFlags(true, lineComment, blockComment, potentialBlockCommentEnd, stringValue);
    }

    protected TokenizingFlags notCommentStart() {
        return new TokenizingFlags(false, lineComment, blockComment, potentialBlockCommentEnd, stringValue);
    }

    protected TokenizingFlags lineCommentStart() {
        return new TokenizingFlags(false, true, blockComment, potentialBlockCommentEnd, stringValue);
    }

    protected TokenizingFlags blockCommentStart() {
        return new TokenizingFlags(false, lineComment, true, potentialBlockCommentEnd, stringValue);
    }

    protected TokenizingFlags potentialBlockCommentEnd() {
        return new TokenizingFlags(potentialCommentStart, lineComment, blockComment, true, stringValue);
    }

    protected TokenizingFlags lineCommentEnd() {
        return new TokenizingFlags(potentialCommentStart, false, blockComment, potentialBlockCommentEnd, stringValue);
    }

    protected TokenizingFlags blockCommentEnd() {
        return new TokenizingFlags(potentialCommentStart, lineComment, false, false, stringValue);
    }

    public TokenizingFlags notBlockCommentEnd() {
        return new TokenizingFlags(potentialCommentStart, lineComment, blockComment, false, stringValue);
    }

    public TokenizingFlags stringValueStart() {
        return new TokenizingFlags(potentialCommentStart, lineComment, blockComment, potentialBlockCommentEnd, true);
    }

    public TokenizingFlags stringValueEnd() {
        return new TokenizingFlags(potentialCommentStart, lineComment, blockComment, potentialBlockCommentEnd, false);
    }
}
