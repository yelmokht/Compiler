import java.util.ArrayList;

public class LLVM {
    private AST ast;
    private StringBuilder code = new StringBuilder();
    private ArrayList<String> namedVariables = new ArrayList<>();
    private int numberedVariableCounter = 0;
    private int instructionCounter = 0;
    private int minusCounter = 0;
    private String ifTrueLabel = "ifTrue_" + instructionCounter;
    private String ifFalseLabel =  "ifFalse_" + instructionCounter;
    private String whileLoopLabel = "whileLoop_" + instructionCounter;
    private String whileBodyLabel = "whileBody_" + instructionCounter;
    private String WhileEndLabel = "whileEnd_" + instructionCounter;

    public LLVM(AST ast) {
        this.ast = ast;
        generateCode(ast);
    }

    public void generateCode(ParseTree parseTree) {
        Symbol current = parseTree.getLabel();
        switch (current.getValue().toString()) {
            case "Program":
                program(parseTree);
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
            case "If":
                if_(parseTree);
                break;
            case "While":
                while_(parseTree);
                break;
            case "Print":
                print(parseTree);
                break;
            case "Read":
                read(parseTree);
                break;
            default:
                throw new RuntimeException("Invalid rule");
        }
    }

    public String addNamedVariable(String varname) {
        if(!this.namedVariables.contains(varname)){
            this.namedVariables.add(varname);
            code.append("%" + varname + " = alloca i32\n");
        }
        return varname;
    }

    public String addNumberedVariable() {
        numberedVariableCounter++;
        return String.valueOf(numberedVariableCounter);
    }

    public void addReadFunction() {
            code.append("@.strR = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1\n")
                .append("\n")
                .append("define i32 @readInt() {\n")
                .append("  %x = alloca i32, align 4\n")
                .append("  %1 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %x)\n")
                .append("  %2 = load i32, i32* %x, align 4\n")
                .append("  ret i32 %2\n")
                .append("}\n")
                .append("declare i32 @__isoc99_scanf(i8*, ...)");
        }

        public void addPrintFunction() {
        code.append("@.strP = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1\n")
            .append("\n")
            .append("; Function Attrs: nounwind uwtable\n")
            .append("define void @println(i32 %x) #0 {\n")
            .append("  %1 = alloca i32, align 4\n")
            .append("  store i32 %x, i32* %1, align 4\n")
            .append("  %2 = load i32, i32* %1, align 4\n")
            .append("  %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)\n")
            .append("  ret void\n")
            .append("}\n")
            .append("\n")
            .append("declare i32 @printf(i8*, ...) #1;\n")
            .append("\n");
    }

