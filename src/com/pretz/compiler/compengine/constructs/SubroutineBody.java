package com.pretz.compiler.compengine.constructs;

import io.vavr.collection.List;

import java.util.Objects;

public class SubroutineBody implements Construct {
    private final List<Construct> subroutineBody;

    public SubroutineBody(List<Construct> subroutineBody) {
        this.subroutineBody = subroutineBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubroutineBody that = (SubroutineBody) o;
        return subroutineBody.equals(that.subroutineBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subroutineBody);
    }
}
