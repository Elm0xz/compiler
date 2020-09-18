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
import com.pretz.compiler.compengine.expression.Expression;
import com.pretz.compiler.compengine.expression.Op;
import com.pretz.compiler.compengine.expression.OpTerm;
import com.pretz.compiler.compengine.expression.Term;
import com.pretz.compiler.compengine.expression.TermType;
import com.pretz.compiler.compengine.statement.DoStatement;
import com.pretz.compiler.compengine.statement.LetStatement;
import com.pretz.compiler.compengine.statement.ReturnStatement;
import com.pretz.compiler.compengine.terminal.Identifier;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.IdentifierType;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.terminal.TerminalType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import io.vavr.collection.List;

public class ElementTestUtils {

    protected Token token(String token, TokenType identifier) {
        return new Token(token, identifier);
    }

    protected Identifier classDefIdentifier(Token token) {
        return new Identifier(token, IdentifierMeaning.DEFINITION, IdentifierType.CLASS);
    }

    protected Identifier classDefIdentifier(String token) {
        return new Identifier(token, TerminalType.IDENTIFIER, IdentifierMeaning.DEFINITION, IdentifierType.CLASS);
    }

    protected Identifier classUsageIdentifier(String token) {
        return new Identifier(token, TerminalType.IDENTIFIER, IdentifierMeaning.USAGE, IdentifierType.CLASS);
    }

    protected Identifier subroutineDefIdentifier(String token) {
        return new Identifier(token, TerminalType.IDENTIFIER, IdentifierMeaning.DEFINITION, IdentifierType.SUBROUTINE);
    }

    protected Identifier subroutineUsageIdentifier(String token) {
        return new Identifier(token, TerminalType.IDENTIFIER, IdentifierMeaning.USAGE, IdentifierType.SUBROUTINE);
    }

    protected Identifier varDefIdentifier(String token) {
        return new Identifier(token, TerminalType.IDENTIFIER, IdentifierMeaning.DEFINITION, IdentifierType.VAR);
    }

    protected Identifier varUsageIdentifier(String token) {
        return new Identifier(token, TerminalType.IDENTIFIER, IdentifierMeaning.USAGE, IdentifierType.VAR);
    }

    protected Class classC(Token className, List<Construct> constructs) {
        return new Class(classDefIdentifier(className), constructs);
    }

    protected Terminal terminal(String token, TerminalType keyword) {
        return new Terminal(token, keyword);
    }

    protected VarNames oneVarName(String varName) {
        return new VarNames(List.of(varDefIdentifier(varName)));
    }

    protected VarNames varNames(String... varNames) {
        return new VarNames(List.of(varNames).map(this::varDefIdentifier));
    }

    protected Type type(String type) {
        if (isPrimitive(type)) {
            return new Type(terminal(type, TerminalType.KEYWORD_CONST));
        } else {
            return new Type(varUsageIdentifier(type));
        }
    }

    private boolean isPrimitive(String type) {
        return type.equals("int") || type.equals("boolean") ||
                type.equals("char") || type.equals("void");
    }

    protected ClassVarDec classVarDec(String keyword, String type, VarNames varNames) {
        return new ClassVarDec(terminal(keyword, TerminalType.KEYWORD_CONST), type(type), varNames);
    }

    protected VarDec varDec(String type, VarNames varNames) {
        return new VarDec(terminal("var", TerminalType.KEYWORD_CONST), type(type), varNames);
    }

    protected Parameter parameter(String type, String varName) {
        return new Parameter(type(type), varDefIdentifier(varName));
    }

    protected SubroutineDec subroutineDec(String method, String type, String subroutineName, String classIdentifier,
                                          ParameterList parameterList, SubroutineBody subroutineBody) {
        return new SubroutineDec(terminal(method, TerminalType.KEYWORD_CONST),
                type(type),
                subroutineDefIdentifier(subroutineName),
                parameterList,
                subroutineBody, classDefIdentifier(classIdentifier));
    }

    protected SubroutineDec subroutineDec(String method, String type, String subroutineName,
                                          ParameterList parameterList, SubroutineBody subroutineBody) {
        return new SubroutineDec(terminal(method, TerminalType.KEYWORD_CONST),
                type(type),
                subroutineDefIdentifier(subroutineName),
                parameterList,
                subroutineBody, classDefIdentifier("dummyClass"));
    }

    protected Term constantTerm(String token, TerminalType terminalType) {
        return new Term(TermType.CONSTANT, terminal(token, terminalType));
    }

    protected Term varNameTerm(String token) {
        return new Term(TermType.VAR, varUsageIdentifier(token));
    }

    protected Term varArrayTerm(String token, Expression expression) {
        return new Term(TermType.VAR_ARRAY, List.of(varUsageIdentifier(token), expression));
    }

    protected Term subroutineCallTerm(String token, Expression expression) {
        return new Term(TermType.SUBROUTINE_CALL, subroutineUsageIdentifier(token), expression);
    }

    protected Term subroutineCallTerm(String token, List<Expression> expressions) {
        return new Term(TermType.SUBROUTINE_CALL, List.of((Element) subroutineUsageIdentifier(token)).appendAll(expressions));
    }

    protected Term classSubroutineCallTerm(String classToken, String subrToken, Expression expression) {
        return new Term(TermType.SUBROUTINE_CALL,
                List.of(
                        classUsageIdentifier(classToken),
                        subroutineUsageIdentifier(subrToken),
                        expression));
    }

    protected Term classSubroutineCallTerm(String classToken, String subrToken, List<Expression> expressions) {
        return new Term(TermType.SUBROUTINE_CALL,
                List.of(
                        (Element) classUsageIdentifier(classToken),
                        subroutineUsageIdentifier(subrToken))
                        .appendAll(expressions));
    }

    //it does not take into account other possible nested terms apart from variable
    protected Term unaryOpTerm(String token, String unaryOp) {
        return new Term(TermType.UNARY_OP,
                List.of(
                        unaryOp(unaryOp),
                        varNameTerm(token)
                ));
    }

    private Op unaryOp(String unaryOp) {
        return new Op(terminal(unaryOp, TerminalType.UNARY_OP));
    }

    protected Term expressionInBracketsTerm(Expression expression) {
        return new Term(TermType.EXPRESSION_IN_BRACKETS, expression);
    }

    protected Op op(String symbol) {
        return new Op(terminal(symbol, TerminalType.OP));
    }

    protected OpTerm opTerm(String symbol, Term term) {
        return new OpTerm(
                op(symbol),
                term);
    }

    protected Expression expression(Term term) {
        return new Expression(term);
    }

    protected Expression expression(Term term, List<OpTerm> opTerms) {
        return new Expression(term, opTerms);
    }

    //it does not check if term is legit subroutine call
    protected DoStatement doStatement(Term subroutineCall) {
        return new DoStatement(subroutineCall);
    }

    protected ReturnStatement returnStatement(Expression expression) {
        return new ReturnStatement(expression);
    }

    protected LetStatement letStatement(String var, Expression expression) {
        return letStatement(var, null, expression);
    }

    protected LetStatement letStatement(String var, Expression arrayExpression, Expression expression) {
        return new LetStatement(
                varUsageIdentifier(var),
                arrayExpression,
                expression);
    }
}
