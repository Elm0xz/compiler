package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.constructs.Class;
import com.pretz.compiler.compengine.constructs.ClassVarDec;
import com.pretz.compiler.compengine.constructs.Construct;
import com.pretz.compiler.compengine.constructs.Parameter;
import com.pretz.compiler.compengine.constructs.ParameterList;
import com.pretz.compiler.compengine.constructs.Statement;
import com.pretz.compiler.compengine.constructs.SubroutineBody;
import com.pretz.compiler.compengine.constructs.SubroutineDec;
import com.pretz.compiler.compengine.constructs.Type;
import com.pretz.compiler.compengine.constructs.VarDec;
import com.pretz.compiler.compengine.constructs.VarNames;
import com.pretz.compiler.tokenizer.Token;
import com.pretz.compiler.tokenizer.Tokens;
import io.vavr.collection.List;

import java.util.ArrayList;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class CompilationEngine {

    private final CompilationValidator compilationValidator;

    public CompilationEngine(CompilationValidator compilationValidator) {
        this.compilationValidator = compilationValidator;
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
        Token identifier = consumeClassNameIdentifier(tokens);
        consumeClassOrSubroutineBodyOpeningBracket(tokens);
        List<Construct> declarations = compileDeclarations(tokens);
        consumeClassOrSubroutineBodyClosingBracket(tokens);
        return new Class(identifier, declarations);
    }

    private void consumeClassKeyword(Tokens tokens) {
        compilationValidator.validateClassKeyword(tokens.current());
        tokens.advance();
    }

    private Token consumeClassNameIdentifier(Tokens tokens) {
        Token identifier = compilationValidator.validateClassIdentifier(tokens.current());
        tokens.advance();
        return identifier;
    }

    private void consumeClassOrSubroutineBodyOpeningBracket(Tokens tokens) {
        compilationValidator.validateOpeningCurlyBracket(tokens.current());
        tokens.advance();
    }

    private List<Construct> compileDeclarations(Tokens tokens) {
        ArrayList<Construct> declarations = new ArrayList<>(); //TODO refactor to something cleaner?
        while (!tokens.isLast()) {
            declarations.add(Match(tokens.current()).of(
                    Case($(compilationValidator.isClassVarDec()), () -> compileClassVarDec(tokens)),
                    Case($(compilationValidator.isSubroutineDec()), () -> compileSubroutine(tokens)),
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
        compilationValidator.validateClosingCurlyBracket(tokens.current());
    }

    private Token consumeStartingKeyword(Tokens tokens) { //TODO this should be also validated + test
        Token startingKeyword = tokens.current();
        tokens.advance();
        return startingKeyword;
    }

    private Type consumeType(Tokens tokens) {
        compilationValidator.validateType(tokens.current());
        Type type = new Type(tokens.current());
        tokens.advance();
        return type;
    }

    private List<Token> consumeVarNames(Tokens tokens) {
        ArrayList<Token> varNames = new ArrayList<>(); //TODO refactor to something cleaner
        while (compilationValidator.isNotSemicolon(tokens.current())) {
            varNames.add(consumeVarName(tokens));
            consumeVarDecComma(tokens);
        }
        return List.ofAll(varNames);
    }

    private Token consumeVarName(Tokens tokens) {
        compilationValidator.validateVarName(tokens.current());
        Token varName = tokens.current();
        tokens.advance();
        return varName;
    }

    private void consumeVarDecComma(Tokens tokens) {
        compilationValidator.validateCommaOrSemicolon(tokens.current());
        if (compilationValidator.isNotSemicolon(tokens.current()))
            tokens.advance();
    }

    private Type consumeSubroutineType(Tokens tokens) {
        compilationValidator.validateTypeOrVoid(tokens.current());
        Type type = new Type(tokens.current());
        tokens.advance();
        return type;
    }

    private Token consumeSubroutineName(Tokens tokens) {
        compilationValidator.validateSubroutineName(tokens.current());
        Token varName = tokens.current();
        tokens.advance();
        return varName;
    }

    private void consumeSubroutineParametersOpeningBracket(Tokens tokens) {
        compilationValidator.validateOpeningRoundBracket(tokens.current());
        tokens.advance();
    }

    private ParameterList compileParameterList(Tokens tokens) {
        ArrayList<Parameter> parameters = new ArrayList<>(); //TODO refactor to something cleaner
        while (compilationValidator.isNotClosingRoundBracket(tokens.current())) {
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
        compilationValidator.validateCommaOrClosingRoundBracket(tokens.current());
        if (compilationValidator.isNotClosingRoundBracket(tokens.current()))
            tokens.advance();
    }

    private void consumeSubroutineParametersClosingBracket(Tokens tokens) {
        //No need for additional validation here as we already checked it in consumeParameterListComma
        tokens.advance();
    }

    private SubroutineBody compileSubroutineBody(Tokens tokens) {
        consumeClassOrSubroutineBodyOpeningBracket(tokens);
        ArrayList<Construct> subroutineBody = new ArrayList<>(); //TODO refactor to something cleaner
        while (compilationValidator.isNotClosingCurlyBracket(tokens.current())) {
            subroutineBody.add(Match(tokens.current()).of(
                    Case($(compilationValidator.isVarDec()), () -> compileVarDec(tokens)),
                    Case($(compilationValidator.isStatement()), () -> compileStatement(tokens)),
                    Case($(), this::throwInvalidSubroutineBodyException)
            ));
        }
        consumeClassOrSubroutineBodyClosingBracket(tokens);
        return new SubroutineBody(List.ofAll(subroutineBody));
    }

    private Construct throwInvalidSubroutineBodyException() {
        throw new CompilationException(CompilationException.INVALID_SUBROUTINE_BODY);
    }

    private VarDec compileVarDec(Tokens tokens) {
        Token startingKeyword = consumeStartingKeyword(tokens);
        Type type = consumeType(tokens);
        List<Token> varNames = consumeVarNames(tokens);
        consumeSemicolon(tokens);
        return new VarDec(startingKeyword, type, new VarNames(varNames));
    }

    private Statement compileStatement(Tokens tokens) {
        //TODO consume statement -> maybe separate class for this?
        /*tokens.advance();
        tokens.advance();
        tokens.advance();*/
        return new Statement();
    }
}
