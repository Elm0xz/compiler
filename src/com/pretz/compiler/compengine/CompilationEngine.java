package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.construct.Class;
import com.pretz.compiler.compengine.construct.ClassVarDec;
import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.construct.Parameter;
import com.pretz.compiler.compengine.construct.ParameterList;
import com.pretz.compiler.compengine.construct.SubroutineBody;
import com.pretz.compiler.compengine.construct.SubroutineDec;
import com.pretz.compiler.compengine.construct.Type;
import com.pretz.compiler.compengine.construct.VarDec;
import com.pretz.compiler.compengine.construct.VarNames;
import com.pretz.compiler.compengine.terminal.Identifier;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.IdentifierType;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.validator.Validation;
import com.pretz.compiler.compengine.validator.ValidatorFactory;
import com.pretz.compiler.tokenizer.token.Tokens;
import io.vavr.collection.List;

import java.util.ArrayList;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class CompilationEngine {

    private final ValidatorFactory validator;
    private final StatementCompilationEngine statementCompilationEngine;
    private final CompilationMatcher matcher;

    public CompilationEngine(ValidatorFactory validator,
                             StatementCompilationEngine statementCompilationEngine,
                             CompilationMatcher matcher) {
        this.validator = validator;
        this.statementCompilationEngine = statementCompilationEngine;
        this.matcher = matcher;
    }

    /**
     * This method returns compiled class parsing tree. It starts by validating if file begins with 'class' keyword,
     * then validates class identifier and returns it if valid.
     *
     * @param tokens - list of input tokens from one file
     * @return full parse tree of class
     */
    public Class compileClass(Tokens tokens) {
        consumeClassKeyword(tokens);
        Identifier identifier = consumeClassIdentifier(tokens);
        consumeClassOrSubroutineBodyOpeningBracket(tokens);
        List<Construct> declarations = compileDeclarations(tokens);
        consumeClassOrSubroutineBodyClosingBracket(tokens);
        return new Class(identifier, declarations);
    }

    private void consumeClassKeyword(Tokens tokens) {
        validator.create(Validation.CLASS_KEYWORD).validate(tokens.current());
        tokens.advance();
    }

    private Identifier consumeClassIdentifier(Tokens tokens) {
        Identifier identifier = new Identifier(tokens.current(), validator.create(Validation.CLASS_IDENTIFIER),
                IdentifierMeaning.DEFINITION, IdentifierType.CLASS);
        tokens.advance();
        return identifier;
    }

    private void consumeClassOrSubroutineBodyOpeningBracket(Tokens tokens) {
        validator.create(Validation.OPENING_CURLY_BRACKET).validate(tokens.current());
        tokens.advance();
    }

    private List<Construct> compileDeclarations(Tokens tokens) {
        ArrayList<Construct> declarations = new ArrayList<>(); //TODO refactor to something cleaner?
        while (!tokens.isLast()) {
            declarations.add(Match(tokens.current()).of(
                    Case($(matcher.isClassVarDec()), () -> compileClassVarDec(tokens)),
                    Case($(matcher.isSubroutineDec()), () -> compileSubroutine(tokens)),
                    Case($(), this::throwInvalidDeclarationException)
            ));
        }
        return List.ofAll(declarations);
    }

    private ClassVarDec compileClassVarDec(Tokens tokens) {
        Terminal startingKeyword = consumeStartingKeyword(tokens);
        Type type = consumeType(tokens);
        List<Identifier> varNames = consumeVarNames(tokens);
        consumeSemicolon(tokens);
        return new ClassVarDec(startingKeyword, type, new VarNames(varNames));
    }

    private void consumeSemicolon(Tokens tokens) {
        //refactored for readability, don't use for simple token advance
        tokens.advance();
    }

    private SubroutineDec compileSubroutine(Tokens tokens) {
        Terminal startingKeyword = consumeStartingKeyword(tokens);
        Type type = consumeSubroutineType(tokens);
        Identifier subroutineName = consumeSubroutineName(tokens);
        consumeSubroutineParametersOpeningBracket(tokens);
        ParameterList parameterList = compileParameterList(tokens);
        consumeSubroutineParametersClosingBracket(tokens);
        SubroutineBody subroutineBody = compileSubroutineBody(tokens);
        return new SubroutineDec(startingKeyword, type, subroutineName, parameterList, subroutineBody);
    }

    private Construct throwInvalidDeclarationException() {
        throw new CompilationException(CompilationException.INVALID_DECLARATION);
    }

    private void consumeClassOrSubroutineBodyClosingBracket(Tokens tokens) {
        validator.create(Validation.CLOSING_CURLY_BRACKET).validate(tokens.current());
        tokens.advance();
    }

    /**
     * Consume starting keyword of class variable, subroutine or subroutine variable declaration.
     *
     * @param tokens - tokens list from tokenizer.
     * @return starting keyword saved as parsing tree terminal.
     */
    private Terminal consumeStartingKeyword(Tokens tokens) {
        Terminal startingKeyword = new Terminal(tokens.current());
        tokens.advance();
        return startingKeyword;
    }

    private Type consumeType(Tokens tokens) {
        Type type = new Type(tokens.current(), validator.create(Validation.TYPE));
        tokens.advance();
        return type;
    }

    private List<Identifier> consumeVarNames(Tokens tokens) {
        ArrayList<Identifier> varNames = new ArrayList<>(); //TODO refactor to something cleaner
        while (matcher.isNotSemicolon(tokens.current())) {
            varNames.add(consumeVarName(tokens));
            consumeVarDecComma(tokens);
        }
        return List.ofAll(varNames);
    }

    private Identifier consumeVarName(Tokens tokens) {
        Identifier varName = new Identifier(tokens.current(), validator.create(Validation.VAR_NAME),
                IdentifierMeaning.DEFINITION, IdentifierType.VAR);
        tokens.advance();
        return varName;
    }

    private void consumeVarDecComma(Tokens tokens) {
        validator.create(Validation.COMMA_OR_SEMICOLON).validate(tokens.current());
        if (matcher.isNotSemicolon(tokens.current()))
            tokens.advance();
    }

    private Type consumeSubroutineType(Tokens tokens) {
        Type type = new Type(tokens.current(), validator.create(Validation.TYPE_OR_VOID));
        tokens.advance();
        return type;
    }

    private Identifier consumeSubroutineName(Tokens tokens) {
        Identifier varName = new Identifier(tokens.current(), validator.create(Validation.SUBROUTINE_NAME),
                IdentifierMeaning.DEFINITION, IdentifierType.SUBROUTINE);
        tokens.advance();
        return varName;
    }

    private void consumeSubroutineParametersOpeningBracket(Tokens tokens) {
        validator.create(Validation.OPENING_ROUND_BRACKET).validate(tokens.current());
        tokens.advance();
    }

    private ParameterList compileParameterList(Tokens tokens) {
        ArrayList<Parameter> parameters = new ArrayList<>(); //TODO refactor to something cleaner
        while (matcher.isNotClosingRoundBracket(tokens.current())) {
            parameters.add(consumeParameter(tokens));
            consumeParameterListComma(tokens);
        }
        return new ParameterList(List.ofAll(parameters));
    }

    private Parameter consumeParameter(Tokens tokens) {
        Type parameterType = consumeType(tokens);
        Identifier parameterName = consumeVarName(tokens);
        return new Parameter(parameterType, parameterName);
    }

    private void consumeParameterListComma(Tokens tokens) {
        validator.create(Validation.COMMA_OR_CLOSING_ROUND_BRACKET).validate(tokens.current());
        if (matcher.isNotClosingRoundBracket(tokens.current()))
            tokens.advance();
    }

    private void consumeSubroutineParametersClosingBracket(Tokens tokens) {
        //No need for additional validation here as we already checked it in consumeParameterListComma
        tokens.advance();
    }

    private SubroutineBody compileSubroutineBody(Tokens tokens) {
        consumeClassOrSubroutineBodyOpeningBracket(tokens);
        ArrayList<Construct> subroutineBody = new ArrayList<>(); //TODO refactor to something cleaner
        while (matcher.isNotClosingCurlyBracket(tokens.current())) {
            subroutineBody.add(Match(tokens.current()).of(
                    Case($(matcher.isVarDec()), () -> compileVarDec(tokens)),
                    Case($(matcher.isStatement()), () -> statementCompilationEngine.compileStatement(tokens)),
                    Case($(), this::throwInvalidSubroutineBodyException)
            ));
        }
        consumeClassOrSubroutineBodyClosingBracket(tokens);
        return new SubroutineBody(List.ofAll(subroutineBody));
    }

    private Construct throwInvalidSubroutineBodyException() {
        throw new CompilationException(CompilationException.INVALID_SUBROUTINE_BODY_KEYWORD);
    }

    private VarDec compileVarDec(Tokens tokens) {
        Terminal startingKeyword = consumeStartingKeyword(tokens);
        Type type = consumeType(tokens);
        List<Identifier> varNames = consumeVarNames(tokens);
        consumeSemicolon(tokens);
        return new VarDec(startingKeyword, type, new VarNames(varNames));
    }
}
