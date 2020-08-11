package com.pretz.compiler.compengine.statement;

import com.pretz.compiler.compengine.expression.Term;

import java.util.Objects;

public class DoStatement implements Statement {
    private final Term subroutineCall; //TODO this should be only subroutinecall, not any other expression - maybe enum?

    public DoStatement(Term subroutineCall) {
        this.subroutineCall = subroutineCall;
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
}
