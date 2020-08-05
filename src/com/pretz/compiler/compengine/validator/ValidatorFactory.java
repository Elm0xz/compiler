package com.pretz.compiler.compengine.validator;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class ValidatorFactory {

    public Validator create(Validation validation) {
        return Match(validation).of(
                Case($(it -> it.equals(Validation.CLASS_KEYWORD)), ElementValidators.ClassKeywordValidator::new),
                Case($(it -> it.equals(Validation.OPENING_CURLY_BRACKET)), ElementValidators.OpeningCurlyBracketValidator::new),
                Case($(it -> it.equals(Validation.CLOSING_CURLY_BRACKET)), ElementValidators.ClosingCurlyBracketValidator::new),
                Case($(it -> it.equals(Validation.COMMA_OR_SEMICOLON)), ElementValidators.CommaOrSemicolonValidator::new),
                Case($(it -> it.equals(Validation.OPENING_ROUND_BRACKET)), ElementValidators.OpeningRoundBracketValidator::new),
                Case($(it -> it.equals(Validation.COMMA_OR_CLOSING_ROUND_BRACKET)), ElementValidators.CommaOrRoundBracketValidator::new),
                Case($(it -> it.equals(Validation.CLASS_IDENTIFIER)), ClassIdentifierValidator::new),
                Case($(it -> it.equals(Validation.SUBROUTINE_NAME)), SubroutineNameValidator::new),
                Case($(it -> it.equals(Validation.TYPE_OR_VOID)), TypeOrVoidValidator::new),
                Case($(it -> it.equals(Validation.TYPE)), TypeValidator::new),
                Case($(it -> it.equals(Validation.VAR_NAME)), VarNameValidator::new)
        );
    }
}
