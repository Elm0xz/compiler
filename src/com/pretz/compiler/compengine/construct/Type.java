package com.pretz.compiler.compengine.construct;

import com.pretz.compiler.compengine.validator.Validator;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.tokenizer.token.Token;

import java.util.Objects;

public class Type implements Construct {
    private final Terminal type;

    public Type(Token type, Validator validator) {
        this.type = new Terminal(type, validator);
    }

    public Type(Terminal type) {
        this.type = type;
    }

    @Override
    public String toXml(int indLvl) {
        return type.toXml(indLvl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type1 = (Type) o;
        return type.equals(type1.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
