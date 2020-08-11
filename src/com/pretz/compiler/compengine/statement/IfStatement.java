package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.expression.Expression;
import io.vavr.collection.List;

import java.util.Objects;

public class IfStatement implements Statement {
    private final Expression condition;
    private final List<Statement> ifStatements;
    private final List<Statement> elseStatements;

    public IfStatement(Expression condition, List<Statement> ifStatements, List<Statement> elseStatements) {
        this.condition = condition;
        this.ifStatements = ifStatements;
        this.elseStatements = elseStatements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IfStatement that = (IfStatement) o;
        return condition.equals(that.condition) &&
                ifStatements.equals(that.ifStatements) &&
                Objects.equals(elseStatements, that.elseStatements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, ifStatements, elseStatements);
    }
}
