import java.util.List;
import java.util.Map;

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
    public boolean parse(Map<Integer, Map<String, List<String>>> contextFreeGrammar, int[][] actionTable, String inputWord) {
        // TODO: Implement the parsing algorithm
        return false;
    }
}
