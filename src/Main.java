/**
 * Main class parses a given input string and generates the parse tree of the input string in LaTeX.
 * Ensures also that the command is correctly written and handles errors.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 3) {
            if ("-wt".equals(args[1])) {
                String input = args[0].replace("\"", ""); // Remove quotes from the first argument.
                String filename = args[2];

                if (filename.endsWith(".tex")) {
                    Parser parser = new Parser();
                    parser.parse(input);

                    ParseTree parseTree = new ParseTree(input, filename);
                    parseTree.toLatex();
                } else {
                    throw new IllegalArgumentException("The filename must end with .tex");
                }
            } else {
                throw new IllegalArgumentException("Use -wt <filename.tex> to write the parse tree in LaTeX.");
            }
        } else {
            throw new IllegalArgumentException("Insufficient arguments. Usage: java Main \"input_string\" -wt <filename.tex>");
        }
    }
}
