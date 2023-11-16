import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ContextFreeGrammar {
    private static final String ARROW = "→";
    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";
    private final List<String> variables = new ArrayList<>();
    private final List<String> terminals = new ArrayList<>();
    private final List<String> variablesAndTerminals = new ArrayList<>();
    private final Map<Integer, Rule> rules = new HashMap<>();
    private String startSymbol;

    public ContextFreeGrammar(String filePath) throws IOException {
        setupGrammar(filePath);
    }

    public List<String> getVariables() {
        return variables;
    }

    public List<String> getTerminals() {
        return terminals;
    }

    public List<String> getVariablesAndTerminals() {
        return variablesAndTerminals;
    }

    public Map<Integer, Rule> getRules() {
        return rules;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public boolean isVariable(String variable) {
        return variables.contains(variable);
    }

    public boolean isTerminal(String terminal) {
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

        // Rules
        for (String line : lines) {
            int number = Integer.parseInt(line.substring(line.indexOf(LEFT_BRACKET) + 1, line.indexOf(RIGHT_BRACKET)));
            String leftHandSide = line.substring(line.indexOf(RIGHT_BRACKET) + 2, line.indexOf(ARROW) - 1);
            String rightHandSide = line.substring(line.indexOf(ARROW) + 2);
            variablesSet.add(leftHandSide);
            variablesAndTerminalsSet.add(leftHandSide);
            variablesAndTerminalsSet.addAll(Arrays.asList(rightHandSide.split(" ")));
            Rule rule = new Rule(leftHandSide, new ArrayList<>(List.of(rightHandSide.split(" "))), number);
            rules.put(number, rule);
        }

        // Start symbol
        startSymbol = rules.get(1).getLeftHandSide();

        // Variables
        variables.addAll(variablesSet);

        // Terminals
        terminals.addAll(variablesAndTerminalsSet);
        terminals.removeAll(variables);

        //Variables and terminals
        variablesAndTerminals.addAll(variables);
        variablesAndTerminals.addAll(terminals);
    }

}
