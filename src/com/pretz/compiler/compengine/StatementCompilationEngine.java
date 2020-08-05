package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.elements.expression.Expression;
import com.pretz.compiler.compengine.elements.expression.Op;
import com.pretz.compiler.compengine.elements.expression.OpTerm;
import com.pretz.compiler.compengine.elements.expression.Term;
import com.pretz.compiler.compengine.elements.statement.ReturnStatement;
import com.pretz.compiler.compengine.elements.statement.Statement;
import com.pretz.compiler.compengine.elements.terminal.TerminalMapper;
import com.pretz.compiler.tokenizer.token.KeywordType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.Tokens;
import io.vavr.collection.List;

import java.util.ArrayList;
import java.util.function.Predicate;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class StatementCompilationEngine {

    private final CompilationValidator validator;
    private final TerminalMapper mapper;

    public StatementCompilationEngine(CompilationValidator validator, TerminalMapper mapper) {
        this.validator = validator;
        this.mapper = mapper;
    }

    protected Statement compileStatement(Tokens tokens) {
        return Match(tokens.current()).of(
                Case($(isReturnStatement()), () -> compileReturnStatement(tokens)),
                Case($(), this::throwInvalidStatementException));
    }

    private Predicate<Token> isReturnStatement() {
        return it -> it.is(KeywordType.RETURN);
    }

    private ReturnStatement compileReturnStatement(Tokens tokens) {
        consumeStartingStatementKeyword(tokens);
        Expression expression = null;
        if (validator.isNotSemicolon(tokens.current()))
            expression = compileExpression(tokens);
        return new ReturnStatement(expression);
    }

    private void consumeStartingStatementKeyword(Tokens tokens) {
        tokens.advance();
    }

    private Expression compileExpression(Tokens tokens) {
        Term term = consumeTerm(tokens);
        ArrayList<OpTerm> opTerms = new ArrayList<>(); //todo refactor to something better
        while (!validator.isNonOpSymbol(tokens.current())) {
            opTerms.add(consumeOpTerm(tokens));
        }
        return new Expression(term, List.empty());
    }

    private Term consumeTerm(Tokens tokens) {
        Term term = Match(tokens.current()).of(
                Case($(validator.isConstant()), () -> new Term(mapper.from(tokens.current()))),
                Case($(validator.isVarName()), () -> new Term(mapper.from(tokens.current()))), //todo here additional logic for arrays etc.
                Case($(validator.isUnaryOp()), () -> consumeUnaryOpTerm(tokens)),
                Case($(), this::throwInvalidTermException) //todo think of some test cases for this
        );
        tokens.advance();
        return term;
    }

    private Term consumeUnaryOpTerm(Tokens tokens) {
        Token unaryOp = tokens.current();
        tokens.advance();
        Term term = consumeTerm(tokens);
        return new Term(mapper.from(unaryOp), null);//TODO FINISHED HERE YESTERDAY
        //TODO probably create Constant and Identifier constructs and start using Token only as DTO for parser
    }

    private OpTerm consumeOpTerm(Tokens tokens) {
        Op op = consumeOp(tokens);
        Term term = consumeTerm(tokens);
        return new OpTerm(op, term);
    }

    private Op consumeOp(Tokens tokens) {
        Op op = new Op(mapper.from(tokens.current()));
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
