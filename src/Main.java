import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        if(parseTools.isGrammarLL1(contextFreeGrammar)) {
            //int[][] actionTable = parseTools.constructLL1ActionTableFromCFG(contextFreeGrammar);
            //parser.parse(contextFreeGrammar, actionTable, inputFile);
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
