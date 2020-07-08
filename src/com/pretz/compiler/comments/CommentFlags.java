package com.pretz.compiler.comments;

public class CommentFlags {
    private final boolean potentialCommentStart;
    private final boolean lineComment;
    private final boolean blockComment;
    private final boolean potentialBlockCommentEnd;

    protected CommentFlags(boolean potentialCommentStart, boolean lineComment, boolean blockComment, boolean potentialBlockCommentEnd) {
        this.potentialCommentStart = potentialCommentStart;
        this.lineComment = lineComment;
        this.blockComment = blockComment;
        this.potentialBlockCommentEnd = potentialBlockCommentEnd;
    }

    protected CommentFlags() {
        this(false, false, false, false);
    }

    public boolean isPotentialCommentStart() {
        return potentialCommentStart;
    }

    public boolean isLineComment() {
        return lineComment;
    }

    public boolean isBlockComment() {
        return blockComment;
    }

    public boolean isPotentialBlockCommentEnd() {
        return potentialBlockCommentEnd;
    }

    protected CommentFlags potentialCommentStart() {
        return new CommentFlags(true, lineComment, blockComment, potentialBlockCommentEnd);
    }

    protected CommentFlags notCommentStart() {
        return new CommentFlags(false, lineComment, blockComment, potentialBlockCommentEnd);
    }

    protected CommentFlags lineCommentStart() {
        return new CommentFlags(false, true, blockComment, potentialBlockCommentEnd);
    }

    protected CommentFlags blockCommentStart() {
        return new CommentFlags(false, lineComment, true, potentialBlockCommentEnd);
    }

    protected CommentFlags potentialBlockCommentEnd() {
        return new CommentFlags(potentialCommentStart, lineComment, blockComment, true);
    }

    protected CommentFlags lineCommentEnd() {
        return new CommentFlags(potentialCommentStart, false, blockComment, potentialBlockCommentEnd);
    }

    protected CommentFlags blockCommentEnd() {
        return new CommentFlags(potentialCommentStart, lineComment, false, false);
    }

    public CommentFlags notBlockCommentEnd() {
        return new CommentFlags(potentialCommentStart, lineComment, blockComment, false);
    }
}
