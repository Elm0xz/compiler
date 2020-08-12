package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.terminal.Terminal;

import java.util.Objects;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.closingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.openingRoundBracket;
import static com.pretz.compiler.util.XmlUtils.semicolon;

public class SubroutineDec implements Construct {
    private static final String CONSTRUCT_NAME = "subroutineDec";

    private final Terminal startingKeyword;
    private final Type type;
    private final Terminal subroutineName;
    private final ParameterList parameterList;
    private final SubroutineBody subroutineBody;


    public SubroutineDec(Terminal startingKeyword, Type type, Terminal subroutineName,
                         ParameterList parameterList, SubroutineBody subroutineBody) {
        this.startingKeyword = startingKeyword;
        this.type = type;
        this.subroutineName = subroutineName;
        this.parameterList = parameterList;
        this.subroutineBody = subroutineBody;
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
}
