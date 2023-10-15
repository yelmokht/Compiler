all:
	jflex LexicalAnalyzer.flex
	javac LexicalAnalyzer.java
	java Main euclid.pmp

test:
	jflex LexicalAnalyzer.flex
	javac LexicalAnalyzer.java
	java Main ./test/test.txt
