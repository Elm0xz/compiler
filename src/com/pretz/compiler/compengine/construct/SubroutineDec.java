package com.pretz.compiler.compengine.construct;

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

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.closingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.openingRoundBracket;

public class SubroutineDec implements Construct, Scope {
    private static final String CONSTRUCT_NAME = "subroutineDec";

    private final Terminal startingKeyword;
    private final Type type;
    private final Identifier subroutineName;
    private final ParameterList parameterList;
    private final SubroutineBody subroutineBody;
    private final SymbolTable subroutineSymbolTable;

    public SubroutineDec(Terminal startingKeyword, Type type, Identifier subroutineName,
                         ParameterList parameterList, SubroutineBody subroutineBody, Identifier classIdentifier) {
        this.startingKeyword = startingKeyword;
        this.type = type;
        this.subroutineName = subroutineName;
        this.parameterList = parameterList;
        this.subroutineBody = subroutineBody;
        this.subroutineSymbolTable = new SubroutineSymbolTableFactory()
                .create(subroutineName, filterParametersAndVarDecs(parameterList, subroutineBody, classIdentifier));
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
    public int hashCode() {
        return Objects.hash(startingKeyword, type, subroutineName, parameterList, subroutineBody);
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

    @Override
    public SymbolTable symbolTable() {
        return subroutineSymbolTable;
    }

    @Override
    public String toVm(SymbolTable symbolTable) {
        return functionKeyword(symbolTable);
    }

    //TODO(M) Unit test this
    private String functionKeyword(SymbolTable symbolTable) {
        return List.of(VmKeyword.FUNCTION,
                symbolTable.scope() + "." + subroutineName.token(),
                subroutineSymbolTable.numberByKind(Kind.VAR))
                .mkString(" ");
    }
}
