package com.pretz.compiler.compengine.expression;

import com.pretz.compiler.compengine.Element;
import com.pretz.compiler.compengine.VmContext;
import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.terminal.Identifier;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.IdentifierType;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.terminal.TerminalType;
import io.vavr.collection.List;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.pretz.compiler.compengine.VmKeyword.ADD;
import static com.pretz.compiler.compengine.VmKeyword.CALL;
import static com.pretz.compiler.compengine.VmKeyword.POINTER;
import static com.pretz.compiler.compengine.VmKeyword.PUSH;
import static com.pretz.compiler.compengine.terminal.TerminalType.STRING_CONST;
import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.closingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.closingSquareBracket;
import static com.pretz.compiler.util.XmlUtils.comma;
import static com.pretz.compiler.util.XmlUtils.dot;
import static com.pretz.compiler.util.XmlUtils.openingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.openingSquareBracket;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.anyOf;
import static io.vavr.Predicates.is;

public class Term implements Construct {
    private static final String CONSTRUCT_NAME = "term";
    private static final String EXPRESSION_LIST = "expressionList";

    private final List<Element> termParts;
    private final TermType termType;

    public Term(TermType termType, Element element) {
        this.termType = termType;
        this.termParts = List.of(element);
    }

    public Term(TermType termType, Element element1, Element element2) {
        this.termType = termType;
        this.termParts = List.of(element1, element2);
    }

