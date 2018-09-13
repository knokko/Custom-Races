package nl.knokko.races.event.parameter;

public class EntityParameter extends EventParameter {
	
	static final EventParameter[] INFO = {
		new NumberParameter("CurrentSpeed"),
		new NumberParameter("DimensionID"),
		new NumberParameter("RemainingFireTicks"),
		new NumberParameter("CurrentHearts"),
		new NumberParameter("LightLevel"),
		new NumberParameter("MaxHearts"),
		new NumberParameter("SpeedX"),
		new NumberParameter("SpeedY"),
		new NumberParameter("SpeedZ"),
		new NumberParameter("MovementSpeed"),
		new TextParameter("Name"),
		new NumberParameter("Temperature"),
		new NumberParameter("WorldTime"),
		new NumberParameter("CurrentX"),
		new NumberParameter("CurrentY"),
		new NumberParameter("CurrentZ"),
		new BooleanParameter("InLava"),
		new BooleanParameter("InWater"),
		new BooleanParameter("SeeSky")
	};

	public EntityParameter(String name) {
		super(name);
	}

	@Override
	public ParameterType getType() {
		return ParameterType.ENTITY;
	}

	@Override
	public EventParameter[] getChildren() {
		return INFO;
	}
}