package nl.knokko.races.function;

public class NamedFunction {
	
	private final String name;
	private final Function function;

	public NamedFunction(String name, Function function) {
		this.name = name;
		this.function = function;
	}
	
	public String getName(){
		return name;
	}
	
	public Function getFunction(){
		return function;
	}
}
