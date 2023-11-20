import java.util.Objects;

public class Symbol {
	public static final int UNDEFINED_POSITION = -1;
	public static final Object NO_VALUE = null;
	private LexicalUnit type; //LexicalUnit.NUMBER
	private Object value; //0
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
	
	public Symbol(LexicalUnit unit,Object value){
		this(unit,UNDEFINED_POSITION,UNDEFINED_POSITION,value);
	}

	public boolean isTerminal(){
		return this.type != null;
	}
	
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

	public void setValue(Object value) {
		this.value = value;
	}

	public String toTexString() {
		if (this.getType() == LexicalUnit.VARIABLE) {
			return this.getValue().toString().replace("<", "\\textless ").replace(">", "\\textgreater ");
		} else {
			if ("<".equals(this.getValue())) {
				return "\\textless";
			} else if (">".equals(this.getValue())) {
				return "\\textgreater";
			} else {
				return this.getValue().toString();
			}
		}
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Symbol symbol = (Symbol) o;
		return Objects.equals(type, symbol.type) &&
				Objects.equals(value, symbol.value);
	}

	@Override
	public int hashCode(){
		final String value	= this.value != null? this.value.toString() : "null";
		final String type		= this.type  != null? this.type.toString()  : "null";
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
