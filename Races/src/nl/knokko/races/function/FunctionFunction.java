package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public class FunctionFunction extends Function {
	
	private String name;

	public FunctionFunction(String functionName) {
		name = functionName;
	}

	public FunctionFunction(BitBuffer buffer) {
		super(buffer);
		name = buffer.readString();
	}

	@Override
	public double value(RaceStatsConditions params) {
		return params.getProgress().getRace().getFunction(name).value(params);
	}

	@Override
	protected void saveSubData(BitBuffer buffer) {
		buffer.addString(name);
	}

	@Override
	protected byte getID() {
		return 18;
	}

	@Override
	public boolean usesField(String name) {
		return false;
	}

	@Override
	public boolean usesFunction(String name) {
		return this.name.equals(name);
	}

	@Override
	public void renameFields(String old, String newName) {}

	@Override
	public void renameFunctions(String old, String newName) {
		if(name.equals(old))
			name = newName;
	}

	@Override
	public String toString() {
		return name + "()";
	}
	
	@Override
	public boolean usesChoise(String id) {
		return false;
	}
}
