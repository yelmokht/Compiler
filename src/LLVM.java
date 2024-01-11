import java.util.ArrayList;

/**
 * The LLVM class represents a code generator for LLVM intermediate representation.
 * It generates LLVM code based on an Abstract Syntax Tree (AST).
 */
public class LLVM {
    private AST ast;
    private StringBuilder code = new StringBuilder();
    private ArrayList<String> namedVariables = new ArrayList<>();
    private int numberedVariableCounter = 0;
    private int instructionCounter = 0;
    private int tabulation = 0;
    private String ifTrueLabel = "ifTrue_";
    private String ifFalseLabel =  "ifFalse_";
    private String ifEndLabel = "ifEnd_";
    private String whileLoopLabel = "whileLoop_";
    private String whileBodyLabel = "whileBody_";
    private String whileEndLabel = "whileEnd_";

    public LLVM(AST ast) {
        this.ast = ast;
        generateCode(ast);
    }

    /**
     * Generates LLVM code based on the given parse tree.
     * Depending of the label of the parse tree, a different function is called.
     * @param parseTree The parse tree representing the program.
     */
    public void generateCode(ParseTree parseTree) {
        switch (parseTree.getLabel().getValue().toString()) {
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

    /**
     * Appends the given code to the LLVM code with proper tabulation.
     * @param code the code to be added
     */
    public void addCode(String code) {
        for (int i = 0; i < tabulation; i++) {
            this.code.append("\t");
        }
        this.code.append(code);
    }

    /**
     * Adds a named variable to the LLVM code and returns the variable name.
     * If the variable is not already present, it is added to the list of named variables
     * and the corresponding LLVM code is generated.
     * @param varname the name of the variable to be added
     * @return the name of the added variable
     */
    public String addNamedVariable(String varname) {
        if(!this.namedVariables.contains(varname)){
            this.namedVariables.add(varname);
            addCode("%" + varname + " = alloca i32\n");
        }
        return varname;
    }

    /**
        * Increments the numberedVariableCounter and returns the incremented value as a String for local variables.
        * @return The incremented value of the numberedVariableCounter as a String.
        */
    public String addNumberedVariable() {
        numberedVariableCounter++;
        return String.valueOf(numberedVariableCounter);
    }

    /**
     * Adds a read function to the LLVM code.
     * The read function reads an integer from the input and returns it.
     */
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

    /**
     * Adds a print function to the LLVM code.
     * This function appends the necessary LLVM code to define a print function
     * that prints an integer value to the console.
     */
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

    /**
     * Recursively allocates variables from the given parse tree.
     * If a terminal node represents a variable name, it is added to the list of named variables.
     * If a non-terminal node is encountered, the method is called recursively on its children.
     * @param parseTree the parse tree to allocate variables from
     */
    public void allocateVariables(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isTerminal() && grandchild.getLabel().getType() == LexicalUnit.VARNAME) {
                addNamedVariable(grandchild.getLabel().getValue().toString());
            } else {
                allocateVariables(grandchild);
            }
        }
    }

    /**
     * Generates LLVM code for the rule <Program>.
     * @param parseTree The parse tree representing the program.
     */
    public void program(ParseTree parseTree) {
        addReadFunction();
        addPrintFunction();
        code.append("define i32 @main() {\n");
        tabulation++;
        allocateVariables(ast); //We need to allocate variables before generating code
        code(parseTree.getChildren().get(1));
        addCode("ret i32 0\n");
        tabulation--;
        addCode("}\n");
    }

