import java.util.List;

/**
 * Represents a rule in the grammar with its left-hand side, right-hand side and number.
 */
public class Rule {
    private final Symbol leftHandSide;
    private final List<Symbol> rightHandSide;
    private final int number;

    /**
     * Constructs a new Rule with the specified left-hand side, right-hand side, and number.
     *
     * @param leftHandSide The symbol on the left-hand side.
     * @param rightHandSide The list of symbols on the right-hand side.
     * @param number The unique number associated with the rule.
     */
    public Rule(Symbol leftHandSide, List<Symbol> rightHandSide, int number) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.number = number;
    }

    public Symbol getLeftHandSide() {
        return leftHandSide;
    }

    public List<Symbol> getRightHandSide() {
        return rightHandSide;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "leftHandSide='" + leftHandSide + '\'' +
                ", rightHandSide=" + rightHandSide +
                ", number=" + number +
                '}';
    }
}
