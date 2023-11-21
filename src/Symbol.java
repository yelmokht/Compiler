import java.util.Objects;

/**
 * Represents tokens for the scanner and symbols (terminal or variable) for the grammar.
 */
public class Symbol {
	public static final int UNDEFINED_POSITION = -1;
	public static final Object NO_VALUE = null;
	private LexicalUnit type;
	private final Object value;
	private final int line, column;

	public Symbol(LexicalUnit unit, int line, int column, Object value){
		this.type	= unit;
		this.line	= line+1;
		this.column	= column;
		this.value	= value;
	}
	
	public Symbol(LexicalUnit unit, int line, int column){
		this(unit, line, column, NO_VALUE);
	}
	
	public Symbol(LexicalUnit unit, int line){
		this(unit, line, UNDEFINED_POSITION, NO_VALUE);
	}
	
	public Symbol(LexicalUnit unit){
		this(unit, UNDEFINED_POSITION, UNDEFINED_POSITION, NO_VALUE);
	}

	/**
	 * Creates a symbol for the grammar (terminal with TERMINAL type or variable with VARIABLE type)
	 */
	public Symbol(LexicalUnit unit, Object value){
		this(unit, UNDEFINED_POSITION, UNDEFINED_POSITION, value);
	}

	public LexicalUnit getType(){
		return this.type;
	}
	
	public Object getValue(){
		return this.value;
	}
	
	public int getLine(){
		return this.line;
	}
	
	public int getColumn(){
		return this.column;
	}

	public void setType(LexicalUnit type) {
		this.type = type;
	}

	public boolean isToken(){
		return this.type != null;
	}

	public boolean isNull(){
		return this.type == null;
	}

	/**
	 * Checks if the symbol is a terminal symbol.
	 * @return True if the symbol is a terminal, false otherwise.
	 */
	public boolean isTerminal(){
		return this.type == LexicalUnit.TERMINAL;
	}

	/**
	 * Checks if the symbol is a non-terminal symbol (it is variable then).
	 * @return True if the symbol is a non-terminal, false otherwise.
	 */
	public boolean isVariable(){
		return this.type == LexicalUnit.VARIABLE;
	}


	/**
	 * Converts the current object to its LaTeX representation as a string.
	 * <p></p>
	 * If the object is a variable, it replaces special variable characters with their LaTeX equivalents.
	 * If the object is a terminal, it checks for specific special characters and replaces them with their LaTeX equivalents.
	 * @return The LaTeX representation of the object as a string.
	 */
	public String toTexString() {
		if (this.isVariable()) {
			// If it is a variable, replace special variable characters in variable with their LaTeX equivalents
			return this.getValue().toString()
					.replace(Format.START_VARIABLE, Format.START_VARIABLE_LATEX)
					.replace(Format.FINISH_VARIABLE, Format.FINISH_VARIABLE_LATEX);
		}

		// If the object is not a variable so a terminal, get its value as a string
		String value = this.getValue().toString();

		// Replace special character terminal with their LaTeX equivalents
		return switch (value) {
			case Format.EPSILON -> Format.EPSILON_LATEX;
			case Format.SMALLER -> Format.SMALLER_LATEX;
			default -> value;
		};
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Symbol symbol = (Symbol) o;
		return Objects.equals(type, symbol.type) && Objects.equals(value, symbol.value);
	}

	@Override
	public int hashCode(){
		final String value = this.value != null? this.value.toString() : "null";
		final String type = this.type != null? this.type.toString()  : "null";
		return new String(value+"_"+type).hashCode();
	}
	
	@Override
	public String toString(){
		if (this.isToken()){
			if (this.isVariable() || this.isTerminal()) {
				return this.value.toString();
			} else {
				final String value = this.value != null ? this.value.toString() : "null";
				final String type = this.type != null ? this.type.toString() : "null";
				return "token: " + value + "\tlexical unit: " + type;
			}
		}
		return "Non-terminal symbol";
	}

}
