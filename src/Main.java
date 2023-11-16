import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Main class parses a given input file and generates the parse tree of the input file in LaTeX.
 * Ensures also that the command is correctly written and handles errors.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 3) {
            throw new IllegalArgumentException("Incorrect number of arguments. Usage: java Main <input_file> or java Main -wt <filename.tex> <input_file>");
        }

        if (args.length == 1 && !args[0].equals("-wt")) {
            String inputFile = args[0];
            parseAndBuildParseTree(inputFile, null); //Argument optionnel -wt
        } else if (args.length == 3 && args[0].equals("-wt") && args[1].endsWith(".tex")) {
            String filename = args[1];
            String inputFile = args[2];
            parseAndBuildParseTree(inputFile, filename);
        } else {
            throw new IllegalArgumentException("Invalid arguments. Use -wt <filename.tex> <input_file>");
        }
    }

    private static void parseAndBuildParseTree(String inputFile, String filename) throws IOException {
        ParseTools parseTools = new ParseTools();
        Parser parser = new Parser();

        //Get the transformed grammar from file
        ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar("src/resources/CFG.pmp");


        //If grammar is LL(1), parse the file
        if(parseTools.isGrammarLLK(contextFreeGrammar, 1)) {
            parseTools.printFirstKSets("src/resources/firstKSets.pmp");
            parseTools.printFollowKSets("src/resources/followKSets.pmp");
            parseTools.printFirstKAlphaFollowKA("src/resources/firstKAlphaFollowKASets.pmp");
            String[][] actionTable = parseTools.constructLL1ActionTableFromCFG(contextFreeGrammar);
            parseTools.printActionTable(contextFreeGrammar, "src/resources/actionTable.txt");

            FileReader fileReader = new FileReader(inputFile);
            LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(fileReader);
            List<String> inputWord = new LinkedList<>();
            while (!lexicalAnalyzer.yyatEOF()) {
                Symbol symbol = lexicalAnalyzer.nextToken();
                String word = String.valueOf(symbol.getValue());

                if (symbol.getType() == LexicalUnit.VARNAME) {
                    word = "[VarName]";
                }

                if (symbol.getType() == LexicalUnit.NUMBER) {
                    word = "[Number]";
                }

                if (!word.equals("null")) {
                    inputWord.add(word);
                }
            }
            parser.parse(contextFreeGrammar, actionTable, inputWord);
        } else {
            throw new IllegalArgumentException("This context free grammar cannot be LL(1). Exiting ...");
        }

        //If filename is given, return the parse tree in LaTeX
        if (filename != null) {
            //ParseTree parseTree = new ParseTree(inputFile, filename);
            //parseTree.toLatex();
        }

    }
}
