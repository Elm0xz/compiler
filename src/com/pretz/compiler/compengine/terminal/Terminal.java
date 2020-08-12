package com.pretz.compiler.compengine.terminal;

import com.pretz.compiler.compengine.Element;
import com.pretz.compiler.compengine.validator.Validator;
import com.pretz.compiler.tokenizer.token.KeywordType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.util.Lexicals;

import java.util.Objects;

import static com.pretz.compiler.util.XmlUtils.indent;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class Terminal implements Element {
    private final String token;
    private final TerminalType type;
    private final TerminalKeywordType keywordType;

    public Terminal(Token token, Validator validator) {
        validator.validate(token);
        this.token = token.token();
        this.type = map(token.type());
        this.keywordType = map(token.keyword());
    }

    public Terminal(Token token) {
        this.token = token.token();
        this.type = map(token.type());
        this.keywordType = map(token.keyword());
    }

    private static TerminalType map(TokenType type) {
        return Enum.valueOf(TerminalType.class, type.toString().toUpperCase());
    }

    private static TerminalKeywordType map(KeywordType type) {
        return Enum.valueOf(TerminalKeywordType.class, type.toString().toUpperCase());
    }

    public Terminal(String token, TerminalType type) {
        this.token = token;
        this.type = type;
        if (type != TerminalType.KEYWORD)
            this.keywordType = TerminalKeywordType.NOT_A_KEYWORD;
        else
            this.keywordType = setKeywordType(token);
    }

    private TerminalKeywordType setKeywordType(String token) {
        if (Lexicals.keywords().contains(token))
            return TerminalKeywordType.valueOf(token.toUpperCase());
        else throw new IllegalArgumentException("Invalid keyword type");
    }

    public String toXml(int indLvl) {
        return indent(indLvl) + "<" + toTag() + "> " + token + " </" + toTag() + ">\n";
    }

    private String toTag() {
        return Match(type).of(
                Case($(TerminalType.IDENTIFIER), "identifier"),
                Case($(TerminalType.KEYWORD), "keyword"),
                Case($(), "NOT YET IMPLEMENTED")
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Terminal terminal = (Terminal) o;
        return token.equals(terminal.token) &&
                type == terminal.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, type);
    }
}
