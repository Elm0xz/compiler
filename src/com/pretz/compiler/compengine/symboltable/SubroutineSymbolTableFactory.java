package com.pretz.compiler.compengine.symboltable;

import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.construct.Parameter;
import com.pretz.compiler.compengine.construct.VarDec;
import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import java.util.function.Predicate;

public class SubroutineSymbolTableFactory implements SymbolTableFactory {

    //TODO ugly as hell
    @Override
    public SymbolTable create(Identifier identifier, List<Construct> declarations) {
        var parameters = asParameter(declarations.filter(it -> it instanceof Parameter));
        Map<SymbolId, Identifier> parametersTable =
                parameters
                        .zipWithIndex()
                        .map(this::from)
                        .toMap(it -> it);
        var varDecs = asVarDec(declarations.filter(it -> it instanceof VarDec));
        Map<SymbolId, Identifier> varDecsTable =
                varDecs
                        .flatMap(it -> from(it, varDecs))
                        .toMap(it -> it);
        return new SymbolTable(identifier, parametersTable.merge(varDecsTable));
    }

    private List<Parameter> asParameter(List<Construct> declarations) {
        return declarations.map(it -> (Parameter) it);
    }

    private Tuple2<SymbolId, Identifier> from(Tuple2<Parameter, Integer> parameterWithIndex) {
        return new Tuple2<>(new SymbolId(parameterWithIndex._1.type(),
                Kind.ARGUMENT, parameterWithIndex._2), parameterWithIndex._1.varName());
    }

    private List<VarDec> asVarDec(List<Construct> declarations) {
        return declarations.map(it -> (VarDec) it);
    }

    private List<Tuple2<SymbolId, Identifier>> from(VarDec varDec, List<VarDec> declarations) {
        return varDec.varNames().varNames()
                .zipWithIndex()
                .map(it -> new Tuple2<>(symbolId(varDec, nextId(it, varDec, declarations)), it._1));
    }

    //TODO maybe it could be simplified by first grouping variable declarations by type and kind and then indexing
    private Integer nextId(Tuple2<Identifier, Integer> varNameWithIndex, VarDec varDec, List<VarDec> declarations) {
        return declarations
                .filter(isSameTypeAndKind(varDec))
                .takeUntil(isCurrent(varDec))
                .flatMap(it -> it.varNames().varNames())
                .length() + varNameWithIndex._2;
    }

    private Predicate<VarDec> isSameTypeAndKind(VarDec varDec) {
        return it -> it.type().equals(varDec.type()) && it.startingKeyword().equals(varDec.startingKeyword());
    }

    private Predicate<VarDec> isCurrent(VarDec classVarDec) {
        return it -> it.equals(classVarDec);
    }

    private SymbolId symbolId(VarDec varDec, int index) {
        return new SymbolId(varDec.type(), varDec.startingKeyword().keywordType(), index);
    }
}
