import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void givenEuclidMatchExpectedOutput() throws IOException {
        String inputFilePath = "test/resources/input/euclid.pmp";
        String expectedOutputFilePath = "test/resources/output/euclid.out";
        assertTrue("The file does not exist", new File(inputFilePath).isFile());
        assertTrue("The file does not exist", new File(expectedOutputFilePath).isFile());
        LexicalAnalyzer.main(new String[]{inputFilePath});
        List<String> actualLines = Files.readAllLines(Paths.get(tempFilePath));
        List<String> expectedLines = Files.readAllLines(Paths.get(expectedOutputFilePath));
        assertEquals("The content between the two files does not correspond", expectedLines, actualLines);
    }

    @Test
    public void givenFactorialMatchExpectedOutput() throws IOException {
        String inputFilePath = "test/resources/input/factorial.pmp";
        String expectedOutputFilePath = "test/resources/output/factorial.out";
        assertTrue("The file does not exist", new File(inputFilePath).isFile());
        assertTrue("The file does not exist", new File(expectedOutputFilePath).isFile());
        LexicalAnalyzer.main(new String[]{inputFilePath});
        List<String> actualLines = Files.readAllLines(Paths.get(tempFilePath));
        List<String> expectedLines = Files.readAllLines(Paths.get(expectedOutputFilePath));
        assertEquals("The content between the two files does not correspond", expectedLines, actualLines);
    }

    @Test
    public void givenMaximumMatchExpectedOutput() throws IOException {
        String inputFilePath = "test/resources/input/maximum.pmp";
        String expectedOutputFilePath = "test/resources/output/maximum.out";
        assertTrue("The file does not exist", new File(inputFilePath).isFile());
        assertTrue("The file does not exist", new File(expectedOutputFilePath).isFile());
        LexicalAnalyzer.main(new String[]{inputFilePath});
        List<String> actualLines = Files.readAllLines(Paths.get(tempFilePath));
        List<String> expectedLines = Files.readAllLines(Paths.get(expectedOutputFilePath));
        assertEquals("The content between the two files does not correspond", expectedLines, actualLines);
    }

    @Test
    public void givenPrimeMatchExpectedOutput() throws IOException {
        String inputFilePath = "test/resources/input/prime.pmp";
        String expectedOutputFilePath = "test/resources/output/prime.out";
        assertTrue("The file does not exist", new File(inputFilePath).isFile());
        assertTrue("The file does not exist", new File(expectedOutputFilePath).isFile());
        LexicalAnalyzer.main(new String[]{inputFilePath});
        List<String> actualLines = Files.readAllLines(Paths.get(tempFilePath));
        List<String> expectedLines = Files.readAllLines(Paths.get(expectedOutputFilePath));
        assertEquals("The content between the two files does not correspond", expectedLines, actualLines);
    }
}
