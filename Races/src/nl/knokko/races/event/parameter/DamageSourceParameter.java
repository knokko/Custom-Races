package nl.knokko.races.event.parameter;

public class DamageSourceParameter extends EventParameter {

	public DamageSourceParameter(String name) {
		super(name);
	}

	@Override
	public ParameterType getType() {
		return ParameterType.DAMAGESOURCE;
	}

	@Override
	public EventParameter[] getChildren() {
		return null;
	}
}