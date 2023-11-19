JAVA = java
JAVAC = javac
JFLEX = jflex
JAR = jar
JAR_NAME = part2.jar
SRC_DIR = src
BUILD_DIR = out/production/Compiler
JAR_DIR = dist
INPUT_FILE = sourceFile.pmp
OUTPUT_FILE = sourceFile.tex

lex: $(SRC_DIR)/LexicalAnalyzer.flex
	$(JFLEX) $(SRC_DIR)/LexicalAnalyzer.flex

compile: lex
	$(JAVAC) -d $(BUILD_DIR) -cp $(BUILD_DIR) $(SRC_DIR)/*.java

parse_only: compile
	$(JAVA) -cp $(BUILD_DIR) Main $(INPUT_FILE)

parse_and_build_tree: compile
	$(JAVA) -cp $(BUILD_DIR) Main -wt $(OUTPUT_FILE) $(INPUT_FILE)

jar: compile
	$(JAR) cfe $(JAR_DIR)/$(JAR_NAME) Main -C $(BUILD_DIR) .

launch_parse_only: jar
	$(JAVA) -jar $(JAR_DIR)/$(JAR_NAME) $(INPUT_FILE)

launch_parse_and_build_tree: jar
	$(JAVA) -jar $(JAR_DIR)/$(JAR_NAME) -wt $(OUTPUT_FILE) $(INPUT_FILE)

all: parse_and_build_tree

.PHONY: clean

clean:
	rm -rf $(JAR_DIR)/$(JAR_NAME) $(BUILD_DIR)/*
