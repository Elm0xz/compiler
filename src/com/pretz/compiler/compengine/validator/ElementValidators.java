package com.pretz.compiler.compengine.validator;

import com.pretz.compiler.compengine.CompilationException;
import com.pretz.compiler.tokenizer.token.KeywordType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;

public class ElementValidators {

    public static class ClassKeywordValidator implements Validator {

        @Override
        public void validate(Token token) {
            if (token.isNot(TokenType.KEYWORD) && token.isNot(KeywordType.CLASS))
                throw new CompilationException(CompilationException.NOT_A_CLASS);
        }
    }

    public static class OpeningCurlyBracketValidator implements Validator {

        @Override
        public void validate(Token token) {
            if (isNotOpeningCurlyBracket(token))
                throw new CompilationException(CompilationException.NOT_AN_OPENING_CURLY_BRACKET);
        }

        private boolean isNotOpeningCurlyBracket(Token token) {
            return token.isNot("{") || token.isNot(TokenType.SYMBOL);
        }
    }

    public static class ClosingCurlyBracketValidator implements Validator {

        @Override
        public void validate(Token token) {
            if (isNotClosingCurlyBracket(token))
                throw new CompilationException(CompilationException.NOT_A_CLOSING_CURLY_BRACKET);
        }

        private boolean isNotClosingCurlyBracket(Token token) {
            return token.isNot("}") || token.isNot(TokenType.SYMBOL);
        }
    }

    public static class CommaOrSemicolonValidator implements Validator {

        @Override
        public void validate(Token token) {
            if (isNotComma(token) && isNotSemicolon(token))
                throw new CompilationException(CompilationException.MISSING_VAR_COMMA);
        }

        private boolean isNotComma(Token token) {
            return token.isNot(TokenType.SYMBOL) || token.isNot(",");
        }

        protected boolean isNotSemicolon(Token token) {
            return token.isNot(TokenType.SYMBOL) || token.isNot(";");
        }
    }

    public static class OpeningRoundBracketValidator implements Validator {

        @Override
        public void validate(Token token) {
            if (isNotOpeningRoundBracket(token))
                throw new CompilationException(CompilationException.NOT_AN_OPENING_ROUND_BRACKET);
        }

        private boolean isNotOpeningRoundBracket(Token token) {
            return token.isNot("(") || token.isNot(TokenType.SYMBOL);
        }
    }

    public static class CommaOrRoundBracketValidator implements Validator {

        @Override
        public void validate(Token token) {
            if (isNotComma(token) && isNotClosingRoundBracket(token))
                throw new CompilationException(CompilationException.MISSING_PARAMETER_COMMA_OR_CLOSING_ROUND_BRACKET);
        }

        private boolean isNotComma(Token token) {
            return token.isNot(TokenType.SYMBOL) || token.isNot(",");
        }

        private boolean isNotClosingRoundBracket(Token token) {
            return token.isNot(")") || token.isNot(TokenType.SYMBOL);
        }
    }
}
