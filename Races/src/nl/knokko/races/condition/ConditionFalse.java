package nl.knokko.races.condition;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ConditionFalse extends Condition {

	public ConditionFalse() {}

	public ConditionFalse(BitInput bits) {
		super(bits);
	}

	@Override
	public boolean value(RaceStatsConditions params) {
		return false;
	}

	@Override
	protected byte getID() {
		return 9;
	}

	@Override
	protected void saveSubData(BitOutput buffer) {}

	@Override
	public String toString() {
		return "No";
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
	public boolean usesChoise(String id) {
		return false;
	}
}
