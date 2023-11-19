import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ContextFreeGrammar {
    private static final String ARROW = "→";
    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";
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


    public void setupGrammar(String file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(file));
        Set<String> variablesSet = new LinkedHashSet<>();
        Set<String> variablesAndTerminalsSet = new LinkedHashSet<>();
        List<String> v = new ArrayList<>();
        List<String> t = new ArrayList<>();
        List<String> vt = new ArrayList<>();

        // Rules
        for (String line : lines) {
            int number = Integer.parseInt(line.substring(line.indexOf(LEFT_BRACKET) + 1, line.indexOf(RIGHT_BRACKET)));
            String leftHandSide = line.substring(line.indexOf(RIGHT_BRACKET) + 2, line.indexOf(ARROW) - 1);
            String rightHandSide = line.substring(line.indexOf(ARROW) + 2);
            variablesSet.add(leftHandSide);
            variablesAndTerminalsSet.add(leftHandSide);
            variablesAndTerminalsSet.addAll(Arrays.asList(rightHandSide.split(" ")));
        }

        // Variables
        v.addAll(variablesSet);

        // Terminals
        t.addAll(variablesAndTerminalsSet);
        t.removeAll(v);

        //Variables and terminals
        vt.addAll(v);
        vt.addAll(t);

        for (String s : v) {
            variables.add(new Symbol(LexicalUnit.VARIABLE, s));
        }

        for (String s : t) {
            terminals.add(new Symbol(LexicalUnit.TERMINAL, s));
        }

        variablesAndTerminals.addAll(variables);
        variablesAndTerminals.addAll(terminals);

        for (String line : lines) {
            int number = Integer.parseInt(line.substring(line.indexOf(LEFT_BRACKET) + 1, line.indexOf(RIGHT_BRACKET)));
            String leftHandSide = line.substring(line.indexOf(RIGHT_BRACKET) + 2, line.indexOf(ARROW) - 1);
            String rightHandSide = line.substring(line.indexOf(ARROW) + 2);
            Symbol l = new Symbol(LexicalUnit.VARIABLE, leftHandSide);
            List<Symbol> r = new ArrayList<>();
            for (String s : rightHandSide.split(" ")) {
                if (v.contains(s)) {
                    r.add(new Symbol(LexicalUnit.VARIABLE, s));
                } else {
                    r.add(new Symbol(LexicalUnit.TERMINAL, s));
                }
            }
            Rule rule = new Rule(l, r, number);
            rules.put(number, rule);
        }

        // Start symbol
        startSymbol = rules.get(1).getLeftHandSide();

    }

}
