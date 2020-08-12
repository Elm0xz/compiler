package com.pretz.compiler;

import com.pretz.compiler.compengine.CompilationEngineFactory;
import com.pretz.compiler.input.JackFileFilter;
import com.pretz.compiler.tokenizer.JackTokenizer;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static io.vavr.API.Tuple;

public class JackAnalyzer {

    public static void main(String[] args) {
        //System.out.println(args[0]); //for debugging evaluator input
        if (args.length == 0) {
            throw new IllegalArgumentException("Empty file name - nothing to compile");
        }
        String fileName = args[0];

        new JackAnalyzer().validate(fileName)
                .stream()
                .map(file -> Tuple(new JackTokenizer().tokenize(file), file))
                /*.collect(Collectors.toList())*/
                /*.forEach(fileTokens -> new JackXmlWriter().write(fileTokens._1, fileTokens._2));*/
                .map(tokensAndFile -> Tuple(new CompilationEngineFactory().create().compileClass(tokensAndFile._1), tokensAndFile._2))
                .forEach(parseTreeAndFile -> new JackParseTreeXmlWriter().write(parseTreeAndFile._1.toXml(0), parseTreeAndFile._2));
    }

    /**
     * Validates input filename.
     *
     * @param fileName input path or filename
     * @throws IllegalArgumentException if cannot parse input successfully because of malformed path,
     * input file not being of .jack extension or directory not containing any .jack files.
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
}