    public Term(TermType termType, List<Element> elements) {
        this.termType = termType;
        this.termParts = elements;
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                termToXml(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    private String termToXml(int indLvl) {
        return Match(termType).of(
                Case($(TermType.CONSTANT), () -> termParts.get(0).toXml(indLvl)),
                Case($(TermType.VAR), () -> termParts.get(0).toXml(indLvl)),
                Case($(TermType.VAR_ARRAY), () -> varArrayToXml(indLvl)),
                Case($(TermType.SUBROUTINE_CALL), () -> subroutineCallToXml(indLvl)),
                Case($(TermType.EXPRESSION_IN_BRACKETS), () -> expressionInBracketsToXml(indLvl)),
                Case($(TermType.UNARY_OP), () -> unaryOpToXml(indLvl))
        );
    }

    private String varArrayToXml(int indLvl) {
        return termParts.get(0).toXml(indLvl) +
                openingSquareBracket(indLvl) +
                termParts.get(1).toXml(indLvl) +
                closingSquareBracket(indLvl);
    }

    private String expressionInBracketsToXml(int indLvl) {
        return openingRoundBracket(indLvl) +
                termParts.get(0).toXml(indLvl) +
                closingRoundBracket(indLvl);
    }

    public String subroutineCallToXml(int indLvl) {
        if (termParts.size() < 2 || termParts.get(1) instanceof Expression) {
            return basicSubroutineCallToXml(indLvl);
        } else {
            return classSubroutineCallToXml(indLvl);
        }
    }

    private String basicSubroutineCallToXml(int indLvl) {
        return termParts.get(0).toXml(indLvl) +
                openingRoundBracket(indLvl) +
                basicOpeningTag(indLvl, EXPRESSION_LIST) +
                termParts.subSequence(1).map(it -> it.toXml(indLvl))
                        .collect(Collectors.joining(comma(indLvl))) +
                basicClosingTag(indLvl, EXPRESSION_LIST) +
                closingRoundBracket(indLvl);
    }

    private String classSubroutineCallToXml(int indLvl) {
        return termParts.get(0).toXml(indLvl) +
                dot(indLvl) +
                termParts.get(1).toXml(indLvl) +
                openingRoundBracket(indLvl) +
                basicOpeningTag(indLvl, EXPRESSION_LIST) +
                termParts.subSequence(2).map(it -> it.toXml(indLvl))
                        .collect(Collectors.joining(comma(indLvl))) +
                basicClosingTag(indLvl, EXPRESSION_LIST) +
                closingRoundBracket(indLvl);
    }

    private String unaryOpToXml(int indLvl) {
        return termParts.map(it -> it.toXml(indLvl))
                .collect(Collectors.joining());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return termParts.equals(term.termParts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termParts);
    }

    @Override
    public String toString() {
        return "Term{" +
                "termParts=" + termParts +
                ", termType=" + termType +
                '}';
    }

    @Override
    public String toVm(VmContext vmContext) {
        return Match(termType).of(
                Case($(anyOf(is(TermType.CONSTANT), is(TermType.VAR))), () -> simpleTermToVm(vmContext)),
                Case($(TermType.UNARY_OP), () -> unaryOpToVm(vmContext)),
                Case($(TermType.SUBROUTINE_CALL), () -> subroutineCallToVm(vmContext)),
                Case($(TermType.VAR_ARRAY), () -> varArrayToVm(vmContext)),
                Case($(TermType.EXPRESSION_IN_BRACKETS), () -> expressionInBracketsToVm(vmContext))
        );
    }

    private String simpleTermToVm(VmContext vmContext) {
        //if ((Terminal) termParts.head().type().equals(STRING_CONST))
            return PUSH + " " + termParts.get(0).toVm(vmContext);
    }

    private String unaryOpToVm(VmContext vmContext) {
        return termParts.get(1).toVm(vmContext) + termParts.get(0).toVm(vmContext);
    }

    //TODO(!!!) some additional work probably needed...
    private String subroutineCallToVm(VmContext vmContext) {
        return Match(termParts.head()).of(
                Case($(isFunctionCall()), () -> functionCallToVm(vmContext)),
                Case($(isThisObjectMethodCall()), () -> thisObjectMethodCallToVm(vmContext)),
                Case($(isAnotherObjectMethodCall()), () -> anotherObjectMethodCallToVm(vmContext))
        );
    }

    /**
     * Function call has following syntax: Class.doStuff(x,y,z). First two elements of termParts are identifier, following are parameters.
     */
    private String functionCallToVm(VmContext vmContext) {
        return functionCallParametersToVm(vmContext) + functionCallIdentifierToVm(vmContext) + "\n";
    }

    private String functionCallParametersToVm(VmContext vmContext) {
        return functionCallParameters().map(it -> it.toVm(vmContext)).mkString();
    }

    private String functionCallIdentifierToVm(VmContext vmContext) {
        return CALL + " " + functionCallIdentifier().map(it -> it.toVm(vmContext)).mkString(".") + " " + functionCallParameters().size();
    }

    private List<Element> functionCallParameters() {
        return termParts.drop(2);
    }

    private List<Element> functionCallIdentifier() {
        return termParts.take(2);
    }

    /**
     * Method call on current object has following syntax: doStuff(x,y,z). First element is identifier, following are parameters.
     * Current object identifier is implicit.
     */
    private String thisObjectMethodCallToVm(VmContext vmContext) {
        return thisObjectMethodCallParametersToVm(vmContext) + thisObjectMethodCallIdentifierToVm(vmContext) + "\n";
    }

    private String thisObjectMethodCallParametersToVm(VmContext vmContext) {
        return PUSH + " " + POINTER + " " + "0\n" +
                thisObjectMethodCallParameters().map(it -> it.toVm(vmContext)).mkString();
    }

    private String thisObjectMethodCallIdentifierToVm(VmContext vmContext) {
        return CALL + " " + thisObjectIdentifier(vmContext) + "." +
                thisObjectMethodCallIdentifier().toVm(vmContext) + " " + thisObjectMethodCallParametersNumber();
    }

    private int thisObjectMethodCallParametersNumber() {
        return thisObjectMethodCallParameters().size() + 1;
    }

    private List<Element> thisObjectMethodCallParameters() {
        return termParts.drop(1);
    }

    private String thisObjectIdentifier(VmContext vmContext) {
        return objectIdentifierFromLabel(vmContext.label());
        //return symbolTable.get(thisIdentifier()).type();
    }

    //ugly regex hack, gods forgive me
    private String objectIdentifierFromLabel(String label) {
        return label.substring(1).split("\\.")[0];
    }

    private Identifier thisIdentifier() {
        return new Identifier("this", TerminalType.IDENTIFIER, IdentifierMeaning.USAGE, IdentifierType.CLASS);
    }

    private Element thisObjectMethodCallIdentifier() {
        return termParts.head();
    }

    /**
     * Method call on another object has following syntax: anotherObject.doStuff(x,y,z).
     * First two elements of termParts are identifier, following are parameters.
     */
    private String anotherObjectMethodCallToVm(VmContext vmContext) {
        return anotherObjectMethodCallParametersToVm(vmContext) + anotherObjectMethodCallIdentifierToVm(vmContext) + "\n";
    }

    private String anotherObjectMethodCallParametersToVm(VmContext vmContext) {
        return anotherObjectMethodCallParameters().map(it -> it.toVm(vmContext)).mkString();
    }

    private List<Element> anotherObjectMethodCallParameters() {
        return termParts.drop(2).prepend(anotherObjectReference());
    }

    private Term anotherObjectReference() {
        Identifier identifier = (Identifier) termParts.head();
        return new Term(TermType.VAR, new Identifier(identifier.token(), identifier.type(), IdentifierMeaning.USAGE, IdentifierType.VAR));
    }

    private String anotherObjectMethodCallIdentifierToVm(VmContext vmContext) {
        return CALL + " " + anotherObjectMethodCallIdentifier().map(it -> it.toVm(vmContext)).mkString(".") + " " + anotherObjectMethodCallParametersNumber();
    }

    private List<Element> anotherObjectMethodCallIdentifier() {
        return List.of(objectReference(), termParts.get(1));
    }

    private Identifier objectReference() {
        Identifier identifier = (Identifier) termParts.head();
        return new Identifier(identifier.token(), identifier.type(), IdentifierMeaning.USAGE, IdentifierType.OBJECT);
    }

    private int anotherObjectMethodCallParametersNumber() {
        return anotherObjectMethodCallParameters().size();
    }

    private Predicate<Element> isFunctionCall() {
        return it -> it instanceof Identifier
                && ((Identifier) it).identifierType().equals(IdentifierType.CLASS)
                && Character.isUpperCase(((Identifier) it).token().charAt(0));
    }

    private Predicate<Element> isThisObjectMethodCall() {
        return it -> it instanceof Identifier
                && ((Identifier) it).identifierType().equals(IdentifierType.SUBROUTINE);
    }

    private Predicate<Element> isAnotherObjectMethodCall() {
        return it -> it instanceof Identifier
                && ((Identifier) it).identifierType().equals(IdentifierType.CLASS)
                && Character.isLowerCase(((Identifier) it).token().charAt(0));
    }

    private String varArrayToVm(VmContext vmContext) {
        return termParts.map(it -> it.toVm(vmContext)).mkString() +
                ADD + "\n";
    }

    private String expressionInBracketsToVm(VmContext vmContext) {
        return termParts.head().toVm(vmContext);
    }
}
