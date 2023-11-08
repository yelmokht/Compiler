import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ContextFreeGrammar {
    private final String filePath;
    private final Set<String> alphabet = new LinkedHashSet<>();
    private final Set<String> variables = new LinkedHashSet<>();
    private final Set<String> terminals = new LinkedHashSet<>();
    private Map<Integer, Rule> rules = new LinkedHashMap<>();
    private String startSymbol;

    public ContextFreeGrammar(String filePath) throws IOException {
        this.filePath = filePath;
        setupGrammar(filePath);
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public Map<Integer, Rule> getRules() {
        return rules;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public void setupGrammar(String file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(file));

        for (String line : lines) {
            int number = Integer.parseInt(line.substring(line.indexOf("[") + 1, line.indexOf("]")));
            String leftHandSide = line.substring(line.indexOf("]") + 2, line.indexOf("→") - 1);
            String rightHandSide = line.substring(line.indexOf("→") + 2);
            alphabet.add(leftHandSide);
            alphabet.addAll(Arrays.asList(rightHandSide.split(" ")));
            variables.add(leftHandSide);
            terminals.addAll(alphabet);
            terminals.removeAll(variables);
            Rule rule = new Rule(leftHandSide, new ArrayList<>(List.of(rightHandSide.split(" "))));
            rules.put(number, rule);
            startSymbol = rules.get(1).getLeftHandSide();
        }
    }
}
