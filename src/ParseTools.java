import java.util.*;

/**
 * ParseTools class contains all the tools related to grammars.
 */
public class ParseTools {
    private Map<String, Set<String>> firstKSets = new LinkedHashMap<>();
    private Map<String, Set<String>> followKSets = new LinkedHashMap<>();
    private int[][] actionTable;
    public static final String EPSILON = "ε";
    public static final String FOLLOW = "Follow";


    public ParseTools(){}

    //OK
    private Set<String> firstK(ContextFreeGrammar contextFreeGrammar, List<String> stringList) {
        Set<String> firstKSet = new LinkedHashSet<>();
        for (String string : stringList) {
            if (!(contextFreeGrammar.getVariables().contains(string) || contextFreeGrammar.getTerminals().contains(string))) {
                throw new IllegalArgumentException("Cannot compute firstK");
            }
            firstKSet.addAll(firstKSets.get(string));
        }
        return firstKSet;
    }

    //Pas sur car firstK pour le calcul ?
    private Set<String> followK(ContextFreeGrammar cfg, String variable) {
        Set<String> followKSet = new LinkedHashSet<>();

        if (variable.equals(cfg.getStartSymbol())) {
            followKSet.add(EPSILON);
        }

        for (Rule rule : cfg.getRules().values()) {
            List<String> rightHandSide = rule.getRightHandSide();
            for (int i = 0; i < rightHandSide.size(); i++) {
                if (rightHandSide.get(i).equals(variable) && i < rightHandSide.size() - 1) {
                    String nextSymbol = rightHandSide.get(i + 1);
                    followKSet.addAll(computeFirstK(cfg, List.of(nextSymbol)));
                }
            }
        }

        return followKSet;
    }

    //OK
    private Set<String> firstKWithFollowK(ContextFreeGrammar contextFreeGrammar, List<String> stringList) {
        String firstString = stringList.getFirst();
        String lastString = stringList.getLast();
        if (contextFreeGrammar.getTerminals().contains(firstString) && !firstString.equals(EPSILON)) {
            return computeFirstK(contextFreeGrammar, List.of(firstString));
        } else {
            return followK(contextFreeGrammar, lastString); //return if present in followKsets
        }
    }

    //OK
    private Set<String> computeFirstK(ContextFreeGrammar contextFreeGrammar, List<String> stringList) {
        Set<String> firstKSet;
        if (!stringList.contains(FOLLOW)) {
            firstKSet = firstK(contextFreeGrammar, stringList);
        } else {
            firstKSet = firstKWithFollowK(contextFreeGrammar, stringList);
        }
        return firstKSet;
    }

    private Set<String> computeFirstKWithFollowK(ContextFreeGrammar contextFreeGrammar, List<String> stringList) {
        String firstString = stringList.getFirst();
        String lastString = stringList.getLast();
        if (contextFreeGrammar.getTerminals().contains(firstString) && !firstString.equals(EPSILON)) {
            return firstKSets.get(firstString);
        } else {
            return followKSets.get(lastString);
        }
    }

    //OK
    private void initializeFirstKSets(ContextFreeGrammar contextFreeGrammar) {
        for (String terminal : contextFreeGrammar.getTerminals()) {
            firstKSets.put(terminal, new LinkedHashSet<>());
            firstKSets.get(terminal).add(terminal);
        }

        for (String variable : contextFreeGrammar.getVariables()) {
            firstKSets.put(variable, new LinkedHashSet<>());
        }
    }

    //OK
    private void initializeFollowKSets(ContextFreeGrammar cfg) {
        for (String variable : cfg.getVariables()) {
            followKSets.put(variable, new LinkedHashSet<>());
        }
        followKSets.get(cfg.getStartSymbol()).add(EPSILON);
    }

    //OK
    public Map<String, Set<String>> constructFirstKSets(ContextFreeGrammar contextFreeGrammar) {
        initializeFirstKSets(contextFreeGrammar);
        boolean atLeastOneFirstKSetHasBeenUpdated;
        do {
            atLeastOneFirstKSetHasBeenUpdated = false;
            for (Rule rule : contextFreeGrammar.getRules().values()) {
                String A = rule.getLeftHandSide();
                Set<String> oldFollowKSet  = Set.copyOf(firstKSets.get(rule.getLeftHandSide()));
                Set<String> firstKSet = computeFirstK(contextFreeGrammar, rule.getRightHandSide());
                firstKSets.get(A).addAll(firstKSet);
                if (!atLeastOneFirstKSetHasBeenUpdated && !oldFollowKSet.equals(firstKSets.get(rule.getLeftHandSide()))) {
                    atLeastOneFirstKSetHasBeenUpdated = true;
                }
            }
        } while (atLeastOneFirstKSetHasBeenUpdated);
        return firstKSets;
    }

