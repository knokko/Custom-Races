package nl.knokko.races.event.parameter;

public abstract class EventParameter {
	
	private final String name;
	
	public EventParameter(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public abstract ParameterType getType();
	
	public abstract EventParameter[] getChildren();
}