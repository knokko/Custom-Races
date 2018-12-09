package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class FunctionVariable extends Function {
	
	private String name;

	public FunctionVariable(String variableName) {
		name = variableName;
	}

	public FunctionVariable(BitInput buffer) {
		super(buffer);
		name = buffer.readString();
	}

	@Override
	public double value(RaceStatsConditions params) {
		return (Double) params.getProgress().getValue(name);
	}

	@Override
	public void saveSubData(BitOutput buffer) {
		buffer.addString(name);
	}

	@Override
	public byte getID() {
		return 7;
	}

	@Override
	public boolean usesField(String name) {
		return this.name.equals(name);
	}

	@Override
	public boolean usesFunction(String name) {
		return false;
	}

	@Override
	public void renameFields(String old, String newName) {
		if(name.equals(old))
			name = newName;
	}

	@Override
	public void renameFunctions(String old, String newName) {}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean usesChoise(String id) {
		return false;
	}
}
