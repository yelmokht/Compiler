import java.util.*;

/**
 * ParseTools class contains all the tools related to grammars.
 */
public class ParseTools {
    private Map<String, Set<String>> firstKSets = new LinkedHashMap<>();
    private Map<String, Set<String>> followKSets = new LinkedHashMap<>();
    private int[][] actionTable;
    public static final String EPSILON = "ε";


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
        //Doit pouvoir gérer First(T) = {T}
        //Ici, c'est une map d'alphabet, tu checks si y'a et tu retournes. Sinon, tu checkes que c'est un terminal et tu retournes {T} (en l'ayant ajouté à la map)
        if (contextFreeGrammar.getTerminals().contains(x)) {
            firstKSets.putIfAbsent(x, new LinkedHashSet<>(Set.of(x))); // Check and initialize if absent
            return firstKSets.get(x);
        }

        //Doit pouvoir gérer First(V) = {}
        //Ici, c'sst à initialisation, tu checks dans la map d'abord, si y'a pas, alors tu mets {}
        if (contextFreeGrammar.getVariables().contains(x)) {
            firstKSets.putIfAbsent(x, new LinkedHashSet<>()); // Check and initialize if absent
            return firstKSets.get(x);
        }

        //Doit pouvoir gérer First(rightHandSideFollow(V))
        //si le premier character est un terminal, tu retournes First(T)
        //si c'est epsilon, tu retounes le follow
        //sinon tu retournes le follow
        if (x.contains("followK")) {
            String firstChar = x.substring(0,1);
            String content = x.substring(x.indexOf('(') + 1, x.indexOf(')'));

            // Check if the first character is a terminal and not epsilon
            if (contextFreeGrammar.getTerminals().contains(firstChar) && !firstChar.equals(EPSILON)) {
                return firstK(contextFreeGrammar, firstChar);
            } else {
                return followK(followKSets, content);
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
                System.out.println("*** Construct First K set for: " + rule.getLeftHandSide() + " *** \n");

                //Pour chaque élement de la partie droite de la règle
                for (String x : rule.getRightHandSide()) {
                    Set<String> tempSet = firstK(contextFreeGrammar, x); //Calcul FirstK(A)
                    System.out.println("Element encountered: " + x + " - Actual First K set of " + x + ": " + tempSet);
                    set.addAll(tempSet); //Ajout du set
                }

                System.out.println("\nSet obtained: " + set);
                System.out.println();
                Set<String> copySet  = Set.copyOf(firstKSets.get(rule.getLeftHandSide()));
                firstKSets.get(rule.getLeftHandSide()).addAll(set);
                System.out.println("First K set BEFORE for " + rule.getLeftHandSide() + " is :" + copySet);
                System.out.println("First K set AFTER for " + rule.getLeftHandSide() + " is :" + firstKSets.get(rule.getLeftHandSide()) + "\n");

                if (!atLeastOneFirstKSetHasBeenUpdated && !copySet.equals(firstKSets.get(rule.getLeftHandSide()))) {
                    System.out.println("AT LEAST ONE FIRST K SET HAS BEEN UPDATED\n");
                    atLeastOneFirstKSetHasBeenUpdated = true;
                }

                set.clear();
            }

            System.out.println("/////////// REPEAT " + atLeastOneFirstKSetHasBeenUpdated + "///////////\n");
        }
        return firstKSets;
    }

    private Set<String> followK(Map<String, Set<String>> firstKMap, String x) {
        //TODO
        return null;
    }


    public Map<String, Set<String>> constructFollowKSets(ContextFreeGrammar cfg) {
        //TODO
        return null;
    }

    public boolean isGrammarLL1(ContextFreeGrammar contextFreeGrammar) {
        //Compute the First and Follow sets
        firstKSets = constructFirstKSets(contextFreeGrammar);
        followKSets = constructFollowKSets(contextFreeGrammar);

        System.out.println("First K sets: " + firstKSets);
        System.out.println("Follow K sets: " + followKSets);

        /*
        for (Rule rule : contextFreeGrammar.getRules().values()) {

            //Check the rules that are multiples
            ArrayList<Rule> multipleOccurences = new ArrayList<>();
            for (Rule r : contextFreeGrammar.getRules().values()) {
                if (rule.getLeftHandSide().equals(r.getLeftHandSide())) {
                    multipleOccurences.add(r);
                }
            }

            //Apply the definition
            Set<String> set = new LinkedHashSet<>();
            for (Rule x : multipleOccurences) {
                Set<String> newSet = firstK(firstK  Sets, x.getRightHandSide() + followK(followKSets, rule.getLeftHandSide())); //on doit aussi envoyer le followK sets
                set.retainAll(newSet);
            }

            if (!set.isEmpty()){
                return false;
            }

        }

         */
        return false;
    }



    public int[][] constructLL1ActionTableFromCFG(ContextFreeGrammar contextFreeGrammar){
        //TODO
        return null;
    }

}
