package com.pretz.compiler.tokenizing;

import com.pretz.compiler.Token;
import com.pretz.compiler.TokenType;
import io.vavr.collection.List;

import static com.pretz.compiler.TokenizingUtils.removeLastFrom;

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
        return new TokenizingAccumulator(startStringConstToken(), flags.stringConstStart());
    }

    public TokenizingAccumulator stringConstEnd() {
        return new TokenizingAccumulator(endStringConstToken(), flags.stringConstEnd());
    }

    public TokenizingAccumulator add(Character ch) {
        return new TokenizingAccumulator(addToLastToken(ch), flags);
    }

    private List<Token> startStringConstToken() {
        return tokens.append(new Token("", TokenType.STRING_CONST));
    }

    private List<Token> endStringConstToken() {
        return tokens.append(new Token("", TokenType.UNKNOWN));
    }

    private List<Token> addToLastToken(Character ch) {
        if (tokens.isEmpty()) {
            return tokens.append(new Token("" + ch, TokenType.UNKNOWN));
        }
        return removeLastFrom(tokens).append(tokens.last().add(ch));
    }
}
