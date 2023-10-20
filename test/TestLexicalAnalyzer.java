import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;

public class TestLexicalAnalyzer {

    @Test
    public void testTest1File() throws IOException {
        File input = new File("test/resources/euclid.pmp");
        File output = new File("test/resources/output.pmp");
        File expectedOutput = new File("test/resources/euclid.out");
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);
        String[] argv = new String[] {input.getPath()};
        LexicalAnalyzer.main(argv);
        printStream.close();
        fileOutputStream.close();
        List<String> actualLines = Files.readAllLines(output.toPath());
        List<String> expectedLines = Files.readAllLines(expectedOutput.toPath());
        assert actualLines.equals(expectedLines) : "Lines do not match.";
    }
}

