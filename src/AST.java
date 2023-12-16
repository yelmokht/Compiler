public class AST {
    private ParseTree parseTree;

    public AST(ParseTree parseTree) {
        this.parseTree = parseTree;
        removeUselessNodes();
    }
    
    public void removeUselessNodes() {
        //TODO
    }
}
