package com.pretz.compiler.compengine.constructs;

import com.pretz.compiler.tokenizer.Token;

import java.util.Objects;

public class SubroutineDec implements Construct {
    private final Token startingKeyword;
    private final Type type;
    private final Token subroutineName;
    private final ParameterList parameterList;
    private final SubroutineBody subroutineBody;


    public SubroutineDec(Token startingKeyword, Type type, Token subroutineName, ParameterList parameterList, SubroutineBody subroutineBody) {
        this.startingKeyword = startingKeyword;
        this.type = type;
        this.subroutineName = subroutineName;
        this.parameterList = parameterList;
        this.subroutineBody = subroutineBody;
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
