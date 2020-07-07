package com.pretz.compiler;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JackAnalyzer {

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Empty file name - nothing to compile");
        }
        String fileName = args[0];
        new JackAnalyzer().outputFileNameFrom(fileName)
                .forEach(System.out::println);
    }

    private List<String> outputFileNameFrom(String fileName) {
        File inputFile = new File(fileName);
        if (inputFile.isDirectory()) {
            return outputFromDirectory(inputFile);
        }
        if (inputFile.isFile()) {
            return outputFromFile(inputFile);
        } else throw new IllegalArgumentException("File parsing error!");
    }

    private List<String> outputFromFile(File inputFile) {
        if (inputFile.getName().endsWith(".jack")) {
            return List.of(toXml(inputFile));
        } else throw new IllegalArgumentException("Not a .jack file!");
    }

    private List<String> outputFromDirectory(File inputFile) {
        File[] inputFiles = inputFile.listFiles(new JackFileFilter());
        if (inputFiles.length == 0) {
            throw new IllegalArgumentException("No .jack files in given directory!");
        } else {
            return Arrays.stream(inputFiles)
                    .map(this::toXml)
                    .collect(Collectors.toList());
        }
    }

    private String toXml(File file) {
        return file.getName().replaceAll("\\.jack", ".xml");
    }
}
