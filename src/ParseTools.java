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

    public Map<String, Set<String>> getFirstKSets() {
        return firstKSets;
    }

    public Map<String, Set<String>> getFollowKSets() {
        return followKSets;
    }

    public int[][] getActionTable() {
        return actionTable;
    }

    private Set<String> firstK(ContextFreeGrammar contextFreeGrammar, String x) {
        if (contextFreeGrammar.getTerminals().contains(x)) {
            firstKSets.putIfAbsent(x, new LinkedHashSet<>(Set.of(x))); // Check and initialize if absent
            return firstKSets.get(x);
        }

        if (contextFreeGrammar.getVariables().contains(x)) {
            firstKSets.putIfAbsent(x, new LinkedHashSet<>()); // Check and initialize if absent
            return firstKSets.get(x);
        }

        if (x.contains(FOLLOW)) {
            String firstChar = x.substring(0,x.indexOf("<"));
            String content = x.substring(x.indexOf(FOLLOW));
            System.out.println("X : " + x);
            System.out.println("First char : " + firstChar);
            System.out.println("Content : " + content);

            // Check if the first character is a terminal and not epsilon
            if (contextFreeGrammar.getTerminals().contains(firstChar) && !firstChar.equals(EPSILON)) {
                return firstK(contextFreeGrammar, firstChar);
            } else {
                return followK(contextFreeGrammar, content);
            }
        } else {
            throw new IllegalArgumentException("Cannot compute firstK");
        }
    }


    public Map<String, Set<String>> constructFirstKSets(ContextFreeGrammar contextFreeGrammar) {
        boolean atLeastOneFirstKSetHasBeenUpdated = true;

        while (atLeastOneFirstKSetHasBeenUpdated) {
            atLeastOneFirstKSetHasBeenUpdated = false;
            firstK(contextFreeGrammar, contextFreeGrammar.getStartSymbol());

            //Pour chaque règle
            for (Rule rule : contextFreeGrammar.getRules().values()) {
                Set<String> set = new LinkedHashSet<>();

                //Pour chaque élement de la partie droite de la règle
                for (String x : rule.getRightHandSide()) {
                    Set<String> tempSet = firstK(contextFreeGrammar, x); //Calcul FirstK(A)
                    set.addAll(tempSet); //Ajout du set
                }

                Set<String> copySet  = Set.copyOf(firstKSets.get(rule.getLeftHandSide()));
                firstKSets.get(rule.getLeftHandSide()).addAll(set);

                if (!atLeastOneFirstKSetHasBeenUpdated && !copySet.equals(firstKSets.get(rule.getLeftHandSide()))) {
                    atLeastOneFirstKSetHasBeenUpdated = true;
                }

                set.clear();
            }

            System.out.println("/////////// REPEAT " + atLeastOneFirstKSetHasBeenUpdated + "///////////\n");
        }
        return firstKSets;
    }
    private Set<String> followK(ContextFreeGrammar cfg, String x) {
        Set<String> followKSets = new LinkedHashSet<>();
    
        if (x.equals(cfg.getStartSymbol())) {
            followKSets.add(EPSILON);
        }
    
        for (Rule rule : cfg.getRules().values()) {
            List<String> rightHandSide = rule.getRightHandSide();
            for (int i = 0; i < rightHandSide.size(); i++) {
                if (rightHandSide.get(i).equals(x) && i < rightHandSide.size() - 1) {
                    String nextSymbol = rightHandSide.get(i + 1);
                    followKSets.addAll(firstK(cfg, nextSymbol));
                }
            }
        }
    
        return followKSets;
    }


    public Map<String, Set<String>> constructFollowKSets(ContextFreeGrammar cfg) {
        Map<String, Set<String>> followKSets = new LinkedHashMap<>();
        String startSymbol = cfg.getStartSymbol();
    
        // Initialize Followk sets
        for (String variable : cfg.getVariables()) {
            followKSets.put(variable, new LinkedHashSet<>());
        }
        followKSets.get(startSymbol).add(EPSILON);
    
        boolean followKSetHasBeenUpdated;
        do {
            followKSetHasBeenUpdated = false;
            for (Rule rule : cfg.getRules().values()) {
                String leftHandSide = rule.getLeftHandSide();
                List<String> rightHandSide = rule.getRightHandSide();
    
                for (int i = 0; i < rightHandSide.size(); i++) {
                    String B = rightHandSide.get(i);
                    if (cfg.getVariables().contains(B)) {
                        List<String> beta = rightHandSide.subList(i + 1, rightHandSide.size());
                        Set<String> oldFollowKSet = new LinkedHashSet<>(followKSets.get(B));
                        Set<String> newFollowKSet = new LinkedHashSet<>();

                        String x = String.join("", beta);
                        if (x.isEmpty()) {
                            x = EPSILON;
                            Set<String> set = new LinkedHashSet<>(firstK(cfg, x));
                            newFollowKSet.addAll(set);
                        } else {
                            for (String y : beta) {
                                Set<String> set = new LinkedHashSet<>(firstK(cfg, y));
                                newFollowKSet.addAll(set);
                            }
                        }
                        newFollowKSet.addAll(followKSets.get(leftHandSide));
                        followKSets.get(B).addAll(newFollowKSet);
    
                        if (!oldFollowKSet.equals(followKSets.get(B))) {
                            followKSetHasBeenUpdated = true;
                        }
                    }
                }
            }
        } while (followKSetHasBeenUpdated);
    
        return followKSets;
    }
    public List<List<Integer>> occurrencesRules(ContextFreeGrammar contextFreeGrammar) {
        //To simplify
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



    public boolean isGrammarLL1(ContextFreeGrammar contextFreeGrammar) {
        //Compute the First and Follow sets
        firstKSets = constructFirstKSets(contextFreeGrammar);
        followKSets = constructFollowKSets(contextFreeGrammar);

        System.out.println("First K sets: " + firstKSets);
        System.out.println("Follow K sets: " + followKSets);

        //Liste des règles qui apparaissent plus d'une fois
        List<List<Integer>> listRules = occurrencesRules(contextFreeGrammar);

        for (List<Integer> sameRules : listRules) {
            Set<String> set = new LinkedHashSet<>();
            for (int ruleNumber : sameRules) {
                String leftHandSide = contextFreeGrammar.getRules().get(ruleNumber).getLeftHandSide();
                String rightHandSide = String.join("", contextFreeGrammar.getRules().get(ruleNumber).getRightHandSide()).replaceAll("[,]", "");
                String definition = rightHandSide + FOLLOW + leftHandSide;
                Set<String> firstKset = firstK(contextFreeGrammar, definition);
                set.retainAll(firstKset);
                if (!set.isEmpty()) {
                    return false;
                }
            }
        }
        System.out.print("This grammar is LL1");
        return true;
    }


    public int[][] constructLL1ActionTableFromCFG(ContextFreeGrammar contextFreeGrammar){

        if (firstKSets.isEmpty() && followKSets.isEmpty()) {
            firstKSets = constructFirstKSets(contextFreeGrammar);
            followKSets = constructFollowKSets(contextFreeGrammar);
        }

        // Initialize the action table with -1
        actionTable = new int[contextFreeGrammar.getVariables().size()][contextFreeGrammar.getTerminals().size() + 1];
        for (int[] row : actionTable) {
            Arrays.fill(row, -1);
        }
    
        // Fill the action table
        for (Rule rule : contextFreeGrammar.getRules().values()) {
            String leftHandSide = rule.getLeftHandSide();
            String rightHandSide = String.join("", rule.getRightHandSide()).replaceAll("[,]", "");
            String definition = rightHandSide + FOLLOW + leftHandSide;

            for (String a : firstK(contextFreeGrammar, definition)) {
                actionTable[contextFreeGrammar.getVariables().indexOf(leftHandSide)][contextFreeGrammar.getTerminals().indexOf(a)] = rule.getNumber();
            }

            /*
            // For each terminal a in First(A -> alpha)
            for (String a : firstK(contextFreeGrammar, String.join("", rightHandSide))) {
                if (!a.equals(EPSILON)) {
                    actionTable[contextFreeGrammar.getVariables().indexOf(leftHandSide)][contextFreeGrammar.getTerminals().indexOf(a)] = rule.getNumber();
                }
            }
    
            // If epsilon is in First(A -> alpha), for each terminal b in Follow(A), add A -> alpha to M[A, b]
            if (firstK(contextFreeGrammar, String.join("", rightHandSide)).contains(EPSILON)) {
                for (String b : followKSets.get(leftHandSide)) {
                    if (!b.equals(EPSILON)) {
                        actionTable[contextFreeGrammar.getVariables().indexOf(leftHandSide)][contextFreeGrammar.getTerminals().indexOf(b)] = rule.getNumber();
                    }
                }
                // If epsilon is in Follow(A), add A -> alpha to M[A, $]
                if (followKSets.get(leftHandSide).contains(EPSILON)) {
                    actionTable[contextFreeGrammar.getVariables().indexOf(leftHandSide)][contextFreeGrammar.getTerminals().size()] = rule.getNumber();
                }
            }

             */
        }

        System.out.println();
        for (int i = 0; i < actionTable.length; i++) {
            // Iterate through each element in the current row
            for (int j = 0; j < actionTable[i].length; j++) {
                System.out.print(actionTable[i][j] + " ");
            }
            // Move to the next line after printing each row
            System.out.println();
        }

        return actionTable;
    }

}
