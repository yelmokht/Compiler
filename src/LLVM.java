import java.util.ArrayList;
import java.util.function.Function;

public class LLVM {
    private AST ast;
    private StringBuilder code = new StringBuilder();
    private ArrayList<String> namedVariables = new ArrayList<>();
    private int numberedVariableCounter = 0;
    private int instructionCounter = 0;
    private int minusCounter = 0;

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

    public String program(ParseTree parseTree) {
        addReadFunction();
        addPrintFunction();
        code.append("define i32 @main() {\n"); //begin
        allocateVariables(parseTree);
        generateCode(parseTree.getChildren().get(1)); //<Code>
        code.append("ret i32 0\n");
        code.append("}\n"); //end
        return null;
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

    public String code(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isNonTerminal()) {
                generateCode(grandchild); //<Instruction>
            } else {
                //Throw error
            }
        }
        return null;
    }

    public String instlist(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isNonTerminal()) {
                generateCode(grandchild); //<Instruction>
            }
        }
        return null;
    }

    public String assign(ParseTree parseTree) {
        String namedVariable = parseTree.getChildren().get(0).getLabel().getValue().toString();
        addNamedVariable(namedVariable);
        String value = exprarith(parseTree.getChildren().get(2)); //<ExprArith>
        code.append("store i32 " + value + ", i32* %" + namedVariable + "\n");
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
                        numberedVariable = "%" + addNumberedVariable();
                        code.append(numberedVariable + " = add i32 " + leftProd + ", " + rightProd + "\n");
                        return numberedVariable;
                    case MINUS:
                        numberedVariable = "%" + addNumberedVariable();
                        code.append(numberedVariable + " = sub i32 " + leftProd + ", " + rightProd + "\n");
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
                        numberedVariable = "%" + addNumberedVariable();
                        code.append(numberedVariable + " = sdiv i32 " + leftAtom + ", " + rightAtom + "\n");
                        return numberedVariable;
                    case TIMES:
                        numberedVariable = "%" + addNumberedVariable();
                        code.append(numberedVariable + " = mul i32 " + leftAtom + ", " + rightAtom + "\n");
                        return numberedVariable;
                    default:
                        //Throw error
                        break;
                }
            }
        }
        return null;
    }

    public int unaryMinus(ParseTree parseTree, int minusCounter) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isTerminal() && grandchild.getLabel().getType() == LexicalUnit.MINUS) {
                minusCounter++;
            } else if (grandchild.getLabel().isNonTerminal() && grandchild.getLabel().getValue().equals("Atom")) {
                unaryMinus(grandchild, minusCounter);
            } else {
                //Throw error
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
                //Throw error
                break;
        }
        return result;
    }

    public String if_(ParseTree parseTree) {
        if (parseTree.getChildren().size() == 5) {
            String boolValue = cond(parseTree.getChildren().get(1)); //<Cond>
            String myLabelIfTrue = parseTree.getChildren().get(3).getLabel().getValue().toString(); //<Instruction>
            String myLabelIfFalse = "nextInstruction"; //TODO
            code.append("br i1 %" + boolValue + ", label %" + myLabelIfTrue + ", label %" + myLabelIfFalse + "\n");
            code.append(myLabelIfTrue + ":\n");
            generateCode(parseTree.getChildren().get(3)); //<Instruction>
            code.append("ret i32 0\n");
            code.append(myLabelIfFalse + ":\n");
        } else {
            String boolValue = cond(parseTree.getChildren().get(1)); //<Cond>
            String myLabelIfTrue = parseTree.getChildren().get(3).getLabel().getValue().toString() + "_" + instructionCounter; //<Instruction1>
            instructionCounter++;
            String myLabelIfFalse = parseTree.getChildren().get(5).getLabel().getValue().toString() + "_" + instructionCounter; //<Instruction2>
            instructionCounter++;
            code.append("br i1 %" + boolValue + ", label %" + myLabelIfTrue + ", label %" + myLabelIfFalse + "\n");
            code.append(myLabelIfTrue + ":\n");
            generateCode(parseTree.getChildren().get(3)); //<Instruction1>
            code.append("ret i32 0\n");
            code.append(myLabelIfFalse + ":\n");
            generateCode(parseTree.getChildren().get(5)); //<Instruction2>
            code.append("ret i32 0\n");
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
                        code.append("%" + numberedVariable + "= or i32 " + leftConj + ", " + rightConj + "\n");
                        return numberedVariable;
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
                        code.append("%" + numberedVariable + "= and i32 " + leftSimpleCond + ", " + rightSimpleCond + "\n");
                        return numberedVariable;
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
                    code.append("%" + numberedVariable + " = icmp eq i32 " + leftComp + ", " + rightComp + "\n");
                    return numberedVariable;
                case SMALLER:
                    numberedVariable = addNumberedVariable();
                    code.append("%" + numberedVariable + " = icmp slt i32 " + leftComp + ", " + rightComp + "\n");
                    return numberedVariable;
                default:
                    break;
            }
        }
        return null;
    }

    private String while_(ParseTree parseTree) {
        String myLabelWhileLoop = "whileLoop_" + instructionCounter;
        code.append("br label %" + myLabelWhileLoop + "\n");
        code.append(myLabelWhileLoop + ":\n");
        String cmp = cond(parseTree.getChildren().get(1)); //<Cond>
        String myLabelWhileBody = "body_" + instructionCounter;
        String myLabelWhileEnd = "end_" + instructionCounter;
        code.append("br i1 %" + cmp + ", label %" + myLabelWhileBody + ", label %" + myLabelWhileEnd + "\n");
        code.append(myLabelWhileBody + ":\n");
        instructionCounter++;
        generateCode(parseTree.getChildren().get(3)); //<Instruction>
        code.append("br label %" + myLabelWhileLoop + "\n");
        code.append(myLabelWhileEnd + ":\n");
        return null;
    }

    private String print(ParseTree parseTree) {
        String varname = parseTree.getChildren().get(2).getLabel().getValue().toString();
        code.append("call void @println(i32 %" + varname + ")\n");
        return null;
    }

    private String read(ParseTree parseTree) {
        String varname = parseTree.getChildren().get(2).getLabel().getValue().toString();
        code.append("%" + varname + " = call i32 @readInt()\n");
        return null;
    }

    public String getCode() {   
        return code.toString();
    }
}
