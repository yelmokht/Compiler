default: build

jflex:
	jflex src/LexicalAnalyzer.flex

javadoc:
	javadoc -private src/*.java -d doc/javadoc

build: jflex
	javac -d more -cp src/ src/Main.java
	jar cfe dist/part3.jar Main -C more .

test: build
	java -jar dist/part3.jar test/_input/00-euclid.pmp > test/llvm/00-euclid.ll
	llvm-as test/llvm/00-euclid.ll -o test/llvm/00-euclid.bc
	lli test/llvm/00-euclid.bc

testing: build
	for testFile in test/_input/*.pmp ; do \
		echo "\nTest file:" $$testFile ; \
		echo "\tWrite LLVM code from $$(basename $$testFile .pmp).tex..."; \
		java -jar dist/part3.jar -wt /tmp/$$(basename $$testFile .pmp).tex $$testFile > test/llvm/$$(basename $$testFile .pmp).ll ; \
		echo "\tCompile LLVM code from $$(basename $$testFile .pmp).ll..."; \
		llvm-as test/llvm/$$(basename $$testFile .pmp).ll -o test/llvm/$$(basename $$testFile .pmp).bc ; \
		echo "\tRun LLVM code from $$(basename $$testFile .pmp).bc..."; \
		lli test/llvm/$$(basename $$testFile .pmp).bc ; \
		echo "Done"; \
		echo "" ; \
	done

all: javadoc build testing

clean:
	rm -rf more/* dist/part3.jar doc/javadoc/*