package com.pretz.compiler;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public class Lexicals {
    private static final Set<Character> symbols = HashSet.of(
            '{', '}',
            '(', ')',
            '[', ']',
            '.', ',',
            ';',
            '+', '-',
            '*', '/',
            '&', '|',
            '<', '>',
            '=', '~');

    private static final Set<String> keywords = HashSet.of(
            "class",
            "constructor",
            "function", "method",
            "field", "static",
            "var", "int",
            "char", "boolean",
            "void",
            "true", "false",
            "null",
            "this",
            "let", "do",
            "if", "else",
            "while",
            "return");


    public static Set<Character> symbols() {
        return symbols;
    }

    public static Set<String> keywords() {
        return keywords;
    }
}
