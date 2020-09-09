package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.expression.Expression;
import com.pretz.compiler.compengine.expression.Op;
import com.pretz.compiler.compengine.expression.OpTerm;
import com.pretz.compiler.compengine.expression.Term;
import com.pretz.compiler.compengine.expression.TermType;
import com.pretz.compiler.compengine.statement.DoStatement;
import com.pretz.compiler.compengine.statement.IfStatement;
import com.pretz.compiler.compengine.statement.LetStatement;
import com.pretz.compiler.compengine.statement.ReturnStatement;
import com.pretz.compiler.compengine.statement.Statement;
import com.pretz.compiler.compengine.statement.WhileStatement;
import com.pretz.compiler.compengine.terminal.Identifier;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.IdentifierType;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.validator.Validation;
import com.pretz.compiler.compengine.validator.ValidatorFactory;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
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
                Case($(matcher.isDoStatement()), () -> compileDoStatement(tokens)),
                Case($(matcher.isWhileStatement()), () -> compileWhileStatement(tokens)),
                Case($(matcher.isIfStatement()), () -> compileIfStatement(tokens)),
                Case($(matcher.isLetStatement()), () -> compileLetStatement(tokens)),
                Case($(), this::throwInvalidStatementException));
    }

    private ReturnStatement compileReturnStatement(Tokens tokens) {
        consumeStartingStatementKeyword(tokens);
        Expression expression = null;
        if (matcher.isNotSemicolon(tokens.current()))
            expression = compileExpression(tokens);
        consumeSemicolon(tokens);
        return new ReturnStatement(expression);
    }

    private DoStatement compileDoStatement(Tokens tokens) {
        consumeStartingStatementKeyword(tokens);
        Term subroutineCall = consumeSubroutineCall(tokens);
        consumeSemicolon(tokens);
        return new DoStatement(subroutineCall);
    }

    private WhileStatement compileWhileStatement(Tokens tokens) {
        consumeStartingStatementKeyword(tokens);
        Expression condition = consumeCondition(tokens);
        List<Statement> statements = consumeNestedStatements(tokens);
        return new WhileStatement(condition, List.ofAll(statements));
    }

    private Statement compileIfStatement(Tokens tokens) {
        consumeStartingStatementKeyword(tokens);
        Expression condition = consumeCondition(tokens);
        List<Statement> ifStatements = consumeNestedStatements(tokens);
        List<Statement> elseStatements = List.empty();
        if (matcher.isElseKeyword(tokens.current())) {
            consumeStartingStatementKeyword(tokens);
            elseStatements = consumeNestedStatements(tokens);
        }
        return new IfStatement(condition, ifStatements, elseStatements);
    }

    private Statement compileLetStatement(Tokens tokens) {
        consumeStartingStatementKeyword(tokens);
        Identifier varName = consumeIdentifier(tokens, IdentifierMeaning.DEFINITION, IdentifierType.VAR);
        Expression arrayExpression = null;
        if (matcher.isNotEqualsSign(tokens.current())) {
            consumeArrayOpeningSquareBracket(tokens);
            arrayExpression = compileExpression(tokens);
            consumeArrayClosingSquareBracket(tokens);
        }
        consumeEqualsSign(tokens);
        Expression assignedExpression = compileExpression(tokens);
        consumeSemicolon(tokens);
        return new LetStatement(varName, arrayExpression, assignedExpression);
    }

    private Expression consumeCondition(Tokens tokens) {
        consumeOpeningRoundBracket(tokens);
        Expression condition = compileExpression(tokens);
        consumeClosingRoundBracket(tokens);
        return condition;
    }

    private List<Statement> consumeNestedStatements(Tokens tokens) {
        consumeStatementBodyOpeningCurlyBracket(tokens);
        ArrayList<Statement> statements = new ArrayList<>();//TODO(L) refactor to sth nicer
        while (matcher.isNotClosingCurlyBracket(tokens.current())) {
            statements.add(compileStatement(tokens));
        }
        consumeStatementBodyClosingCurlyBracket(tokens);
        return List.ofAll(statements);
    }

    private void consumeStartingStatementKeyword(Tokens tokens) {
        tokens.advance();
    }

    private void consumeSemicolon(Tokens tokens) {
        //TODO(L) some validation on this?
        tokens.advance();
    }

    private void consumeEqualsSign(Tokens tokens) {
        tokens.advance();
    }

    private Expression compileExpression(Tokens tokens) {
        Term term = consumeTerm(tokens);
        ArrayList<OpTerm> opTerms = new ArrayList<>(); //todo(L) refactor to something better
        while (!matcher.isNonOpSymbol(tokens.current())) {
            opTerms.add(consumeOpTerm(tokens));
        }
        return new Expression(term, List.ofAll(opTerms));
    }

    private Term consumeTerm(Tokens tokens) {
        return Match(tokens.current()).of(
                Case($(matcher.isConstant()), () -> consumeSimpleTerm(tokens, TermType.CONSTANT)),
                Case($(matcher.isVarNameOrSubroutineCall()), () -> consumeVarNameTerm(tokens)),
                Case($(matcher.isUnaryOp()), () -> consumeUnaryOpTerm(tokens)),
                Case($(matcher.isExpressionInBrackets()), () -> consumeExpressionInBrackets(tokens)),
                Case($(), this::throwInvalidTermException) //todo(L) think of some test cases for this
        );
    }

    //TODO(L) maybe two separate methods?
    private Term consumeSimpleTerm(Tokens tokens, TermType termType) {
        Term simpleTerm;
        if (tokens.current().is(TokenType.IDENTIFIER)) {
            simpleTerm = new Term(termType, new Identifier(tokens.current(), IdentifierMeaning.USAGE, IdentifierType.VAR));
        }
        else {
            //TODO(L) there is no validation here, why?
            simpleTerm = new Term(termType, new Terminal(tokens.current()));
        }
        tokens.advance();
        return simpleTerm;
    }

    private Term consumeVarNameTerm(Tokens tokens) {
        return Match(tokens).of(
                Case($(matcher.isVarNameArray()), () -> consumeVarNameArray(tokens)),
                Case($(matcher.isSubroutineCall()), () -> consumeSubroutineCall(tokens)),
                Case($(), () -> consumeSimpleTerm(tokens, TermType.VAR))
        );
    }

    private Term consumeVarNameArray(Tokens tokens) {
        Terminal varName = consumeIdentifier(tokens, IdentifierMeaning.USAGE, IdentifierType.VAR);
        consumeArrayOpeningSquareBracket(tokens);
        Expression expression = compileExpression(tokens);
        consumeArrayClosingSquareBracket(tokens);
        return new Term(TermType.VAR_ARRAY, List.of(varName, expression));
    }

    private Identifier consumeIdentifier(Tokens tokens, IdentifierMeaning meaning, IdentifierType identifierType) {
        Identifier identifier = new Identifier(tokens.current(), meaning, identifierType);
        tokens.advance();
        return identifier;
    }

    private Term consumeSubroutineCall(Tokens tokens) {
        List<Element> subroutineCallIdentifier = consumeSubroutineCallIdentifier(tokens); //TODO(L) refactor into something better
        consumeOpeningRoundBracket(tokens);
        List<? extends Element> expressionList = consumeExpressionList(tokens);
        consumeClosingRoundBracket(tokens);
        return new Term(TermType.SUBROUTINE_CALL, subroutineCallIdentifier.appendAll(expressionList)); //TODO quite ugly conversion
    }

    private List<Element> consumeSubroutineCallIdentifier(Tokens tokens) {//TODO(L) ugly types
        ArrayList<Element> identifiers = new ArrayList<>(); //TODO(L) refactor into something better
        if (matcher.isDot(tokens.ll1())) {
            identifiers.add(consumeIdentifier(tokens, IdentifierMeaning.USAGE, IdentifierType.CLASS));
            consumeDot(tokens);
        }
        identifiers.add(consumeIdentifier(tokens, IdentifierMeaning.USAGE, IdentifierType.SUBROUTINE));
        return List.ofAll(identifiers);
    }

    private void consumeDot(Tokens tokens) {
        tokens.advance();
    }

    private List<Expression> consumeExpressionList(Tokens tokens) {
        ArrayList<Expression> expressions = new ArrayList<>();
        while (matcher.isNotClosingRoundBracket(tokens.current())) {
            expressions.add(compileExpression(tokens));
            consumeExpressionListComma(tokens);
        }
        return List.ofAll(expressions);
    }

    private void consumeExpressionListComma(Tokens tokens) {
        validator.create(Validation.COMMA_OR_CLOSING_ROUND_BRACKET).validate(tokens.current());
        if (matcher.isNotClosingRoundBracket(tokens.current()))
            tokens.advance();
    }

    private Term consumeExpressionInBrackets(Tokens tokens) {
        consumeOpeningRoundBracket(tokens);
        Expression expression = compileExpression(tokens);
        consumeClosingRoundBracket(tokens);
        return new Term(TermType.EXPRESSION_IN_BRACKETS, expression);
    }

    private void consumeOpeningRoundBracket(Tokens tokens) {
        validator.create(Validation.OPENING_ROUND_BRACKET).validate(tokens.current());
        tokens.advance();
    }

    private void consumeClosingRoundBracket(Tokens tokens) {
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

    private void consumeStatementBodyOpeningCurlyBracket(Tokens tokens) {
        validator.create(Validation.OPENING_CURLY_BRACKET).validate(tokens.current());
        tokens.advance();
    }

    private void consumeStatementBodyClosingCurlyBracket(Tokens tokens) {
        validator.create(Validation.CLOSING_CURLY_BRACKET).validate(tokens.current());
        tokens.advance();
    }

    private Term consumeUnaryOpTerm(Tokens tokens) {
        Token unaryOp = tokens.current();
        tokens.advance();
        Term term = consumeTerm(tokens);
        return new Term(TermType.UNARY_OP, List.of(new Terminal(unaryOp), term));
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
