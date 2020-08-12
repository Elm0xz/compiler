package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.expression.Expression;
import io.vavr.collection.List;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.closingCurlyBracket;
import static com.pretz.compiler.util.XmlUtils.closingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.openingCurlyBracket;
import static com.pretz.compiler.util.XmlUtils.openingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.simpleStartingKeyword;

public class WhileStatement implements Statement {
    private static final String CONSTRUCT_NAME = "whileStatement";
    private static final String KEYWORD = "while";
    private static final String STATEMENTS = "statements";

    private final Expression condition;
    private final List<Statement> statements;

    public WhileStatement(Expression condition, List<Statement> statements) {
        this.condition = condition;
        this.statements = statements;
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                simpleStartingKeyword(indLvl, KEYWORD) +
                openingRoundBracket(indLvl) +
                condition.toXml(indLvl) +
                closingRoundBracket(indLvl) +
                openingCurlyBracket(indLvl) +
                basicOpeningTag(indLvl, STATEMENTS) +
                statementsToXml(indLvl + 1) +
                basicClosingTag(indLvl, STATEMENTS) +
                closingCurlyBracket(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    private String statementsToXml(int indLvl) {
        return statements.map(it -> it.toXml(indLvl))
                .collect(Collectors.joining());
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
