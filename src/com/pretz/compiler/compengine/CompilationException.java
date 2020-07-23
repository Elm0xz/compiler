package com.pretz.compiler.compengine;

public class CompilationException extends RuntimeException {
    public CompilationException(String s) {
        super(s);
    }

    public static final String NOT_A_CLASS = "Token set does not begin with 'class' keyword";
    public static final String INVALID_CLASS_IDENTIFIER = "Identifier expected when there wasn't any";
    public static final String NOT_AN_OPENING_BRACKET = "Opening bracket expected when there wasn't any";
    public static final String NOT_A_CLOSING_BRACKET = "Closing bracket expected when there wasn't any";

}
