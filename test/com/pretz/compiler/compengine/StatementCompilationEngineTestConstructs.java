package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.expression.Expression;
import com.pretz.compiler.compengine.statement.DoStatement;
import com.pretz.compiler.compengine.statement.IfStatement;
import com.pretz.compiler.compengine.statement.LetStatement;
import com.pretz.compiler.compengine.statement.ReturnStatement;
import com.pretz.compiler.compengine.statement.WhileStatement;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.TerminalType;
import com.pretz.compiler.tokenizer.token.TokenType;
import io.vavr.collection.List;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class StatementCompilationEngineTestConstructs {

    private final ElementTestUtils $ = new ElementTestUtils();

    protected ReturnStatement emptyReturnStatement() {
        return $.returnStatement(null);
    }

    protected ReturnStatement returnStatementWithIntegerConstant() {
        return $.returnStatement($.expression($.constantTerm("5", TerminalType.INT_CONST)));
    }


    protected ReturnStatement returnStatementWithStringConstant() {
        return $.returnStatement($.expression($.constantTerm("Foo", TerminalType.STRING_CONST)));
    }

    protected ReturnStatement returnStatementWithKeywordConstant(String keyword) {
        return $.returnStatement($.expression($.constantTerm(keyword, TerminalType.KEYWORD_CONST)));
    }

    protected ReturnStatement returnStatementWithVarName() {
        return $.returnStatement($.expression($.varNameTerm("duck")));
    }

    protected ReturnStatement returnStatementWithOperators() {
        return $.returnStatement($.expression(
                $.unaryOpTerm("duck", "-"),
                List.of(
                        $.opTerm("+", $.varNameTerm("dog")),
                        $.opTerm("+", $.constantTerm("3", TerminalType.INT_CONST)))));
    }

    protected ReturnStatement returnStatementWithArray(Expression arrayIndexExpression) {
        return $.returnStatement($.expression($.varArrayTerm("duck", arrayIndexExpression)));
    }

    protected ReturnStatement returnStatementWithAdditionalBrackets() {
        return $.returnStatement($.expression(
                $.expressionInBracketsTerm(
                        $.expression($.varNameTerm("x"),
                                List.of($.opTerm("*", $.constantTerm("3", TerminalType.INT_CONST))))),
                List.of($.opTerm("+", $.varNameTerm("y")))));
    }

    protected ReturnStatement returnStatementWithSubroutineCall() {
        return $.returnStatement($.expression($.subroutineCallTerm("doStuff", $.expression($.constantTerm("5", TerminalType.INT_CONST)))));
    }

    protected ReturnStatement returnStatementWithClassSubroutineCall() {
        return $.returnStatement($.expression($.classSubroutineCallTerm("stuff", "doStuff",
                $.expression($.constantTerm("5", TerminalType.INT_CONST)))));
    }

    protected DoStatement doStatementSimple() {
        return $.doStatement($.subroutineCallTerm("doStuff", $.expression($.constantTerm("5", TerminalType.INT_CONST))));
    }

    protected DoStatement doStatementWithClassSubroutineCall() {
        return $.doStatement($.classSubroutineCallTerm("stuff", "doStuff", $.expression($.constantTerm("5", TerminalType.INT_CONST))));
    }

    protected WhileStatement whileStatement() {
        return new WhileStatement(
                $.expression($.constantTerm("true", TerminalType.KEYWORD_CONST)),
                List.of($.doStatement($.subroutineCallTerm("doStuff", $.expression($.constantTerm("3", TerminalType.INT_CONST)))),
                        $.doStatement($.subroutineCallTerm("doAnotherStuff", $.expression($.constantTerm("false", TerminalType.KEYWORD_CONST))))
                )
        );
    }

    protected IfStatement ifStatement() {
        return new IfStatement(
                $.expression($.varNameTerm("x"), List.of($.opTerm("=", $.constantTerm("3", TerminalType.INT_CONST))
                )),
                List.of($.doStatement($.subroutineCallTerm("doStuff", $.expression($.constantTerm("3", TerminalType.INT_CONST))))),
                List.of($.doStatement($.subroutineCallTerm("doAnotherStuff", $.expression($.constantTerm("false", TerminalType.KEYWORD_CONST)))))
        );
    }

    protected LetStatement letStatement() {
        return new LetStatement(
                $.varUsageIdentifier("x"),
                $.expression($.constantTerm("5", TerminalType.INT_CONST)),
                $.expression($.subroutineCallTerm("doStuff", $.expression($.constantTerm("3", TerminalType.INT_CONST))))
        );
    }

    static Stream<Arguments> arrayIndexTokensExpressions() {
        final ElementTestUtils $ = new ElementTestUtils();
        return Stream.of(
                Arguments.of(
                        List.of($.token("x", TokenType.IDENTIFIER)),
                        $.expression($.varNameTerm("x"))),
                Arguments.of(
                        List.of($.token("5", TokenType.INT_CONST)),
                        $.expression($.constantTerm("5", TerminalType.INT_CONST))),
                Arguments.of(
                        List.of($.token("Foo", TokenType.STRING_CONST)),
                        $.expression($.constantTerm("Foo", TerminalType.STRING_CONST))),
                Arguments.of(
                        List.of($.token("-", TokenType.SYMBOL),
                                $.token("a", TokenType.IDENTIFIER)),
                        $.expression($.unaryOpTerm("a", "-"))),
                Arguments.of(
                        List.of($.token("y", TokenType.IDENTIFIER),
                                $.token("[", TokenType.SYMBOL),
                                $.token("x", TokenType.IDENTIFIER),
                                $.token("]", TokenType.SYMBOL)),
                        $.expression($.varArrayTerm("y", $.expression($.varNameTerm("x")))))
        );
    }
}
