/*
First part - User code
If no javadoc comment, JFlex will generate one automatically
*/

/*
Second part - Options and declarations/macros
*/
%%

%class Lexer    //Generate class with the name Lexer and write the code in the file Lexer.java
%unicode 		//Enable unicode input
%char           //Enable char counting
%line 			//Enable line counting
%column 		//Enable column counting
%type Symbol    //Enable returned values of type Symbol as tokens
%standalone 	//Generate a scanner that is not called by a parser

//Code copied verbatim into the generated class
%{
    //TODO
%}

//Need to specify another end of file using %eofval because we used a user defined type (Symbol). The default value is null
//The code included in %eofval{ ... %eofval} will be copied verbatim into the scanning method and will be executed each time the end of file is reached.
//We don"t use %eof because it is executed once instead of each time
%eofval{
	return new Symbol(LexicalUnit.END_OF_STREAM, yyline, yycolumn);
%eofval}

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
