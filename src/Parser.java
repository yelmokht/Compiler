import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Parser class is a recursive descent LL(k) parser that parses a given input file according to its action table and
 * its CFG.
 */
public class Parser {
    private final ParseTools parseTools;
    private ParseTree parseTree;
    private final List<Integer> leftMostDerivation = new ArrayList<>();
    private int index = 0;
    private int counter = 0;

    /**
     * Constructs a new Parser.
     */
    public Parser(ParseTools parseTools) {
        this.parseTools = parseTools;
    }

    /**
     * Parses the input and prints the leftmost derivation of the given input word.
     *
     * @param contextFreeGrammar An LL(1) Context-Free Grammar.
     * @param actionTable The action table of the Context-Free Grammar.
     * @param inputWord The input word to be parsed.
     * @param stack The stack used in the parsing process.
     * @param j The index pointing to the current symbol in the input word.
     * @return True if the input word is a valid part of the grammar and prints the leftmost derivation on stdout.
     *         False if the input word is not part of the grammar, and an explanatory error is thrown.
     */
    public boolean parseRecursive(ContextFreeGrammar contextFreeGrammar, String[][] actionTable, List<Symbol> inputWord, Stack<Symbol> stack, int j) {
        // Get the list of variables and terminals
        List<Symbol> VAndT = contextFreeGrammar.getVariablesAndTerminals();
        List<Symbol> T = contextFreeGrammar.getTerminals();

        // Check if the stack is not empty
        if (!stack.isEmpty()) {
            // Get the symbol at the top of the stack
            Symbol x = stack.peek();

            // Find the corresponding row and column in the action table
            int row = VAndT.indexOf(x);
            Symbol lookAhead = inputWord.get(j - 1);
            int column = T.indexOf(lookAhead);

            // Produce i
            if (contextFreeGrammar.isRule(actionTable[row][column])) {
                int ruleNumber = Integer.parseInt(actionTable[row][column]);
                List<Symbol> alpha = contextFreeGrammar.getRules().get(ruleNumber).getRightHandSide();
                stack.pop();

                // Push(alpha) onto the stack
                for (Symbol symbol : alpha.reversed()) {
                    if (!symbol.equals(ParseTools.EPSILON)) {
                        stack.push(symbol);
                    }
                }

                // Print(i) in the leftmost derivation
                leftMostDerivation.add(ruleNumber);
                System.out.print(ruleNumber + " ");

                // Call the function recursively with the updated stack
                return parseRecursive(contextFreeGrammar, actionTable, inputWord, stack, j);

                // Match
            } else if (actionTable[row][column].equals("M")) {
                stack.pop();
                j += 1;

                // Call the function recursively with the updated stack and j
                return parseRecursive(contextFreeGrammar, actionTable, inputWord, stack, j);

                // Accept
            } else if (actionTable[row][column].equals("A")) {
                return true;

                // Error
            } else {
                System.err.println("\nError, unexpected symbol encountered: " + lookAhead + "\nAt line and column : "
                        + lookAhead.getLine() + ":" + lookAhead.getColumn()
                        + "\nExpected symbols: " + parseTools.getFirstKSets().get(x));
                return false;
            }
        }

        return true;
    }

    /**
     * Initiates the parsing process by calling the recursive parsing function with initial parameters.
     *
     * @param contextFreeGrammar An LL(1) Context-Free Grammar.
     * @param actionTable        The action table of the Context-Free Grammar.
     * @param inputWord          The input word to be parsed.
     * @return                   True if the input word is a valid part of the grammar.
     *                           False if the input word is not part of the grammar.
     */
    public boolean parse(ContextFreeGrammar contextFreeGrammar, String[][] actionTable, List<Symbol> inputWord) {
        // Initialize the stack with the start symbol and the index to the first symbol in the input word
        Stack<Symbol> stack = new Stack<>();
        stack.push(contextFreeGrammar.getStartSymbol());
        int j = 1;

        // Call the recursive parsing function with initial parameters
        return parseRecursive(contextFreeGrammar, actionTable, inputWord, stack, j);
    }


    /**
     * Constructs a parse tree for a given input word.
     *
     * @param contextFreeGrammar The context-free grammar to use for parsing.
     * @param inputWord The input word to parse.
     * @return The parse tree of the input
     */
    public ParseTree buildParseTree(ContextFreeGrammar contextFreeGrammar, List<Symbol> inputWord) {
        // Initialize a stack with the start symbol of the grammar
        Stack<Symbol> stack = new Stack<>();
        stack.push(contextFreeGrammar.getStartSymbol());

        // Build the parse tree using the buildTree function
        parseTree = buildTree(contextFreeGrammar, stack, inputWord).get(0);

        return parseTree;
    }

    /**
     * Recursively builds a parse tree for a given input.
     *
     * @param contextFreeGrammar The context-free grammar to use for parsing.
     * @param stack The stack of symbols
     * @param inputWord The input
     * @return A list of parse tree nodes representing the children of the current node.
     */
    public List<ParseTree> buildTree(ContextFreeGrammar contextFreeGrammar, Stack<Symbol> stack, List<Symbol> inputWord) {
        // List to store the children of the current parse tree node
        List<ParseTree> children = new ArrayList<>();

        // Iterate until the stack is empty
        while (!stack.isEmpty()) {
            // Pop the top symbol from the stack
            Symbol symbol = stack.pop();

            // If the symbol is a terminal
            if (symbol.getType() == LexicalUnit.TERMINAL) {
                // Create a parent node for terminal symbols [Number] or [VarName] with a child with the value of the input
                if (symbol.getValue().equals("[Number]") || symbol.getValue().equals("[VarName]")) {
                    ParseTree child = new ParseTree(inputWord.get(counter));
                    ParseTree parent = new ParseTree(symbol, List.of(child));
                    children.add(parent);
                } else {
                    // Create a child node for other terminal symbols
                    ParseTree child = new ParseTree(symbol);
                    children.add(child);
                }

                // Increment the counter for the lookahead
                if (!symbol.equals(ParseTools.EPSILON)) {
                    counter += 1;
                }
            }

            // If the symbol is a variable
            if (symbol.getType() == LexicalUnit.VARIABLE) {
                // Get the right-hand side of the rule for the current variable
                List<Symbol> symbols = contextFreeGrammar.getRules().get(leftMostDerivation.get(index)).getRightHandSide();

                // Create a new stack with the right-hand side of the rule
                Stack<Symbol> newStack = new Stack<>();
                for (Symbol s : symbols.reversed()) {
                    newStack.push(s);
                }

                // Increment the index to determine which rule to apply from the leftmost derivation
                index += 1;

                // Create the parent node for the variable with its children created recursively
                ParseTree parent = new ParseTree(symbol, buildTree(contextFreeGrammar, newStack, inputWord));
                children.add(parent);
            }
        }

        // Return the list of children for the current parse tree node
        return children;
    }

}
