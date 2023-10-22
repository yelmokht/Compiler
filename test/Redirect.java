import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Redirect class initiates the lexical analyzer by reading the specified file and
 * redirect the sequence of matched lexical units along with the symbol table content to a file.
 */
public class Redirect {
    private static final String inputFilePath = "test/resources/input/maximum.pmp";
    private static final String outputFilePath = "test/resources/output/maximum.out";
    private static PrintStream outputStream;
    public static void main(String[] args) throws IOException {
        redirectStandardOutputStream();
        LexicalAnalyzer.main(new String[]{inputFilePath});
        restoreStandardOutputStream();
    }

    public static void redirectStandardOutputStream() throws FileNotFoundException {
        outputStream = new PrintStream(outputFilePath);
        System.setOut(outputStream);
    }

    public static void restoreStandardOutputStream() {
        outputStream.close();
        System.setOut(System.out);
    }
}
