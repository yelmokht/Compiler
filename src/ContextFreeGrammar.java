import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ContextFreeGrammar {
    private final String filePath;
    private Set<String> alphabet;
    private Set<String> variables;
    private Set<String> terminals;
    private Map<Integer, Rule> rules;
    private String startSymbol;

    public ContextFreeGrammar(String filePath) throws IOException {
        this.filePath = filePath;
        setupGrammar(filePath);
    }

    public Map<Integer, Rule> getRules() {
        return rules;
    }

    public void setupGrammar(String file) throws IOException {
        alphabet = new LinkedHashSet<>();
        variables = new LinkedHashSet<>();
        terminals = new LinkedHashSet<>();
        rules = new LinkedHashMap<>();

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
        }

        System.out.println("Alphabet: " + alphabet);
        System.out.println("Variables: " + variables);
        System.out.println("Terminaux: " + terminals);
        System.out.println("Règles: " + rules);
    }
}
