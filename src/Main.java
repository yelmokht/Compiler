/**
 * Main class initiates the lexical analyzer by reading the specified file and
 * outputting the sequence of matched lexical units along with the symbol table content on stdout.
 */
public class Main {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                throw new IllegalArgumentException("Insufficient arguments. Usage: java Main <input_file>");
            }
            String inputFile = args[0];
            LexicalAnalyzer.main(new String[]{inputFile});
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
