import java.util.ArrayList;

public class LLVM {
    private AST ast;
    private StringBuilder code = new StringBuilder();
    private ArrayList<String> namedVariables = new ArrayList<>();
    private ArrayList<String> numberedVariables = new ArrayList<>();
    private ArrayList<String> numbers = new ArrayList<>();

    public LLVM(AST ast) {
        this.ast = ast;
        generateCode(ast);
    }

    public String generateCode(ParseTree parseTree) {
        Symbol current = parseTree.getLabel();
        String result = null;
        switch (current.getValue().toString()) {
            case "Program":
                result = program(parseTree);
                break;
            case "Code":
                result = code(parseTree);
                break;
            case "InstList":
                result = instlist(parseTree);
                break;
            case "Assign":
                result = assign(parseTree);
                break;
            case "ExprArith":
                result = exprarith(parseTree);
                break;
            case "Prod":
                result = prod(parseTree);
                break;
            case "Atom":
                result = atom(parseTree);
                break;
            case "If":
                result = if_(parseTree);
                break;
            case "Cond":
                result = cond(parseTree);
                break;
            case "Conj":
                result = conj(parseTree);
                break;
            case "SimpleCond":
                result = simplecond(parseTree);
                break;
            case "While":
                result = while_(parseTree);
                break;
            case "Print":
                result = print(parseTree);
                break;
            case "Read":
                result = read(parseTree);
                break;
            default:
                break;
        }
        return result;
    }

    public String addNumber(String number) {
        if(!this.numbers.contains(number)){
            this.numbers.add(number);
        }
        return number;
    }

    public String addNamedVariable(String varname) {
        if(!this.namedVariables.contains(varname)){
            this.namedVariables.add(varname);
            code.append("%" + varname + " = alloca i32\n");
        }
        return varname;
    }

    public String addNumberedVariable() {
        numberedVariables.add(String.valueOf(numberedVariables.size() + 1));
        return numberedVariables.get(numberedVariables.size() - 1);
    }

    public String program(ParseTree parseTree) {
        code.append("define i32 @main() {\n"); //begin
        generateCode(parseTree.getChildren().get(1)); //<Code>
        code.append("}\n"); //end
        return null;
    }

    public String code(ParseTree parseTree) {
        generateCode(parseTree.getChildren().get(0)); //<InstList>
        return null;
    }

