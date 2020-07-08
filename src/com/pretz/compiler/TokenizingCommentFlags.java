package com.pretz.compiler;

class TokenizingCommentFlags {
    private final boolean potentialCommentStart;
    private final boolean lineComment;
    private final boolean blockComment;
    private final boolean potentialBlockCommentEnd;

    protected TokenizingCommentFlags(boolean potentialCommentStart, boolean lineComment, boolean blockComment, boolean potentialBlockCommentEnd) {
        this.potentialCommentStart = potentialCommentStart;
        this.lineComment = lineComment;
        this.blockComment = blockComment;
        this.potentialBlockCommentEnd = potentialBlockCommentEnd;
    }

    protected TokenizingCommentFlags() {
        this(false, false, false, false);
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

    protected TokenizingCommentFlags potentialCommentStart() {
        return new TokenizingCommentFlags(true, lineComment, blockComment, potentialBlockCommentEnd);
    }

    protected TokenizingCommentFlags notCommentStart() {
        return new TokenizingCommentFlags(false, lineComment, blockComment, potentialBlockCommentEnd);
    }

    protected TokenizingCommentFlags lineCommentStart() {
        return new TokenizingCommentFlags(false, true, blockComment, potentialBlockCommentEnd);
    }

    protected TokenizingCommentFlags blockCommentStart() {
        return new TokenizingCommentFlags(false, lineComment, true, potentialBlockCommentEnd);
    }

    protected TokenizingCommentFlags potentialBlockCommentEnd() {
        return new TokenizingCommentFlags(potentialCommentStart, lineComment, blockComment, true);
    }

    protected TokenizingCommentFlags lineCommentEnd() {
        return new TokenizingCommentFlags(potentialCommentStart, false, blockComment, potentialBlockCommentEnd);
    }

    protected TokenizingCommentFlags blockCommentEnd() {
        return new TokenizingCommentFlags(potentialCommentStart, lineComment, false, false);
    }

    public TokenizingCommentFlags notBlockCommentEnd() {
        return new TokenizingCommentFlags(potentialCommentStart, lineComment, blockComment, false);
    }
}
