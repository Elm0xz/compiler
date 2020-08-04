package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.elements.construct.Class;
import com.pretz.compiler.compengine.elements.construct.ClassVarDec;
import com.pretz.compiler.compengine.elements.construct.Construct;
import com.pretz.compiler.compengine.elements.construct.Parameter;
import com.pretz.compiler.compengine.elements.construct.ParameterList;
import com.pretz.compiler.compengine.elements.construct.SubroutineBody;
import com.pretz.compiler.compengine.elements.construct.SubroutineDec;
import com.pretz.compiler.compengine.elements.terminal.Terminal;
import com.pretz.compiler.compengine.elements.terminal.TerminalMapper;
import com.pretz.compiler.compengine.elements.construct.Type;
import com.pretz.compiler.compengine.elements.construct.VarDec;
import com.pretz.compiler.compengine.elements.construct.VarNames;
import com.pretz.compiler.tokenizer.Token;
import com.pretz.compiler.tokenizer.Tokens;
import io.vavr.collection.List;

import java.util.ArrayList;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class CompilationEngine {

    private final CompilationValidator validator;
    private final StatementCompilationEngine statementCompilationEngine;
    private final TerminalMapper mapper;

    public CompilationEngine(CompilationValidator validator, StatementCompilationEngine statementCompilationEngine, TerminalMapper mapper) {
        this.validator = validator;
        this.statementCompilationEngine = statementCompilationEngine;
        this.mapper = mapper;
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
        Terminal identifier = consumeClassNameIdentifier(tokens);
        consumeClassOrSubroutineBodyOpeningBracket(tokens);
        List<Construct> declarations = compileDeclarations(tokens);
        consumeClassOrSubroutineBodyClosingBracket(tokens);
        return new Class(identifier, declarations);
    }

    private void consumeClassKeyword(Tokens tokens) {
        validator.validateClassKeyword(tokens.current());
        tokens.advance();
    }

    private Terminal consumeClassNameIdentifier(Tokens tokens) {
        Token identifier = validator.validateClassIdentifier(tokens.current());
        tokens.advance();
        return mapper.from(identifier);
    }

    private void consumeClassOrSubroutineBodyOpeningBracket(Tokens tokens) {
        validator.validateOpeningCurlyBracket(tokens.current());
        tokens.advance();
    }

    private List<Construct> compileDeclarations(Tokens tokens) {
        ArrayList<Construct> declarations = new ArrayList<>(); //TODO refactor to something cleaner?
        while (!tokens.isLast()) {
            declarations.add(Match(tokens.current()).of(
                    Case($(validator.isClassVarDec()), () -> compileClassVarDec(tokens)),
                    Case($(validator.isSubroutineDec()), () -> compileSubroutine(tokens)),
                    Case($(), this::throwInvalidDeclarationException)
            ));
        }
        return List.ofAll(declarations);
    }

    private ClassVarDec compileClassVarDec(Tokens tokens) {
        Token startingKeyword = consumeStartingKeyword(tokens); //TODO separate validations for different starting keywords
        Type type = consumeType(tokens);
        List<Token> varNames = consumeVarNames(tokens);
        consumeSemicolon(tokens);
        return new ClassVarDec(startingKeyword, type, new VarNames(varNames));
    }

    private void consumeSemicolon(Tokens tokens) {
        //refactored for readability, don't use for simple token advance
        tokens.advance();
    }

    private SubroutineDec compileSubroutine(Tokens tokens) {
        Token startingKeyword = consumeStartingKeyword(tokens);
        Type type = consumeSubroutineType(tokens);
        Token subroutineName = consumeSubroutineName(tokens);
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
        validator.validateClosingCurlyBracket(tokens.current());
    }

    private Token consumeStartingKeyword(Tokens tokens) { //TODO this should be also validated + test
        Token startingKeyword = tokens.current();
        tokens.advance();
        return startingKeyword;
    }

    private Type consumeType(Tokens tokens) {
        validator.validateType(tokens.current());
        Type type = new Type(tokens.current());
        tokens.advance();
        return type;
    }

    private List<Token> consumeVarNames(Tokens tokens) {
        ArrayList<Token> varNames = new ArrayList<>(); //TODO refactor to something cleaner
        while (validator.isNotSemicolon(tokens.current())) {
            varNames.add(consumeVarName(tokens));
            consumeVarDecComma(tokens);
        }
        return List.ofAll(varNames);
    }

    private Token consumeVarName(Tokens tokens) {
        validator.validateVarName(tokens.current());
        Token varName = tokens.current();
        tokens.advance();
        return varName;
    }

    private void consumeVarDecComma(Tokens tokens) {
        validator.validateCommaOrSemicolon(tokens.current());
        if (validator.isNotSemicolon(tokens.current()))
            tokens.advance();
    }

    private Type consumeSubroutineType(Tokens tokens) {
        validator.validateTypeOrVoid(tokens.current());
        Type type = new Type(tokens.current());
        tokens.advance();
        return type;
    }

    private Token consumeSubroutineName(Tokens tokens) {
        validator.validateSubroutineName(tokens.current());
        Token varName = tokens.current();
        tokens.advance();
        return varName;
    }

    private void consumeSubroutineParametersOpeningBracket(Tokens tokens) {
        validator.validateOpeningRoundBracket(tokens.current());
        tokens.advance();
    }

    private ParameterList compileParameterList(Tokens tokens) {
        ArrayList<Parameter> parameters = new ArrayList<>(); //TODO refactor to something cleaner
        while (validator.isNotClosingRoundBracket(tokens.current())) {
            parameters.add(consumeParameter(tokens));
            consumeParameterListComma(tokens);
        }
        return new ParameterList(List.ofAll(parameters));
    }

    private Parameter consumeParameter(Tokens tokens) {
        Type parameterType = consumeType(tokens);
        Token parameterName = consumeVarName(tokens);
        return new Parameter(parameterType, parameterName);
    }

    private void consumeParameterListComma(Tokens tokens) {
        validator.validateCommaOrClosingRoundBracket(tokens.current());
        if (validator.isNotClosingRoundBracket(tokens.current()))
            tokens.advance();
    }

    private void consumeSubroutineParametersClosingBracket(Tokens tokens) {
        //No need for additional validation here as we already checked it in consumeParameterListComma
        tokens.advance();
    }

    private SubroutineBody compileSubroutineBody(Tokens tokens) {
        consumeClassOrSubroutineBodyOpeningBracket(tokens);
        ArrayList<Construct> subroutineBody = new ArrayList<>(); //TODO refactor to something cleaner
        while (validator.isNotClosingCurlyBracket(tokens.current())) {
            subroutineBody.add(Match(tokens.current()).of(
                    Case($(validator.isVarDec()), () -> compileVarDec(tokens)),
                    Case($(validator.isStatement()), () -> statementCompilationEngine.compileStatement(tokens)),
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
        Token startingKeyword = consumeStartingKeyword(tokens);
        Type type = consumeType(tokens);
        List<Token> varNames = consumeVarNames(tokens);
        consumeSemicolon(tokens);
        return new VarDec(startingKeyword, type, new VarNames(varNames));
    }
}
