import java.util.List;
import java.util.stream.Collectors;

public class Rule {
    private final Symbol leftHandSide;
    private final List<Symbol> rightHandSide;
    private final int number;


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
                "leftHandSide='" + leftHandSide.getValue() + '\'' +
                ", rightHandSide=" + rightHandSide +
                ", number=" + number +
                '}';
    }
}
