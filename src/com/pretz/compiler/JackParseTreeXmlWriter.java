package com.pretz.compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JackParseTreeXmlWriter {

    public void write(String parseTree, File file) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(toXml(file)));
            writer.write(parseTree);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toXml(File file) {
        return file.getAbsolutePath().replaceAll("\\.jack", "_1.xml");
    }
}
