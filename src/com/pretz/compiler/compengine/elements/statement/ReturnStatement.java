package com.pretz.compiler.compengine.elements.statement;

import com.pretz.compiler.compengine.elements.expression.Expression;
import io.vavr.control.Option;

import java.util.Objects;

public class ReturnStatement implements Statement {
    private final Option<Expression> expression;

    public ReturnStatement(Expression expression) {
        this.expression = Option.of(expression);
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
