package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.expression.Term;

import java.util.Objects;

import static com.pretz.compiler.util.XmlUtils.basicClosingTag;
import static com.pretz.compiler.util.XmlUtils.basicOpeningTag;
import static com.pretz.compiler.util.XmlUtils.semicolon;
import static com.pretz.compiler.util.XmlUtils.simpleStartingKeyword;

public class DoStatement implements Statement {
    private static final String CONSTRUCT_NAME = "doStatement";
    private static final String KEYWORD = "do";

    private final Term subroutineCall; //TODO(L) this should be only subroutinecall, not any other expression - maybe use enum?

    public DoStatement(Term subroutineCall) {
        this.subroutineCall = subroutineCall;
    }

    @Override
    public String toXml(int indLvl) {
        indLvl++;
        return basicOpeningTag(indLvl - 1, CONSTRUCT_NAME) +
                simpleStartingKeyword(indLvl, KEYWORD) +
                subroutineCall.subroutineCallToXml(indLvl) +
                semicolon(indLvl) +
                basicClosingTag(indLvl - 1, CONSTRUCT_NAME);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoStatement that = (DoStatement) o;
        return subroutineCall.equals(that.subroutineCall);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subroutineCall);
    }

    @Override
    public String toString() {
        return "DoStatement{" +
                "subroutineCall=" + subroutineCall +
                '}';
    }
}
