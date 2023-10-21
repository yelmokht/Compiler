import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * TestLexicalAnalyzer class is used to test if the lexical analyzer implemented has the expected output.
 */
public class TestLexicalAnalyzer {
    private static final String tempFilePath = "test/resources/temp.pmp";
    private static PrintStream tempStream;
    @Before
    public void redirectStandardOutputStream() throws FileNotFoundException {
        tempStream = new PrintStream(tempFilePath);
        System.setOut(tempStream);
    }

    @After
    public void restoreStandardOutputStream() throws IOException {
        tempStream.close();
        Files.deleteIfExists(Paths.get(tempFilePath));
        System.setOut(System.out);
    }

    @Test
    public void testEuclid() throws IOException {
        String inputFilePath = "test/resources/input/euclid.pmp";
        String expectedOutputFilePath = "test/resources/output/euclid.out";
        LexicalAnalyzer.main(new String[]{inputFilePath});
        List<String> actualLines = Files.readAllLines(Paths.get(tempFilePath));
        List<String> expectedLines = Files.readAllLines(Paths.get(expectedOutputFilePath));
        assertEquals("Lines do not match.", expectedLines, actualLines);
    }
}
