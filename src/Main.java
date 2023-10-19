import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                throw new IllegalArgumentException("Insufficient arguments. Usage: java MyClass arg1");
            }
            String arg1 = args[0];
            File inputFile = new File(arg1);
            String[] argv = new String[] {inputFile.getPath()};
            LexicalAnalyzer.main(argv);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}