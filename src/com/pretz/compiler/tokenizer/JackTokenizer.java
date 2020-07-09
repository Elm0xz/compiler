package com.pretz.compiler.tokenizer;

import com.pretz.compiler.tokenizer.comments.CommentAccumulator;
import com.pretz.compiler.tokenizer.comments.CommentFlags;
import com.pretz.compiler.input.JackInputReader;
import com.pretz.compiler.tokenizer.tokenizing.TokenizingAccumulator;
import io.vavr.collection.Stream;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.anyOf;

//TODO a little cleanup
public class JackTokenizer {

    public List<Token> tokenize(File file) {

        List<Token> tokens = Stream.ofAll(new JackInputReader().read(file))
                .foldLeft(new CommentAccumulator(), this::removeComments)
                .tokens().toStream()
                .foldLeft(new TokenizingAccumulator(), this::tokenize)
                .tokens().dropRight(1)
                .toJavaList();
        tokens.forEach(System.out::println);
        return tokens;
    }

    private CommentAccumulator removeComments(CommentAccumulator acc, Character ch) {
        return Match(new CommentStep(acc, ch)).of(
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

    private TokenizingAccumulator tokenize(TokenizingAccumulator acc, Character ch) {
        return Match(new TokenizingStep(acc, ch)).of(
                Case($(isStringConstStart()), st -> st.acc.stringConstStart()),
                Case($(isStringConstEnd()), st -> st.acc.stringConstEnd()),
                Case($(isStringConst()), st -> st.acc.add(st.ch)),
                Case($(isWhitespace()), st -> st.acc.whitespace()),
                Case($(isSymbol()), st -> st.acc.symbol(st.ch)),
                Case($(), st -> st.acc.add(st.ch)));
    }

    private Predicate<CommentStep> isPotentialCommentStart() {
        return (it) -> !isComment(it.acc.flags())
                && !it.acc.flags().isPotentialCommentStart() && it.ch == '/';
    }

    private boolean isComment(CommentFlags flags) {
        return flags.isBlockComment() || flags.isLineComment();
    }

    private Predicate<CommentStep> isNotCommentStart() {
        return (it) -> it.acc.flags().isPotentialCommentStart() && !(it.ch == '*' || it.ch == '/');
    }

    private Predicate<CommentStep> isBlockCommentStart() {
        return (it) -> it.acc.flags().isPotentialCommentStart() && it.ch == '*';
    }

    private Predicate<CommentStep> isLineCommentStart() {
        return (it) -> it.acc.flags().isPotentialCommentStart() && it.ch == '/';
    }

    private Predicate<CommentStep> isLineCommentEnd() {
        return (it) -> it.acc.flags().isLineComment() && (it.ch == '\r' || it.ch == '\n');
    }

    private Predicate<CommentStep> isLineComment() {
        return (it) -> it.acc.flags().isLineComment();
    }

    private Predicate<CommentStep> isBlockComment() {
        return (it) -> it.acc.flags().isBlockComment();
    }

    private Predicate<CommentStep> isPotentialBlockCommentEnd() {
        return (it) -> it.acc.flags().isBlockComment() && it.ch == '*';
    }

    private Predicate<CommentStep> isBlockCommentEnd() {
        return (it) -> it.acc.flags().isBlockComment() && it.acc.flags().isPotentialBlockCommentEnd() && it.ch == '/';
    }

    private Predicate<CommentStep> isNotBlockCommentEnd() {
        return (it) -> it.acc.flags().isBlockComment() && it.acc.flags().isPotentialBlockCommentEnd() && it.ch != '/';
    }

    private Predicate<TokenizingStep> isStringConstStart() {
        return (it) -> it.ch == '\"' && !it.acc.flags().isStringConst();
    }

    private Predicate<TokenizingStep> isStringConstEnd() {
        return (it) -> it.ch == '\"' && it.acc.flags().isStringConst();
    }

    private Predicate<TokenizingStep> isStringConst() {
        return (it) -> it.acc.flags().isStringConst();
    }

    private Predicate<TokenizingStep> isWhitespace() {
        return (it) -> Character.isWhitespace(it.ch) && !it.acc.flags().isStringConst();
    }

    private Predicate<TokenizingStep> isSymbol() {
        return (it) -> Lexicals.symbols().contains(it.ch);
    }

    private record CommentStep(CommentAccumulator acc, Character ch) {
    }

    private record TokenizingStep(TokenizingAccumulator acc, Character ch) {
    }
}
