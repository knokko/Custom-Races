package nl.knokko.races.event.parameter;

public class BooleanParameter extends EventParameter {
	
	public static interface Getter {
		
		boolean getValue();
	}

	public BooleanParameter(String name) {
		super(name);
	}

	@Override
	public ParameterType getType() {
		return ParameterType.BOOLEAN;
	}

	@Override
	public EventParameter[] getChildren() {
		return null;
	}
}