    /**
     * Generates LLVM code for the rule <Code>.
     * @param parseTree The parse tree to generate code for.
     */
    public void code(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isNonTerminal()) {
                generateCode(grandchild);
            } else {
                throw new RuntimeException("Invalid code");
            }
        }
    }

    /**
     * Generates LLVM code for the rule <InstList>.
     * @param parseTree The parse tree to generate code for.
     */
    public void instlist(ParseTree parseTree) {
        for (ParseTree grandchild : parseTree.getChildren()) {
            if (grandchild.getLabel().isNonTerminal()) {
                generateCode(grandchild); //We call the function generateCode for each child that is an <Instruction>
            }
        }
    }

    /**
     * Generates LLVM code for the rule <Assign>.
     * @param parseTree The parse tree to generate code for.
     */
    public void assign(ParseTree parseTree) {
        String namedVariable = parseTree.getChildren().get(0).getLabel().getValue().toString();
        addNamedVariable(namedVariable);
        String value = exprarith(parseTree.getChildren().get(2));
        addCode("store i32 " + value + ", i32* %" + namedVariable + "\n");
    }

    /**
     * Generates LLVM code for the rule <ExprArith>.
     * @param parseTree The parse tree to generate code for.
     */
    public String exprarith(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return prod(parseTree.getChildren().get(0)); //<Prod>
        } else if (n >= 3 && n % 2 == 1) {
            String leftProd = "";
            String rightProd = "";
            String last = "";
            for (int i = 0; i < n - 1; i += 2) {
                if (last.isEmpty()) {
                    // If we are at the first iteration, we need to generate code for the first two children
                    leftProd = prod(parseTree.getChildren().get(i));
                    rightProd = prod(parseTree.getChildren().get(i+2));
                } else {
                    // If we are at the second iteration or more, we need to use the last numbered variable generated
                    leftProd = last;
                    rightProd = prod(parseTree.getChildren().get(i+2));
                }
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case PLUS:
                        numberedVariable = "%" + addNumberedVariable();
                        addCode(numberedVariable + " = add i32 " + leftProd + ", " + rightProd + "\n");
                        break;
                    case MINUS:
                        numberedVariable = "%" + addNumberedVariable();
                        addCode(numberedVariable + " = sub i32 " + leftProd + ", " + rightProd + "\n");
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

    /**
     * Generates LLVM code for the rule <Prod>.
     * @param parseTree The parse tree to generate code for.
     */
    public String prod(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return atom(parseTree.getChildren().get(0)); //<Atom>
        } else if (n >= 3 && n % 2 == 1) {
            // If we have a prod with more than one child, we need to generate code for each child and keep the last numbered variable generated
            String leftAtom = "";
            String rightAtom = "";
            String last = "";
            for (int i = 0; i < n - 1; i += 2) {
                if (last.isEmpty()) {
                    //If we are at the first iteration, we need to generate code for the first two children
                    leftAtom = atom(parseTree.getChildren().get(i));
                    rightAtom = atom(parseTree.getChildren().get(i+2));
                } else {
                    //If we are at the second iteration or more, we need to use the last numbered variable generated
                    leftAtom = last;
                    rightAtom = atom(parseTree.getChildren().get(i+2));
                }
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case DIVIDE:
                        numberedVariable = "%" + addNumberedVariable();
                        addCode(numberedVariable + " = sdiv i32 " + leftAtom + ", " + rightAtom + "\n");
                        break;
                    case TIMES:
                        numberedVariable = "%" + addNumberedVariable();
                        addCode(numberedVariable + " = mul i32 " + leftAtom + ", " + rightAtom + "\n");
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


    /**
     * Calculates the number of unary minus operators in the given parse tree.
     * 
     * @param parseTree The parse tree to analyze.
     * @param minusCounter The current count of unary minus operators.
     * @return The updated count of unary minus operators.
     */
    public int unaryMinus(ParseTree parseTree, int minusCounter) {
        //If we have a unary minus, we need to increment the counter and call the method recursively on the child for - <Atom>
        ParseTree grandchild = parseTree.getChildren().get(0);
        if (grandchild.getLabel().isTerminal() && grandchild.getLabel().getType() == LexicalUnit.MINUS) {
            minusCounter++;
            minusCounter = unaryMinus(parseTree.getChildren().get(1), minusCounter);
        }
        return minusCounter;
    }

    /**
     * This method takes a ParseTree as input and returns a String representing the atom.
     * The atom can be a unary minus, an expression enclosed in parentheses, a variable name, or a number.
     * @param parseTree The ParseTree representing the atom.
     * @return The String representation of the atom.
     * @throws RuntimeException if the atom is invalid.
     */
    public String atom(ParseTree parseTree) {
        ParseTree grandchild = parseTree.getChildren().get(0);
        String result = "";
        switch (grandchild.getLabel().getType()) {
            case MINUS:
                int minusCounter = 0;
                minusCounter = unaryMinus(parseTree, minusCounter);
                if (minusCounter % 2 == 1) {
                    //If the number of unary minus operators is odd, we need to substract the value from 0
                    String numberedVariable = "%" + addNumberedVariable();
                    ParseTree child = null;
                    for (int i = 0; i < minusCounter; i++) {
                        ParseTree tree = child == null ? parseTree : child;
                        child = tree.getChildren().get(1);
                    }
                    addCode(numberedVariable + " = sub i32 0, " + atom(child) + "\n");
                    result = numberedVariable;
                } else {
                    //If the number of unary minus operators is even, we can just return the value
                    ParseTree child = null;
                    for (int i = 0; i < minusCounter; i++) {
                        ParseTree tree = child == null ? parseTree : child;
                        child = tree.getChildren().get(1);
                    }
                    result = atom(child);

                }
                break;
            case LPAREN:
                result = exprarith(parseTree.getChildren().get(1));
                break;
            case VARNAME:
                String nameVariable = "%"+ addNamedVariable(grandchild.getLabel().getValue().toString());
                String numberedVariable = "%" + addNumberedVariable();
                addCode(numberedVariable + " = load i32, i32* " + nameVariable + "\n"); 
                result = numberedVariable;
                break;
            case NUMBER:
                result = grandchild.getLabel().getValue().toString();
                break;
            default:
                throw new RuntimeException("Invalid atom");
        }
        return result;
    }

    /**
     * Generates LLVM code for the rule <Cond>.
     * @param parseTree The parse tree to generate code for.
     * @return The last numbered variable generated.
     */
    public String cond(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return conj(parseTree.getChildren().get(0)); //<Conj>
        } else if (n >= 3 && n % 2 == 1) {
            //If we have a cond with more than one child, we need to generate code for each child and keep the last numbered variable generated
            String leftConj = "";
            String rightConj = "";
            String last = "";
            for (int i = 0; i < n - 1; i += 2) {
                if (last.isEmpty()) {
                    leftConj = conj(parseTree.getChildren().get(i));
                    rightConj = conj(parseTree.getChildren().get(i+2));
                } else {
                    leftConj = last;
                    rightConj = conj(parseTree.getChildren().get(i+2));
                }
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case OR:
                        numberedVariable = "%" + addNumberedVariable();
                        addCode(numberedVariable + " = or i1 " + leftConj + ", " + rightConj + "\n");
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

    /**
     * Generates LLVM code for the rule <Conj>.
     * @param parseTree The parse tree to generate code for.
     * @return The last numbered variable generated.
     */
    private String conj(ParseTree parseTree) {
        int n = parseTree.getChildren().size();
        if (n == 1) {
            return simplecond(parseTree.getChildren().get(0)); //<SimpleCond>
        } else if (n >= 3 && n % 2 == 1) {
            //If we have a conj with more than one child, we need to generate code for each child and keep the last numbered variable generated
            String leftSimpleCond = "";
            String rightSimpleCond = "";
            String last = "";
            for (int i = 0; i < n - 1; i += 2) {
                if (last.isEmpty()) {
                    leftSimpleCond = simplecond(parseTree.getChildren().get(i));
                    rightSimpleCond = simplecond(parseTree.getChildren().get(i+2));
                } else {
                    leftSimpleCond = last;
                    rightSimpleCond = simplecond(parseTree.getChildren().get(i+2));
                }
                String numberedVariable = "";
                switch (parseTree.getChildren().get(i+1).getLabel().getType()) {
                    case AND:
                        numberedVariable = "%" + addNumberedVariable();
                        addCode(numberedVariable + " = and i1 " + leftSimpleCond + ", " + rightSimpleCond + "\n");
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

    /**
     * Generates LLVM code for the rule <SimpleCond>.
     * @param parseTree The parse tree to generate code for.
     * @return The last numbered variable generated.
     */
    private String simplecond(ParseTree parseTree) {
        if (parseTree.getChildren().size() == 1) {
            return cond(parseTree.getChildren().get(0)); //<Cond>
        } else {
            String leftComp = exprarith(parseTree.getChildren().get(0));
            String rightComp = exprarith(parseTree.getChildren().get(2));
            String numberedVariable = "";
            switch (parseTree.getChildren().get(1).getLabel().getType()) {
                case EQUAL:
                    numberedVariable = "%" + addNumberedVariable();
                    addCode(numberedVariable + " = icmp eq i32 " + leftComp + ", " + rightComp + "\n");
                    return numberedVariable;
                case SMALLER:
                    numberedVariable = "%" + addNumberedVariable();
                    addCode(numberedVariable + " = icmp slt i32 " + leftComp + ", " + rightComp + "\n");
                    return numberedVariable;
                default:
                    throw new RuntimeException("Invalid simplecond");
            }
        }
    }

    /**
     * Generates LLVM code for the first <If>.
     * @param parseTree The parse tree to generate code for.
     */
    public void if_1(ParseTree parseTree) {
        String trueLabel = ifTrueLabel + instructionCounter;
        String endLabel = ifEndLabel + instructionCounter;
        String boolValue = cond(parseTree.getChildren().get(1)); //<Cond>
        addCode("br i1 " + boolValue + ", label %" + trueLabel + ", label %" + endLabel + "\n");
        addCode(trueLabel + ":\n");
        tabulation++;
        instructionCounter++;
        generateCode(parseTree.getChildren().get(3)); //<Instruction>
        addCode("br label %" + endLabel + "\n");
        tabulation--;
        addCode(endLabel + ":\n");
        tabulation++;
    }

    /**
     * Generates LLVM code for the second <If>.
     * @param parseTree The parse tree to generate code for.
     */
    public void if_2(ParseTree parseTree) {
        String trueLabel = ifTrueLabel + instructionCounter;
        String falseLabel = ifFalseLabel + instructionCounter;
        String endLabel = ifEndLabel + instructionCounter;
        String boolValue = cond(parseTree.getChildren().get(1)); //<Cond>
        addCode("br i1 " + boolValue + ", label %" + trueLabel + ", label %" + falseLabel + "\n");
        addCode(trueLabel + ":\n");
        tabulation++;
        instructionCounter++;
        generateCode(parseTree.getChildren().get(3)); //<Instruction1>
        addCode("br label %" + endLabel + "\n");
        tabulation--;
        addCode(falseLabel + ":\n");
        tabulation++;
        instructionCounter++;
        generateCode(parseTree.getChildren().get(5)); //<Instruction2>
        addCode("br label %" + endLabel + "\n");
        tabulation--;
        addCode(endLabel + ":\n");
        tabulation++;
    }

    /**
     * Generates LLVM code for the rule <If>.
     * @param parseTree The parse tree to generate code for.
     */
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

    /**
     * Generates LLVM code for the rule <While>.
     * @param parseTree The parse tree to generate code for.
     */
    private void while_(ParseTree parseTree) {
        String loopLabel = whileLoopLabel + instructionCounter;
        String bodyLabel = whileBodyLabel + instructionCounter;
        String endLabel = whileEndLabel + instructionCounter;
        addCode("br label %" + loopLabel + "\n");
        if (tabulation > 1) {
            tabulation--;
        }
        addCode(loopLabel + ":\n");
        tabulation++;
        String boolValue = cond(parseTree.getChildren().get(1)); //We need to generate code for the condition
        addCode("br i1 " + boolValue + ", label %" + bodyLabel + ", label %" + endLabel + "\n");
        tabulation--;
        addCode(bodyLabel+ ":\n");
        tabulation++;
        instructionCounter++;
        generateCode(parseTree.getChildren().get(3)); //We need to generate code for the instruction
        addCode("br label %" + loopLabel + "\n");
        tabulation--;
        addCode(endLabel + ":\n");
        tabulation++;
    }

    /**
     * Generates LLVM code for the rule <Print>.
     * @param parseTree The parse tree to generate code for.
     */
    private void print(ParseTree parseTree) {
        String varname = parseTree.getChildren().get(2).getLabel().getValue().toString();
        if (namedVariables.contains(varname)) {
            //Need to load the variable in i32 from i32*
            String numberedVariable = addNumberedVariable();
            addCode("%" + numberedVariable + " = load i32, i32* %" + varname + "\n");
            addCode("call void @println(i32 %" + numberedVariable + ")\n");
        } else {
            addCode("call void @println(i32 %" + varname + ")\n");
        }
    }

    /**
     * Generates LLVM code for the rule <Read>.
     * @param parseTree The parse tree to generate code for.
     */
    private void read(ParseTree parseTree) {
        String varname = parseTree.getChildren().get(2).getLabel().getValue().toString();
        String numberedVariable = addNumberedVariable();
        addCode("%" + numberedVariable + " = call i32 @readInt()\n");
        addCode("store i32 %" + numberedVariable + ", i32* %" + varname + "\n");
    }

    /**
     * Returns the generated LLVM code.
     * @return The generated LLVM code.
     */
    public String getCode() {   
        return code.toString();
    }
}
