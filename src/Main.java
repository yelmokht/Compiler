/**
 * Main class parses a given input file and generates the parse tree of the input file in LaTeX.
 * Ensures also that the command is correctly written and handles errors.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw new IllegalArgumentException("Incorrect number of arguments. Usage: java Main <input_file> or java Main -wt <filename.tex> <input_file>");
        }

        if (args.length == 1 && !args[0].equals("-wt")) {
            String inputFile = args[0];
            parseAndBuildParseTree(inputFile, null); //We don't output the parse tree
        } else if (args.length == 3 && args[0].equals("-wt") && args[1].endsWith(".tex")) {
            String filename = args[1];
            String inputFile = args[2];
            parseAndBuildParseTree(inputFile, filename);
        } else {
            throw new IllegalArgumentException("Invalid arguments. Use -wt <filename.tex> <input_file>");
        }
    }

    private static void parseAndBuildParseTree(String inputFile, String filename) {
        Parser parser = new Parser();
        parser.parse(inputFile);

        if (filename != null) {
            ParseTree parseTree = new ParseTree(inputFile, filename);
            parseTree.toLatex();
        }
    }
}
