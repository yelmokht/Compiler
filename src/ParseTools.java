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

    public boolean isGrammarLL1(Map<Integer, Map<String, List<String>>> grammar){
        //TODO
        return false;
    }


    public int[][] constructLL1ActionTableFromCFG(Map<Integer, Map<String, List<String>>> contextFreeGrammar){
        //TODO
        return null;
    }

}
