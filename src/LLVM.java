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
                code.append("define i32 @main() {\n"); //begin
                generateCode(parseTree.getChildren().get(1)); //<Code>
                code.append("\n"); //end
                break;
            case "Code":
                generateCode(parseTree.getChildren().get(0));//<InstList>
                break;
            case "InstList":
                for (ParseTree grandchild : parseTree.getChildren()) {
                    if (grandchild.getLabel().isTerminal()) {
                        code.append("\n"); //begin, dots and end
                    } else {
                        generateCode(grandchild); //<Instruction>
                    }
                }
                break;
            case "Assign":
                String varname = parseTree.getChildren().get(0).getLabel().getValue().toString();
                code.append("%" + varname + " = alloca i32\n");
                generateCode(parseTree.getChildren().get(2)); //<ExprArith>
                code.append("store i32 %" + varCount + ", i32* %" + varname + "\n");
                break;
            case "ExprArith":
                for (ParseTree grandchild : parseTree.getChildren()) {
                    if (grandchild.getLabel().isTerminal()) {
                        switch (grandchild.getLabel().getType()) {
                            case PLUS:
                                code.append("%" + varCount + "add i32 %" + (varCount - 1) + ", i32 %" + (varCount - 2)+ "\n"); //+
                                varCount++;
                                break;
                            case MINUS:
                                code.append("%" + varCount + "sub i32 %" + (varCount - 1) + ", i32 %" + (varCount - 2)+ "\n"); //-
                                varCount++;
                                break;
                            default:
                                break;
                        }
                    } else {
                        generateCode(grandchild); //<Prod>
                    }
                }
                break;
            case "Prod":
                for (ParseTree grandchild : parseTree.getChildren()) {
                    if (grandchild.getLabel().isTerminal()) {
                        switch (grandchild.getLabel().getType()) {
                            case TIMES:
                                code.append("%" + varCount + "mul i32 %" + (varCount - 1) + ", i32 %" + (varCount - 2)+ "\n"); //+
                                varCount++;
                                break;
                            case DIVIDE:
                                code.append("%" + varCount + "sdiv i32 %" + (varCount - 1) + ", i32 %" + (varCount - 2)+ "\n"); //-
                                varCount++;
                                break;
                            default:
                                break;
                        }
                    } else {
                        generateCode(grandchild); //<Prod>
                    }
                }
                break;
            case "Atom":
                for (ParseTree grandchild : parseTree.getChildren()) {
                    if (grandchild.getLabel().isTerminal()) {
                        switch (grandchild.getLabel().getType()) {
                            case MINUS:
                                //TODO
                                break;
                            default:
                                break;
                            case VARNAME:
                                String varName = grandchild.getLabel().getValue().toString();
                                code.append("%" + varCount + " = load i32, i32* %" + varName + "\n");
                                varCount++;
                                break;
                            case NUMBER:
                                String number = grandchild.getLabel().getValue().toString();
                                code.append("%" + varCount + " = add i32 " + number + ", 0\n");
                                varCount++;
                                break;
                        }
                    } else {
                        generateCode(grandchild); //<Atom> or <ExprArith>
                    }
                }
                break;
            case "If":
                if (parseTree.getChildren().size() == 5) { //if cond then instruction else
                    code.append("br i1 %" + (varCount - 2) + ", label %" + (varCount - 1) + ", label %" + varCount + "\n");
                    varCount++;
                } else { //if cond then instruction else instruction
                    code.append("br i1 %" + (varCount - 2) + ", label %" + (varCount - 1) + ", label %" + varCount + "\n");
                }
                break;
            case "Cond":
                //TODO
                break;
            case "Conj":
                //TODO
                break;
            case "SimpleCond":
                //TODO
                break;
            case "Comp":
                //TODO
                break;
            case "While":
                //TODO
                break;
            case "Print":
                for (ParseTree grandchild : parseTree.getChildren()) {
                    if (grandchild.getLabel().getType().equals(LexicalUnit.VARNAME)) {
                        String varName = grandchild.getLabel().getValue().toString();
                        code.append("call void @println(i32 %" + varName + ")");
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
            default:
                break;
        }
    }

    public String getCode() {
        return code.toString();
    }
}
