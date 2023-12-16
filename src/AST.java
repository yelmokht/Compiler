import java.util.ArrayList;
import java.util.List;

public class AST {
    private ParseTree parseTree;
    private Symbol label;
    private List<ParseTree> children;
    private Symbol current;

    public AST(ParseTree parseTree) {
        this.parseTree = parseTree;
        this.label = parseTree.getLabel();
        this.children = generateAST(parseTree);
    }
    
    public List<ParseTree> generateAST(ParseTree parseTree) {
        List<ParseTree> children = new ArrayList<ParseTree>();
        for (ParseTree child : parseTree.getChildren()) {
            if (child.getChildren().isEmpty()) {
                //Terminals
                current = child.getLabel();
                switch (current.getType()) {
                    case EPSILON:
                        break;
                    case LBRACK:
                        break;
                    case RBRACK:
                        break;
                    default:
                        children.add(child);
                }
            } else {
                //Variables
                current = child.getLabel();
                switch (current.getValue().toString()) {
                    case "Instruction":
                        for (ParseTree grandchild : generateAST(child)) {
                            children.add(grandchild);
                        }
                        break;
                    case "InstListTail":
                        for (ParseTree grandchild : generateAST(child)) {
                            children.add(grandchild);
                        }
                        break;
                    case "ExprArith":
                        for (ParseTree grandchild : generateAST(child)) {
                            children.add(grandchild);
                        }
                        break;
                    case "ExprArith'":
                        for (ParseTree grandchild : generateAST(child)) {
                            children.add(grandchild);
                        }
                        break;
                    case "Prod":
                        for (ParseTree grandchild : generateAST(child)) {
                            children.add(grandchild);
                        }
                        break;
                    case "Prod'":
                        for (ParseTree grandchild : generateAST(child)) {
                            children.add(grandchild);
                        }
                        break;
                    case "IfTail":
                        for (ParseTree grandchild : generateAST(child)) {
                            children.add(grandchild);
                        }
                        break;
                    case "Cond'":
                        for (ParseTree grandchild : generateAST(child)) {
                            children.add(grandchild);
                        }
                        break;
                    case "Conj'":
                        for (ParseTree grandchild : generateAST(child)) {
                            children.add(grandchild);
                        }
                        break;
                    case "Comp":
                        for (ParseTree grandchild : generateAST(child)) {
                            children.add(grandchild);
                        }
                        break;
                    default:
                        ParseTree parent = new ParseTree(current, generateAST(child));
                        children.add(parent);
                }
            }
        }
        return children;
    }

    /* Pure LaTeX version (using the forest package) */
    /**
     * Writes the tree as LaTeX code.
     * 
     * @return the String representation of the tree as LaTeX code.
     */
    public String toLaTexTree() {
        StringBuilder treeTeX = new StringBuilder();
        treeTeX.append("[");
        treeTeX.append("{" + label.toTexString() + "}");   // Implement this yourself in Symbol.java
        treeTeX.append(" ");

        for (ParseTree child : children) {
            treeTeX.append(child.toLaTexTree());
        }
        treeTeX.append("]");
        return treeTeX.toString();
    }

    /**
     * Writes the tree as a forest picture. Returns the tree in forest enviroment using the LaTeX code of the tree.
     * 
     * @return the String representation of the tree as forest LaTeX code.
     */
    public String toForestPicture() {
        return "\\begin{forest}for tree={rectangle, draw, l sep=20pt}" + toLaTexTree() + ";\n\\end{forest}";
    }

    /**
     * Writes the tree as a LaTeX document which can be compiled using PDFLaTeX.
     * 
     * This method uses the forest package.
     * <br>
     * <br>
     * The result can be used with the command:
     * 
     * <pre>
     * pdflatex some-file.tex
     * </pre>
     * 
     * @return a String of a full LaTeX document (to be compiled with pdflatex)
     */
    public String toLaTeXusingForest() {
        return "\\documentclass[border=5pt]{standalone}\n\n\\usepackage{forest}\n\n\\begin{document}\n\n" +
                toForestPicture()
                + "\n\n\\end{document}\n%% Local Variables:\n%% TeX-engine: lualatex\n%% End:";
    }

    /* Tikz version (using graphs and graphdrawing libraries, with GD library trees, requiring LuaLaTeX) */
    /**
     * Writes the tree as TikZ code. TikZ is a language to specify drawings in LaTeX files.
     * 
     * @return the String representation of the tree as TikZ code.
     */
    public String toTikZ() {
        StringBuilder treeTikZ = new StringBuilder();
        treeTikZ.append("node {");
        treeTikZ.append(label.toTexString());  // Implement this yourself in Symbol.java
        treeTikZ.append("}\n");
        for (ParseTree child : children) {
            treeTikZ.append("child { ");
            treeTikZ.append(child.toTikZ());
            treeTikZ.append(" }\n");
        }
        return treeTikZ.toString();
    }

    /**
     * Writes the tree as a TikZ picture. A TikZ picture embeds TikZ code so that LaTeX undertands it.
     * 
     * @return the String representation of the tree as a TikZ picture.
     */
    public String toTikZPicture() {
        return "\\begin{tikzpicture}[tree layout,every node/.style={draw,rounded corners=3pt}]\n\\" + toTikZ() + ";\n\\end{tikzpicture}";
    }

    /**
     * Writes the tree as a LaTeX document which can be compiled using LuaLaTeX.
     * 
     * This method uses the Tikz package.
     * <br>
     * <br>
     * The result can be used with the command:
     * 
     * <pre>
     * lualatex some-file.tex
     * </pre>
     * 
     * @return a String of a full LaTeX document (to be compiled with lualatex)
     */
    public String toLaTeXusingTikz() {
        return "\\documentclass[border=5pt]{standalone}\n\n\\usepackage{tikz}\\usetikzlibrary{graphs,graphdrawing}\\usegdlibrary{trees}\n\n\\begin{document}\n\n" +
                toTikZPicture()
                + "\n\n\\end{document}\n%% Local Variables:\n%% TeX-engine: lualatex\n%% End:";
    }

    /* Alias */
    /**
     * Writes the tree as a LaTeX document which can be compiled using LuaLaTeX.
     * 
     * This is an alias of {@link toLaTeXusingForest() toLaTeXusingForest}.
     * <br>
     * <br>
     * The result can be used with the command:
     * 
     * <pre>
     * pdflatex some-file.tex
     * </pre>
     * 
     * @return a String of a full LaTeX document (to be compiled with pdflatex)
     */
    public String toLaTeX() {
        return this.toLaTeXusingForest();
    }
}
