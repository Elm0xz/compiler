package com.pretz.compiler.compengine.terminal;

import com.pretz.compiler.compengine.CompilationException;
import com.pretz.compiler.compengine.Element;
import com.pretz.compiler.compengine.VmContext;
import com.pretz.compiler.compengine.VmKeyword;
import com.pretz.compiler.compengine.expression.OpType;
import com.pretz.compiler.compengine.validator.Validator;
import com.pretz.compiler.tokenizer.token.KeywordType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.util.Lexicals;
import io.vavr.collection.List;

import java.util.Objects;
import java.util.function.Predicate;

import static com.pretz.compiler.compengine.VmKeyword.ADD;
import static com.pretz.compiler.compengine.VmKeyword.AND;
import static com.pretz.compiler.compengine.VmKeyword.CALL;
import static com.pretz.compiler.compengine.VmKeyword.CONSTANT;
import static com.pretz.compiler.compengine.VmKeyword.DIV;
import static com.pretz.compiler.compengine.VmKeyword.EQ;
import static com.pretz.compiler.compengine.VmKeyword.GT;
import static com.pretz.compiler.compengine.VmKeyword.LT;
import static com.pretz.compiler.compengine.VmKeyword.MULT;
import static com.pretz.compiler.compengine.VmKeyword.NEG;
import static com.pretz.compiler.compengine.VmKeyword.OR;
import static com.pretz.compiler.compengine.VmKeyword.POINTER;
import static com.pretz.compiler.compengine.VmKeyword.PUSH;
import static com.pretz.compiler.compengine.VmKeyword.STRING_APPEND;
import static com.pretz.compiler.compengine.VmKeyword.STRING_NEW;
import static com.pretz.compiler.compengine.VmKeyword.SUB;
import static com.pretz.compiler.compengine.terminal.TerminalKeywordType.FALSE;
import static com.pretz.compiler.compengine.terminal.TerminalKeywordType.NULL;
import static com.pretz.compiler.compengine.terminal.TerminalKeywordType.THIS;
import static com.pretz.compiler.compengine.terminal.TerminalKeywordType.TRUE;
import static com.pretz.compiler.compengine.terminal.TerminalType.INT_CONST;
import static com.pretz.compiler.compengine.terminal.TerminalType.KEYWORD_CONST;
import static com.pretz.compiler.compengine.terminal.TerminalType.OP;
import static com.pretz.compiler.compengine.terminal.TerminalType.STRING_CONST;
import static com.pretz.compiler.compengine.terminal.TerminalType.UNARY_OP;
import static com.pretz.compiler.util.XmlUtils.indent;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class Terminal implements Element {
    protected final String token;
    private final TerminalType type;
    private final TerminalKeywordType keywordType;

    public Terminal(Token token, Validator validator, OpType opType) {
        validator.validate(token);
        this.token = token.token();
        this.type = mapOpFromSymbol(opType);
        this.keywordType = map(token.keyword());
    }

    public Terminal(Token token, Validator validator) {
        validator.validate(token);
        this.token = token.token();
        this.type = mapFromNonSymbol(token.type());
        this.keywordType = map(token.keyword());
    }

    public Terminal(Token token) {
        this.token = token.token();
        this.type = mapFromNonSymbol(token.type());
        this.keywordType = map(token.keyword());
    }

    private TerminalType mapOpFromSymbol(OpType opType) {
        return new TerminalTypeMapper().fromSymbol(opType);
    }

    private static TerminalType mapFromNonSymbol(TokenType type) {
        return new TerminalTypeMapper().fromNonSymbol(type);
    }

    private static TerminalKeywordType map(KeywordType type) {
        return Enum.valueOf(TerminalKeywordType.class, type.toString().toUpperCase());
    }

    private TerminalKeywordType setKeywordType(String token) {
        if (Lexicals.keywords().contains(token))
            return TerminalKeywordType.valueOf(token.toUpperCase());
        else throw new IllegalArgumentException("Invalid keyword type");
    }

    public String toXml(int indLvl) {
        return indent(indLvl) + "<" + toTag() + "> " + translateToken() + " </" + toTag() + ">\n";
    }

    public TerminalType type() {
        return type;
    }

    public TerminalKeywordType keywordType() {
        return keywordType;
    }

    private String toTag() {
        return Match(type).of(
                Case($(TerminalType.IDENTIFIER), "identifier"),
                Case($(KEYWORD_CONST), "keyword"),
                Case($(OP), "symbol"),
                Case($(UNARY_OP), "symbol"),
                Case($(INT_CONST), "integerConstant"),
                Case($(STRING_CONST), "stringConstant")
        );
    }

    private String translateToken() {
        return Match(token).of(
                Case($(isLessThan()), "&lt;"),
                Case($(isGreaterThan()), "&gt;"),
                Case($(isQuotationMark()), "&quot;"),
                Case($(isAmpersand()), "&amp;"),
                Case($(), token));
    }

    private Predicate<String> isLessThan() {
        return it -> it.equals("<");
    }

    private Predicate<String> isGreaterThan() {
        return it -> it.equals(">");
    }

    private Predicate<String> isQuotationMark() {
        return it -> it.equals("\"");
    }

    private Predicate<String> isAmpersand() {
        return it -> it.equals("&");
    }

    //TODO(L) this constructor is only used in test, should be handled by factory method instead
    public Terminal(String token, TerminalType type) {
        this.token = token;
        this.type = type;
        if (type != KEYWORD_CONST)
            this.keywordType = TerminalKeywordType.NOT_A_KEYWORD;
        else
            this.keywordType = setKeywordType(token);
    }

    public String token() {
        return token;
    }

    //TODO implement other terminal types
    @Override
    public String toVm(VmContext vmContext) {
        return Match(type).of(
                Case($(KEYWORD_CONST), this::keywordConstToVm),
                Case($(OP), this::opToVm),
                Case($(UNARY_OP), this::unaryOpToVm),
                Case($(INT_CONST), CONSTANT + " " + token + "\n"),
                Case($(STRING_CONST), this::stringConstToVm)
        );
    }

    private String keywordConstToVm() {
        return Match(keywordType).of(
                Case($(TRUE), CONSTANT + " 1\n"
                        + NEG + "\n"),
                Case($(FALSE), CONSTANT + " 0\n"),
                Case($(THIS), POINTER + " 0\n"),
                Case($(NULL), CONSTANT + " 0\n"),
                Case($(), this::throwIllegalKeywordException));
    }

    private String opToVm() {
        return Match(token).of(
                Case($("+"), ADD + "\n"),
                Case($("-"), SUB + "\n"),
                Case($("&"), AND + "\n"),
                Case($("|"), OR + "\n"),
                Case($(">"), GT + "\n"),
                Case($("<"), LT + "\n"),
                Case($("="), EQ + "\n"),
                Case($("*"), CALL + " " + MULT + "\n"),
                Case($("/"), CALL + " " + DIV + "\n"),
                Case($(), "Generic Op - NOT YET IMPLEMENTED\n"));
    }

    private String unaryOpToVm() {
        return Match(token).of(
                Case($("-"), VmKeyword.NEG + "\n"),
                Case($("~"), VmKeyword.NOT + "\n"),
                Case($(), this::throwIllegalOpException));
    }

    private String stringConstToVm() {
        return createNewString() +
                List.ofAll(token.toCharArray())
                        .map(this::characterToVm)
                        .mkString();
    }

    private String createNewString() {
        return CONSTANT + " " + token.length() + "\n" +
                CALL + " " + STRING_NEW + " 1\n";
    }

    //TODO(!!!) Here you need to add String.new etc (Average/Main.jack)
    private String characterToVm(char ch) {
        return PUSH + " " + CONSTANT + " " + (int) ch + "\n" +
                CALL + " " + STRING_APPEND + " 2\n";
    }

    private String throwIllegalOpException() {
        throw new CompilationException(CompilationException.INVALID_OP);
    }

    private String throwIllegalKeywordException() {
        throw new CompilationException(CompilationException.INVALID_OP);
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

    @Override
    public String toString() {
        return "Terminal{" +
                "token='" + token + '\'' +
                ", type=" + type +
                ", keywordType=" + keywordType +
                '}';
    }
}
