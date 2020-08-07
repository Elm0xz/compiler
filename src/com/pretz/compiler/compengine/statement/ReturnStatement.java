package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.expression.Expression;

import java.util.Objects;

public class ReturnStatement implements Statement {
    private final Expression expression; //TODO this can be empty! think about using vavr option or refactor equals to avoid NullPointerException

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnStatement that = (ReturnStatement) o;
        return expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }
}
