import java.util.*;

/**
 * ParseTools class contains all the tools related to grammars.
 */
public class ParseTools {
    public ParseTools(){
    }

    private Set<String> firstK(Map<String, Set<String>> firstKMap, String x) {
        if (!firstKMap.containsKey(x)) {
            return new LinkedHashSet<>();
        } else {
            return firstKMap.get(x);
        }
    }

    public Map<String, Set<String>> constructFirstKSets(Map<Integer, Rule> cfg) {
        Map<String, Set<String>> firstKMap = new LinkedHashMap<>();

        //Initialization
        for (Rule rule : cfg.values()) {
            if (!firstKMap.containsKey(rule.getLeftHandSide())) {
                firstKMap.put(rule.getLeftHandSide(), firstK(firstKMap, rule.getLeftHandSide()));
            }
        }

        boolean atLeastOneFirstKSetHasBeenUpdated = true;
        while (atLeastOneFirstKSetHasBeenUpdated) {
            atLeastOneFirstKSetHasBeenUpdated = false;
            for (Rule rule : cfg.values()) {

                Set<String> set = new LinkedHashSet<>();
                for (String x : rule.getRightHandSide()) {
                    set.addAll(firstK(firstKMap, x));
                }

                Set<String> copySet  = firstKMap.get(rule.getLeftHandSide());
                firstKMap.get(rule.getLeftHandSide()).addAll(set);

                if (!atLeastOneFirstKSetHasBeenUpdated && !copySet.equals(firstKMap.get(rule.getLeftHandSide()))) {
                    atLeastOneFirstKSetHasBeenUpdated = true;
                }
                set.clear();
            }
        }
        return firstKMap;
    }

    private Set<String> followK(Map<String, Set<String>> firstKMap, String x) {
        //TODO
        return null;
    }


    public Map<String, Set<String>> constructFollowKSets(Map<Integer, Rule> cfg) {
        Map<String, Set<String>> followKMap = new LinkedHashMap<>();
        return followKMap;
    }

    public boolean isGrammarLL1(ContextFreeGrammar cfg) {
        //Compute the First and Follow sets
        /*
        Map<String, Set<String>> firstKSets = constructFirstKSets(cfg);
        Map<String, Set<String>> followKSets = constructFollowKSets(cfg);

        for (Rule rule : cfg.values()) {

            //Check the rules that are multiples
            ArrayList<Rule> multipleOccurences = new ArrayList<>();
            for (Rule r : cfg.values()) {
                if (rule.getLeftHandSide().equals(r.getLeftHandSide())) {
                    multipleOccurences.add(r);
                }
            }

            //Apply the definition
            Set<String> set = new LinkedHashSet<>();
            for (Rule x : multipleOccurences) {
                Set<String> newSet = firstK(firstKSets, rule.getRightHandSide() + followK(followKSets, rule.getLeftHandSide()));
                set.retainAll(newSet);
            }

            if (!set.isEmpty()){
                return false;
            }

        }

         */
        return true;
    }



    public int[][] constructLL1ActionTableFromCFG(ContextFreeGrammar contextFreeGrammar){
        //TODO
        return null;
    }

}
