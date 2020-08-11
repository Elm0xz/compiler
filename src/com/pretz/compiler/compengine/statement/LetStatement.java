package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.expression.Expression;
import com.pretz.compiler.compengine.terminal.Terminal;

import java.util.Objects;

public class LetStatement implements Statement {
    private final Terminal varName;
    private final Expression arrayExpression;
    private final Expression assignedExpression;

    public LetStatement(Terminal varName, Expression arrayExpression, Expression assignedExpression) {
        this.varName = varName;
        this.arrayExpression = arrayExpression;
        this.assignedExpression = assignedExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LetStatement that = (LetStatement) o;
        return varName.equals(that.varName) &&
                Objects.equals(arrayExpression, that.arrayExpression) &&
                assignedExpression.equals(that.assignedExpression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varName, arrayExpression, assignedExpression);
    }
}
