package com.pretz.compiler.compengine.expression;

import com.pretz.compiler.compengine.Element;
import com.pretz.compiler.compengine.VmContext;
import com.pretz.compiler.compengine.VmKeyword;
import com.pretz.compiler.compengine.construct.Construct;
import com.pretz.compiler.compengine.terminal.Identifier;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.IdentifierType;
import io.vavr.collection.List;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.pretz.compiler.compengine.VmKeyword.CALL;
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
                Case($(), () -> "Generic Term - NOT YET IMPLEMENTED!"));
    }

    private String simpleTermToVm(VmContext vmContext) {
        return VmKeyword.PUSH + " " + termParts.get(0).toVm(vmContext);
    }

    private String unaryOpToVm(VmContext vmContext) {
        return termParts.get(1).toVm(vmContext) + termParts.get(0).toVm(vmContext);
    }

    private String subroutineCallToVm(VmContext vmContext) {
        return subroutineCallParametersToVm(vmContext)
                + subroutineCallIdentifierToVm(vmContext) + "\n";
    }

    private String subroutineCallParametersToVm(VmContext vmContext) {
        return subroutineCallParameters().map(it -> it.toVm(vmContext)).mkString();
    }

    private List<Element> subroutineCallParameters() {
        if (isClassSubroutineCall()) {
            return classSubroutineCallParameters();
        } else return termParts.drop(1);
    }

    /**Checks if subroutine is function or method and pushes reference to method's 'this' on stack in the second case.*/
    private List<Element> classSubroutineCallParameters() {
        if (isStaticFunctionCall()) {
            return termParts.drop(2);
        } else {
            return termParts.drop(2).prepend(thisReference());
        }
    }

    private Term thisReference() {
        Identifier identifier = (Identifier) termParts.head();
        return new Term(TermType.VAR, new Identifier(identifier.token(), identifier.type(), IdentifierMeaning.USAGE, IdentifierType.VAR));
    }

    private String subroutineCallIdentifierToVm(VmContext vmContext) {
        return CALL + " " + subroutineCallIdentifier().map(it -> it.toVm(vmContext)).mkString(".") + " " + subroutineCallParametersNumber();
    }

    private List<Element> subroutineCallIdentifier() {
        if (isClassSubroutineCall()) {
            return classSubroutineCallIdentifier();
        } else return List.of(termParts.get(0));
    }

    private List<Element> classSubroutineCallIdentifier() {
        if (isStaticFunctionCall()) {
            return termParts.take(2);
        } else {
            return List.of(objectReference(), termParts.get(1));
        }
    }

    private Identifier objectReference() {
        Identifier identifier = (Identifier) termParts.head();
        return new Identifier(identifier.token(), identifier.type(), IdentifierMeaning.USAGE, IdentifierType.OBJECT);
    }

    private int subroutineCallParametersNumber() {
        if (isClassSubroutineCall()) {
            return classSubroutineCallParametersNumber();
        } else return termParts.size() - 1;
    }

    private int classSubroutineCallParametersNumber() {
        if (isStaticFunctionCall()) {
            return termParts.size() - 2;
        } else {
            return termParts.size() - 1;
        }
    }

    private boolean isClassSubroutineCall() {
        return termParts.head() instanceof Identifier && ((Identifier) termParts.head()).identifierType().equals(IdentifierType.CLASS);
    }

    private boolean isStaticFunctionCall() {
        return (termParts.head() instanceof Identifier && Character.isUpperCase(((Identifier) termParts.head()).token().charAt(0)));
    }
}
