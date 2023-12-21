default: build

jflex:
	jflex src/LexicalAnalyzer.flex

javadoc:
	javadoc -private src/*.java -d doc/javadoc

build: jflex
	javac -d more -cp src/ src/Main.java
	jar cfe dist/part3.jar Main -C more .

testing:
	for testFile in test/*.pmp ; do \
		echo "\nTest file:" $$testFile ; \
		java -jar dist/part3.jar -wt /tmp/$$(basename $$testFile .pmp).tex $$testFile ; \
		echo "\tCompiling tree figure $$(basename $$testFile .pmp).tex... \c"; \
		pdflatex -interaction=nonstopmode -output-directory /tmp /tmp/$$(basename $$testFile .pmp).tex $$testFile > /dev/null ; \
		mv /tmp/$$(basename $$testFile .pmp).pdf test-out2/ ; \
		echo "Done"; \
		echo "" ; \
	done

all: build testing

test: build
	java -jar dist/part3.jar -wt /tmp/tmp6.tex test/06-ExprArithWithParentheses.pmp
	echo "Compiling tree figure tmp6.tex..."
	@pdflatex -interaction=nonstopmode -output-directory /tmp /tmp/tmp6.tex 06-ExprArithWithParentheses.pmp > /dev/null
	@mv /tmp/tmp6.pdf ./
	@echo "Done"
	@echo ""

clean:
	rm -rf more/* dist/part3.jar doc/javadoc/*