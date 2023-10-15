/*
First part - User code
If no javadoc comment, JFlex will generate one automatically
*/

/*
Second part - Options and declarations/macros
*/
%%

%class LexicalAnalyzer    //Generate class with the name LexicalAnalyzer and write the code in the file LexicalAnalyzer.java
%unicode 		//Enable unicode input
%char           //Enable char counting
%line 			//Enable line counting
%column 		//Enable column counting
%type Symbol    //Enable returned values of type Symbol as tokens
%standalone 	//Generate a scanner that is not called by a parser
%xstate YYINITIAL, LONG_COMMENT_STATE, SHORT_COMMENT_STATE

//Code copied verbatim into the generated class
%{
    private int firstOccurenceA;
    private int firstOccurenceB;
    private int firstOccurenceC;


%}

//Need to specify another end of file using %eofval because we used a user defined type (Symbol). The default value is null
//The code included in %eofval{ ... %eofval} will be copied verbatim into the scanning method and will be executed each time the end of file is reached.
//We don"t use %eof because it is executed once instead of each time
%eofval{
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

%eof{
    System.out.println("\nVariables");
    System.out.println("a " + firstOccurenceA);
    System.out.println("b " + firstOccurenceB);
    System.out.println("c " + firstOccurenceC);
%eof}

//Macros
AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha = {AlphaUpperCase}|{AlphaLowerCase}
Numeric = [0-9]
AlphaNumeric = {Alpha}|{Numeric}
EndOfLine = "\r"?"\n"
Space = "\t" | " "
Line = .*{EndOfLine}
Sign = [+-]
Integer = {Sign}?(([1-9][0-9]*)|0)
Decimal = \.[0-9]*
Exponent = [eE]{Integer}
Real = {Integer}{Decimal}?{Exponent}?
Identifier = {Alpha}{AlphaNumeric}*

%%

/*
Third part - Regular expression and actions

These regular expressions and actions are executed when the scanner matches the associated regular expression.
As the scanner reads its input, it keeps track of all regular expressions and activates the action of the expression
that has the longest match.
*/

<YYINITIAL> {
	"''" {yybegin(LONG_COMMENT_STATE);}
	"**" {yybegin(SHORT_COMMENT_STATE);}
}

<LONG_COMMENT_STATE> {
    . {}
    "''" {yybegin(YYINITIAL);}
}

<SHORT_COMMENT_STATE> {
    . {}
    {EndOfLine} {yybegin(YYINITIAL);}
}

{Alpha} {Symbol s = new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
{Numeric} {Symbol s = new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext()); System.out.println(s.toString()); return s;}
{Space} {}
{EndOfLine} {}
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
