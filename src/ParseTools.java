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

    /**
     * Firstk(X) = Firstk (X1) ⊙k Firstk (X2) ⊙k · · · ⊙k Firstk (Xn)
     * @param contextFreeGrammar
     * @param k
     * @param stringList
     * @return
     */
    private Set<String> firstK(ContextFreeGrammar contextFreeGrammar, int k, List<String> stringList) {
        Set<String> firstKSet = new LinkedHashSet<>();
        for (String string : stringList) {
            if (!(contextFreeGrammar.getVariables().contains(string) || contextFreeGrammar.getTerminals().contains(string))) {
                throw new IllegalArgumentException("Cannot compute firstK");
            }

            if (firstKSet.size() < k) {
                firstKSet.addAll(firstKSets.get(string));
            }
        }
        return firstKSet;
    }

    /**
     * Firstk(β) ⊙k Followk(A)
     * @param cfg
     * @param k
     * @param variable
     * @return
     */
    private Set<String> followK(ContextFreeGrammar cfg, int k, String variable) {
        Set<String> followKSet = new LinkedHashSet<>();

        if (variable.equals(cfg.getStartSymbol())) {
            followKSet.add(EPSILON);
        }

        for (Rule rule : cfg.getRules().values()) {
            List<String> rightHandSide = rule.getRightHandSide();
            for (int i = 0; i < rightHandSide.size(); i++) {
                if (rightHandSide.get(i).equals(variable) && i < rightHandSide.size() - 1) {
                    String nextSymbol = rightHandSide.get(i + 1);
                    followKSet.addAll(computeFirstK(cfg, k, List.of(nextSymbol)));
                }
            }
        }

        return followKSet;
    }

    /**
     * Firstk(αFollowk(A))
     * @param contextFreeGrammar
     * @param k
     * @param stringList
     * @return
     */
    private Set<String> firstKWithFollowK(ContextFreeGrammar contextFreeGrammar, int k, List<String> stringList) {
        String firstString = stringList.getFirst();
        String lastString = stringList.getLast();
        if (contextFreeGrammar.getTerminals().contains(firstString) && !firstString.equals(EPSILON)) {
            return computeFirstK(contextFreeGrammar, k, List.of(firstString));
        } else {
            return followK(contextFreeGrammar, k, lastString); //return if present in followKsets
        }
    }

    /**
     * Firstk(X) avec X = X1X2X3...Xn ou αFollowk(A) et on calcule
     * @param contextFreeGrammar
     * @param k
     * @param stringList
     * @return
     */
    private Set<String> computeFirstK(ContextFreeGrammar contextFreeGrammar, int k, List<String> stringList) {
        Set<String> firstKSet;
        if (!stringList.contains(FOLLOW)) {
            firstKSet = firstK(contextFreeGrammar, k, stringList);
        } else {
            firstKSet = firstKWithFollowK(contextFreeGrammar, k, stringList);
        }
        return firstKSet;
    }

    /**
     * Firstk(X) avec X = X1X2X3...Xn ou αFollowk(A) et on reprend les valeurs des différents sets
     * @param contextFreeGrammar
     * @param k
     * @param stringList
     * @return
     */
    private Set<String> computeFirstKWithFollowK(ContextFreeGrammar contextFreeGrammar, int k, List<String> stringList) {
        String firstString = stringList.getFirst();
        String lastString = stringList.getLast();
        if (contextFreeGrammar.getTerminals().contains(firstString) && !firstString.equals(EPSILON)) {
            return firstKSets.get(firstString);
        } else {
            return followKSets.get(lastString);
        }
    }

    /**
     * foreach a ∈ T do Firstk (a) ← {a} ; foreach A ∈ V do Firstk (A) ← {};
     * @param contextFreeGrammar
     */
    private void initializeFirstKSets(ContextFreeGrammar contextFreeGrammar) {
        for (String terminal : contextFreeGrammar.getTerminals()) {
            firstKSets.put(terminal, new LinkedHashSet<>());
            firstKSets.get(terminal).add(terminal);
        }

        for (String variable : contextFreeGrammar.getVariables()) {
            firstKSets.put(variable, new LinkedHashSet<>());
        }
    }

    /**
     * foreach A ∈ V \ {S} do
     * Followk(A) ← {}
     * Followk(S) ← {ε}
     * @param cfg
     */
    private void initializeFollowKSets(ContextFreeGrammar cfg) {
        for (String variable : cfg.getVariables()) {
            followKSets.put(variable, new LinkedHashSet<>());
        }
        followKSets.get(cfg.getStartSymbol()).add(EPSILON);
    }

    /**
     * foreach A → X1 X2 · · · Xn ∈ P do
     * Firstk (A) ← Firstk (A) ∪ (Firstk(X1) ⊙k Firstk(X2) ⊙k ... ⊙k Firstk(Xn))
     * @param contextFreeGrammar
     * @param k
     * @return
     */
    public Map<String, Set<String>> constructFirstKSets(ContextFreeGrammar contextFreeGrammar, int k) {
        initializeFirstKSets(contextFreeGrammar);
        boolean atLeastOneFirstKSetHasBeenUpdated;
        do {
            atLeastOneFirstKSetHasBeenUpdated = false;
            for (Rule rule : contextFreeGrammar.getRules().values()) {
                String A = rule.getLeftHandSide();
                Set<String> oldFollowKSet  = Set.copyOf(firstKSets.get(rule.getLeftHandSide()));
                Set<String> firstKSet = computeFirstK(contextFreeGrammar, k, rule.getRightHandSide());
                firstKSets.get(A).addAll(firstKSet);
                if (!atLeastOneFirstKSetHasBeenUpdated && !oldFollowKSet.equals(firstKSets.get(rule.getLeftHandSide()))) {
                    atLeastOneFirstKSetHasBeenUpdated = true;
                }
            }
        } while (atLeastOneFirstKSetHasBeenUpdated);
        return firstKSets;
    }

    /**
     * foreach A → αBβ ∈ P do
     * Followk (B) ← Followk (B) ∪ (Firstk(β) ⊙k Followk(A))
     * @param contextFreeGrammar
     * @param k
     * @return
     */
    public Map<String, Set<String>> constructFollowKSets(ContextFreeGrammar contextFreeGrammar, int k) {
        initializeFollowKSets(contextFreeGrammar);
        boolean atLeastOneFollowKSetHasBeenUpdated;
        do {
            atLeastOneFollowKSetHasBeenUpdated = false;
            for (Rule rule : contextFreeGrammar.getRules().values()) {
                String A = rule.getLeftHandSide();
                List<String> rightHandSide = rule.getRightHandSide();
                for (int i = 0; i < rightHandSide.size(); i++) {
                    String B = rightHandSide.get(i);
                    if (contextFreeGrammar.getVariables().contains(B)) {
                        List<String> beta = rightHandSide.subList(i + 1, rightHandSide.size());
                        Set<String> oldFollowKSet = new LinkedHashSet<>(followKSets.get(B));
                        Set<String> followKSet = beta.isEmpty() ? computeFirstK(contextFreeGrammar, k, List.of(EPSILON)) : computeFirstK(contextFreeGrammar, k, beta); // ⊙k Follow(A)?
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

    /**
     * Firstk(α1Followk(A)) ∩ Firstk(α2Followk(A)) = {}
     * @param contextFreeGrammar
     * @param k
     * @return
     */
    public boolean isGrammarLLK(ContextFreeGrammar contextFreeGrammar, int k) {
        firstKSets = constructFirstKSets(contextFreeGrammar, k);
        followKSets = constructFollowKSets(contextFreeGrammar, k);

        System.out.println("First " + k + " sets : " + firstKSets);
        System.out.println("Follow " + k + " sets : " + followKSets);
        System.out.println("Rules : " + contextFreeGrammar.getRules());

        for (List<Integer> sameRules : occurrencesRules(contextFreeGrammar)) {
            System.out.println("We check for rules : " + sameRules);
            Rule firstRule = contextFreeGrammar.getRules().get(sameRules.get(0));
            String leftHandSide = firstRule.getLeftHandSide();
            List<String> rightHandSide = new ArrayList<>(firstRule.getRightHandSide()); //Not reference the list but just make a copy
            rightHandSide.addAll(List.of(FOLLOW, leftHandSide));
            Set<String> intersectionSet = new LinkedHashSet<>(computeFirstKWithFollowK(contextFreeGrammar, k, rightHandSide)); //Have to look up in table

            for (int ruleNumber : sameRules) {
                Rule rule = contextFreeGrammar.getRules().get(ruleNumber);
                String lhs = rule.getLeftHandSide();
                List<String> rhs = new ArrayList<>(rule.getRightHandSide()); //Not reference but copy
                rhs.addAll(List.of(FOLLOW, lhs));
                Set<String> ruleFirstKSet = computeFirstKWithFollowK(contextFreeGrammar, k, rhs); //Have to look up in table
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


    /**
     * foreach a ∈ First(αFollow(A))
     * @param contextFreeGrammar
     * @return
     */
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

            for (String a : computeFirstKWithFollowK(contextFreeGrammar, 1, rightHandSide)) {
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
