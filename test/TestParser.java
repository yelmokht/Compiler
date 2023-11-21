import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for the parser.
 */
public class TestParser {

    @Test
    public void givenInputParsingIsAccepted() throws IOException {
        // Get the directory path and check if the directory exists
        Path directoryPath = Paths.get("test/resources/parser/input");
        assertTrue("The directory does not exist", directoryPath.toFile().isDirectory());

        // Get all files from a directory and check if the directory is not empty
        File[] files = directoryPath.toFile().listFiles();
        assertNotNull("The directory is empty", files);

        for (File file : files) {
            String basename = file.getName().substring(0, file.getName().indexOf("."));
            String treeFile = "test/resources/parser/parse_tree/" + basename + ".tex";
            String lmdFile = "test/resources/parser/left_most_derivation/lmd_" + file.getName();

            // Redirect System.out to a file
            try (PrintStream printStream = new PrintStream(new FileOutputStream(lmdFile))) {
                System.setOut(printStream);

                String[] args = new String[]{"-wt", treeFile, file.getPath()};
                Main.main(args);
            }
            System.out.println();
        }
    }

}
