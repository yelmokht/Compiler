import java.util.Objects;

/**
 * Represents tokens for the scanner.
 * Also represents a symbol (terminal or variable) in the grammar.
 */
public class Symbol {
	public static final int UNDEFINED_POSITION = -1;
	public static final Object NO_VALUE = null;
	public static final String EPSILON = "ε";
	private LexicalUnit type;
	private final Object value;
	private final int line,column;

	public Symbol(LexicalUnit unit,int line,int column,Object value){
		this.type	= unit;
		this.line	= line+1;
		this.column	= column;
		this.value	= value;
	}
	
	public Symbol(LexicalUnit unit,int line,int column){
		this(unit,line,column,NO_VALUE);
	}
	
	public Symbol(LexicalUnit unit,int line){
		this(unit,line,UNDEFINED_POSITION,NO_VALUE);
	}
	
	public Symbol(LexicalUnit unit){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,NO_VALUE);
	}

	/**
	 * Creates a symbol for the grammar (terminal with TERMINAL type or variable with VARIABLE type)
	 */
	public Symbol(LexicalUnit unit, Object value){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,value);
	}

	/**
	 * Checks if the symbol is a terminal symbol.
	 * @return True if the symbol is a terminal, false otherwise.
	 */
	public boolean isTerminal(){
		return this.type != null;
	}

	/**
	 * Checks if the symbol is a non-terminal symbol (it is variable then).
	 * @return True if the symbol is a non-terminal, false otherwise.
	 */
	public boolean isNonTerminal(){
		return this.type == LexicalUnit.VARIABLE;
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

	public String toTexString() {
		if (this.getType() == LexicalUnit.VARIABLE) {
			return this.getValue().toString().replace("<", "\\textless ").replace(">", "\\textgreater ");
		}

		String value = this.getValue().toString();

        return switch (value) {
            case "<" -> "\\textless";
            case ">" -> "\\textgreater";
            case EPSILON -> "$\\varepsilon$";
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
		if (this.isTerminal()){
			if (this.type == LexicalUnit.VARIABLE || this.type == LexicalUnit.TERMINAL) {
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
