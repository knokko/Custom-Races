package nl.knokko.races.event.parameter;

public class NumberParameter extends EventParameter {
	
	private static final EventParameter[] PARAMS = {};

	public NumberParameter(String name) {
		super(name);
	}

	@Override
	public ParameterType getType() {
		return ParameterType.NUMBER;
	}

	@Override
	public EventParameter[] getChildren() {
		return PARAMS;
	}
}