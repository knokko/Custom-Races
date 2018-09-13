package nl.knokko.races.event.parameter;

public class TextParameter extends EventParameter {
	
	private static final EventParameter[] PARAMS = {};

	public TextParameter(String name) {
		super(name);
	}

	@Override
	public ParameterType getType() {
		return ParameterType.TEXT;
	}

	@Override
	public EventParameter[] getChildren() {
		return PARAMS;
	}
}