package com.pretz.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JackInputReader {

    public Stream<Character> read(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JackInputSpliterator spliterator = new JackInputSpliterator(reader);
        return StreamSupport.stream(spliterator, false);
    }

    private static class JackInputSpliterator implements Spliterator<Character> {
        private final BufferedReader reader;

        private JackInputSpliterator(BufferedReader reader) {
            this.reader = reader;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {
            try {
                int c = reader.read();
                if (c != -1) {
                    char ch = (char) c;
                    action.accept(ch);
                    return true;
                } else return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public Spliterator<Character> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return ORDERED | NONNULL | IMMUTABLE;
        }
    }
}
