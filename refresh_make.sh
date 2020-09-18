#!/usr/bin/env bash

rm JackAnalyzer
rm JackAnalyzer.jar
rm project11.zip
cd out/production/compiler/
jar cvfm ../../../JackCompiler.jar ../../../manifest .
cd ../../../
make
zip project11.zip makefile lang.txt manifest JackCompiler JackCompiler.jar vavr-1.0.0-alpha-3.jar
