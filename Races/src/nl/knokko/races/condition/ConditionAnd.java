package nl.knokko.races.condition;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public class ConditionAnd extends Condition {
	
	private final Condition condition1;
	private final Condition condition2;

	public ConditionAnd(Condition condition1, Condition condition2) {
		this.condition1 = condition1;
		this.condition2 = condition2;
	}

	public ConditionAnd(BitBuffer bits) {
		super(bits);
		condition1 = Condition.fromBits(bits);
		condition2 = Condition.fromBits(bits);
	}

	@Override
	public boolean value(RaceStatsConditions params) {
		return condition1.value(params) && condition2.value(params);
	}

	@Override
	protected byte getID() {
		return 6;
	}

	@Override
	protected void saveSubData(BitBuffer buffer) {
		condition1.save(buffer);
		condition2.save(buffer);
	}

	@Override
	public boolean usesField(String name) {
		return condition1.usesField(name) || condition2.usesField(name);
	}

	@Override
	public boolean usesFunction(String name) {
		return condition1.usesFunction(name) || condition2.usesFunction(name);
	}

	@Override
	public void renameFields(String old, String newName) {
		condition1.renameFields(old, newName);
		condition2.renameFields(old, newName);
	}

	@Override
	public void renameFunctions(String old, String newName) {
		condition1.renameFunctions(old, newName);
		condition2.renameFunctions(old, newName);
	}

	@Override
	public String toString() {
		return "(" + condition1 + ") and (" + condition2 + ")";
	}

	@Override
	public boolean usesChoise(String id) {
		return condition1.usesChoise(id) || condition2.usesChoise(id);
	}
}
