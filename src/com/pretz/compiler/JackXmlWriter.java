package com.pretz.compiler;

import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class JackXmlWriter {

    public void write(List<Token> tokens, File file) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(toXml(file)));
            writer.write("<tokens>\n");
            tokens.forEach(token -> write(token, writer));
            writer.write("</tokens>\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void write(Token token, BufferedWriter writer) {
        try {
            writer.write(translate(token));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String translate(Token token) {
        return "<" + translateTokenType(token) + "> " + translateTokenName(token) + " </" + translateTokenType(token) + ">\n";
    }

    private String translateTokenType(Token token) {
        return Match(token.type()).of(
                Case($(isKeyword()), "keyword"),
                Case($(isSymbol()), "symbol"),
                Case($(isIntegerConst()), "integerConstant"),
                Case($(isStringConst()), "stringConstant"),
                Case($(isIdentifier()), "identifier"),
                Case($(), ""));
    }

    private Predicate<TokenType> isKeyword() {
        return it -> it == TokenType.KEYWORD;
    }

    private Predicate<TokenType> isSymbol() {
        return it -> it == TokenType.SYMBOL;
    }

    private Predicate<TokenType> isIntegerConst() {
        return it -> it == TokenType.INT_CONST;
    }

    private Predicate<TokenType> isStringConst() {
        return it -> it == TokenType.STRING_CONST;
    }

    private Predicate<TokenType> isIdentifier() {
        return it -> it == TokenType.IDENTIFIER;
    }

    private String translateTokenName(Token token) {
        return Match(token.token()).of(
                Case($(isLessThan()), "&lt;"),
                Case($(isGreaterThan()), "&gt;"),
                Case($(isQuotationMark()), "&quot;"),
                Case($(isAmpersand()), "&amp;"),
                Case($(), token.token()));
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

    private String toXml(File file) {
        return file.getAbsolutePath().replaceAll("\\.jack", "_1T.xml");
    }
}
