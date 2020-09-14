package com.pretz.compiler.compengine.terminal;

import com.pretz.compiler.compengine.CompilationException;
import com.pretz.compiler.compengine.Element;
import com.pretz.compiler.compengine.VmKeyword;
import com.pretz.compiler.compengine.expression.OpType;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.validator.Validator;
import com.pretz.compiler.tokenizer.token.KeywordType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.util.Lexicals;

import java.util.Objects;
import java.util.function.Predicate;

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
                Case($(TerminalType.KEYWORD_CONST), "keyword"),
                Case($(TerminalType.OP), "symbol"),
                Case($(TerminalType.UNARY_OP), "symbol"),
                Case($(TerminalType.INT_CONST), "integerConstant"),
                Case($(TerminalType.STRING_CONST), "stringConstant")
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

    //TODO(L) this constructor is only used in test, should be handled by factory method instead
    public Terminal(String token, TerminalType type) {
        this.token = token;
        this.type = type;
        if (type != TerminalType.KEYWORD_CONST)
            this.keywordType = TerminalKeywordType.NOT_A_KEYWORD;
        else
            this.keywordType = setKeywordType(token);
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "token='" + token + '\'' +
                ", type=" + type +
                ", keywordType=" + keywordType +
                '}';
    }

    public String token() {
        return token;
    }

    //TODO implement other terminal types
    @Override
    public String toVm(SymbolTable symbolTable) {
        return Match(type).of(
                Case($(TerminalType.KEYWORD_CONST), "NOT YET IMPLEMENTED\n"),
                Case($(TerminalType.OP), this::opToVm),
                Case($(TerminalType.UNARY_OP), this::unaryOpToVm),
                Case($(TerminalType.INT_CONST), VmKeyword.CONSTANT + " " + token + "\n"),
                Case($(TerminalType.STRING_CONST), "NOT YET IMPLEMENTED\n"),
                Case($(), "NOT YET IMPLEMENTED\n")
        );
    }

    //TODO implement comparison operators
    private String opToVm() {
        return Match(token).of(
                Case($("+"), VmKeyword.ADD + "\n"),
                Case($("-"), VmKeyword.SUB + "\n"),
                Case($("&"), VmKeyword.AND + "\n"),
                Case($("|"), VmKeyword.OR + "\n"),
                Case($(), "NOT YET IMPLEMEMENTED\n"));
    }

    private String unaryOpToVm() {
        return Match(token).of(
                Case($("-"), VmKeyword.NEG + "\n"),
                Case($("~"), VmKeyword.NOT + "\n"),
                Case($(), this::throwIllegalOpException));
    }

    private String throwIllegalOpException() {
        throw new CompilationException(CompilationException.ILLEGAL_OP);
    }
}
