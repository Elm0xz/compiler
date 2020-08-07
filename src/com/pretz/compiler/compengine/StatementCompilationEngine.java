package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.expression.Expression;
import com.pretz.compiler.compengine.expression.Op;
import com.pretz.compiler.compengine.expression.OpTerm;
import com.pretz.compiler.compengine.expression.Term;
import com.pretz.compiler.compengine.statement.ReturnStatement;
import com.pretz.compiler.compengine.statement.Statement;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.validator.Validation;
import com.pretz.compiler.compengine.validator.ValidatorFactory;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.Tokens;
import io.vavr.collection.List;

import java.util.ArrayList;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class StatementCompilationEngine {

    private final CompilationMatcher matcher;
    private final ValidatorFactory validator;

    public StatementCompilationEngine(CompilationMatcher matcher, ValidatorFactory validator) {
        this.matcher = matcher;
        this.validator = validator;
    }

    protected Statement compileStatement(Tokens tokens) {
        return Match(tokens.current()).of(
                Case($(matcher.isReturnStatement()), () -> compileReturnStatement(tokens)),
                Case($(), this::throwInvalidStatementException));
    }

    private ReturnStatement compileReturnStatement(Tokens tokens) {
        consumeStartingStatementKeyword(tokens);
        Expression expression = null;
        if (matcher.isNotSemicolon(tokens.current()))
            expression = compileExpression(tokens);
        return new ReturnStatement(expression);
    }

    private void consumeStartingStatementKeyword(Tokens tokens) {
        tokens.advance();
    }

    private Expression compileExpression(Tokens tokens) {
        Term term = consumeTerm(tokens);
        ArrayList<OpTerm> opTerms = new ArrayList<>(); //todo refactor to something better
        while (!matcher.isNonOpSymbol(tokens.current())) {
            opTerms.add(consumeOpTerm(tokens));
        }
        return new Expression(term, List.ofAll(opTerms));
    }

    private Term consumeTerm(Tokens tokens) {
        return Match(tokens.current()).of(
                Case($(matcher.isConstant()), () -> consumeSimpleTerm(tokens)),
                Case($(matcher.isVarNameOrSubroutineCall()), () -> consumeVarNameTerm(tokens)),
                Case($(matcher.isUnaryOp()), () -> consumeUnaryOpTerm(tokens)),
                Case($(matcher.isExpressionInBrackets()), () -> consumeExpressionInBrackets(tokens)),
                Case($(), this::throwInvalidTermException) //todo think of some test cases for this
        );
    }

    private Term consumeSimpleTerm(Tokens tokens) {
        Term simpleTerm = new Term(new Terminal(tokens.current()));
        tokens.advance();
        return simpleTerm;
    }

    private Term consumeVarNameTerm(Tokens tokens) {
        Term term = Match(tokens).of(
                Case($(matcher.isVarNameArray()), () -> consumeVarNameArray(tokens)),
                Case($(matcher.isSubroutineCall()), () -> consumeSubroutineCall(tokens)),
                Case($(), () -> consumeSimpleTerm(tokens))
        );
        return term;
    }

    private Term consumeVarNameArray(Tokens tokens) {
        Terminal varName = new Terminal(tokens.current());
        tokens.advance();
        consumeArrayOpeningSquareBracket(tokens);
        Expression expression = compileExpression(tokens);
        consumeArrayClosingSquareBracket(tokens);
        return new Term(varName, expression);
    }

    private Term consumeSubroutineCall(Tokens tokens) { //TODO implement this!
        throw new NotImplementedException("not yet implemented");
    }

    private Term consumeExpressionInBrackets(Tokens tokens) {
        consumeExpressionOpeningRoundBracket(tokens);
        Expression expression = compileExpression(tokens);
        consumeExpressionClosingRoundBracket(tokens);
        return new Term(expression);
    }

    private void consumeExpressionOpeningRoundBracket(Tokens tokens) {
        validator.create(Validation.OPENING_ROUND_BRACKET).validate(tokens.current());
        tokens.advance();
    }

    private void consumeExpressionClosingRoundBracket(Tokens tokens) {
        validator.create(Validation.CLOSING_ROUND_BRACKET).validate(tokens.current());
        tokens.advance();
    }

    private void consumeArrayOpeningSquareBracket(Tokens tokens) {
        validator.create(Validation.OPENING_SQUARE_BRACKET).validate(tokens.current());
        tokens.advance();
    }

    private void consumeArrayClosingSquareBracket(Tokens tokens) {
        validator.create(Validation.CLOSING_SQUARE_BRACKET).validate(tokens.current());
        tokens.advance();
    }

    private Term consumeUnaryOpTerm(Tokens tokens) {
        Token unaryOp = tokens.current();
        tokens.advance();
        Term term = consumeTerm(tokens);
        return new Term(new Terminal(unaryOp), term);
    }

    private OpTerm consumeOpTerm(Tokens tokens) {
        Op op = consumeOp(tokens);
        Term term = consumeTerm(tokens);
        return new OpTerm(op, term);
    }

    private Op consumeOp(Tokens tokens) {
        Op op = new Op(tokens.current());
        tokens.advance();
        return op;
    }

    private Statement throwInvalidStatementException() {
        throw new CompilationException(CompilationException.INVALID_STATEMENT);
    }

    private Term throwInvalidTermException() {
        throw new CompilationException(CompilationException.INVALID_TERM);
    }
}
