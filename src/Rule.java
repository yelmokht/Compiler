import java.util.List;

public class Rule {
    private final String leftHandSide;
    private final List<String> rightHandSide;
    private final int number;


    public Rule(String leftHandSide, List<String> rightHandSide, int number) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.number = number;
    }

    public String getLeftHandSide() {
        return leftHandSide;
    }

    public List<String> getRightHandSide() {
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
