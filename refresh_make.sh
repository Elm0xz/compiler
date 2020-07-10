#!/usr/bin/env bash

rm JackAnalyzer
rm JackAnalyzer.jar
rm project10.zip
cd out/production/compiler/
jar cvfm ../../../JackAnalyzer.jar ../../../manifest .
cd ../../../
make
zip project10.zip makefile lang.txt manifest JackAnalyzer JackAnalyzer.jar lib/vavr-1.0.0-alpha-3.jar
