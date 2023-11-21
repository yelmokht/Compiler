import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

public class TestActionTable {

    @Test
    public void constructedActionTableMatchExpectedActionTable() throws IOException {
        Path directoryPath = Paths.get("test/resources/grammar/ll1");
        assertTrue("The directory does not exist", directoryPath.toFile().isDirectory());

        File[] files = directoryPath.toFile().listFiles();
        assertNotNull("The directory is empty", files);

        int k = 1;

        for (File file : files) {
            assertTrue("The file does not exist", file.isFile());
            String expectedActionTableFilePath = "test/resources/action_table/expected/expected_action_table_" + file.getName();
            assertTrue("The file does not exist", new File(expectedActionTableFilePath).isFile());

            ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar(file.getPath());
            ParseTools parseTools = new ParseTools();

            parseTools.isGrammarLLK(contextFreeGrammar, k);
            parseTools.constructLL1ActionTable(contextFreeGrammar);

            String actualActionTableFilePath = "test/resources/action_table/actual/action_table_" + file.getName();
            parseTools.printActionTable(contextFreeGrammar, actualActionTableFilePath);
            assertTrue("The file does not exist", new File(actualActionTableFilePath).isFile());

            List<String> outputLines = Files.readAllLines(Paths.get(actualActionTableFilePath));
            List<String> expectedLines = Files.readAllLines(Paths.get(expectedActionTableFilePath));

            assertEquals("The constructed action table is not correct", expectedLines, outputLines);
        }
    }

}
