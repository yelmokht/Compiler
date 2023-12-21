import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * Project Part 2: Parser
 *
 * @author Marie Van Den Bogaard, Léo Exibard, Gilles Geeraerts, Sarah Winter, edited by Mathieu Sassolas
 *
 */

public class Main{
    /**
     *
     * The parser
     *
     * @param args  The argument(s) given to the program
     * @throws IOException java.io.IOException if an I/O-Error occurs
     * @throws FileNotFoundException java.io.FileNotFoundException if the specified file does not exist
     *
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, SecurityException, Exception {
        // Display the usage when no arguments are given
        if(args.length == 0){
            System.out.println("Usage:  java -jar part3.jar [FILE]\n"
                               + "\tFILE:\n"
                               + "\tA .ppm file containing a PascalMaisPresque program\n"
                               );
            System.exit(0);
        } else {
            FileReader codeSource = null;
            try {
                codeSource = new FileReader(args[args.length-1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ParseTree parseTree = null;
            Parser parser = new Parser(codeSource);
            try {
                parseTree = parser.parse();
                AST ast = new AST(parseTree);
                LLVM llvm = new LLVM(ast);
                System.out.print(llvm.getCode());
            } catch (ParseException e) {
                System.out.println("Error:> " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error:> " + e);
            }
        }
    }
}
