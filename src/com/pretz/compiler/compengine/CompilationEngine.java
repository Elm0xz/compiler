package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.constructs.Class;
import com.pretz.compiler.compengine.constructs.ClassVarDec;
import com.pretz.compiler.compengine.constructs.Construct;
import com.pretz.compiler.compengine.constructs.SubroutineDec;
import com.pretz.compiler.compengine.constructs.Type;
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
        consumeClassOpeningBracket(tokens);
        List<Construct> declarations = consumeDeclarations(tokens);
        consumeClassClosingBracket(tokens);
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

    private void consumeClassOpeningBracket(Tokens tokens) {
        compilationValidator.validateOpeningBracket(tokens.current());
        tokens.advance();
    }

    private List<Construct> consumeDeclarations(Tokens tokens) {
        ArrayList<Construct> declarations = new ArrayList<>(); //TODO refactor to something cleaner?
        while (!tokens.isLast()) {
            declarations.add(Match(tokens.current()).of(
                    Case($(compilationValidator.isClassVarDec()), () -> compileClassVarDec(tokens)),
                    Case($(compilationValidator.isSubroutineDec()), () -> compileSubroutineDec(tokens)),
                    Case($(), this::throwInvalidDeclarationException)
            ));
            tokens.advance();
        }
        return List.ofAll(declarations);
    }

    private ClassVarDec compileClassVarDec(Tokens tokens) {
        Token startingKeyword = consumeClassVarStartingKeyword(tokens);
        Type type = consumeClassVarType(tokens);
        List<Token> varNames = consumeClassVarNames(tokens);
        return new ClassVarDec(startingKeyword, type, new VarNames(varNames));
    }

    private SubroutineDec compileSubroutineDec(Tokens tokens) {
        return null;
    }

    private Construct throwInvalidDeclarationException() {
        throw new CompilationException(CompilationException.INVALID_DECLARATION);
    }

    private void consumeClassClosingBracket(Tokens tokens) {
        compilationValidator.validateClosingBracket(tokens.current());
    }

    private Token consumeClassVarStartingKeyword(Tokens tokens) {
        Token startingKeyword = tokens.current();
        tokens.advance();
        return startingKeyword;
    }

    private Type consumeClassVarType(Tokens tokens) {
        compilationValidator.validateType(tokens.current());
        Type type = new Type(tokens.current());
        tokens.advance();
        return type;
    }

    private List<Token> consumeClassVarNames(Tokens tokens) {
        ArrayList<Token> varNames = new ArrayList<>(); //TODO refactor to something cleaner
        while (compilationValidator.isNotSemicolon(tokens.current())) {
            varNames.add(consumeVarName(tokens));
            consumeCommaIfPresent(tokens);
        }
        return List.ofAll(varNames);
    }

    private Token consumeVarName(Tokens tokens) {
        Token varName = tokens.current();
        tokens.advance();
        return varName;
    }

    private void consumeCommaIfPresent(Tokens tokens) {
        compilationValidator.validateCommaOrSemicolon(tokens.current());
        if (compilationValidator.isNotSemicolon(tokens.current()))
            tokens.advance();
    }
}
