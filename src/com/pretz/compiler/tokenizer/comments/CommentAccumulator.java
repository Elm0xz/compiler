package com.pretz.compiler.tokenizer.comments;

import io.vavr.collection.List;

import static com.pretz.compiler.ListUtils.appendTo;
import static com.pretz.compiler.ListUtils.removeLastFrom;

public class CommentAccumulator {
    private final List<Character> tokens;
    private final CommentFlags flags;

    private CommentAccumulator(List<Character> tokens, CommentFlags flags) {
        this.tokens = tokens;
        this.flags = flags;
    }

    public CommentAccumulator() {
        this.tokens = List.of();
        this.flags = new CommentFlags();
    }

    public List<Character> tokens() {
        return tokens;
    }

    public CommentFlags flags() {
        return flags;
    }

    public CommentAccumulator potentialCommentStart(Character ch) {
        return new CommentAccumulator(appendTo(tokens, ch), flags.potentialCommentStart());
    }

    public CommentAccumulator notCommentStart(Character ch) {
        return new CommentAccumulator(appendTo(tokens, ch), flags.notCommentStart());
    }

    public CommentAccumulator blockCommentStart() {
        return new CommentAccumulator(removeLastFrom(tokens), flags.blockCommentStart());
    }

    public CommentAccumulator lineCommentStart() {
        return new CommentAccumulator(removeLastFrom(tokens), flags.lineCommentStart());
    }

    public CommentAccumulator lineCommentEnd(Character ch) {
        return new CommentAccumulator(appendTo(tokens, ch), flags.lineCommentEnd());
    }

    public CommentAccumulator potentialBlockCommentEnd(Character ch) {
        return new CommentAccumulator(appendTo(tokens, ch), flags.potentialBlockCommentEnd());
    }

    public CommentAccumulator blockCommentEnd() {
        return new CommentAccumulator(removeLastFrom(tokens), flags.blockCommentEnd());
    }

    public CommentAccumulator notBlockCommentEnd() {
        return new CommentAccumulator(removeLastFrom(tokens), flags.notBlockCommentEnd());
    }

    public CommentAccumulator token(Character ch) {
        return new CommentAccumulator(appendTo(tokens, ch), flags);
    }

    private String getLastToken(List<String> tokens) {
        return tokens.get(tokens.size() - 1);
    }
}
