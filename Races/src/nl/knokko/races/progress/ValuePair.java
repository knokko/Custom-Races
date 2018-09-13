package nl.knokko.races.progress;

public class ValuePair {
	
	private final ProgressType type;
	private Object value;

	public ValuePair(ProgressType type) {
		this.type = type;
		value = type.getDefaultValue();
	}
	
	@Override
	public String toString(){
		return type + " is " + value;
	}
	
	@Override
	public int hashCode(){
		return type.hashCode() + value.hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof ValuePair){
			ValuePair vp = (ValuePair) other;
			return vp.type.equals(type) && vp.value.equals(value);
		}
		return false;
	}
	
	public ProgressType getType(){
		return type;
	}
	
	public Object getValue(){
		return value;
	}
	
	public void setValue(Object value){
		this.value = value;
	}
}
