import java.lang.reflect.Array;
import java.util.ArrayList;

public class LLVM {
    private AST ast;
    private StringBuilder code = new StringBuilder();
    private ArrayList<String> variables = new ArrayList<>();
    private ArrayList<String> numbers = new ArrayList<>();
    private int varCount = 0;

    public LLVM(AST ast) {
        this.ast = ast;
        generateCode(ast);
    }

    public void generateCode(ParseTree parseTree) {
        Symbol current = parseTree.getLabel();
        switch (current.getValue().toString()) {
            case "Program":
                program(parseTree); //OK
                break;
            case "Code":
                code(parseTree);
                break;
            case "InstList":
                instlist(parseTree);
                break;
            case "Assign":
                assign(parseTree);
                break;
            case "ExprArith":
                exprarith(parseTree);
                break;
            case "Prod":
                prod(parseTree);
                break;
            case "Atom":
                atom(parseTree);
                break;
            case "If":
                processIf(parseTree);
                break;
            case "Cond":
                cond(parseTree);
                break;
            case "Conj":
                conj(parseTree);
                break;
            case "SimpleCond":
                simpleCond(parseTree);
                break;
            case "Comp":
                comp(parseTree);
                break;
            case "While":
                whileProcess(parseTree);
                break;
            case "Print":
                print(parseTree);
                break;
            case "Read":
                read(parseTree);
                break;
            default:
                break;
        }
    }


    public void program(ParseTree parseTree) {
        code.append("define i32 @main() {\n"); //begin
        generateCode(parseTree.getChildren().get(1)); //<Code>
        code.append("}\n"); //end
    }

    public void code(ParseTree parseTree) {
        generateCode(parseTree.getChildren().get(0));//<InstList>
    }

    public void instlist(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isTerminal()) {
                code.append("\n"); //begin, dots and end
            } else {
                generateCode(grandchild); //<Instruction>
            }
        }
    }

    public void assign(ParseTree parseTree) {
        String varname = parseTree.getChildren().get(0).getLabel().getValue().toString();
        code.append("%" + varname + " = alloca i32\n");
        generateCode(parseTree.getChildren().get(2)); //<ExprArith>
        code.append("store i32 %" + (varCount - 1) + ", i32* %" + varname + "\n");
    }

    public void exprarith(ParseTree parseTree) {
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
    }

    public void prod(ParseTree parseTree) {
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
    }

    public void atom(ParseTree parseTree) {
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
                        numbers.add(number);
                        break;
                }
            } else {
                generateCode(grandchild); //<Atom> or <ExprArith>
            }
        }
    }

    public void processIf(ParseTree parseTree) {
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
    }

    public void cond(ParseTree parseTree) {
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
    }

    private void conj(ParseTree parseTree) {
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
    }

    private void simpleCond(ParseTree parseTree) {
        if (parseTree.getChildren().get(1).getLabel().toString().equals("Cond")) {
            generateCode(parseTree.getChildren().get(1)); //<Cond>
        } else {
            generateCode(parseTree.getChildren().get(0)); //<ExprArith>
            generateCode(parseTree.getChildren().get(2)); //<ExprArith>
            generateCode(parseTree.getChildren().get(1)); //<Comp>
        }
    }

    private void comp(ParseTree parseTree) {
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
    }

    private void whileProcess(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            generateCode(grandchild); //<Cond> or <Instruction>
        }
    }

    private void print(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().getType().equals(LexicalUnit.VARNAME)) {
                String varName = grandchild.getLabel().getValue().toString();
                code.append("call void @println(i32 %" + varName + ")");
            } else {
                //TODO: throw exception
            }
        }
    }

    private void read(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {;
            if (grandchild.getLabel().getType().equals(LexicalUnit.VARNAME)) {
                String varName = grandchild.getLabel().getValue().toString();
                code.append("%" + varName + " = call i32 @readInt()");
            } else {
                //TODO: throw exception
            }
        }
    }

    public String getCode() {
        return code.toString();
    }
}
