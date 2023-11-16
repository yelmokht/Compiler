import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestParser {

    @Test
    public void checkGrammarIsLL1() throws IOException {
        String inputFilePath = "test/resources/CFG.pmp";
        assertTrue("The file does not exist", new File(inputFilePath).isFile());
        ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar(inputFilePath);
        ParseTools parseTools = new ParseTools();
        int k = 1;
        assertTrue("The grammar is not LL(" + k + ")", parseTools.isGrammarLLK(contextFreeGrammar, k));
    }

    @Test
    public void constructedActionTableMatchExpectedActionTable() throws IOException {
        String inputFilePath = "test/resources/CFG.pmp";
        String expectedActionTableFilePath = "test/resources/expectedActionTable.pmp";
        assertTrue("The file does not exist", new File(expectedActionTableFilePath).isFile());
        ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar(inputFilePath);
        ParseTools parseTools = new ParseTools();
        int k = 1;
        parseTools.isGrammarLLK(contextFreeGrammar, k);
        parseTools.constructLL1ActionTableFromCFG(contextFreeGrammar);
        String outputActionTableFilePath = "test/resources/actionTable.pmp";
        parseTools.printActionTable(contextFreeGrammar, outputActionTableFilePath);
        assertTrue("The file does not exist", new File(outputActionTableFilePath).isFile());
        List<String> actionTableLines = Files.readAllLines(Paths.get(outputActionTableFilePath));
        List<String> expectedActionTableLines = Files.readAllLines(Paths.get(expectedActionTableFilePath));
        assertEquals("The constructed action table is not correct", actionTableLines, expectedActionTableLines);
    }

    @Test
    public void givenWordParsingIsAccepted() throws IOException {
        String inputFilePath = "test/resources/CFG.pmp";
        String expectedActionTableFilePath = "test/resources/expectedActionTable.pmp";
        assertTrue("The file does not exist", new File(inputFilePath).isFile());
        assertTrue("The file does not exist", new File(expectedActionTableFilePath).isFile());
        ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar(inputFilePath);
        ParseTools parseTools = new ParseTools();
        int k = 1;
        parseTools.isGrammarLLK(contextFreeGrammar, k);
        parseTools.constructLL1ActionTableFromCFG(contextFreeGrammar);
    }

}
