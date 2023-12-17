public class LLVM {
    private AST ast;
    private StringBuilder code;

    public LLVM(AST ast) {
        this.ast = ast;
    }

    public void generateCode(ParseTree parseTree) {
        for (ParseTree child : parseTree.getChildren()) {
            Symbol current = child.getLabel();
            switch (current.getValue().toString()) {
                case "Program":
                    code.append("define i32 @main() {\n");
                    break;
                case "Code":
                    generateCode(child);
                    break;
                case "InstList":
                    for (ParseTree grandchild : child.getChildren()) {
                        //Terminal
                        if (grandchild.getLabel().getType().equals(LexicalUnit.DOTS)) {
                            code.append("\n");
                        } else {
                            //Variable
                            generateCode(child);
                        }
                    }
                    break;
                case "Assign":
                    
                    break;
                case "If":
                    break;
                case "While":
                    break;
                case "Print":
                    for (ParseTree grandchild : child.getChildren()) {
                        if (grandchild.getLabel().getType().equals(LexicalUnit.VARNAME)) {
                            String varName = grandchild.getLabel().getValue().toString();
                            code.append("call void @println(i32 %" + varName + ")\n"); //check if %varName exist ? + don't forder to add println() function
                        } else {
                            //TODO: throw exception
                        }
                    }
                    break;
                case "Read":
                    for (ParseTree grandchild : child.getChildren()) {
                        if (grandchild.getLabel().getType().equals(LexicalUnit.VARNAME)) {
                            String varName = grandchild.getLabel().getValue().toString();
                            code.append("%" + varName + " = call i32 @readInt()\n"); //Don"t forget to add readInt() function
                        } else {
                            //TODO: throw exception
                        }
                    }
                    break;
                case "ExprArith":
                    for (ParseTree grandchild : child.getChildren()) {
                        if (grandchild.getLabel().getValue().toString().equals("Prod")) {
                            
                        }
                    break;
                case "Prod":
                    
                    break;
                case "Atom":
                    break;
                case "Cond":
                    break;
                case "Conj":
                    break;
                case "SimpleCond":
                    break;
            }
        }
    }

}
