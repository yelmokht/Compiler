import java.util.ArrayList;
import java.util.List;

public class AST extends ParseTree {
    private Symbol current;

    public AST(ParseTree parseTree) {
        super(parseTree.getLabel(), parseTree.getChildren());
        this.children = generateAST(parseTree);
    }

    public List<ParseTree> generateAST(ParseTree parseTree) {
    List<ParseTree> children = new ArrayList<ParseTree>();
    for (ParseTree child : parseTree.getChildren()) {
        if (child.getChildren().isEmpty()) {
            //Terminals
            current = child.getLabel();
            switch (current.getType()) {
                case EPSILON:
                    break;
                case LBRACK:
                    break;
                case RBRACK:
                    break;
                default:
                    children.add(child);
            }
        } else {
            //Variables
            current = child.getLabel();
            switch (current.getValue().toString()) {
                case "Instruction":
                    for (ParseTree grandchild : generateAST(child)) {
                        children.add(grandchild);
                    }
                    break;
                case "InstListTail":
                    for (ParseTree grandchild : generateAST(child)) {
                        children.add(grandchild);
                    }
                    break;
                case "ExprArith'":
                    for (ParseTree grandchild : generateAST(child)) {
                        children.add(grandchild);
                    }
                    break;
                case "Prod'":
                    for (ParseTree grandchild : generateAST(child)) {
                        children.add(grandchild);
                    }
                    break;
                case "IfTail":
                    for (ParseTree grandchild : generateAST(child)) {
                        children.add(grandchild);
                    }
                    break;
                case "Cond'":
                    for (ParseTree grandchild : generateAST(child)) {
                        children.add(grandchild);
                    }
                    break;
                case "Conj'":
                    for (ParseTree grandchild : generateAST(child)) {
                        children.add(grandchild);
                    }
                    break;
                default:
                    ParseTree parent = new ParseTree(current, generateAST(child));
                    children.add(parent);
            }
        }
    }
    return children;
    }
}
