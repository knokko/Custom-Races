package nl.knokko.races.function;

import nl.knokko.races.condition.Condition;
import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public class FunctionIf extends Function {
	
	private final Function function;
	
	private final Condition condition;

	public FunctionIf(Function function, Condition condition) {
		this.function = function;
		this.condition = condition;
	}

	public FunctionIf(BitBuffer buffer) {
		super(buffer);
		function = Function.fromBits(buffer);
		condition = Condition.fromBits(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		return condition.value(params) ? function.value(params) : 0;
	}

	@Override
	protected void saveSubData(BitBuffer buffer) {
		function.save(buffer);
		condition.save(buffer);
	}

	@Override
	protected byte getID() {
		return 23;
	}

	@Override
	public boolean usesField(String name) {
		return function.usesField(name) || condition.usesField(name);
	}

	@Override
	public boolean usesFunction(String name) {
		return function.usesFunction(name) || condition.usesFunction(name);
	}

	@Override
	public void renameFields(String old, String newName) {
		function.renameFields(old, newName);
		condition.renameFields(old, newName);
	}

	@Override
	public void renameFunctions(String old, String newName) {
		function.renameFunctions(old, newName);
		condition.renameFunctions(old, newName);
	}

	@Override
	public String toString() {
		return "if(" + condition + "){" + function + "}else{0}";
	}

	@Override
	public boolean usesChoise(String id) {
		return function.usesChoise(id) || condition.usesChoise(id);
	}
}
