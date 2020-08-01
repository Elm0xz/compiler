package com.pretz.compiler.compengine;

public class CompilationException extends RuntimeException {

    public CompilationException(String s) {
        super(s);
    }

    public static final String NOT_A_CLASS = "Token set does not begin with 'class' keyword";
    public static final String INVALID_CLASS_IDENTIFIER = "Invalid class identifier";
    public static final String NOT_AN_OPENING_CURLY_BRACKET = "Opening curly bracket expected when there wasn't any";
    public static final String NOT_A_CLOSING_CURLY_BRACKET = "Closing curly bracket expected when there wasn't any";
    public static final String INVALID_DECLARATION = "Not a variable or subroutine declaration";
    public static final String INVALID_TYPE = "Invalid type";
    public static final String MISSING_VAR_COMMA = "Variable declarations should be separated by comma";
    public static final String MISSING_PARAMETER_COMMA_OR_CLOSING_ROUND_BRACKET = "Parameter declarations should be separated by comma and ended by round bracket";
    public static final String NOT_AN_OPENING_ROUND_BRACKET = "Opening round bracket expected when there wasn't any";
    public static final String INVALID_VARNAME = "Invalid variable name";
    public static final String INVALID_SUBROUTINE_NAME = "Invalid subroutine name";
    public static final String INVALID_SUBROUTINE_BODY = "Invalid keyword in subroutine body";
}
