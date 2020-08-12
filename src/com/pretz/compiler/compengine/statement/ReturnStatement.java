package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.expression.Expression;

import java.util.Objects;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.semicolon;
import static com.pretz.compiler.util.XmlUtils.simpleStartingKeyword;

public class ReturnStatement implements Statement {
    private static final String CONSTRUCT_NAME = "returnStatement";
    private static final String KEYWORD = "return";

    private final Expression expression; //TODO this can be empty

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                simpleStartingKeyword(indLvl, KEYWORD) +
                returnExpressionToXml(indLvl) +
                semicolon(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    private String returnExpressionToXml(int indLvl) {
        if (expression == null) {
            return "";
        } else {
            return expression.toXml(indLvl);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnStatement that = (ReturnStatement) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }
}
