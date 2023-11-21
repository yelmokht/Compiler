import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Main class parses a given input file and generates the parse tree of the input file in LaTeX.
 * Ensures also that the command is correctly written and handles errors.
 */
public class Main {
    private static final String RESOURCES_DIRECTORY = "src/resources/";

    public static void main(String[] args) throws IOException {
        if (args.length < 1 || args.length > 3) {
            throw new IllegalArgumentException("Incorrect number of arguments. Usage: java Main <input_file> or java Main -wt <filename.tex> <input_file>");
        }
        if (args.length == 1 && !args[0].equals("-wt")) {
            String inputFile = args[0];
            parse(inputFile); // Parse the input file only
        } else if (args.length == 3 && args[0].equals("-wt") && args[1].endsWith(".tex")) {
            String filename = args[1];
            String inputFile = args[2];
            parseAndBuildParseTree(inputFile, filename); // Parse the input file and build the parse tree
        } else {
            throw new IllegalArgumentException("Invalid arguments. Use -wt <filename.tex> <input_file>");
        }
    }

    /**
     * Scans the input file using the LexicalAnalyzer and generates two lists of Symbols:
     * 1. input1: With terminals changed (ex: 0 -> [Number] and x -> [VarName])
     * 2. input2: Without terminals changed (ex: 0 -> 0 and x -> x)
     * <p></p>
     * input1 will be used by the parser for the parsing process.
     * input2 will be used by the parser to generate the parse tree.
     * @param inputFile The path to the input file.
     * @return A list containing two lists of Symbols.
     * @throws IOException If an I/O error occurs.
     */
    private static List<List<Symbol>> scan(String inputFile) throws IOException {
        List<List<Symbol>> inputsList = new LinkedList<>();
        List<Symbol> input1 = new LinkedList<>();
        List<Symbol> input2 = new LinkedList<>();
        FileReader fileReader = new FileReader(inputFile);
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(fileReader);

        while (!lexicalAnalyzer.yyatEOF()) {
            Symbol symbol = lexicalAnalyzer.nextToken();
            if (symbol.getValue() != null) {
                if (symbol.getType() == LexicalUnit.NUMBER) {
                    input1.add(new Symbol(LexicalUnit.TERMINAL, "[Number]"));
                } else if (symbol.getType() == LexicalUnit.VARNAME) {
                    input1.add(new Symbol(LexicalUnit.TERMINAL, "[VarName]"));
                } else {
                    symbol.setType(LexicalUnit.TERMINAL);
                    input1.add(symbol);
                }
                symbol.setType(LexicalUnit.TERMINAL);
                input2.add(symbol);
            }
        }
        inputsList.add(input1); // With terminals changed (ex: 0 -> [Number] and x -> [VarName])
        inputsList.add(input2); // Without terminals changed (ex: 0 -> 0 and x -> x)
        fileReader.close();
        lexicalAnalyzer.yyclose();
        return inputsList;
    }


    /**
     * Parses the input file using the LL(1) recursive descent parser and output on stdout the leftmost derivation
     * of the input if it is correct or an explanatory error message if there is a syntax error.
     * @param inputFile The path to the input file to be parsed.
     * @return A Parser object representing the parsing result if the input word is part of the grammar.
     * @throws IllegalArgumentException If the grammar is not LL(1) or the input word is not part of the grammar.
     * @throws IOException If there is an error reading the input file.
     */
    private static Parser parse(String inputFile) throws IOException {
        ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar(RESOURCES_DIRECTORY + "cfg.pmp");

        // Check if the grammar is LL(1)
        ParseTools parseTools = new ParseTools();
        if (!parseTools.isGrammarLLK(contextFreeGrammar, 1)) {
            throw new IllegalArgumentException("The grammar is not LL(1).");
        }

        // Construct LL(1) Action Table
        String[][] actionTable = parseTools.constructLL1ActionTable(contextFreeGrammar);

        // Save sets and action table for debugging
        parseTools.writeFirstKSets("first_k_sets.txt");
        parseTools.writeFollowKSets("follow_k_sets.txt");
        parseTools.writeFirstKAlphaFollowKA("first_k_alpha_follow_k_A.txt");
        parseTools.writeActionTable(contextFreeGrammar, "action_table_LL1.txt");

        // Scan the input file
        List<Symbol> inputWord = scan(inputFile).get(0); // We need the input word with terminals changed for the action table

        // Initialize Parser and attempt parsing
        Parser parser = new Parser(parseTools);
        if (!parser.parse(contextFreeGrammar, actionTable, inputWord)) {
            throw new IllegalArgumentException("The input word is not part of the grammar.");
        }

        return parser;
    }

    /**
     * Parses the input file, builds the parse tree of the input and writes it to a LaTeX file called filename.tex
     * @param inputFile The path to the input file to be parsed.
     * @param filename  The name of the output LaTeX file to store the parse tree.
     * @throws IOException If there is an error reading the input file or writing the parse tree file.
     */
    private static void parseAndBuildParseTree(String inputFile, String filename) throws IOException {
        ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar(RESOURCES_DIRECTORY + "cfg.pmp");

        // Parse the input file
        Parser parser = parse(inputFile);

        // Scan the input file to get the input word without changing terminals for the parse tree
        List<Symbol> inputWord = scan(inputFile).get(1);

        // Build the parse tree using the parser and input word
        ParseTree parseTree = parser.buildParseTree(contextFreeGrammar, inputWord);

        // Write the parse tree to a LaTeX file called filename.tex
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(parseTree.toLaTeX());
        } catch (IOException e) {
            throw new IOException("Error while writing the parse tree in LaTeX. Exiting ...");
        }
    }
}
