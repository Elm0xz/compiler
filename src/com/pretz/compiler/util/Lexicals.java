package com.pretz.compiler.util;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

public class Lexicals {

    private static final Set<Character> nonOps = HashSet.of(
            '{', '}',
            '(', ')',
            '[', ']',
            '.', ',',
            ';'
    );

    private static final Set<Character> ops = HashSet.of(
            '+', '-',
            '*', '/',
            '&', '|',
            '<', '>',
            '='
    );

    private static final Set<Character> unaryOps = HashSet.of(
            '-', '~'
    );

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
        return nonOps.addAll(ops).addAll(unaryOps);
    }

    public static Set<String> keywords() {
        return keywords;
    }

    public static Set<Character> ops() {
        return ops;
    }

    public static Set<Character> unaryOps() {
        return unaryOps;
    }

    public static Set<Character> nonOps() {
        return nonOps;
    }
}
