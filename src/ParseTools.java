
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
    private Map<String, Set<String>> firstKAlphaFollowKASets = new LinkedHashMap<>();
    private String[][] actionTable;

    public ParseTools(){}

    public Map<String, Set<String>> getFirstKSets() {
        return firstKSets;
    }

    /**
     * Computes Firstk(X) = Firstk(X1) ⊙k Firstk(X2) ⊙k ... ⊙k Firstk(Xn)
     * @param contextFreeGrammar The context-free grammar
     * @param k The parameter k
     * @param stringList The list of strings where X is a list (X1, X2, ..., Xn)
     * @return First(X) set
     */
    private Set<String> firstK(ContextFreeGrammar contextFreeGrammar, int k, List<String> stringList) {
        Set<String> firstKSet = new LinkedHashSet<>();

        for (String string : stringList) {
            if (!(contextFreeGrammar.isVariable(string) || contextFreeGrammar.isTerminal(string))) {
                throw new IllegalArgumentException("Cannot compute firstK");
            }

            if (firstKSets.get(string).isEmpty()) {
                return new LinkedHashSet<>();
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

        if (followKSet.contains(EPSILON)) {
            followKSet.remove(EPSILON);
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
    private Set<String> firstKAlphaFollowKA(ContextFreeGrammar contextFreeGrammar, int k, List<String> stringList) {
        String alpha = stringList.get(0);
        String A = stringList.get(stringList.size() - 1);
        if (!alpha.equals(EPSILON)) {
            return firstK(contextFreeGrammar, k, List.of(alpha));
        } else {
            return followKSets.get(A);
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
        // Initialize First sets
        for (String terminal : contextFreeGrammar.getTerminals()) {
            firstKSets.put(terminal, new LinkedHashSet<>(List.of(terminal)));
        }

        for (String variable : contextFreeGrammar.getVariables()) {
            firstKSets.put(variable, new LinkedHashSet<>());
        }

        // Compute First sets
        boolean atLeastOneFirstKSetHasBeenUpdated;
        do {
            atLeastOneFirstKSetHasBeenUpdated = false;
            for (Rule rule : contextFreeGrammar.getRules().values()) {
                String A = rule.getLeftHandSide();
                Set<String> oldFollowKSet  = new LinkedHashSet<>(firstKSets.get(A));
                Set<String> firstKSet = firstK(contextFreeGrammar, k, rule.getRightHandSide());
                firstKSets.get(A).addAll(firstKSet);
                if (!atLeastOneFirstKSetHasBeenUpdated && !oldFollowKSet.equals(firstKSets.get(rule.getLeftHandSide()))) {
                    atLeastOneFirstKSetHasBeenUpdated = true;
                }
            }
        } while (atLeastOneFirstKSetHasBeenUpdated);
        return firstKSets;
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
        //Initialize Follow sets
        for (String variable : contextFreeGrammar.getVariables()) {
            followKSets.put(variable, new LinkedHashSet<>());
        }

        followKSets.get(contextFreeGrammar.getStartSymbol()).add(EPSILON);

        // Compute Follow sets
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
                        Set<String> followKSet = beta.isEmpty() ? followK(contextFreeGrammar, k, A, List.of(EPSILON)) : followK(contextFreeGrammar, k, A, beta);
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
        // Compute Firstk and Followk sets
        firstKSets = constructFirstKSets(contextFreeGrammar, k);
        followKSets = constructFollowKSets(contextFreeGrammar, k);

        // Check that Firstk(alphaFollowk(A)) is empty for all pairs of rules A -> alpha
        for (List<Integer> sameRules : occurrencesRules(contextFreeGrammar)) {
            Rule firstRule = contextFreeGrammar.getRules().get(sameRules.get(0));
            String leftHandSide = firstRule.getLeftHandSide();
            List<String> rightHandSide = new ArrayList<>(firstRule.getRightHandSide());
            rightHandSide.addAll(List.of(FOLLOW, leftHandSide));
            Set<String> intersectionSet = new LinkedHashSet<>(firstKAlphaFollowKA(contextFreeGrammar, k, rightHandSide));

            for (int ruleNumber : sameRules) {
                Rule rule = contextFreeGrammar.getRules().get(ruleNumber);
                String A = rule.getLeftHandSide();
                List<String> alphaFollowKA = new ArrayList<>(rule.getRightHandSide());
                alphaFollowKA.addAll(List.of(FOLLOW, A));
                Set<String> ruleFirstKSet = firstKAlphaFollowKA(contextFreeGrammar, k, alphaFollowKA);
                firstKAlphaFollowKASets.put(String.valueOf(alphaFollowKA), new LinkedHashSet<>(ruleFirstKSet));
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
     * Constructs the LL(1) action table from a context free grammar
     * @param contextFreeGrammar The context-free grammar
     * @return The action table
     */
    public String[][] constructLL1ActionTableFromCFG(ContextFreeGrammar contextFreeGrammar){
        /* Initialize action table */
        List<String> T = contextFreeGrammar.getTerminals();
        List<String> V = contextFreeGrammar.getVariables();
        List<String> VAndT = contextFreeGrammar.getVariablesAndTerminals();
        Collection<Rule> P = contextFreeGrammar.getRules().values();

        actionTable = new String[VAndT.size()][T.size()];

        for (String a : T) {

            for (String A : V) {
                actionTable[VAndT.indexOf(A)][T.indexOf(a)] = "0";
            }

            for (String b : T) {
                    actionTable[VAndT.indexOf(b)][T.indexOf(a)] = "0";
            }

            actionTable[VAndT.indexOf(a)][T.indexOf(a)] = "M";
        }

        String terminal = contextFreeGrammar.getRules().get(1).getRightHandSide().getLast();
        //actionTable[VAndT.indexOf(terminal)][T.indexOf(terminal)] = "A";

        /* Add Produce actions */
        for (Rule rule : P) {
            String A = rule.getLeftHandSide();
            List<String> alpha = new ArrayList<>(rule.getRightHandSide());
            alpha.addAll(List.of(FOLLOW, A));

            for (String a : firstKAlphaFollowKA(contextFreeGrammar, 1, alpha)) {
                int i = rule.getNumber();

                if (actionTable[VAndT.indexOf(A)][T.indexOf(a)].equals("0")) {
                    actionTable[VAndT.indexOf(A)][T.indexOf(a)] = String.valueOf(i);

                } else {
                    System.err.println("Conflict in M[" + A + "," + a + "]: " + "existing rule number is " + actionTable[VAndT.indexOf(A)][T.indexOf(a)] + ", trying to add rule number " + i);
                }
            }
        }

        return actionTable;
    }

    public void printFirstKSets(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (String variable : firstKSets.keySet()) {
                writer.println("Firstk(" + variable + ") = " + firstKSets.get(variable));
            }
            System.out.println("Content written to file successfully to : " + filePath + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printFollowKSets(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (String variable : followKSets.keySet()) {
                writer.println("Followk(" + variable + ") = " + followKSets.get(variable));
            }
            System.out.println("Content written to file successfully to : " + filePath + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printFirstKAlphaFollowKA(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (String alphaFollowKA : firstKAlphaFollowKASets.keySet()) {
                writer.println("Firstk(" + alphaFollowKA + ") = " + firstKAlphaFollowKASets.get(alphaFollowKA));
            }
            System.out.println("Content written to file successfully to : " + filePath + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the LL(1) action table on the standard output
     * @param contextFreeGrammar The context-free grammar
     */
    public void printActionTable(ContextFreeGrammar contextFreeGrammar, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            int maxAlphabetLength = contextFreeGrammar.getVariablesAndTerminals().stream().map(String::length).max(Integer::compare).orElse(0);
            int maxTerminalsLength = contextFreeGrammar.getTerminals().stream().map(String::length).max(Integer::compare).orElse(0);
            int firstPadding = maxAlphabetLength + 7;
            int inBetweenColumnsPadding = maxTerminalsLength + 2;

            // Headers
            writer.printf("%-" + firstPadding + "s", "");
            for (String terminal : contextFreeGrammar.getTerminals()) {
                if (!terminal.equals(EPSILON)) {
                    writer.printf("%-" + inBetweenColumnsPadding + "s", terminal);
                }
            }
            writer.println();

            // Table
            for (int i = 0; i < actionTable.length; i++) {
                String string = contextFreeGrammar.getVariablesAndTerminals().get(i);
                if (!string.equals(EPSILON)) {
                    writer.printf("%-" + firstPadding + "s", string);

                    for (int j = 0; j < actionTable[0].length; j++) {
                        if (!contextFreeGrammar.getTerminals().get(j).equals(EPSILON)) {
                            writer.printf("%-" + inBetweenColumnsPadding + "s", actionTable[i][j]);
                        }
                    }
                    writer.println();
                }
            }
            System.out.println("Table exported to: " + filePath + ")");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


