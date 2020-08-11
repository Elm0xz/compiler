package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.expression.Expression;
import io.vavr.collection.List;

import java.util.Objects;

public class WhileStatement implements Statement {
    private final Expression condition;
    private final List<Statement> statements;

    public WhileStatement(Expression condition, List<Statement> statements) {
        this.condition = condition;
        this.statements = statements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhileStatement that = (WhileStatement) o;
        return condition.equals(that.condition) &&
                statements.equals(that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, statements);
    }
}
