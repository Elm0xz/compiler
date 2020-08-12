package com.pretz.compiler.util;

public class XmlUtils {

    public static String indent(int indLvl) {
        String indent = "";
        int i = indLvl;
        while (i > 0) {
            indent = indent.concat("\t");
            i--;
        }
        return indent;
    }

    public static String basicOpeningTag(int indLvl, String tagName) {
        return indent(indLvl) + "<" + tagName + ">\n";
    }

    public static String basicClosingTag(int indLvl, String tagName) {
        return indent(indLvl) + "</" + tagName + ">\n";
    }

    public static String simpleStartingKeyword(int indLvl, String keyword) {
        return indent(indLvl) + "<keyword> " + keyword +  " </keyword>\n";
    }

    public static String comma(int indLvl) {
        return indent(indLvl) + xmlSymbol(",");
    }

    public static String openingRoundBracket(int indLvl) {
        return indent(indLvl) + xmlSymbol("(");
    }

    public static String closingRoundBracket(int indLvl) {
        return indent(indLvl) + xmlSymbol(")");
    }

    public static String openingCurlyBracket(int indLvl) {
        return indent(indLvl) + xmlSymbol("{");
    }

    public static String closingCurlyBracket(int indLvl) {
        return indent(indLvl) + xmlSymbol("}");
    }

    public static String semicolon(int indLvl) {
        return indent(indLvl) + xmlSymbol(";");
    }
    private static String xmlSymbol(String symbol) {
        return "<symbol> " + symbol + " </symbol>\n";
    }
}
