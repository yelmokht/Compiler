import java.util.List;
import java.util.Stack;

/**
 * Parser class is a recursive descent LL(k) parser that parses a given input file according to its action table and
 * its CFG.
 */
public class Parser {

    /**
     * Constructs a new Parser.
     */
    public Parser() {
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
    public boolean parse(ContextFreeGrammar contextFreeGrammar, String[][] actionTable, List<String> inputWord) {
        List<String> VAndT = contextFreeGrammar.getVariablesAndTerminals();
        List<String> T = contextFreeGrammar.getTerminals();
        Stack<String> stack = new Stack<>();
        int j = 1;

        stack.push(contextFreeGrammar.getStartSymbol());
        System.out.println("input words : " + inputWord);

        while (!stack.isEmpty()) {
            String x = stack.peek();

            System.out.println("J : " + j);
            System.out.println();
            System.out.println("X : " + x);
            int row = VAndT.indexOf(x);
            String lookAhead = inputWord.get(j-1);
            int column = T.indexOf(lookAhead);

            System.out.println("ROW : " + row);
            System.out.println("LOOK-AHEAD : " + lookAhead);
            System.out.println("COLUMN : " + column);
            System.out.println("M["+ x +"][" + lookAhead + "] = " + actionTable[row][column]);
            System.out.println();

            // Produce i
            if (contextFreeGrammar.isRule(actionTable[row][column])) {
                int ruleNumber = Integer.parseInt(actionTable[row][column]);
                List<String> alpha = contextFreeGrammar.getRules().get(ruleNumber).getRightHandSide();
                System.out.println("BEFORE STACK : " + stack);
                stack.pop();

                // Push(alpha)
                for (String string : alpha.reversed()) {
                    if (!string.equals(ParseTools.EPSILON)) {
                        stack.push(string);
                    }

                }

                // Print(i)
                System.out.println("AFTER STACK : " + stack);
                System.out.print(ruleNumber + " ");
                System.out.println();
                System.out.println();

            // Match
            } else if (actionTable[row][column].equals("M")) {
                stack.pop();
                j += 1;

            // Accept
            } else if (actionTable[row][column].equals("A")) {
                return true;

            // Error
            } else {
                System.err.println("Error, unexpected symbol : " + lookAhead + ". Expected symbols : TODO"); // + expected symbols); Union First(Follow) pour la rule A
                return false;
            }
        }
        return false;
    }

}
