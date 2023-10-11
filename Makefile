all:
	jflex LexicalAnalyzer.flex
	javac LexicalAnalyzer.java

test:
	java LexicalAnalyzer ./test/test.txt
