all: createCompiler
createCompiler:
	touch JackCompiler
	chmod +x JackCompiler
	java -version
	echo "java -Dfile.encoding=UTF-8 -classpath vavr-1.0.0-alpha-3.jar -jar JackCompiler.jar \"\044{1}\"" >> JackCompiler
