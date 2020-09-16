package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.VmContext;
import io.vavr.collection.List;

import static com.pretz.compiler.compengine.VmKeyword.GOTO;
import static com.pretz.compiler.compengine.VmKeyword.IF_GOTO;
import static com.pretz.compiler.compengine.VmKeyword.LABEL;

public final class ConditionalUtils {

    private ConditionalUtils() {};

    protected static String statementsToVm(VmContext vmContext, List<Statement> statements, String prefix) {
        return statements.zipWithIndex()
                .map(it -> it._1().toVm(vmContext.addStatementId(prefix + it._2()))).mkString();
    }

    protected static String falseToGotoVm(String label) {
        return IF_GOTO + " " + falseLabel(label);
    }

    protected static String trueToGotoVm(String label) {
        return GOTO + " " + trueLabel(label);
    }

    protected static String falseToLabelVm(String label) {
        return LABEL + " " + falseLabel(label);
    }

    protected static String trueToLabelVm(String label) {
        return LABEL + " " + trueLabel(label);
    }

    protected static String falseLabel(String label) {
        return label + "false\n";
    }

    protected static String trueLabel(String label) {
        return label + "true\n";
    }
}