    //Pas sur car manque Follow(A)
    public Map<String, Set<String>> constructFollowKSets(ContextFreeGrammar cfg) {
        initializeFollowKSets(cfg);
        boolean atLeastOneFollowKSetHasBeenUpdated;
        do {
            atLeastOneFollowKSetHasBeenUpdated = false;
            for (Rule rule : cfg.getRules().values()) {
                String A = rule.getLeftHandSide();
                List<String> rightHandSide = rule.getRightHandSide();
                for (int i = 0; i < rightHandSide.size(); i++) {
                    String B = rightHandSide.get(i);
                    if (cfg.getVariables().contains(B)) {
                        List<String> beta = rightHandSide.subList(i + 1, rightHandSide.size());
                        Set<String> oldFollowKSet = new LinkedHashSet<>(followKSets.get(B));
                        Set<String> followKSet = beta.isEmpty() ? computeFirstK(cfg, List.of(EPSILON)) : computeFirstK(cfg, beta); // ⊙ Follow(A)?
                        followKSets.get(B).addAll(followKSet);
                        if (!atLeastOneFollowKSetHasBeenUpdated && !oldFollowKSet.equals(followKSets.get(B))) {
                            atLeastOneFollowKSetHasBeenUpdated = true;
                        }
                    }
                }
            }
        } while (atLeastOneFollowKSetHasBeenUpdated);
        return followKSets;
    }

    //OK
    public List<List<Integer>> occurrencesRules(ContextFreeGrammar contextFreeGrammar) {
        List<List<Integer>> list = new LinkedList<>();
        Map<Integer, Rule> rules = contextFreeGrammar.getRules();
        Set<Integer> processedRules = new HashSet<>();
        for (int ruleNumber1 : rules.keySet()) {
            if (!processedRules.contains(ruleNumber1)) {
                List<Integer> subList = new LinkedList<>();
                subList.add(ruleNumber1);
                for (int ruleNumber2 : rules.keySet()) {
                    if (ruleNumber1 != ruleNumber2 && rules.get(ruleNumber1).getLeftHandSide().equals(rules.get(ruleNumber2).getLeftHandSide())) {
                        subList.add(ruleNumber2);
                        processedRules.add(ruleNumber2);
                    }
                }
                if (subList.size() > 1) {
                    list.add(subList);
                }
                processedRules.add(ruleNumber1);
            }
        }
        return list;
    }

    //La grammaire n'est pas LL1 => à corriger
    public boolean isGrammarLL1(ContextFreeGrammar contextFreeGrammar) {
        firstKSets = constructFirstKSets(contextFreeGrammar);
        followKSets = constructFollowKSets(contextFreeGrammar);

        System.out.println("First K sets : " + firstKSets);
        System.out.println("Follow K sets : " +followKSets);
        System.out.println("Rules : " + contextFreeGrammar.getRules());

        for (List<Integer> sameRules : occurrencesRules(contextFreeGrammar)) {
            System.out.println("We check for rules : " + sameRules);
            Rule firstRule = contextFreeGrammar.getRules().get(sameRules.get(0));
            String leftHandSide = firstRule.getLeftHandSide();
            List<String> rightHandSide = new ArrayList<>(firstRule.getRightHandSide()); //Not reference the list but just make a copy
            rightHandSide.addAll(List.of(FOLLOW, leftHandSide));
            Set<String> intersectionSet = new LinkedHashSet<>(computeFirstKWithFollowK(contextFreeGrammar, rightHandSide)); //Have to look up in table

            for (int ruleNumber : sameRules) {
                Rule rule = contextFreeGrammar.getRules().get(ruleNumber);
                String lhs = rule.getLeftHandSide();
                List<String> rhs = new ArrayList<>(rule.getRightHandSide()); //Not reference but copy
                rhs.addAll(List.of(FOLLOW, lhs));
                Set<String> ruleFirstKSet = computeFirstKWithFollowK(contextFreeGrammar, rhs); //Have to look up in table
                System.out.println("[" + ruleNumber + "] First("  + rhs + ") = " + ruleFirstKSet);
                intersectionSet.retainAll(ruleFirstKSet);
            }

            System.out.println("Resulting set : " + intersectionSet + "\n");
            if (!intersectionSet.isEmpty()) {
                System.err.println("\nConflict in rules : " + sameRules + "\nResulting set : " + intersectionSet + "\nMust be : []");
                //return false;
            }
        }

        //System.out.println("This context free grammar is LL(1)");
        return true;
    }



    public int[][] constructLL1ActionTableFromCFG(ContextFreeGrammar contextFreeGrammar){

        //Initialize actionTable
        actionTable = new int[contextFreeGrammar.getVariables().size()][contextFreeGrammar.getTerminals().size() + 1];
        for (int[] row : actionTable) {
            Arrays.fill(row, -1);
        }

        //Algo
        for (Rule rule : contextFreeGrammar.getRules().values()) {
            String leftHandSide = rule.getLeftHandSide();
            List<String> rightHandSide = new ArrayList<>(rule.getRightHandSide()); //Not reference the list but just make a copy
            rightHandSide.addAll(List.of(FOLLOW, leftHandSide));

            for (String a : computeFirstKWithFollowK(contextFreeGrammar, rightHandSide)) {
                actionTable[contextFreeGrammar.getVariables().indexOf(leftHandSide)][contextFreeGrammar.getTerminals().indexOf(a)] = rule.getNumber();
            }
        }

        System.out.println();

        //Print Action Table
        for (int i = 0; i < actionTable.length; i++) {
            for (int j = 0; j < actionTable[i].length; j++) {
                System.out.print(actionTable[i][j] + " ");
            }
            System.out.println();
        }

        return actionTable;

    }

}
