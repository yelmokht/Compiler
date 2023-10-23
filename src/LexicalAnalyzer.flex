import java.util.*;

/**
 * LexicalAnalyzer class generates a lexical analyzer with a defined language to process input files.
 * This class recognizes keywords, operators, variables, numbers, handles comments, and reports unrecognized symbols.
 */

%%

%class LexicalAnalyzer      //Generate class with the name LexicalAnalyzer.java
%unicode 		            //Enable unicode input
%char                       //Enable char counting
%line 			            //Enable line counting
%column 		            //Enable column counting
%type Symbol                //Enable returned values of type Symbol as tokens
%standalone 	            //Generate a scanner that is not called by a parser
%xstate YYINITIAL, LONG_COMMENT_STATE, SHORT_COMMENT_STATE, EXIT_STATE

//Java code
%{
    private int lastLineComment;
    private final List<Symbol> variables = new ArrayList<>();

    private boolean containsValue(List<Symbol> list, Symbol symbol) {
        for (Symbol s : list) {
            if (Objects.equals(s.getValue(), symbol.getValue())) {
                return true;
            }
        }
        return false;
    }

    private void alpha(Symbol s) {
        if (!containsValue(variables, s)) {
            variables.add(s);
        }
    }
    private void exit() {
        System.out.println("Exiting...");
        yybegin(EXIT_STATE);
    }
%}

//The code in %eof is reached when EOF is reached
%eof{
    if (yystate() == LONG_COMMENT_STATE) {
        System.err.println("Unclosed comment detected at line : " + lastLineComment);
    }
    if (yystate()==YYINITIAL){
        System.out.println("\nVariables");
        for (Symbol variable : variables) {
            System.out.println(variable.getValue() + "\t" + variable.getLine());
        }
    }
%eof}

//After %eof, we go to the %eofval. Since we used a user defined type (Symbol), we need to return this type at the end.
%eofval{
    System.out.println(""); //To match euclid.out space at the end
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

//Macros
AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha = {AlphaUpperCase}|{AlphaLowerCase}
Numeric = [0-9]
AlphaNumeric = {Alpha}|{Numeric}
Variable = {AlphaLowerCase}{AlphaNumeric}*
Number = {Numeric}+
Space = "\t" | " "
EndOfLine = "\r"?"\n"

%%

//States
<YYINITIAL> {
	"''" {lastLineComment = yyline + 1; yybegin(LONG_COMMENT_STATE);}
	"**" {yybegin(SHORT_COMMENT_STATE);}
}

<LONG_COMMENT_STATE> {
    .|{EndOfLine} {}
    "''" {yybegin(YYINITIAL);}
}

<SHORT_COMMENT_STATE> {
    . {}
    {EndOfLine} {yybegin(YYINITIAL);}
}

<EXIT_STATE> {
    . {}
    {EndOfLine} {}
}

//Regular expressions
"begin" {Symbol s = new Symbol(LexicalUnit.BEG, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"end" {Symbol s = new Symbol(LexicalUnit.END, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"..." {Symbol s = new Symbol(LexicalUnit.DOTS, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
":=" {Symbol s = new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"(" {Symbol s = new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
")" {Symbol s = new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"-" {Symbol s = new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"+" {Symbol s = new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"*" {Symbol s = new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"/" {Symbol s = new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"if" {Symbol s = new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"then" {Symbol s = new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"else" {Symbol s = new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"AND" {Symbol s = new Symbol(LexicalUnit.AND, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"OR" {Symbol s = new Symbol(LexicalUnit.OR, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"[" {Symbol s = new Symbol(LexicalUnit.LBRACK, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"]" {Symbol s = new Symbol(LexicalUnit.RBRACK, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"=" {Symbol s = new Symbol(LexicalUnit.EQUAL, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"<" {Symbol s = new Symbol(LexicalUnit.SMALLER, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"while" {Symbol s = new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"do" {Symbol s = new Symbol(LexicalUnit.DO, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"print" {Symbol s = new Symbol(LexicalUnit.PRINT, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
"read" {Symbol s = new Symbol(LexicalUnit.READ, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
{Variable} {Symbol s = new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext()); alpha(s); System.out.println(s.toString()); return s;}
{Number} {Symbol s = new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
{Space} {}
{EndOfLine} {}
. {System.out.println("Unrecognized symbol: " + yytext());exit();}