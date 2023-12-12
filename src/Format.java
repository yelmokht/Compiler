/**
 * This class defines characters that correspond to the grammar format in a file and their LaTeX equivalents
 * used for the parse tree in the output file.
 * <p></p>
 * Each rule must follow the format :
 * <p></p>
 * [ruleNumber] {@code <variable>} → symbol1 {@code <variable1>} symbol2 {@code <variable2>} ...
 * <p></p>
 * where [ruleNumber] is the rule number, {@code <variable1>} {@code <variable2>} ... are variables symbols,
 * and symbol1 symbol2 ... are terminals symbols.
 * <p></p>
 */
public class Format {
    // Square brackets for enumeration of the rules
    public static final String LEFT_BRACKET = "[";
    public static final String RIGHT_BRACKET = "]";

    // Variable markers and their LaTeX equivalents
    public static final String START_VARIABLE = "<";
    public static final String START_VARIABLE_LATEX = "\\textless ";
    public static final String FINISH_VARIABLE = ">";
    public static final String FINISH_VARIABLE_LATEX = "\\textgreater ";

    // Arrow and delimiter for rule representation
    public static final String ARROW = "→";
    public static final String DELIMITER = " ";

    // Epsilon symbol and its LaTeX equivalent for the empty word
    public static final String EPSILON = "ε";
    public static final String EPSILON_LATEX = "$\\varepsilon$ ";

    // Smaller than symbol and its LaTeX equivalent
    public static final String SMALLER = "<";
    public static final String SMALLER_LATEX = "\\textless ";

    public static final String LBRACK = "{";
    public static final String LBRACK_LATEX = "\\texttt{\\{} ";
    public static final String RBRACK = "}";
    public static final String RBRACK_LATEX = "\\texttt{\\}} ";

    // If another special character is needed for a different grammar with different characters, it can be added here
    // If the format of the grammar is different, it can be changed here
}