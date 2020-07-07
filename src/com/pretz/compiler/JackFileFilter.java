package com.pretz.compiler;

import java.io.File;
import java.io.FilenameFilter;

public class JackFileFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".jack");
    }
}
