package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.VmContext;
import com.pretz.compiler.compengine.expression.Expression;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.collection.List;

import java.util.Objects;

import static com.pretz.compiler.compengine.VmKeyword.ADD;
import static com.pretz.compiler.compengine.VmKeyword.ARRAY;
import static com.pretz.compiler.compengine.VmKeyword.POINTER;
import static com.pretz.compiler.compengine.VmKeyword.POP;
import static com.pretz.compiler.compengine.VmKeyword.PUSH;
import static com.pretz.compiler.compengine.VmKeyword.TEMP;
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

    private final Identifier varName;
    private final Expression arrayExpression;
    private final Expression assignedExpression;

    public LetStatement(Identifier varName, Expression arrayExpression, Expression assignedExpression) {
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
    public String toVm(VmContext vmContext) {
        if (arrayExpression ==  null) {
            return basicLetExpressionToVm(vmContext);
        } else return arrayLetExpressionToVm(vmContext);

    }

    private String basicLetExpressionToVm(VmContext vmContext) {
        return List.of(rightSideToVm(vmContext),
                leftSideToVm(vmContext.symbolTable()))
                .mkString();
    }

    private String arrayLetExpressionToVm(VmContext vmContext) {
        SymbolTable symbolTable = vmContext.symbolTable();
        return PUSH + " " + symbolTable.get(varName).toVm() +
                arrayExpression.toVm(vmContext) +
                ADD + "\n" +
                assignedExpression.toVm(vmContext) +
                POP + " " + TEMP + " 0\n" +
                POP + " " + POINTER + " 1\n" +
                PUSH + " " + TEMP + " 0\n" +
                POP + " " + ARRAY + " 0\n";
    }

    //TODO(M) handle array expressions here too
    private String leftSideToVm(SymbolTable symbolTable) {
        return POP + " " + (symbolTable.get(varName).toVm());
    }

    private String rightSideToVm(VmContext vmContext) {
        return assignedExpression.toVm(vmContext);
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

    @Override
    public String toString() {
        return "LetStatement{" +
                "varName=" + varName +
                ", arrayExpression=" + arrayExpression +
                ", assignedExpression=" + assignedExpression +
                '}';
    }
}
