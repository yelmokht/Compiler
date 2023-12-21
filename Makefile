default: build

jflex:
	jflex src/LexicalAnalyzer.flex

javadoc:
	javadoc -private src/*.java -d doc/javadoc

build: jflex
	javac -d more -cp src/ src/Main.java
	jar cfe dist/part3.jar Main -C more .

testing: build
	for testFile in test/_input/*.pmp ; do \
		echo "\nTest file:" $$testFile ; \
		echo "\tWrite LLVM code from $$(basename $$testFile .pmp).tex... \c"; \
		java -jar dist/part3.jar -wt /tmp/$$(basename $$testFile .pmp).tex $$testFile > test/llvm/$$(basename $$testFile .pmp).ll ; \
		echo "Done"; \
		echo "" ; \
	done

all: javadoc build testing

clean:
	rm -rf more/* dist/part3.jar doc/javadoc/*