package com.pretz.compiler.compengine.expression;

import com.pretz.compiler.compengine.CompilationException;
import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.terminal.TerminalType;
import com.pretz.compiler.compengine.validator.Validator;
import com.pretz.compiler.tokenizer.token.Token;
import com.pretz.compiler.tokenizer.token.TokenType;
import com.pretz.compiler.util.Lexicals;

import java.util.Objects;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;

public class Op implements Construct, Validator {
    private final Terminal op;
    private final OpType opType;

    public Op(Token token, OpType opType) {
        this.opType = opType;
        this.op = new Terminal(token, this, opType);
    }

    //TODO(L) used only in tests
    public Op(Terminal terminal) {
        this.opType = from(terminal.type());
        this.op = terminal;
    }

    private OpType from(TerminalType type) {
        return Match(type).of(
                Case($(TerminalType.OP), OpType.OP),
                Case($(TerminalType.UNARY_OP), OpType.UNARY_OP),
                Case($(), this::throwInvalidOperatorException)
        );
    }

    @Override
    public void validate(Token token) { //TODO(L) test this
        Match(opType).of(
                Case($(OpType.OP), () -> run(() -> validateOp(token))),
                Case($(OpType.UNARY_OP), () -> run(() -> validateUnaryOp(token))),
                Case($(), this::throwInvalidOperatorException)
        );
    }

    private void validateOp(Token token) {
        if (!token.is(TokenType.SYMBOL) ||
                (!Lexicals.ops().contains(token.token().charAt(0))))
            throwInvalidOperatorException();
    }

    private void validateUnaryOp(Token token) {
        if (!token.is(TokenType.SYMBOL) ||
                (!Lexicals.unaryOps().contains(token.token().charAt(0))))
            throw new CompilationException(CompilationException.INVALID_OPERATOR);
    }

    private OpType throwInvalidOperatorException() {
        throw new CompilationException(CompilationException.INVALID_OPERATOR);
    }

    @Override
    public String toXml(int indLvl) {
        return op.toXml(indLvl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Op op1 = (Op) o;
        return op.equals(op1.op);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op);
    }

    @Override
    public String toString() {
        return "Op{" +
                "op=" + op +
                '}';
    }

    @Override
    public String toVm(SymbolTable symbolTable) {
        return op.toVm(symbolTable);
    }
}
