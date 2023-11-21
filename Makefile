JAVA = java
JAVAC = javac
JFLEX = jflex
JAR = jar
JAVADOC = javadoc
JAR_NAME = part2.jar
JAR_DIR = dist
DOC_DIR = doc
LIB_DIR = lib
JAVADOC_DIR = doc/javadoc
BUILD_DIR = more
SRC_DIR = src
TEST_DIR = test
TEST_RESOURCES_DIR = test/resources/parser/input
INPUT_FILE = src/resources/euclid.pmp
OUTPUT_FILE = filename.tex

javadoc: $(SRC_DIR)/*.java
	$(JAVADOC) -private $(SRC_DIR)/*.java -d $(JAVADOC_DIR)

jflex: $(SRC_DIR)/LexicalAnalyzer.flex
	$(JFLEX) $(SRC_DIR)/LexicalAnalyzer.flex

compile: jflex
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

testing:
	for testFile in $(TEST_RESOURCES_DIR)/*.pmp ; do \
		echo "\nTest file:" $$testFile ; \
		$(JAVA) -jar $(JAR_DIR)/$(JAR_NAME) $$testFile ; \
		echo "" ; \
	done

all: launch_parse_and_build_tree

.PHONY: clean

clean:
	rm -rf $(JAVADOC_DIR)/* $(JAR_DIR)/$(JAR_NAME) $(BUILD_DIR)/* *.txt *.tex
