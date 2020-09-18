package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.VmContext;
import com.pretz.compiler.compengine.VmKeyword;
import com.pretz.compiler.compengine.symboltable.Kind;
import com.pretz.compiler.compengine.symboltable.Scope;
import com.pretz.compiler.compengine.symboltable.SubroutineSymbolTableFactory;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.terminal.Identifier;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.IdentifierType;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.terminal.TerminalType;
import io.vavr.collection.List;

import java.util.Objects;

import static com.pretz.compiler.compengine.VmKeyword.ALLOC;
import static com.pretz.compiler.compengine.VmKeyword.ARGUMENT;
import static com.pretz.compiler.compengine.VmKeyword.CALL;
import static com.pretz.compiler.compengine.VmKeyword.CONSTANT;
import static com.pretz.compiler.compengine.VmKeyword.POINTER;
import static com.pretz.compiler.compengine.VmKeyword.POP;
import static com.pretz.compiler.compengine.VmKeyword.PUSH;
import static com.pretz.compiler.compengine.terminal.TerminalKeywordType.CONSTRUCTOR;
import static com.pretz.compiler.compengine.terminal.TerminalKeywordType.METHOD;
import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.closingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.openingRoundBracket;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;

public class SubroutineDec implements Construct, Scope {
    private static final String CONSTRUCT_NAME = "subroutineDec";

    private final Terminal startingKeyword;
    private final Type type;
    private final Identifier subroutineName;
    private final ParameterList parameterList;
    private final SubroutineBody subroutineBody;

    private final VmContext subroutineSymbolTableAndScope;

    public SubroutineDec(Terminal startingKeyword, Type type, Identifier subroutineName,
                         ParameterList parameterList, SubroutineBody subroutineBody, Identifier classIdentifier) {
        this.startingKeyword = startingKeyword;
        this.type = type;
        this.subroutineName = subroutineName;
        this.parameterList = parameterList;
        this.subroutineBody = subroutineBody;
        this.subroutineSymbolTableAndScope = new VmContext(
                subroutineSymbolTable(parameterList, subroutineBody, classIdentifier),
                subroutineScope(subroutineName, classIdentifier));
    }

    private SymbolTable subroutineSymbolTable(ParameterList parameterList, SubroutineBody subroutineBody, Identifier classIdentifier) {
        return new SubroutineSymbolTableFactory()
                .create(filterParametersAndVarDecs(parameterList, subroutineBody, classIdentifier));
    }

    private List<Construct> filterParametersAndVarDecs(ParameterList parameterList, SubroutineBody subroutineBody, Identifier classIdentifier) {
        return parameterList.parameters()
                .prepend(addThisArgument(startingKeyword, classIdentifier))
                .map(it -> (Construct) it)
                .appendAll(subroutineBody.subroutineBody()
                        .filter(it -> it instanceof VarDec)
                );
    }

    private Parameter addThisArgument(Terminal startingKeyword, Identifier classIdentifier) {
        if (startingKeyword.token().equals("method")) {
            Type thisType = new Type(classIdentifier);
            Identifier thisVarName = new Identifier("this", TerminalType.IDENTIFIER, IdentifierMeaning.USAGE, IdentifierType.CLASS);
            return new Parameter(thisType, thisVarName);
        } else return null;
    }

    private String subroutineScope(Identifier subroutineName, Identifier classIdentifier) {
        return classIdentifier.token() + "." + subroutineName.token();
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                startingKeyword.toXml(indLvl) +
                type.toXml(indLvl) +
                subroutineName.toXml(indLvl) +
                openingRoundBracket(indLvl) +
                parameterList.toXml(indLvl) +
                closingRoundBracket(indLvl) +
                subroutineBody.toXml(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    @Override
    public SymbolTable symbolTable() {
        return subroutineSymbolTableAndScope.symbolTable();
    }

    @Override
    public String toVm(VmContext classSymbolTableAndScope) {
        return function() + "\n" +
                init(classSymbolTableAndScope) +
                subroutineBody.subroutineBody()
                        .filterNot(it -> it instanceof VarDec)
                        .zipWithIndex()
                        .map(it -> it._1().toVm(
                                classSymbolTableAndScope.mergeTablesAddingStatementId(
                                        this.subroutineSymbolTableAndScope, it._2().toString())))
                        .mkString();
    }

    //TODO(M) Unit test this
    private String function() {
        return List.of(VmKeyword.FUNCTION,
                subroutineSymbolTableAndScope.label(),
                symbolTable().numberByKind(Kind.VAR))
                .mkString(" ");
    }

    private String init(VmContext classSymbolTableAndScope) {
        return Match(startingKeyword.keywordType()).of(
                Case($(CONSTRUCTOR), () -> initConstructor(classSymbolTableAndScope)),
                Case($(METHOD), this::initMethod),
                Case($(), () -> ""));
    }

    /**
     * Sets 'this' pointer to properly use current object reference (passed as implicit first argument of the method).
     */
    private String initMethod() {
        return PUSH + " " + ARGUMENT + " 0\n" +
                POP + " " + POINTER + " 0\n";
    }

    /**
     * Adds additional memory allocation command when calling constructor.
     */
    private String initConstructor(VmContext classSymbolTableAndScope) {
        return PUSH + " " + CONSTANT + " " + numberOfVariablesToInitialize(classSymbolTableAndScope.symbolTable()) + "\n" +
                callAlloc() +
                popNewObjectReference();
    }

    private Integer numberOfVariablesToInitialize(SymbolTable classSymTab) {
        return classSymTab.numberByKind(Kind.FIELD) + classSymTab.numberByKind(Kind.STATIC);
    }

    private String callAlloc() {
        return CALL + " " + ALLOC + " 1\n";
    }

    private String popNewObjectReference() {
        return POP + " " + POINTER + " 0\n";
    }

    @Override
    public int hashCode() {
        return Objects.hash(startingKeyword, type, subroutineName, parameterList, subroutineBody);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubroutineDec that = (SubroutineDec) o;
        return startingKeyword.equals(that.startingKeyword) &&
                type.equals(that.type) &&
                subroutineName.equals(that.subroutineName) &&
                parameterList.equals(that.parameterList) &&
                subroutineBody.equals(that.subroutineBody);
    }

    @Override
    public String toString() {
        return "SubroutineDec{" +
                "startingKeyword=" + startingKeyword +
                ", type=" + type +
                ", subroutineName=" + subroutineName +
                ", parameterList=" + parameterList +
                ", subroutineBody=" + subroutineBody +
                '}';
    }
}
