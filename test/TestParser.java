import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestParser {
    private static final String filePath = "test/resources/temp.pmp";
    private static PrintStream fileStream;

    @Before
    public void redirectStandardOutputStream() throws IOException {
        fileStream = new PrintStream(filePath);
        System.setOut(fileStream);
    }

    @After
    public void restoreStandardOutputStream() throws IOException {
        fileStream.close();
        System.setOut(System.out);
        Files.deleteIfExists(Paths.get(filePath));
    }
    @Test
    public void givenInputParsingIsAccepted() throws IOException {
        Path directoryPath = Paths.get("test/resources/parser/input");
        assertTrue("The directory does not exist", directoryPath.toFile().isDirectory());

        File[] files = directoryPath.toFile().listFiles();
        assert files != null && files.length > 0 : "The directory is empty";

        for (File file : files) {
            Main.main(new String[]{file.getPath()});

            Path actualFilePath = Paths.get("test/resources/parser/actual/left_most_derivation/lmd_" + file.getName());
            Files.copy(Paths.get(filePath), actualFilePath, StandardCopyOption.REPLACE_EXISTING);
            List<String> actualLines = Files.readAllLines(actualFilePath);
            String expectedFilePath = "test/resources/parser/expected/left_most_derivation/expected_lmd_" + file.getName();
            List<String> expectedLines = Files.readAllLines(Paths.get(expectedFilePath));
            assertEquals("The input word is not an element of L(G)", expectedLines, actualLines);
        }
    }

}
