package nl.knokko.races.progress;

public class ProgressType {
	
	private final Object defaultValue;
	
	private final ValueType type;
	
	private final String name;

	public ProgressType(String name, ValueType type, Object defaultValue) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public String toString(){
		return name + "(" + type.getID() + ")";
	}
	
	@Override
	public int hashCode(){
		return type.getID() * name.hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof ProgressType){
			ProgressType pt = (ProgressType) other;
			return pt.type.equals(type) && pt.name.equals(name);
		}
		return false;
	}
	
	public ValueType getType(){
		return type;
	}
	
	public String getName(){
		return name;
	}
	
	public Object getDefaultValue(){
		return defaultValue;
	}
}
