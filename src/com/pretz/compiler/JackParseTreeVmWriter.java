package com.pretz.compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JackParseTreeVmWriter {

    public void write(String parseTree, File file) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(toVm(file)));
            writer.write(parseTree);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toVm(File file) {
        return file.getAbsolutePath().replaceAll("\\.jack", ".vm");
    }
}
