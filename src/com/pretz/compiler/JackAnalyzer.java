package com.pretz.compiler;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class JackAnalyzer {

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Empty file name - nothing to compile");
        }
        String fileName = args[0];

        new JackAnalyzer().validate(fileName)
                .stream()
                .map(file -> new JackTokenizer().tokenize(file))
                .map(tokenList -> new CompilationEngine().compile(tokenList))//TODO what does it actually return?
                .forEach(System.out::println);//TODO write results to xml
    }

    /**
     * Validates if input filename parameter is correct.
     *
     * @param fileName input path or filename
     * @return list of input .jack files
     */
    private List<File> validate(String fileName) {
        File inputFile = new File(fileName);
        if (inputFile.isDirectory()) {
            return validateDirectory(inputFile);
        }
        if (inputFile.isFile()) {
            return validateFile(inputFile);
        } else throw new IllegalArgumentException("File parsing error!");
    }

    private List<File> validateDirectory(File inputFile) {
        File[] inputFiles = inputFile.listFiles(new JackFileFilter());
        if (inputFiles.length == 0) {
            throw new IllegalArgumentException("No .jack files in given directory!");
        } else {
            return Arrays.asList(inputFiles);
        }
    }

    private List<File> validateFile(File inputFile) {
        if (inputFile.getName().endsWith(".jack")) {
            return List.of(inputFile);
        } else throw new IllegalArgumentException("Not a .jack file!");
    }

    private String toXml(File file) {
        return file.getName().replaceAll("\\.jack", ".xml");
    }
}
