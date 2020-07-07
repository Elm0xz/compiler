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
            File[] inputFiles = inputFile.listFiles(new JackFileFilter());
            if (inputFiles.length == 0) {
                throw new IllegalArgumentException("No .jack files in given directory!");
            } else {
                return Arrays.stream(inputFiles)
                        .map(file -> file.getName().replaceAll("\\.jack", ".xml"))
                        .collect(Collectors.toList());
            }
        }
        if (inputFile.isFile()) {
            if (inputFile.getName().endsWith(".jack")) {
                return List.of(inputFile.getName().replaceAll("\\.jack", ".xml"));
            } else throw new IllegalArgumentException("Not a .jack file!");
        } else throw new IllegalArgumentException("File parsing error!");
    }
}