    public void allocateVariables(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (!(grandchild.getLabel().getValue().toString().equals("Read") || grandchild.getLabel().getValue().toString().equals("Print"))) {
                if (grandchild.getLabel().isTerminal() && grandchild.getLabel().getType() == LexicalUnit.VARNAME) {
                    addNamedVariable(grandchild.getLabel().getValue().toString());
                } else {
                    allocateVariables(grandchild);
                }
            }
        }
    }

    public void program(ParseTree parseTree) {
        addReadFunction();
        addPrintFunction();
        code.append("define i32 @main() {\n");
        allocateVariables(ast);
        code(parseTree.getChildren().get(1));
        code.append("ret i32 0\n}\n");
    }

    public void code(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isNonTerminal()) {
                generateCode(grandchild);
            } else {
                throw new RuntimeException("Invalid code");
            }
        }
    }

    public void instlist(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isNonTerminal()) {
                generateCode(grandchild);
            }
        }
    }

    public void assign(ParseTree parseTree) {
        String namedVariable = parseTree.getChildren().get(0).getLabel().getValue().toString();
        addNamedVariable(namedVariable);
        String value = exprarith(parseTree.getChildren().get(2));
        code.append("store i32 " + value + ", i32* %" + namedVariable + "\n");
    }


    public String exprarith(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return prod(parseTree.getChildren().get(0));
        } else if (n >= 3 && n % 2 == 1) {
            String leftProd = "";
            String rightProd = "";
            String last = "";
            for (int i = 0; i < n - 1; i += 2) {
                if (last.isEmpty()) {
                    leftProd = prod(parseTree.getChildren().get(0));
                    rightProd = prod(parseTree.getChildren().get(2));
                } else {
                    leftProd = last;
                    rightProd = prod(parseTree.getChildren().get(i));
                }
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case PLUS:
                        numberedVariable = "%" + addNumberedVariable();
                        code.append(numberedVariable + " = add i32 " + leftProd + ", " + rightProd + "\n");
                        break;
                    case MINUS:
                        numberedVariable = "%" + addNumberedVariable();
                        code.append(numberedVariable + " = sub i32 " + leftProd + ", " + rightProd + "\n");
                        break;
                    default:
                        throw new RuntimeException("Invalid op");
                }
                last = numberedVariable;
            }
            return last;
        } else {
            throw new RuntimeException("Invalid exprarith");
        }
    }

    public String prod(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return atom(parseTree.getChildren().get(0));
        } else if (n >= 3 && n % 2 == 1) {
            String leftAtom = "";
            String rightAtom = "";
            String last = "";
            for (int i = 0; i < n - 1; i += 2) {
                if (last.isEmpty()) {
                    leftAtom = atom(parseTree.getChildren().get(0));
                } else {
                    leftAtom = last;
                    rightAtom = atom(parseTree.getChildren().get(i));
                }
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case DIVIDE:
                        numberedVariable = "%" + addNumberedVariable();
                        code.append(numberedVariable + " = sdiv i32 " + leftAtom + ", " + rightAtom + "\n");
                        break;
                    case TIMES:
                        numberedVariable = "%" + addNumberedVariable();
                        code.append(numberedVariable + " = mul i32 " + leftAtom + ", " + rightAtom + "\n");
                        break;
                    default:
                        throw new RuntimeException("Invalid op");
                }
                last = numberedVariable;
            }
            return last;
        }
        throw new RuntimeException("Invalid prod");
    }

    public int unaryMinus(ParseTree parseTree, int minusCounter) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isTerminal() && grandchild.getLabel().getType() == LexicalUnit.MINUS) {
                minusCounter++;
            } else if (grandchild.getLabel().isNonTerminal() && grandchild.getLabel().getValue().equals("Atom")) {
                unaryMinus(grandchild, minusCounter);
            } else {
                throw new RuntimeException("Invalid unary minus");
            }
        }
        return minusCounter;
    }

    public String atom(ParseTree parseTree) {
        ParseTree grandchild = parseTree.getChildren().get(0);
        String result = "";
        switch (grandchild.getLabel().getType()) {
            case MINUS:
                if (minusCounter == 0) { //pas calcluer
                    minusCounter = unaryMinus(parseTree, minusCounter);
                    System.out.println("Minus counter: " + minusCounter);
                }
                if (minusCounter % 2 == 0) {
                    result = atom(parseTree.getChildren().get(1));
                } else {
                    String numberedVariable = "%" + addNumberedVariable();
                    code.append(numberedVariable + " = sub i32 0, " + atom(parseTree.getChildren().get(1)) + "\n");
                    result = numberedVariable;
                }
                break;
            case LPAREN:
                result = exprarith(parseTree.getChildren().get(1));
                break;
            case VARNAME:
                result = "%"+ addNamedVariable(grandchild.getLabel().getValue().toString());
                break;
            case NUMBER:
                result = grandchild.getLabel().getValue().toString();
                break;
            default:
                throw new RuntimeException("Invalid atom");
        }
        return result;
    }

    public void if_1(ParseTree parseTree) {
        String boolValue = cond(parseTree.getChildren().get(1)); //<Cond>
        code.append("br i1 %" + boolValue + ", label %" + ifTrueLabel + ", label %" + ifFalseLabel + "\n");
        code.append(ifTrueLabel + ":\n");
        generateCode(parseTree.getChildren().get(3)); //<Instruction>
        code.append(ifFalseLabel + ":\n");
    }

    public void if_2(ParseTree parseTree) {
        String boolValue = cond(parseTree.getChildren().get(1)); //<Cond>
        instructionCounter++;
        code.append("br i1 %" + boolValue + ", label %" + ifTrueLabel + ", label %" + ifFalseLabel + "\n")
            .append(ifTrueLabel + ":\n");
        generateCode(parseTree.getChildren().get(3)); //<Instruction1>
        code.append(ifFalseLabel + ":\n");
        generateCode(parseTree.getChildren().get(5)); //<Instruction2>
    }

    public void if_(ParseTree parseTree) {
        switch (parseTree.getChildren().size()) {
            case 5:
                if_1(parseTree);
                break;
            case 6:
                if_2(parseTree);
                break;
            default:
                throw new RuntimeException("Invalid if");
        }
    }

    public String cond(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return conj(parseTree.getChildren().get(0));
        } else if (n >= 3 && n % 2 == 1) {
            String leftConj = "";
            String rightConj = "";
            String last = "";
            for (int i = 0; i < n - 1; i += 2) {
                if (last.isEmpty()) {
                    leftConj = conj(parseTree.getChildren().get(0));
                } else {
                    leftConj = last;
                    rightConj = conj(parseTree.getChildren().get(i));
                }
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case OR:
                        numberedVariable = addNumberedVariable();
                        code.append("%" + numberedVariable + "= or i32 " + leftConj + ", " + rightConj + "\n");
                        break;
                    default:
                        throw new RuntimeException("Invalid op");
                }
                last = numberedVariable;
            }
            return last;
        }
        throw new RuntimeException("Invalid cond");
    }

    private String conj(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return simplecond(parseTree.getChildren().get(0));
        } else if (n >= 3 && n % 2 == 1) {
            String leftSimpleCond = "";
            String rightSimpleCond = "";
            String last = "";
            for (int i = 0; i < n - 1; i += 2) {
                if (last.isEmpty()) {
                    leftSimpleCond = simplecond(parseTree.getChildren().get(0));
                } else {
                    leftSimpleCond = last;
                    rightSimpleCond = simplecond(parseTree.getChildren().get(i));
                }
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case AND:
                        numberedVariable = addNumberedVariable();
                        code.append("%" + numberedVariable + "= and i32 " + leftSimpleCond + ", " + rightSimpleCond + "\n");
                        break;
                    default:
                        throw new RuntimeException("Invalid op");
                }
                last = numberedVariable;
            }
            return last;
        }
        throw new RuntimeException("Invalid conj");
    }

    private String simplecond(ParseTree parseTree) {
        if (parseTree.getChildren().size() == 1) {
            return cond(parseTree.getChildren().get(0));
        } else {
            String leftComp = exprarith(parseTree.getChildren().get(0));
            String rightComp = exprarith(parseTree.getChildren().get(2));
            String numberedVariable = "";
            switch (parseTree.getChildren().get(1).getLabel().getType()) {
                case EQUAL:
                    numberedVariable = addNumberedVariable();
                    code.append("%" + numberedVariable + " = icmp eq i32 " + leftComp + ", " + rightComp + "\n");
                    return numberedVariable;
                case SMALLER:
                    numberedVariable = addNumberedVariable();
                    code.append("%" + numberedVariable + " = icmp slt i32 " + leftComp + ", " + rightComp + "\n");
                    return numberedVariable;
                default:
                    throw new RuntimeException("Invalid simplecond");
            }
        }
    }

    private void while_(ParseTree parseTree) {
        code.append("br label %" + whileLoopLabel + "\n")
            .append(whileLoopLabel + ":\n");
        String boolValue = cond(parseTree.getChildren().get(1));
        code.append("br i1 %" + boolValue + ", label %" + whileBodyLabel + ", label %" + WhileEndLabel + "\n")
            .append(whileBodyLabel + ":\n");
        instructionCounter++;
        generateCode(parseTree.getChildren().get(3));
        code.append("br label %" + whileLoopLabel + "\n")
            .append(WhileEndLabel + ":\n");
    }

    private void print(ParseTree parseTree) {
        String varname = parseTree.getChildren().get(2).getLabel().getValue().toString();
        if (namedVariables.contains(varname)) {
            String numberedVariable = addNumberedVariable();
            code.append("%" + numberedVariable + " = load i32, i32* %" + varname + "\n");
            code.append("call void @println(i32 %" + numberedVariable + ")\n");
        } else {
            code.append("call void @println(i32 %" + varname + ")\n");
        }
    }

    private void read(ParseTree parseTree) {
        String varname = parseTree.getChildren().get(2).getLabel().getValue().toString();
        code.append("%" + varname + " = call i32 @readInt()\n");
    }

    public String getCode() {   
        return code.toString();
    }
}
