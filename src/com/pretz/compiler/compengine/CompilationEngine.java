package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.constructs.Class;
import com.pretz.compiler.compengine.constructs.ClassVarDec;
import com.pretz.compiler.compengine.constructs.ConstructTree;
import com.pretz.compiler.compengine.constructs.SubroutineDec;
import com.pretz.compiler.tokenizer.Token;
import com.pretz.compiler.tokenizer.TokenType;
import com.pretz.compiler.tokenizer.Tokens;
import io.vavr.collection.List;

public class CompilationEngine {

    /**
     * This method returns compiled class parsing tree. It starts by validating if file begins with 'class' keyword,
     * then validates class identifier and returns it if valid.
     *
     * @param tokens
     * @return
     */
    public Class compileClass(Tokens tokens) {
        validateClassToken(tokens.current());
        tokens.advance();
        Token identifier = validateClassIdentifier(tokens.current());
        tokens.advance();
        validateOpeningBracket(tokens.current());
        tokens.advance();
        //TODO check expression to assess if it is class variable, subroutine or closing bracket
        validateClosingBracket(tokens.current());
        return new Class(identifier, compileClassVarDecs(tokens), compileSubroutines());
    }

    private void validateClassToken(Token token) {
        if (!token.token().equals("class"))
            throw new CompilationException(CompilationException.NOT_A_CLASS);
    }

    private Token validateClassIdentifier(Token token) {
        if (!token.type().equals(TokenType.IDENTIFIER))
            throw new CompilationException(CompilationException.INVALID_CLASS_IDENTIFIER);
        return token;
    }

    private void validateOpeningBracket(Token token) {
        if (!(token.token().equals("{") && token.type().equals(TokenType.SYMBOL)))
            throw new CompilationException(CompilationException.NOT_AN_OPENING_BRACKET);
    }

    private void validateClosingBracket(Token token) {
        if (!(token.token().equals("}") && token.type().equals(TokenType.SYMBOL)))
            throw new CompilationException(CompilationException.NOT_A_CLOSING_BRACKET);
    }

    private List<ClassVarDec> compileClassVarDecs(Tokens tokens) {
        return List.empty();
    }

    private List<SubroutineDec> compileSubroutines() {
        return List.empty();
    }

    private ConstructTree compileClassVarDec(Tokens tokens) {

        return null;
    }
}
