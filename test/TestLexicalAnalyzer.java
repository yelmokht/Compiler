import org.junit.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestLexicalAnalyzer {

    @Test
    public void testTest1File() throws IOException {
        String inputFilePath = "test/resources/euclid.pmp";
        String outputFilePath = "test/resources/output.pmp";
        String expectedOutputFilePath = "test/resources/euclid.out";
        try (PrintStream printStream = new PrintStream(outputFilePath)) {
            System.setOut(printStream);
            LexicalAnalyzer.main(new String[]{inputFilePath});
        }
        List<String> actualLines = Files.readAllLines(Paths.get(outputFilePath));
        List<String> expectedLines = Files.readAllLines(Paths.get(expectedOutputFilePath));
        assertEquals("Lines do not match.", expectedLines, actualLines);
    }
}
