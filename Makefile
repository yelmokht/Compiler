JFLEX = jflex
JAVAC = javac
JAVA = java
JAR = jar
JAR_NAME = part1.jar
FILE_NAME = euclid.pmp
SRC_DIR = src
BUILD_DIR = out/production/Compiler
JAR_DIR = dist

all: run

lex: $(SRC_DIR)/LexicalAnalyzer.flex
	$(JFLEX) $(SRC_DIR)/LexicalAnalyzer.flex

compile: lex
	$(JAVAC) -d $(BUILD_DIR) -cp $(BUILD_DIR) $(SRC_DIR)/*.java

run: compile
	$(JAVA) -cp $(BUILD_DIR) Main $(SRC_DIR)/euclid.pmp

jar: compile
	$(JAR) cfe $(JAR_DIR)/$(JAR_NAME) Main -C $(BUILD_DIR) .

launch: jar
	$(JAVA) -$(JAR) $(JAR_DIR)/$(JAR_NAME) $(SRC_DIR)/$(FILE_NAME)

clean:
	rm $(JAR_DIR)/$(JAR_NAME) $(SRC_DIR)/LexicalAnalyzer.java $(BUILD_DIR)/*

