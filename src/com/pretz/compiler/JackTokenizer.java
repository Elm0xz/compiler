package com.pretz.compiler;

import io.vavr.collection.Stream;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.anyOf;

public class JackTokenizer {

    public List<Token> tokenize(File file) {

        List<Character> tokens = Stream.ofAll(new JackInputReader().read(file))
                .foldLeft(new TokenAccumulator(), this::removeCommentsAndGroupByWhitespace)
                .tokens()
                //TODO remove whitespaces except for these in string literals
                //TODO prepare tokens
                .toJavaList();
        tokens.forEach(System.out::print);
        return List.of();
    }

    private TokenAccumulator removeCommentsAndGroupByWhitespace(TokenAccumulator acc, Character ch) {
        return Match(new TokenizingStep(acc, ch)).of(
                Case($(isPotentialCommentStart()), st -> st.acc.potentialCommentStart(st.ch)),
                Case($(isNotCommentStart()), st -> st.acc.notCommentStart(st.ch)),
                Case($(isBlockCommentStart()), st -> st.acc.blockCommentStart()),
                Case($(isLineCommentStart()), st -> st.acc.lineCommentStart()),
                Case($(isLineCommentEnd()), st -> st.acc.lineCommentEnd(st.ch)),
                Case($(isPotentialBlockCommentEnd()), st -> st.acc.potentialBlockCommentEnd(st.ch)),
                Case($(isBlockCommentEnd()), st -> st.acc.blockCommentEnd()),
                Case($(isNotBlockCommentEnd()), st -> st.acc.notBlockCommentEnd()),
                Case($(anyOf(isLineComment(), isBlockComment())), st -> st.acc),
                Case($(), st -> st.acc.token(st.ch)));
    }

    private Predicate<TokenizingStep> isPotentialCommentStart() {
        return (it) -> !isComment(it.acc.pFlags())
                && !it.acc.pFlags().isPotentialCommentStart() && it.ch == '/';
    }

    private boolean isComment(TokenizingFlags flags) {
        return flags.isBlockComment() || flags.isLineComment();
    }

    private Predicate<TokenizingStep> isNotCommentStart() {
        return (it) -> it.acc.pFlags().isPotentialCommentStart() && !(it.ch == '*' || it.ch == '/');
    }

    private Predicate<TokenizingStep> isBlockCommentStart() {
        return (it) -> it.acc.pFlags().isPotentialCommentStart() && it.ch == '*';
    }

    private Predicate<TokenizingStep> isLineCommentStart() {
        return (it) -> it.acc.pFlags().isPotentialCommentStart() && it.ch == '/';
    }

    private Predicate<TokenizingStep> isLineCommentEnd() {
        return (it) -> it.acc.pFlags().isLineComment() && it.ch == '\r';
    }

    private Predicate<TokenizingStep> isLineComment() {
        return (it) -> it.acc.pFlags().isLineComment();
    }

    private Predicate<TokenizingStep> isBlockComment() {
        return (it) -> it.acc.pFlags().isBlockComment();
    }

    private Predicate<TokenizingStep> isPotentialBlockCommentEnd() {
        return (it) -> it.acc.pFlags().isBlockComment() && it.ch == '*';
    }

    private Predicate<TokenizingStep> isBlockCommentEnd() {
        return (it) -> it.acc.pFlags().isBlockComment() && it.acc.pFlags().isPotentialBlockCommentEnd() && it.ch == '/';
    }

    private Predicate<TokenizingStep> isNotBlockCommentEnd() {
        return (it) -> it.acc.pFlags().isBlockComment() && it.acc.pFlags().isPotentialBlockCommentEnd() && it.ch != '/';
    }

    private record TokenizingStep(TokenAccumulator acc, Character ch) {
    }
}
