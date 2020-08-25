package com.pretz.compiler.compengine;

import com.pretz.compiler.compengine.construct.ClassVarDec;
import com.pretz.compiler.compengine.construct.Type;
import com.pretz.compiler.compengine.construct.VarNames;
import com.pretz.compiler.compengine.symboltable.Kind;
import com.pretz.compiler.compengine.symboltable.SymbolId;
import com.pretz.compiler.compengine.symboltable.SymbolTable;
import com.pretz.compiler.compengine.symboltable.SymbolTableFactory;
import com.pretz.compiler.compengine.terminal.Identifier;
import com.pretz.compiler.compengine.terminal.IdentifierMeaning;
import com.pretz.compiler.compengine.terminal.Terminal;
import com.pretz.compiler.compengine.terminal.TerminalType;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SymbolTableTest {

    private final SymbolTableFactory factory = new SymbolTableFactory();
//TODO tomorrow
/*   public void shouldAddOneClassVariableIdentifier() {
        ClassVarDec classVarDec = new ClassVarDec(ElementTestUtils.terminal("field", TerminalType.KEYWORD),
                new Type(ElementTestUtils.terminal("int", TerminalType.KEYWORD)),
                new VarNames(List.of(ElementTestUtils.identifier("x", IdentifierMeaning.DEFINITION))));

        SymbolTable symbolTable = factory.create(ElementTestUtils.identifier("testClass", IdentifierMeaning.DEFINITION),
                List.of(classVarDec));

        Assertions.assertThat(symbolTable.size()).isEqualTo(1);
        Assertions.assertThat(symbolTable.get(1)).isEqualTo(new Tuple2<>(new SymbolId(
                new Type(ElementTestUtils.terminal("int", TerminalType.KEYWORD)),
                Kind.FIELD, 0),
                ElementTestUtils.identifier("x", IdentifierMeaning.DEFINITION)));
    }*/
}
