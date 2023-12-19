public class LLVM {
    private AST ast;
    private StringBuilder code = new StringBuilder();
    private int varCount = 0;

    public LLVM(AST ast) {
        this.ast = ast;
        generateCode(ast);
    }

    public void generateCode(ParseTree parseTree) {
        Symbol current = parseTree.getLabel();
        switch (current.getValue().toString()) {
            case "Program":
                code.append("define i32 @main() {\n");
                generateCode(parseTree.getChildren().get(1));
                code.append("\n");
                break;
            case "Code":
                generateCode(parseTree.getChildren().get(0));
                break;
            case "InstList":
                for (ParseTree grandchild : parseTree.getChildren()) {
                    if (grandchild.getLabel().isTerminal()) {
                        code.append("\n");
                    } else {
                        generateCode(grandchild);
                    }
                }
                break;
            case "Print":
                for (ParseTree grandchild : parseTree.getChildren()) {
                    if (grandchild.getLabel().getType().equals(LexicalUnit.VARNAME)) {
                        String varName = grandchild.getLabel().getValue().toString();
                        code.append("call void @println(i32 %" + varName + ")"); //check if %varName exist ? + don't forder to add println() function
                    } else {
                        //TODO: throw exception
                    }
                }
                break;
            case "Read":
                for (ParseTree grandchild : parseTree.getChildren()) {;
                    if (grandchild.getLabel().getType().equals(LexicalUnit.VARNAME)) {
                        String varName = grandchild.getLabel().getValue().toString();
                        code.append("%" + varName + " = call i32 @readInt()");
                    } else {
                        //TODO: throw exception
                    }
                }
                break;
        }
    }

    public String getCode() {
        return code.toString();
    }
}
