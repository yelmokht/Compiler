public class LLVM {
    private AST ast;
    private StringBuilder code;
    private int varCount = 0;

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
                    generateCode(child.getChildren().get(2));
                        String varName = child.getChildren().get(0).getLabel().getValue().toString();
                        code.append("%" + varName + " = alloca i32\n");
                        code.append("store i32 %" + varCount + ", i32* %" + varName + "\n");
                        varCount++;
                    
                    break;
                case "If":
                    break;
                case "While":
                    break;
                case "Print":
                    for (ParseTree grandchild : child.getChildren()) {
                        if (grandchild.getLabel().getType().equals(LexicalUnit.VARNAME)) {
                            String varName1 = grandchild.getLabel().getValue().toString();
                            code.append("call void @println(i32 %" + varName1 + ")\n"); //check if %varName exist ? + don't forder to add println() function
                        } else {
                            //TODO: throw exception
                        }
                    }
                    break;
                case "Read":
                    for (ParseTree grandchild : child.getChildren()) {
                        if (grandchild.getLabel().getType().equals(LexicalUnit.VARNAME)) {
                            String varName2 = grandchild.getLabel().getValue().toString();
                            code.append("%" + varName2 + " = call i32 @readInt()\n"); //Don"t forget to add readInt() function
                        } else {
                            //TODO: throw exception
                        }
                    }
                    break;
                case "ExprArith":
                    // Assume ExprArith has two children: an operator and an operand
                    String operator = child.getChildren().get(1).getLabel().getValue().toString();
                    String operand1 = child.getChildren().get(0).getLabel().getValue().toString();
                    String operand2 = child.getChildren().get(2).getLabel().getValue().toString();
                
                    switch (operator) {
                        case "+":
                            code.append("load i32, i32* %" + operand1 + "\n");
                            code.append("add i32 %" + operand1 + ", %" + operand2 + "\n");
                            break;
                        case "-":
                            code.append("sub i32 %" + operand1 + ", %" + operand2 + "\n");
                            break;
                        // Add more cases as needed
                    }
                    break;
                    
                case "Prod":
                    for (ParseTree grandchild : child.getChildren()) {
                        if(grandchild.getLabel().getValue().toString().equals("Atom")){
                            generateCode(grandchild);
                        }
                        else{
                            LexicalUnit prodString = grandchild.getLabel().getType();
                            switch(prodString){
                                case TIMES:
                                    code.append("mul i32 %" + (varCount-1) + ", %" + varCount + "\n");
                                break;
                                case DIVIDE:
                                    code.append("sdiv i32 %" + (varCount-1) + ", %" + varCount + "\n");
                                break;
                        }
                    }


                    }
                    break;
                case "Atom":
                    if(child.getChildren().get(0).getLabel().getType().equals(LexicalUnit.NUMBER)){
                        String varNumber = child.getChildren().get(0).getLabel().getValue().toString();
                        code.append("%" + varCount + " = alloca i32\n");
                        code.append("store i32 "+varNumber+", i32* %" + varCount + "\n");
                        varCount++;
                    }
                    else if (child.getChildren().get(0).getLabel().getType().equals(LexicalUnit.VARNAME)){
                        String varName3 = child.getChildren().get(0).getLabel().getValue().toString();
                        code.append("%" + varCount + " = alloca i32\n");
                        code.append("store i32 %"+varName3+", i32* %" + varCount + "\n");
                        varCount++;
                    }
                    else if (child.getChildren().get(1).getLabel().getValue().toString().equals("ExprArith")){
                        generateCode(child);
                    }
                    else if (child.getChildren().get(0).getLabel().getType().equals(LexicalUnit.MINUS)){
                        //TODO: throw exception
                    }

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

    public String getCode() {
        return code.toString();
    }
}