    public String instlist(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isTerminal()) {
                code.append("\n"); //begin, dots and end
            } else {
                generateCode(grandchild); //<Instruction>
            }
        }
        return null;
    }

    public String assign(ParseTree parseTree) {
        String namedVariable = parseTree.getChildren().get(0).getLabel().getValue().toString();
        addNamedVariable(namedVariable);
        String value = exprarith(parseTree.getChildren().get(2)); //<ExprArith>
        code.append("store i32 " + value + ", i32* %" + namedVariable + "\n"); //MAKE DISTINCION BETWEEN NUMBER AND VARIABLE
        return null;
    }

    public String exprarith(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return prod(parseTree.getChildren().get(0));
        } else {
            for (int i = 0; i < n; i += 3) {
                String leftProd = prod(parseTree.getChildren().get(i));
                String rightProd = prod(parseTree.getChildren().get(i+2));
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case PLUS:
                        numberedVariable = addNumberedVariable();
                        code.append("%" + numberedVariable + " = add i32 " + leftProd + ", i32 " + rightProd + "\n");
                        return numberedVariable;
                    case MINUS:
                        numberedVariable = addNumberedVariable();
                        code.append("%" + numberedVariable + " = sub i32 " + leftProd + ", i32 " + rightProd + "\n");
                        return numberedVariable;
                    default:
                        //Throw error
                        break;
                }
            }
        }
        return null;
    }
    public String prod(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return atom(parseTree.getChildren().get(0));
        } else {
            for (int i = 0; i < n; i += 3) {
                String leftAtom = atom(parseTree.getChildren().get(i));
                String rightAtom = atom(parseTree.getChildren().get(i+2));
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case DIVIDE:
                        numberedVariable = addNumberedVariable();
                        code.append("%" + numberedVariable + " = sdiv i32 " + leftAtom + ", i32 " + rightAtom + "\n");
                        return numberedVariable;
                    case TIMES:
                        numberedVariable = addNumberedVariable();
                        code.append("%" + numberedVariable + " = mul i32 " + leftAtom + ", i32 " + rightAtom + "\n");
                        return numberedVariable;
                    default:
                        //Throw error
                        break;
                }
            }
        }
        return null;
    }

    public String unaryMinus(ParseTree parseTree) {
        //TODO
        return null;
    }

    public String atom(ParseTree parseTree) {
        ParseTree grandchild = parseTree.getChildren().get(0);
        String result = "";
        switch (grandchild.getLabel().getType()) {
            case MINUS:
                result = unaryMinus(parseTree);
                break;
            case LPAREN:
                result = exprarith(parseTree.getChildren().get(1));
                break;
            case VARNAME:
                result = "%" + addNamedVariable(grandchild.getLabel().getValue().toString());
                break;
            case NUMBER:
                result = addNumber(grandchild.getLabel().getValue().toString());
                break;
            default:
                //Throw error
                break;
        }
        return result;
    }

    public String if_(ParseTree parseTree) {
        if (parseTree.getChildren().size() == 5) {
            String boolValue = cond(parseTree.getChildren().get(1)); //<Cond>
            code.append("br i1 %" + boolValue + ", label " + myLabelIfTrue + ", label " + myLabelIfFalse + "\n");
            String myLabelIfTrue = generateCode(parseTree.getChildren().get(3)); //<Instruction>
            String myLabelIfFalse = "%nextInstruction"; //TODO
        } else {
            String boolValue = cond(parseTree.getChildren().get(1)); //<Cond>
            String myLabelIfTrue = generateCode(parseTree.getChildren().get(3)); //<Instruction1>
            String myLabelIfFalse = generateCode(parseTree.getChildren().get(5)); //<Instruction2>
            code.append("br i1 %" + boolValue + ", label " + myLabelIfTrue + ", label " + myLabelIfFalse + "\n");
        }
        return null;
    }

    public String cond(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return conj(parseTree.getChildren().get(0));
        } else {
            for (int i = 0; i < n; i += 3) {
                String leftConj = conj(parseTree.getChildren().get(i));
                String rightConj = conj(parseTree.getChildren().get(i+2));
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case OR:
                        numberedVariable = addNumberedVariable();
                        code.append("%" + numberedVariable + "= or i32 " + leftConj + ", i32 " + rightConj + "\n");
                        break;
                    default:
                        //Throw error
                        break;
                }
            }
        }
        return null;
    }

    private String conj(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return simplecond(parseTree.getChildren().get(0));
        } else {
            for (int i = 0; i < n; i += 3) {
                String leftSimpleCond = simplecond(parseTree.getChildren().get(i));
                String rightSimpleCond= simplecond(parseTree.getChildren().get(i+2));
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case AND:
                        numberedVariable = addNumberedVariable();
                        code.append("%" + numberedVariable + "= or i32 " + leftSimpleCond + ", i32 " + rightSimpleCond + "\n");
                        break;
                    default:
                        //Throw error
                        break;
                }
            }
        }
        return null;
    }

    private String simplecond(ParseTree parseTree) {
        if (parseTree.getChildren().size() == 1) {
            cond(parseTree.getChildren().get(1)); //<Cond>
        } else {
            String leftComp = generateCode(parseTree.getChildren().get(0));
            String rightComp = generateCode(parseTree.getChildren().get(2));
            String numberedVariable = "";
            switch (parseTree.getChildren().get(1).getLabel().getType()) {
                case EQUAL:
                    numberedVariable = addNumberedVariable();
                    code.append("%" + numberedVariable + " = icmp eq i32 " + leftComp + ", i32 " + rightComp + "\n");
                    return numberedVariable;
                case SMALLER:
                    code.append("%" + numberedVariable + " = icmp slt i32 " + leftComp + ", i32 " + rightComp + "\n");
                    return numberedVariable;
                default:
                    break;
            }
        }
        return null;
    }

    private String while_(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            generateCode(grandchild);
        }
        return null;
    }

    private String print(ParseTree parseTree) {
        String varname = parseTree.getChildren().get(2).getLabel().getValue().toString();
        code.append("call void @println(i32 %" + varname + ")");
        return null;
    }

    private String read(ParseTree parseTree) {
        String varname = parseTree.getChildren().get(2).getLabel().getValue().toString();
        code.append("%" + varname + " = call i32 @readInt()");
        return null;
    }

    public String getCode() {
        System.out.println("Named variables: " + namedVariables);
        System.out.println("Numbered variables: " + numberedVariables);
        System.out.println("Numbers: " + numbers);
        return code.toString();
    }
}
