import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * ParseTools class contains all the tools related to grammars.
 */
public class ParseTools {
    public ParseTools(){
    }

    public Map<Integer, Map<String, List<String>>> makeMapFromGrammar(String file) throws IOException {
        Map<Integer, Map<String, List<String>>> cfg = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(file));

        for (String line : lines) {
            int number = Integer.parseInt(line.substring(line.indexOf("[") + 1, line.indexOf("]")));
            String leftHandSide = line.substring(line.indexOf("]") + 2, line.indexOf("→") - 1);
            String rightHandSide = line.substring(line.indexOf("→") + 2);

            Map<String, List<String>> rule = new HashMap<>();
            rule.put(leftHandSide, new ArrayList<>(List.of(rightHandSide.split(" "))));
            cfg.put(number, rule);
        }
        System.out.println(cfg);
        return cfg;
    }

    private Set<String> firstK() {
        //TODO
        return null;
    }

    public Map<String, Set<String>> constructFirstSets(Map<Integer, Map<String, List<String>>> cfg) {
        //TODO => Algorithm
        // List of Hash map<String, Set<Lexical Unit>>
        // Update until each set hash map doesnt update
        return null;
    }

    private Set<String> followK() {
        //TODO
        return null;
    }


        public Map<String, Set<String>> constructFollowSets(Map<Integer, Map<String, List<String>>> cfg) {
        //TODO => Algorithm
        return null;
    }

    private boolean isUnambiguous(Map<Integer, Map<String, List<String>>> grammar) {
        //TODO => return one tree => deterministic
        // Or take into account the properties
        // Check that derivation trees respect the priority of operators
        // We can also check all the derivations
        // Instead we use the stronger definition that use first set and follow set
        // And check if there is things in common. If no, then it is true
        return false;
    }

    private boolean containsLeftRecursion(Map<Integer, Map<String, List<String>>> grammar) {
        //TODO
        // Check that there at least two left hand side rules
        return false;
    }

    private boolean containsCommonPrefixes(Map<Integer, Map<String, List<String>>> grammar) {
        //TODO
        // Check there is at least two same right hand side rule (prefix)
        return false;
    }

    public boolean isGrammarLL1(Map<Integer, Map<String, List<String>>> grammar){
        return isUnambiguous(grammar) && !containsLeftRecursion(grammar) && !containsCommonPrefixes(grammar);
    }

    private void removeUnproductiveVariables() {
        //TODO => Algorithm
    }

    private void removeUnreachableVariables() {
        //TODO => Algorithm
    }

    public void removeUselessVariables() {
        //TODO => Algorithm
        removeUnproductiveVariables();
        removeUnreachableVariables();
    }

    private void removeAmbiguities() {
        //TODO => make grammar non-ambiguous and take into account priorities
        //Checkez si possible d'implémenter un algorithme pour ça
    }

    public void removeLeftRecursion() {
        //TODO => Algorithm
    }

    public void applyFactoring() {
        //TODO
    }

    public Map<Integer, Map<String, List<String>>> transformGrammar(Map<Integer, Map<String, List<String>>> cfg) {
        //TODO => Algorithm
        removeUselessVariables();
        removeAmbiguities(); //Only if the grammar has ambiguities
        removeLeftRecursion();
        applyFactoring(); //When needed
        return cfg;
    }

    public int[][] constructLL1ActionTableFromCFG(Map<Integer, Map<String, List<String>>> contextFreeGrammar){
        //TODO
        return null;
    }

}
