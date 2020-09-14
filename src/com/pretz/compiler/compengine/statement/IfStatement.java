package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.VmContext;
import com.pretz.compiler.compengine.expression.Expression;
import io.vavr.collection.List;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.pretz.compiler.compengine.VmKeyword.GOTO;
import static com.pretz.compiler.compengine.VmKeyword.IF_GOTO;
import static com.pretz.compiler.compengine.VmKeyword.LABEL;
import static com.pretz.compiler.compengine.VmKeyword.NOT;
import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.closingCurlyBracket;
import static com.pretz.compiler.util.XmlUtils.closingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.openingCurlyBracket;
import static com.pretz.compiler.util.XmlUtils.openingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.simpleStartingKeyword;

public class IfStatement implements Statement {
    private static final String CONSTRUCT_NAME = "ifStatement";
    private static final String IF_KEYWORD = "if";
    private static final String ELSE_KEYWORD = "else";
    private static final String STATEMENTS = "statements";

    private final Expression condition;
    private final List<Statement> ifStatements;
    private final List<Statement> elseStatements;

    public IfStatement(Expression condition, List<Statement> ifStatements, List<Statement> elseStatements) {
        this.condition = condition;
        this.ifStatements = ifStatements;
        this.elseStatements = elseStatements;
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                simpleStartingKeyword(indLvl, IF_KEYWORD) +
                openingRoundBracket(indLvl) +
                condition.toXml(indLvl) +
                closingRoundBracket(indLvl) +
                openingCurlyBracket(indLvl) +
                ifStatementsToXml(indLvl) +
                closingCurlyBracket(indLvl) +
                elseStatementsToXml(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    private String ifStatementsToXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, STATEMENTS) +
                statementsToXml(indLvl, ifStatements) +
                basicClosingTag(indLvl - 1, STATEMENTS);
    }

    private String elseStatementsToXml(int indLvl) {
        if (elseStatements.isEmpty()) {
            return "";
        } else {
            return simpleStartingKeyword(indLvl, ELSE_KEYWORD) +
                    openingCurlyBracket(indLvl) +
                    basicOpeningTag(indLvl, STATEMENTS) +
                    statementsToXml(indLvl + 1, elseStatements) +
                    basicClosingTag(indLvl, STATEMENTS) +
                    closingCurlyBracket(indLvl);
        }
    }

    private String statementsToXml(int indLvl, List<Statement> statements) {
        return statements.map(it -> it.toXml(indLvl))
                .collect(Collectors.joining());
    }

    @Override
    public String toVm(VmContext vmContext) {
        return condition.toVm(vmContext) +
                NOT + "\n" +
                elseToGotoVm(vmContext.label()) +
                statementsToVm(vmContext, ifStatements) +
                ifToGotoVm(vmContext.label()) +
                elseToLabelVm(vmContext.label()) +
                statementsToVm(vmContext, elseStatements) +
                ifToLabelVm(vmContext.label());

    }

    private String statementsToVm(VmContext vmContext, List<Statement> statements) {
        return statements.zipWithIndex()
                .map(it -> it._1().toVm(vmContext.addStatementId(it._2()))).mkString();
    }

    private String elseToGotoVm(String label) {
        return IF_GOTO + " " + elseLabel(label);
    }

    private String ifToGotoVm(String label) {
        return GOTO + " " + ifLabel(label);
    }

    private String elseToLabelVm(String label) {
        return LABEL + " " + elseLabel(label);
    }

    private String ifToLabelVm(String label) {
        return LABEL + " " + ifLabel(label);
    }

    private String elseLabel(String label) {
        return label + "b\n";
    }

    private String ifLabel(String label) {
        return label + "a\n";
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

    @Override
    public String toString() {
        return "IfStatement{" +
                "condition=" + condition +
                ", ifStatements=" + ifStatements +
                ", elseStatements=" + elseStatements +
                '}';
    }
}
