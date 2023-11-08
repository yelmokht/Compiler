import java.util.List;

public class Rule {
    private final String leftHandSide;
    private final List<String> rightHandSide;

    public Rule(String leftHandSide, List<String> rightHandSide) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
    }

    public String getLeftHandSide() {
        return leftHandSide;
    }

    public List<String> getRightHandSide() {
        return rightHandSide;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "leftHandSide='" + leftHandSide + '\'' +
                ", rightHandSide=" + rightHandSide +
                '}';
    }
}
