package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.expression.Expression;
import com.pretz.compiler.compengine.terminal.Terminal;

import java.util.Objects;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.closingSquareBracket;
import static com.pretz.compiler.util.XmlUtils.equal;
import static com.pretz.compiler.util.XmlUtils.openingSquareBracket;
import static com.pretz.compiler.util.XmlUtils.semicolon;
import static com.pretz.compiler.util.XmlUtils.simpleStartingKeyword;

public class LetStatement implements Statement {
    private static final String CONSTRUCT_NAME = "letStatement";
    private static final String KEYWORD = "let";

    private final Terminal varName;
    private final Expression arrayExpression;
    private final Expression assignedExpression;

    public LetStatement(Terminal varName, Expression arrayExpression, Expression assignedExpression) {
        this.varName = varName;
        this.arrayExpression = arrayExpression;
        this.assignedExpression = assignedExpression;
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                simpleStartingKeyword(indLvl, KEYWORD) +
                varName.toXml(indLvl) +
                arrayExpressionToXml(indLvl) +
                equal(indLvl) +
                assignedExpression.toXml(indLvl) +
                semicolon(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    private String arrayExpressionToXml(int indLvl) {
        if (arrayExpression == null) {
            return "";
        } else {
            return openingSquareBracket(indLvl) +
                    arrayExpression.toXml(indLvl) +
                    closingSquareBracket(indLvl);
        }
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
