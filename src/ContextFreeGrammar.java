import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a context-free grammar with variables, terminals, rules, and a start symbol.
 * <p></p>
 * Each rule must follow the format [ruleNumber] <variable> → <symbol1> <variable1> <symbol2> <variable2> ...,
 * where [ruleNumber] is the rule number, <variable1>, <variable2> are variables symbols, and <symbol1>,
 * <symbol2>, etc., are terminals symbols.
 * <p></p>
 * The start symbol is the left-hand side of the first rule. Variables are the left-hand sides
 * of all rules. Terminals are symbols on the right-hand side but not on the left-hand side.
 * Variables and terminals include symbols from both sides of all rules.
 * <p></p>
 * Rules, variables, and terminals are read from a file specified Main.
 * The file must adhere to the specified format.
 */

public class ContextFreeGrammar {
    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";
    private static final String START_VARIABLE = "<";
    private static final String FINISH_VARIABLE = ">";
    private static final String ARROW = "→";
    private static final String DELIMITER = " ";
    private final List<Symbol> variables = new ArrayList<>();
    private final List<Symbol> terminals = new ArrayList<>();
    private final List<Symbol> variablesAndTerminals = new ArrayList<>();
    private final Map<Integer, Rule> rules = new HashMap<>();
    private Symbol startSymbol;

    public ContextFreeGrammar(String filePath) throws IOException {
        setupGrammar(filePath);
    }

    public List<Symbol> getVariables() {
        return variables;
    }

    public List<Symbol> getTerminals() {
        return terminals;
    }

    public List<Symbol> getVariablesAndTerminals() {
        return variablesAndTerminals;
    }

    public Map<Integer, Rule> getRules() {
        return rules;
    }

    public Symbol getStartSymbol() {
        return startSymbol;
    }

    private void addVariable(Symbol variable) {
        if (!variables.contains(variable)) {
            variables.add(variable);
        }
    }

    private void addTerminal(Symbol terminal) {
        if (!terminals.contains(terminal)) {
            terminals.add(terminal);
        }
    }

    public boolean isVariable(Symbol variable) {
        return variables.contains(variable);
    }

    public boolean isTerminal(Symbol terminal) {
        return terminals.contains(terminal);
    }

    public boolean isRule(String action) {
        try {
            int ruleNumber = Integer.parseInt(action);
            return rules.containsKey(ruleNumber);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Sets up the context-free grammar by reading rules from the specified file.
     * <p></p>
     * This method initializes the grammar by parsing rules from the provided file. The file
     * should adhere to a specific format where each rule follows the pattern:
     * <p></p>
     *   [ruleNumber] <variable> → <symbol1> <symbol2> ...
     * <p></p>
     * Here, [ruleNumber] is the unique identifier for the rule, <variable> is a variable symbol,
     * and <symbol1>, <symbol2>, etc., represent the sequence of terminals and/or non-terminals on
     * the right-hand side of the rule.
     * <p></p>
     * The method extracts variables, terminals, and rules from the file and populates the corresponding
     * data structures in the grammar object. It also determines the start symbol, which is the left-hand
     * side of the first rule in the file.
     *
     * @param file The path to the file containing the grammar rules.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public void setupGrammar(String file) throws IOException {
            for (String line : Files.readAllLines(Path.of(file))) {
                int number = Integer.parseInt(line.substring(line.indexOf(LEFT_BRACKET) + 1, line.indexOf(RIGHT_BRACKET)));
                String leftHandSideString = line.substring(line.indexOf(RIGHT_BRACKET) + 2, line.indexOf(ARROW) - 1);
                String rightHandSideString = line.substring(line.indexOf(ARROW) + 2);
                Symbol leftHandSide = new Symbol(LexicalUnit.VARIABLE, leftHandSideString);
                addVariable(leftHandSide); // Set list of variables
                List<Symbol> rightHandSide = new ArrayList<>();

                for (String value : rightHandSideString.split(DELIMITER)) {

                    if (value.contains(START_VARIABLE) && value.contains(FINISH_VARIABLE)) {
                        Symbol variable = new Symbol(LexicalUnit.VARIABLE, value);
                        rightHandSide.add(variable);

                    } else {
                        Symbol terminal = new Symbol(LexicalUnit.TERMINAL, value);
                        addTerminal(terminal); // Set list of terminals
                        rightHandSide.add(terminal);
                    }
                }

                Rule rule = new Rule(leftHandSide, rightHandSide, number);
                rules.put(number, rule); //Set map of rules
            }

            // Set list of variablesAndTerminals in order (variables first, then terminals)
            variablesAndTerminals.addAll(variables);
            variablesAndTerminals.addAll(terminals);

            startSymbol = rules.get(1).getLeftHandSide(); // Set start symbol
    }

}
