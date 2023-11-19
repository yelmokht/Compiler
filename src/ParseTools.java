
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * ParseTools class contains all the tools related to grammars.
 */
public class ParseTools {

    public static final Symbol EPSILON = new Symbol(LexicalUnit.TERMINAL, "ε");
    public static final Symbol FOLLOW = new Symbol(LexicalUnit.TERMINAL, "Follow");
    private Map<Symbol, Set<Symbol>> firstKSets = new LinkedHashMap<>();
    private Map<Symbol, Set<Symbol>> followKSets = new LinkedHashMap<>();
    private Map<List<Symbol>, Set<Symbol>> firstKAlphaFollowKASets = new LinkedHashMap<>();
    private String[][] actionTable;

    public ParseTools(){}


    public Map<Symbol, Set<Symbol>> getFirstKSets() {
        return firstKSets;
    }

    /**
     * Computes Firstk(X) = Firstk(X1) ⊙k Firstk(X2) ⊙k ... ⊙k Firstk(Xn)
     * @param contextFreeGrammar The context-free grammar
     * @param k The parameter k
     * @param stringList The list of strings where X is a list (X1, X2, ..., Xn)
     * @return First(X) set
     */
    private Set<Symbol> firstK(ContextFreeGrammar contextFreeGrammar, int k, List<Symbol> stringList) {
        Set<Symbol> firstKSet = new LinkedHashSet<>();

        for (Symbol symbol : stringList) {
            if (!(contextFreeGrammar.isVariable(symbol) || contextFreeGrammar.isTerminal(symbol))) {
                throw new IllegalArgumentException("Cannot compute firstK");
            }

            if (firstKSets.get(symbol).isEmpty()) {
                return new LinkedHashSet<>();
            }

            if (firstKSet.size() < k) {
                firstKSet.addAll(firstKSets.get(symbol));
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
    private Set<Symbol> followK(ContextFreeGrammar contextFreeGrammar, int k, Symbol A, List<Symbol> beta) {
        Set<Symbol> followKSet = new LinkedHashSet<>(firstK(contextFreeGrammar, k, beta));

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
    private Set<Symbol> firstKAlphaFollowKA(ContextFreeGrammar contextFreeGrammar, int k, List<Symbol> stringList) {
        Symbol alpha = stringList.get(0);
        Symbol A = stringList.get(stringList.size() - 1);
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
    private Map<Symbol, Set<Symbol>> constructFirstKSets(ContextFreeGrammar contextFreeGrammar, int k) {
        // Initialize First sets
        for (Symbol terminal : contextFreeGrammar.getTerminals()) {
            firstKSets.put(terminal, new LinkedHashSet<>(List.of(terminal)));
        }

        for (Symbol variable : contextFreeGrammar.getVariables()) {
            firstKSets.put(variable, new LinkedHashSet<>());
        }

        // Compute First sets
        boolean atLeastOneFirstKSetHasBeenUpdated;
        do {
            atLeastOneFirstKSetHasBeenUpdated = false;
            for (Rule rule : contextFreeGrammar.getRules().values()) {
                Symbol A = rule.getLeftHandSide();
                Set<Symbol> oldFollowKSet = new LinkedHashSet<>(firstKSets.get(A));
                Set<Symbol> firstKSet = firstK(contextFreeGrammar, k, rule.getRightHandSide());
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
    private Map<Symbol, Set<Symbol>> constructFollowKSets(ContextFreeGrammar contextFreeGrammar, int k) {
        //Initialize Follow sets
        for (Symbol variable : contextFreeGrammar.getVariables()) {
            followKSets.put(variable, new LinkedHashSet<>());
        }

        followKSets.get(contextFreeGrammar.getStartSymbol()).add(EPSILON);

        // Compute Follow sets
        boolean atLeastOneFollowKSetHasBeenUpdated;
        do {
            atLeastOneFollowKSetHasBeenUpdated = false;
            for (Rule rule : contextFreeGrammar.getRules().values()) {
                Symbol A = rule.getLeftHandSide();
                List<Symbol> rightHandSide = rule.getRightHandSide();
                for (int i = 0; i < rightHandSide.size(); i++) {
                    Symbol B = rightHandSide.get(i);
                    if (contextFreeGrammar.getVariables().contains(B)) {
                        List<Symbol> beta = rightHandSide.subList(i + 1, rightHandSide.size());
                        Set<Symbol> oldFollowKSet = new LinkedHashSet<>(followKSets.get(B));
                        Set<Symbol> followKSet = beta.isEmpty() ? followK(contextFreeGrammar, k, A, List.of(EPSILON)) : followK(contextFreeGrammar, k, A, beta);
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
            Symbol leftHandSide = firstRule.getLeftHandSide();
            List<Symbol> rightHandSide = new ArrayList<>(firstRule.getRightHandSide());
            rightHandSide.addAll(List.of(FOLLOW, leftHandSide));
            Set<Symbol> intersectionSet = new LinkedHashSet<>(firstKAlphaFollowKA(contextFreeGrammar, k, rightHandSide));

            for (int ruleNumber : sameRules) {
                Rule rule = contextFreeGrammar.getRules().get(ruleNumber);
                Symbol A = rule.getLeftHandSide();
                List<Symbol> alphaFollowKA = new ArrayList<>(rule.getRightHandSide());
                alphaFollowKA.addAll(List.of(FOLLOW, A)); //
                Set<Symbol> ruleFirstKSet = firstKAlphaFollowKA(contextFreeGrammar, k, alphaFollowKA);
                firstKAlphaFollowKASets.put(alphaFollowKA, new LinkedHashSet<>(ruleFirstKSet));
                intersectionSet.retainAll(ruleFirstKSet);
            }

            if (!intersectionSet.isEmpty()) {
                System.err.println("\nConflict in rules : " + sameRules + "\nResulting set : " + intersectionSet + "\nMust be : []");
                return false;
            }
        }

        return true;
    }

    /**
     * Constructs the LL(1) action table from a context free grammar
     * @param contextFreeGrammar The context-free grammar
     * @return The action table
     */
    public String[][] constructLL1ActionTableFromCFG(ContextFreeGrammar contextFreeGrammar){
        /* Initialize action table */
        List<Symbol> T = contextFreeGrammar.getTerminals();
        List<Symbol> V = contextFreeGrammar.getVariables();
        List<Symbol> VAndT = contextFreeGrammar.getVariablesAndTerminals();
        Collection<Rule> P = contextFreeGrammar.getRules().values();

        actionTable = new String[VAndT.size()][T.size()];

        for (Symbol a : T) {

            for (Symbol A : V) {
                actionTable[VAndT.indexOf(A)][T.indexOf(a)] = "0";
            }

            for (Symbol b : T) {
                actionTable[VAndT.indexOf(b)][T.indexOf(a)] = "0";
            }

            actionTable[VAndT.indexOf(a)][T.indexOf(a)] = "M";
        }

        Symbol terminal = contextFreeGrammar.getRules().get(1).getRightHandSide().getLast();
        //actionTable[VAndT.indexOf(terminal)][T.indexOf(terminal)] = "A";

        /* Add Produce actions */
        for (Rule rule : P) {
            Symbol A = rule.getLeftHandSide();
            List<Symbol> alpha = new ArrayList<>(rule.getRightHandSide());
            alpha.addAll(List.of(FOLLOW, A));

            for (Symbol a : firstKAlphaFollowKA(contextFreeGrammar, 1, alpha)) {
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
            for (Symbol variable : firstKSets.keySet()) {
                writer.println("Firstk(" + variable.getValue() + ") = " + firstKSets.get(variable));
            }
            //System.out.println("Content written to file successfully to : " + filePath + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printFollowKSets(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Symbol variable : followKSets.keySet()) {
                writer.println("Followk(" + variable.getValue() + ") = " + followKSets.get(variable));
            }
            //System.out.println("Content written to file successfully to : " + filePath + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printFirstKAlphaFollowKA(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (List<Symbol> alphaFollowKA : firstKAlphaFollowKASets.keySet()) {
                writer.println("Firstk(" + alphaFollowKA + ") = " + firstKAlphaFollowKASets.get(alphaFollowKA));
            }
            //System.out.println("Content written to file successfully to : " + filePath + ")");
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
            int maxAlphabetLength = contextFreeGrammar.getVariablesAndTerminals().stream().map(symbol -> symbol.getValue().toString().length()).max(Integer::compare).orElse(0);
            int maxTerminalsLength = contextFreeGrammar.getTerminals().stream().map(symbol -> symbol.getValue().toString().length()).max(Integer::compare).orElse(0);
            int firstPadding = maxAlphabetLength + 7;
            int inBetweenColumnsPadding = maxTerminalsLength + 2;

            // Headers
            writer.printf("%-" + firstPadding + "s", "");
            for (Symbol terminal : contextFreeGrammar.getTerminals()) {
                if (!terminal.equals(EPSILON)) {
                    writer.printf("%-" + inBetweenColumnsPadding + "s", terminal.getValue());
                }
            }
            writer.println();

            // Table
            for (int i = 0; i < actionTable.length; i++) {
                Symbol symbol = contextFreeGrammar.getVariablesAndTerminals().get(i);
                if (!symbol.equals(EPSILON)) {
                    writer.printf("%-" + firstPadding + "s", symbol.getValue());

                    for (int j = 0; j < actionTable[0].length; j++) {
                        if (!contextFreeGrammar.getTerminals().get(j).equals(EPSILON)) {
                            writer.printf("%-" + inBetweenColumnsPadding + "s", actionTable[i][j]);
                        }
                    }
                    writer.println();
                }
            }
            //System.out.println("Table exported to: " + filePath + ")");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


