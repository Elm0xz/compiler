package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.symboltable.ClassSymbolTableFactory;
import com.pretz.compiler.compengine.symboltable.Scope;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.terminal.Identifier;
import io.vavr.collection.List;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.closingCurlyBracket;
import static com.pretz.compiler.util.XmlUtils.openingCurlyBracket;
import static com.pretz.compiler.util.XmlUtils.simpleStartingKeyword;

public class Class implements Construct, Scope {
    private static final String CONSTRUCT_NAME = "class";

    private final Identifier identifier;
    private final List<Construct> declarations;
    private final SymbolTable classSymbolTable;

    public Class(Identifier identifier, List<Construct> declarations) {
        this.identifier = identifier;
        this.declarations = declarations;
        this.classSymbolTable = new ClassSymbolTableFactory().create(identifier, filterClassVariableDeclarations(declarations));
    }

    private List<Construct> filterClassVariableDeclarations(List<Construct> declarations) {
        return declarations.filter(it -> it instanceof ClassVarDec);
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                simpleStartingKeyword(indLvl, CONSTRUCT_NAME) +
                identifier.toXml(indLvl) +
                openingCurlyBracket(indLvl) +
                declarationsToXml(indLvl) +
                closingCurlyBracket(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    private String declarationsToXml(int indLvl) {
        return declarations.map(it -> it.toXml(indLvl))
                .collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class aClass = (Class) o;
        return identifier.equals(aClass.identifier) &&
                declarations.equals(aClass.declarations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, declarations);
    }

    @Override
    public String toString() {
        return "Class{" +
                "identifier=" + identifier +
                ", declarations=" + declarations +
                ", classSymbolTable=" + classSymbolTable +
                '}';
    }

    public Identifier identifier() {
        return identifier;
    }

    public List<Construct> declarations() {
        return declarations;
    }

    @Override
    public SymbolTable symbolTable() {
        return classSymbolTable;
    }

    public String toVm() {
        return this.toVm(classSymbolTable);
    }

    @Override
    public String toVm(SymbolTable symbolTable) {
        return declarations
                .filter(it -> it instanceof SubroutineDec)
                .map(it -> it.toVm(symbolTable))
                .mkString();
    }
}
