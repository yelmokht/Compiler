import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
     * @return
     */
    private Set<String> followK(ContextFreeGrammar cfg, int k, String A, List<String> beta) {
        Set<String> followKSet = new LinkedHashSet<>();
        followKSet.addAll(firstK(cfg, k, beta));
        if (followKSet.size() < k) {
            followKSet.addAll(followKSets.get(A));
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
        String alpha = stringList.get(0);
        String A = stringList.get(stringList.size() - 1);
        if (contextFreeGrammar.getTerminals().contains(alpha) && !alpha.equals(EPSILON)) {
            return computeFirstK(contextFreeGrammar, k, List.of(alpha));
        } else {
            return followKSets.get(A);
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
        String firstString = stringList.get(0);
        String lastString = stringList.get(stringList.size() - 1);
        if ((contextFreeGrammar.getTerminals().contains(firstString) || (contextFreeGrammar.getVariables().contains(firstString) )&& !firstString.equals(EPSILON))) {
            
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
                        Set<String> oldFollowKSet = new LinkedHashSet<>(followKSets.get(A));
                        Set<String> followKSet = beta.isEmpty() ? followK(contextFreeGrammar, 1, A, List.of(EPSILON)) : followK(contextFreeGrammar, 1, A, beta); // ⊙k Follow(A)?
                        followKSets.get(B).addAll(followKSet);
                        if (!atLeastOneFollowKSetHasBeenUpdated && !oldFollowKSet.equals(followKSets.get(A))) {
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

        for (List<Integer> sameRules : occurrencesRules(contextFreeGrammar)) {
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
                intersectionSet.retainAll(ruleFirstKSet);
            }

            if (!intersectionSet.isEmpty()) {
                System.err.println("\nConflict in rules : " + sameRules + "\nResulting set : " + intersectionSet + "\nMust be : []");
                return false;
            }
        }

        System.out.println("This context free grammar is LL(1)");
        return true;
    }

    /**
     *
     * @param contextFreeGrammar
     */
    private void initializeActionTable(ContextFreeGrammar contextFreeGrammar) {
        actionTable = new int[contextFreeGrammar.getVariables().size()][contextFreeGrammar.getTerminals().size()];
        for (int[] row : actionTable) {
            Arrays.fill(row, 0);
        }
    }

    private void addProduceActions(ContextFreeGrammar contextFreeGrammar) {
        for (Rule rule : contextFreeGrammar.getRules().values()) {
            String A = rule.getLeftHandSide();
            List<String> alpha = new ArrayList<>(rule.getRightHandSide());
            alpha.add(FOLLOW + A);
            for (String a : computeFirstKWithFollowK(contextFreeGrammar, 1, alpha)) {
                int ruleNumber = rule.getNumber();
                int variableIndex = contextFreeGrammar.getVariables().indexOf(A);
                int terminalIndex = contextFreeGrammar.getTerminals().indexOf(a);
                if (actionTable[variableIndex][terminalIndex] == 0) {
                    actionTable[variableIndex][terminalIndex] = ruleNumber;
                } else {
                    System.err.println("Conflict in M[" + A + "," + a + "]: " + "existing rule number is " + actionTable[variableIndex][terminalIndex] + ", trying to add rule number " + ruleNumber);
                }
            }
        }
    }

    public void printActionTable(ContextFreeGrammar contextFreeGrammar) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("actionTable.txt"))) {
            int maxVariablesLength = contextFreeGrammar.getVariables().stream().map(String::length).max(Integer::compare).orElse(0);
            int maxTerminalsLength = contextFreeGrammar.getTerminals().stream().map(String::length).max(Integer::compare).orElse(0);
            int firstPadding = maxVariablesLength + 7;
            int inBetweenColumnsPadding = maxTerminalsLength + 2;

            // Headers
            writer.printf("%-" + firstPadding + "s", "");
            for (String terminal : contextFreeGrammar.getTerminals()) {
                writer.printf("%-" + inBetweenColumnsPadding + "s", terminal);
            }
            writer.println();

            // Table
            for (int i = 0; i < actionTable.length; i++) {
                String ruleName = contextFreeGrammar.getRules().get(i + 1).getLeftHandSide();
                writer.printf("%-" + firstPadding + "s", ruleName);
                for (int j = 0; j < actionTable[0].length; j++) {
                    writer.printf("%-" + inBetweenColumnsPadding + "s", actionTable[i][j]);
                }
                writer.println();
            }
            System.out.println("Table exported to: actionTable.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


        /**
     * Construct the LL(1) action table from a context free grammar
     * @param contextFreeGrammar The context-free grammar
     * @return The action table
     */
    public int[][] constructLL1ActionTableFromCFG(ContextFreeGrammar contextFreeGrammar){
        initializeActionTable(contextFreeGrammar);
        addProduceActions(contextFreeGrammar);
        printActionTable(contextFreeGrammar);
        return actionTable;
    }

}
