package nl.knokko.races.condition;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.function.Function;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class ConditionSmaller extends Condition {
	
	private final Function f1;
	private final Function f2;

	public ConditionSmaller(Function function1, Function function2) {
		this.f1 = function1;
		this.f2 = function2;
	}

	public ConditionSmaller(BitInput bits) {
		super(bits);
		f1 = Function.fromBits(bits);
		f2 = Function.fromBits(bits);
	}

	@Override
	public boolean value(RaceStatsConditions params) {
		return f1.value(params) < f2.value(params);
	}

	@Override
	protected byte getID() {
		return 2;
	}

	@Override
	protected void saveSubData(BitOutput buffer) {
		f1.save(buffer);
		f2.save(buffer);
	}
	
	@Override
	public boolean usesField(String name) {
		return f1.usesField(name) || f2.usesField(name);
	}

	@Override
	public boolean usesFunction(String name) {
		return f1.usesFunction(name) || f2.usesFunction(name);
	}

	@Override
	public void renameFields(String old, String newName) {
		f1.renameFields(old, newName);
		f2.renameFields(old, newName);
	}

	@Override
	public void renameFunctions(String old, String newName) {
		f1.renameFunctions(old, newName);
		f2.renameFunctions(old, newName);
	}

	@Override
	public String toString() {
		return "(" + f1 + ") < (" + f2 + ")";
	}
	
	@Override
	public boolean usesChoise(String id) {
		return f1.usesChoise(id) || f2.usesChoise(id);
	}
}
