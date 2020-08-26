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
import com.pretz.compiler.compengine.statement.ReturnStatement;
import com.pretz.compiler.compengine.terminal.Identifier;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.terminal.TerminalType;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import io.vavr.collection.List;

public class ElementTestUtils {

    protected Token token(String token, TokenType identifier) {
        return new Token(token, identifier);
    }

    protected Identifier defIdentifier(Token newClassToken) {
        return new Identifier(newClassToken, IdentifierMeaning.DEFINITION);
    }

    protected Identifier defIdentifier(String token) {
        return new Identifier(token, TerminalType.IDENTIFIER, IdentifierMeaning.DEFINITION);
    }

    protected Identifier identifier(String token, IdentifierMeaning meaning) {
        return new Identifier(token, TerminalType.IDENTIFIER, meaning);
    }

    protected Class classC(Token className, List<Construct> constructs) {
        return new Class(defIdentifier(className), constructs);
    }

    protected Terminal terminal(String token, TerminalType keyword) {
        return new Terminal(token, keyword);
    }

    protected VarNames oneVarName(String varName) {
        return new VarNames(List.of(identifier(varName, IdentifierMeaning.DEFINITION)));
    }

    protected VarNames varNames(String... varNames) {
        return new VarNames(List.of(varNames).map(it -> identifier(it, IdentifierMeaning.DEFINITION)));
    }

    protected Type type(String type) {
        if (isPrimitive(type)) {
            return new Type(terminal(type, TerminalType.KEYWORD));
        } else {
            return new Type(identifier(type, IdentifierMeaning.USAGE));
        }
    }

    private boolean isPrimitive(String type) {
        return type.equals("int") || type.equals("boolean") ||
                type.equals("char") || type.equals("void");
    }

    protected ClassVarDec classVarDec(String keyword, String type, VarNames varNames) {
        return new ClassVarDec(terminal(keyword, TerminalType.KEYWORD), type(type), varNames);
    }

    protected VarDec varDec(String type, VarNames varNames) {
        return new VarDec(terminal("var", TerminalType.KEYWORD), type(type), varNames);
    }

    protected Parameter parameter(String type, String varName) {
        return new Parameter(type(type), identifier(varName, IdentifierMeaning.DEFINITION));
    }

    protected  SubroutineDec subroutineDec(String method, String s, String doStuff,
                                           ParameterList parameterList, SubroutineBody subroutineBody) {
        return new SubroutineDec(terminal(method, TerminalType.KEYWORD),
                type(s),
                identifier(doStuff, IdentifierMeaning.DEFINITION),
                parameterList,
                subroutineBody);
    }

    protected Term constantTerm(String token, TerminalType terminalType) {
        return new Term(TermType.CONSTANT, terminal(token, terminalType));
    }

    protected Term varNameTerm(String token) {
        return new Term(TermType.VAR, identifier(token, IdentifierMeaning.USAGE));
    }

    protected Term varArrayTerm(String token, Expression expression) {
        return new Term(TermType.VAR_ARRAY, List.of(identifier(token, IdentifierMeaning.USAGE), expression));
    }

    protected Term subroutineCallTerm(String token, Expression expression) {
        return new Term(TermType.SUBROUTINE_CALL, identifier(token, IdentifierMeaning.USAGE), expression);
    }

    protected Term classSubroutineCallTerm(String classToken, String subrToken, Expression expression) {
        return new Term(TermType.SUBROUTINE_CALL,
                List.of(
                        identifier(classToken, IdentifierMeaning.USAGE),
                        identifier(subrToken, IdentifierMeaning.USAGE),
                        expression));
    }

    //it does not take into account other possible nested terms apart from variable
    protected Term unaryOpTerm(String token) {
        return new Term(TermType.UNARY_OP,
                List.of(
                        terminal("-", TerminalType.SYMBOL),
                        varNameTerm(token)
                ));
    }

    protected Term expressionInBracketsTerm(Expression expression) {
        return new Term(TermType.EXPRESSION_IN_BRACKETS, expression);
    }

    protected Op op(String symbol) {
        return new Op(terminal(symbol, TerminalType.SYMBOL));
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
}
