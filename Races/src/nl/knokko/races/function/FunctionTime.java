package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class FunctionTime extends Function {

	public FunctionTime() {}

	public FunctionTime(BitInput buffer) {
		super(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		return params.getWorldTime();
	}

	@Override
	protected void saveSubData(BitOutput buffer) {}

	@Override
	protected byte getID() {
		return 31;
	}
	
	@Override
	public boolean usesField(String name) {
		return false;
	}

	@Override
	public boolean usesFunction(String name) {
		return false;
	}

	@Override
	public void renameFields(String old, String newName) {}

	@Override
	public void renameFunctions(String old, String newName) {}

	@Override
	public String toString() {
		return "worldTime";
	}
	
	@Override
	public boolean usesChoise(String id) {
		return false;
	}
}
