all: createCompiler
createCompiler:
	touch JackAnalyzer
	chmod +x JackAnalyzer
	java -version
	echo "java -Dfile.encoding=UTF-8 -classpath ./lib/vavr-1.0.0-alpha-3.jar -jar JackAnalyzer.jar \"\044{1}\"" >> JackAnalyzer
