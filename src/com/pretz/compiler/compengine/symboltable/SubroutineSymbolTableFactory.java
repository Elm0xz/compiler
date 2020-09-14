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

    //TODO(L) ugly as hell
    @Override
    public SymbolTable create(List<Construct> declarations) {
        var parameters = asParameter(declarations.filter(it -> it instanceof Parameter));
        Map<Identifier, Symbol> parametersTable =
                parameters
                        .zipWithIndex()
                        .map(this::from)
                        .toMap(it -> it);
        var varDecs = asVarDec(declarations.filter(it -> it instanceof VarDec));
        Map<Identifier, Symbol> varDecsTable =
                varDecs
                        .flatMap(it -> from(it, varDecs))
                        .toMap(it -> it);
        return new SymbolTable(parametersTable.merge(varDecsTable));
    }

    private List<Parameter> asParameter(List<Construct> declarations) {
        return declarations.map(it -> (Parameter) it);
    }

    private Tuple2<Identifier, Symbol> from(Tuple2<Parameter, Integer> parameterWithIndex) {
        return new Tuple2<>(parameterWithIndex._1.varName(), new Symbol(parameterWithIndex._1.type(),
                Kind.ARGUMENT, parameterWithIndex._2));
    }

    private List<VarDec> asVarDec(List<Construct> declarations) {
        return declarations.map(it -> (VarDec) it);
    }

    private List<Tuple2<Identifier, Symbol>> from(VarDec varDec, List<VarDec> declarations) {
        return varDec.varNames().varNames()
                .zipWithIndex()
                .map(it -> new Tuple2<>(it._1, symbolId(varDec, nextId(it, varDec, declarations))));
    }

    //TODO(L) maybe it could be simplified by first grouping variable declarations by type and kind and then indexing
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

    private Symbol symbolId(VarDec varDec, int index) {
        return new Symbol(varDec.type(), varDec.startingKeyword().keywordType(), index);
    }
}
