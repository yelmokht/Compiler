import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/***
 * Test class for the grammar.
 */
public class TestGrammar {

    @Test
    public void checkGrammarIsLLK() throws IOException {
        Path directoryPath = Paths.get("test/resources/grammar/ll1");
        assertTrue("The directory does not exist", directoryPath.toFile().isDirectory());

        File[] files = directoryPath.toFile().listFiles();
        assert files != null && files.length > 0 : "The directory is empty";

        int k = 1;

        for (File file : files) {
            assertTrue("The file does not exist", file.isFile());

            ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar(file.getPath());
            ParseTools parseTools = new ParseTools();

            assertTrue("The grammar from file " + file.getName() + " is not LL(" + k + ")", parseTools.isGrammarLLK(contextFreeGrammar, k));
        }
    }

    @Test
    public void checkGrammarIsNotLLK() throws IOException {
        Path directoryPath = Paths.get("test/resources/grammar/not_ll1");
        assertTrue("The directory does not exist", directoryPath.toFile().isDirectory());

        File[] files = directoryPath.toFile().listFiles();
        assert files != null && files.length > 0 : "The directory is empty";

        int k = 1;

        for (File file : files) {
            assertTrue("The file does not exist", file.isFile());

            ContextFreeGrammar contextFreeGrammar = new ContextFreeGrammar(file.getPath());
            ParseTools parseTools = new ParseTools();

            assertFalse("The grammar from file " + file.getName() +  " is LL(" + k + ")", parseTools.isGrammarLLK(contextFreeGrammar, k));
        }
    }

}
