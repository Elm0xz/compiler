package com.pretz.compiler.compengine;

public interface Element {

    String toXml(int indLvl);
    String toVm(VmContext vmContext);
}
