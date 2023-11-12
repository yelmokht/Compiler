import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * ParseTools class contains all the tools related to grammars.
 */
public class ParseTools {
    public static final String EPSILON = "ε";
    public static final String FOLLOW = "Follow";
    private Map<String, Set<String>> firstKSets = new LinkedHashMap<>();
    private Map<String, Set<String>> followKSets = new LinkedHashMap<>();
    private int[][] actionTable;

    public ParseTools(){}

    /**
     * Computes Firstk(X) = Firstk(X1) ⊙k Firstk(X2) ⊙k ... ⊙k Firstk(Xn)
     * @param contextFreeGrammar The context-free grammar
     * @param k The parameter k
     * @param stringList The list of strings
     * @return First(X) set
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
     * Computes Followk(X) = Firstk(beta) ⊙k Followk(A)
     * @param contextFreeGrammar The context-free grammar
     * @param k The parameter k
     * @param A The variable
     * @param beta The list of strings
     * @return Followk(X) set
     */
    private Set<String> followK(ContextFreeGrammar contextFreeGrammar, int k, String A, List<String> beta) {
        Set<String> followKSet = new LinkedHashSet<>(firstK(contextFreeGrammar, k, beta));
        if (followKSet.size() < k) {
            followKSet.addAll(followKSets.get(A));
        }
        return followKSet;
    }

    /**
     * Computes Firstk(αFollowk(A))
     * @param contextFreeGrammar The context-free grammar
     * @param k The parameter k
     * @param stringList The list of strings
     * @return Firstk(X) set
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
     * Firstk(X) with X = X1X2X3...Xn or αFollowk(A)
     * @param contextFreeGrammar The context-free grammar
     * @param k The parameter k
     * @param stringList The list of strings
     * @return Firstk(X) set
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
     * Computes Firstk(alphaFollowk(A))
     * @param contextFreeGrammar The context-free grammar
     * @param k The parameter k
     * @param stringList The list of strings
     * @return Firstk(alphaFollowk(A)) set
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
     * Initializes the First sets according to First sets algorithm
     * foreach a ∈ T do Firstk (a) ← {a}
     * foreach A ∈ V do Firstk (A) ← {}
     * @param contextFreeGrammar The context-free grammar
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
     * Computes the First sets using First sets algorithm
     * foreach A → X1 X2 ... Xn ∈ P do :
     * Firstk(A) ← Firstk(A) ∪ (Firstk(X1) ⊙k Firstk(X2) ⊙k ... ⊙k Firstk(Xn))
     * @param contextFreeGrammar The context-free grammar
     * @param k The parameter k in Firstk
     * @return The First sets
     */
    private Map<String, Set<String>> constructFirstKSets(ContextFreeGrammar contextFreeGrammar, int k) {
        initializeFirstKSets(contextFreeGrammar);
        boolean atLeastOneFirstKSetHasBeenUpdated;
        do {
            atLeastOneFirstKSetHasBeenUpdated = false;
            for (Rule rule : contextFreeGrammar.getRules().values()) {
                String A = rule.getLeftHandSide();
                Set<String> oldFollowKSet  = new LinkedHashSet<>(firstKSets.get(A));
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
     * Initializes the Follow sets according to Follow sets algorithm
     * foreach A ∈ V \ {S} do :
     * Followk(A) ← {}
     * Followk(S) ← {ε}
     * @param contextFreeGrammar The context-free grammar
     */
    private void initializeFollowKSets(ContextFreeGrammar contextFreeGrammar) {
        for (String variable : contextFreeGrammar.getVariables()) {
            followKSets.put(variable, new LinkedHashSet<>());
        }
        followKSets.get(contextFreeGrammar.getStartSymbol()).add(EPSILON);
    }

    /**
     * Computes the Follow sets using Follow sets algorithm
     * foreach A → αBβ ∈ P do :
     * Followk(B) ← Followk(B) ∪ (Firstk(β) ⊙k Followk(A))
     * @param contextFreeGrammar The context-free grammar
     * @param k The parameter k in Followk
     * @return The Follow sets
     */
    private Map<String, Set<String>> constructFollowKSets(ContextFreeGrammar contextFreeGrammar, int k) {
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
                        Set<String> followKSet = beta.isEmpty() ? followK(contextFreeGrammar, k, A, List.of(EPSILON)) : followK(contextFreeGrammar, k, A, beta);
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

    /**
     * Computes the rules with same left hand side in the context-free grammar
     * @param contextFreeGrammar The context-free grammar
     * @return The list of rules with same left hand side
     */
    private List<List<Integer>> occurrencesRules(ContextFreeGrammar contextFreeGrammar) {
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
     * Checks that the grammar is LL(k) using the definition of Strong LL(K) :
     * FirstK(alphaFollowK(A)) for all pairs of rules A -> alpha
     * @param contextFreeGrammar The context-free grammar
     * @param k The parameter k in LL(k)
     * @return true if this grammar is LL(k)
     */
    public boolean isGrammarLLK(ContextFreeGrammar contextFreeGrammar, int k) {
        firstKSets = constructFirstKSets(contextFreeGrammar, k);
        followKSets = constructFollowKSets(contextFreeGrammar, k);

        for (List<Integer> sameRules : occurrencesRules(contextFreeGrammar)) {
            Rule firstRule = contextFreeGrammar.getRules().get(sameRules.get(0));
            String leftHandSide = firstRule.getLeftHandSide();
            List<String> rightHandSide = new ArrayList<>(firstRule.getRightHandSide());
            rightHandSide.addAll(List.of(FOLLOW, leftHandSide));
            Set<String> intersectionSet = new LinkedHashSet<>(computeFirstKWithFollowK(contextFreeGrammar, k, rightHandSide));

            for (int ruleNumber : sameRules) {
                Rule rule = contextFreeGrammar.getRules().get(ruleNumber);
                String A = rule.getLeftHandSide();
                List<String> alpha = new ArrayList<>(rule.getRightHandSide());
                alpha.addAll(List.of(FOLLOW, A));
                Set<String> ruleFirstKSet = computeFirstKWithFollowK(contextFreeGrammar, k, alpha);
                intersectionSet.retainAll(ruleFirstKSet);
            }

            if (!intersectionSet.isEmpty()) {
                System.err.println("\nConflict in rules : " + sameRules + "\nResulting set : " + intersectionSet + "\nMust be : []");
                return false;
            }
        }

        System.out.println("This context free grammar is LL(" + k + ")");
        return true;
    }

    /**
     * Initializes the action table
     * @param contextFreeGrammar The context-free grammar
     */
    private void initializeActionTable(ContextFreeGrammar contextFreeGrammar) {
        actionTable = new int[contextFreeGrammar.getVariables().size()][contextFreeGrammar.getTerminals().size()];
        for (int[] row : actionTable) {
            Arrays.fill(row, 0);
        }
    }

    /**
     * Adds the ´Produce´actions in the action table
     * @param contextFreeGrammar The context-free grammar
     */
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

    /**
     * Prints the LL(1) action table on the standard output
     * @param contextFreeGrammar The context-free grammar
     */
    private void printActionTable(ContextFreeGrammar contextFreeGrammar) {
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
     * Constructs the LL(1) action table from a context free grammar
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
