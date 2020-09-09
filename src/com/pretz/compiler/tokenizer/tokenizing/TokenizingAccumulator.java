package com.pretz.compiler.tokenizer.tokenizing;

import com.pretz.compiler.util.Lexicals;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import io.vavr.collection.List;

import java.util.function.Predicate;

import static com.pretz.compiler.util.ListUtils.removeLastFrom;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class TokenizingAccumulator {
    private final List<Token> tokens;
    private final TokenizingFlags flags;

    private TokenizingAccumulator(List<Token> tokens, TokenizingFlags flags) {
        this.tokens = tokens;
        this.flags = flags;
    }

    public TokenizingAccumulator() {
        this.tokens = List.empty();
        this.flags = new TokenizingFlags();
    }

    public List<Token> tokens() {
        return tokens;
    }

    public TokenizingFlags flags() {
        return flags;
    }

    public TokenizingAccumulator stringConstStart() {
        if (tokens.last().token().isEmpty()) {
            return new TokenizingAccumulator(changeCurrentTokenType(TokenType.STRING_CONST), flags.stringConstStart());
        }
        return new TokenizingAccumulator(startStringConstToken(), flags.stringConstStart());
    }

    public TokenizingAccumulator stringConstEnd() {
        return new TokenizingAccumulator(startNewToken(), flags.stringConstEnd());
    }

    public TokenizingAccumulator add(Character ch) {
        return new TokenizingAccumulator(addCharacterToCurrentToken(ch), flags);
    }

    public TokenizingAccumulator whitespace() {
        if (tokens.isEmpty() || tokens.last().token().isEmpty()) {
            return this;
        } else if (tokens.last().type() == TokenType.NEW) {
            return new TokenizingAccumulator(
                    new TokenizingAccumulator(classifyCurrentToken(), flags)
                            .startNewToken(), flags);
        }
        return new TokenizingAccumulator(startNewToken(), flags);
    }

    public TokenizingAccumulator symbol(Character ch) {
        if (tokens.last().token().isEmpty()) {
            return new TokenizingAccumulator(addCharacterToCurrentTokenAndChangeType(ch, TokenType.SYMBOL), flags);
        } else if (tokens.last().type() == TokenType.NEW) {
            return new TokenizingAccumulator(classifyCurrentToken().append(symbolToken(ch)), flags);
        }
        return new TokenizingAccumulator(tokens.append(symbolToken(ch)), flags);
    }

    private List<Token> startStringConstToken() {
        return tokens.append(new Token("", TokenType.STRING_CONST));
    }

    private List<Token> startNewToken() {
        return tokens.append(new Token("", TokenType.NEW));
    }

    private List<Token> addCharacterToCurrentToken(Character ch) {
        if (tokens.isEmpty() || tokens.last().type() == TokenType.SYMBOL) {
            return tokens.append(new Token("" + ch, TokenType.NEW));
        }
        return removeLastFrom(tokens).append(tokens.last().add(ch));
    }

    //TODO(L) maybe it would be better to just remove "empty new" tokens?
    private List<Token> addCharacterToCurrentTokenAndChangeType(Character ch, TokenType newType) {
        if (tokens.isEmpty()) {
            return tokens.append(new Token("" + ch, newType));
        }
        return removeLastFrom(tokens).append(tokens.last().add(ch).changeType(newType));
    }

    private Token symbolToken(Character ch) {
        return new Token("" + ch, TokenType.SYMBOL);
    }

    private List<Token> classifyCurrentToken() {
        TokenType type = Match(tokens.last()).of(
                Case($(isKeyword()), TokenType.KEYWORD),
                Case($(isIntegerConstant()), TokenType.INT_CONST),
                Case($(isIdentifier()), TokenType.IDENTIFIER),
                Case($(), TokenType.UNKNOWN));
        return changeCurrentTokenType(type);
    }

    private Predicate<Token> isKeyword() {
        return (it) -> Lexicals.keywords().contains(it.token());
    }

    private Predicate<Token> isIntegerConstant() {
        return (it) -> it.token().matches("\\d+") && Integer.parseInt(it.token()) < 32768;
    }

    private Predicate<Token> isIdentifier() {
        return (it) -> it.token().matches("^[A-z][a-zA-Z0-9_]*");
    }

    private List<Token> changeCurrentTokenType(TokenType newType) {
        return removeLastFrom(tokens).append(tokens.last().changeType(newType));
    }
}
