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
                code.append("store i32 %" + (varCount - 1) + ", i32* %" + varname + "\n");
                break;
            case "ExprArith":
                int n = parseTree.getChildren().size();
                for (int j = 0; j < n; j += 3) {
                    int k = j+3 < n ? j+3 : n;
                    for (int i = j; i < k ; i += 2) {
                        ParseTree grandchild = parseTree.getChildren().get(i);
                        generateCode(grandchild);
                    }
                    if (n != 1) {
                        ParseTree grandchild = parseTree.getChildren().get(j+1); //
                        if (grandchild.getLabel().isTerminal()) {
                            switch (grandchild.getLabel().getType()) {
                                case PLUS:
                                    code.append("%" + varCount + " = add i32 %" + (varCount - 1) + ", i32 %" + (varCount - 2)+ "\n"); //+
                                    varCount++;
                                    break;
                                case MINUS:
                                    code.append("%" + varCount + " = sub i32 %" + (varCount - 1) + ", i32 %" + (varCount - 2)+ "\n"); //-
                                    varCount++;
                                    break;
                                default:
                                    break;
                            }
                        }
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
                    for (ParseTree grandchild : parseTree.getChildren()) {
                        generateCode(grandchild); //<Cond> and <Instruction>
                    }
                    code.append("br i1 %" + (varCount - 2) + ", label %" + (varCount - 1) + ", label %" + varCount + "\n");
                    varCount++;
                } else { //if cond then instruction else instruction
                    for (ParseTree grandchild : parseTree.getChildren()) {
                        generateCode(grandchild); //<Cond> and <Instruction> and <Instruction2>
                    }
                    code.append("br i1 %" + (varCount - 2) + ", label %" + (varCount - 1) + ", label %" + varCount + "\n");
                }
                break;
            case "Cond":
                for (ParseTree grandchild : parseTree.getChildren()) {
                    if (grandchild.getLabel().isTerminal()) {
                        switch (grandchild.getLabel().getType()) {
                            case OR:
                                code.append("%" + varCount + "or i32 %" + (varCount - 1) + ", i32 %" + (varCount - 2)+ "\n"); //or
                                varCount++;
                                break;
                            default:
                                break;
                        }
                    } else {
                        generateCode(grandchild); //<Conj>
                    }
                }
                break;
            case "Conj":
                for (ParseTree grandchild : parseTree.getChildren()) {
                    if (grandchild.getLabel().isTerminal()) {
                        switch (grandchild.getLabel().getType()) {
                            case AND:
                                code.append("%" + varCount + "and i32 %" + (varCount - 1) + ", i32 %" + (varCount - 2)+ "\n"); //and
                                varCount++;
                                break;
                            default:
                                break;
                        }
                    } else {
                        generateCode(grandchild); //<SimpleCond>
                    }
                }
                break;
            case "SimpleCond":
                if (parseTree.getChildren().get(1).getLabel().toString().equals("Cond")) {
                    generateCode(parseTree.getChildren().get(1)); //<Cond>
                } else {
                    generateCode(parseTree.getChildren().get(0)); //<ExprArith>
                    generateCode(parseTree.getChildren().get(2)); //<ExprArith>
                    generateCode(parseTree.getChildren().get(1)); //<Comp>
                }
                break;
            case "Comp":
                switch (parseTree.getChildren().get(0).getLabel().getType()) {
                    case EQUAL:
                        code.append("%" + varCount + " = icmp eq i32 %" + (varCount - 2) + ", i32 %" + (varCount - 1)+ "\n"); //==
                        varCount++;
                        break;
                    case SMALLER:
                        code.append("%" + varCount + " = icmp slt i32 %" + (varCount - 2) + ", i32 %" + (varCount - 1)+ "\n"); //<
                        varCount++;
                        break;
                    default:
                        break;
                }
                break;
            case "While":
                for (ParseTree grandchild : parseTree.getChildren()) {
                    generateCode(grandchild); //<Cond> or <Instruction>
                }
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
