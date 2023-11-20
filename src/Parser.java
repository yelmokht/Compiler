import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Parser class is a recursive descent LL(k) parser that parses a given input file according to its action table and
 * its CFG.
 */
public class Parser {
    private ParseTools parseTools;
    private ParseTree parseTree;
    private List<Integer> leftMostDerivation = new ArrayList<>();

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
     * @return True if the input word is a valid part of the grammar and prints the leftmost derivation.
     *         False if the input word is not part of the grammar, and an explanatory error is thrown.
     */

    public boolean parseRecursive(ContextFreeGrammar contextFreeGrammar, String[][] actionTable, List<Symbol> inputWord, Stack<Symbol> stack, int j) {
        List<Symbol> VAndT = contextFreeGrammar.getVariablesAndTerminals();
        List<Symbol> T = contextFreeGrammar.getTerminals();

        if (!stack.isEmpty()) {
            Symbol x = stack.peek();
            int row = VAndT.indexOf(x);
            Symbol lookAhead = inputWord.get(j - 1);
            int column = T.indexOf(lookAhead);

            // Produce i
            if (contextFreeGrammar.isRule(actionTable[row][column])) {
                int ruleNumber = Integer.parseInt(actionTable[row][column]);
                List<Symbol> alpha = contextFreeGrammar.getRules().get(ruleNumber).getRightHandSide();
                //System.out.println("Before: " + stack + "\n ");
                stack.pop();

                // Push(alpha)
                for (Symbol symbol : alpha.reversed()) {
                    if (!symbol.equals(ParseTools.EPSILON)) {
                        stack.push(symbol);
                    }
                }

                // Print(i)
                leftMostDerivation.add(ruleNumber);
                System.out.print(ruleNumber + " ");
                //System.out.println("After: " + stack + " \n");
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
                System.err.println("Error, unexpected symbol : " + lookAhead + ". Expected symbols : " + parseTools.getFirstKSets().get(x)); //
                return false;
            }
        }
        System.out.println();
        return true;
    }

    // You can then call this function with the initial values
    public boolean parse(ContextFreeGrammar contextFreeGrammar, String[][] actionTable, List<Symbol> inputWord) {
        Stack<Symbol> stack = new Stack<>();
        stack.push(contextFreeGrammar.getStartSymbol());
        int j = 1;

        return parseRecursive(contextFreeGrammar, actionTable, inputWord, stack, j);
    }

    public void buildParseTree(ContextFreeGrammar contextFreeGrammar) {
        Stack<Symbol> stack = new Stack<>();
        int j = 0;
        stack.push(contextFreeGrammar.getStartSymbol());
        parseTree = new ParseTree(stack.peek(), build(contextFreeGrammar, stack, j, parseTree));
        System.out.println(parseTree.toLaTeX());
    }

    public List<ParseTree> build(ContextFreeGrammar contextFreeGrammar, Stack<Symbol> stack, int j, ParseTree parseTree) {
        List<ParseTree> children = new ArrayList<>();

        System.out.println("Before Stack :" + stack);

        while (!stack.isEmpty()) {
            Symbol symbol = stack.pop();
            System.out.println("APrès pop Stack :" + stack);
            //System.out.println("Symbol: " + symbol);

            if (symbol.getType() == LexicalUnit.TERMINAL) {
                ParseTree terminalNode = new ParseTree(symbol);
                j += 1;
                children.add(terminalNode);
            }

            if (symbol.getType() == LexicalUnit.VARIABLE) {
                List<Symbol> symbols = contextFreeGrammar.getRules().get(leftMostDerivation.get(j)).getRightHandSide();

                for (int i = symbols.size() - 1; i >= 0; i--) {
                    Symbol s = symbols.get(i);
                    if (!s.equals(ParseTools.EPSILON)) {
                        stack.push(s);
                    }
                }

                System.out.println("After push Stack :" + stack);
                j += 1;
                ParseTree nonTerminalNode = new ParseTree(symbol, build(contextFreeGrammar, stack, j, parseTree));
                children.add(nonTerminalNode);
            }
        }
        return children;
    }
}
