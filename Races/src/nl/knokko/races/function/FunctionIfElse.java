package nl.knokko.races.function;

import nl.knokko.races.condition.Condition;
import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class FunctionIfElse extends Function {
	
	private final Function trueFunction;
	private final Function falseFunction;
	
	private final Condition condition;

	public FunctionIfElse(Function trueFunction, Function falseFunction, Condition condition) {
		this.trueFunction = trueFunction;
		this.falseFunction = falseFunction;
		this.condition = condition;
	}

	public FunctionIfElse(BitInput buffer) {
		super(buffer);
		trueFunction = Function.fromBits(buffer);
		falseFunction = Function.fromBits(buffer);
		condition = Condition.fromBits(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		return condition.value(params) ? trueFunction.value(params) : falseFunction.value(params);
	}

	@Override
	protected void saveSubData(BitOutput buffer) {
		trueFunction.save(buffer);
		falseFunction.save(buffer);
		condition.save(buffer);
	}

	@Override
	protected byte getID() {
		return 24;
	}
	
	@Override
	public boolean usesField(String name) {
		return trueFunction.usesField(name) || falseFunction.usesField(name) || condition.usesField(name);
	}

	@Override
	public boolean usesFunction(String name) {
		return trueFunction.usesFunction(name) || falseFunction.usesFunction(name) || condition.usesFunction(name);
	}

	@Override
	public void renameFields(String old, String newName) {
		trueFunction.renameFields(old, newName);
		falseFunction.renameFields(old, newName);
		condition.renameFields(old, newName);
	}

	@Override
	public void renameFunctions(String old, String newName) {
		trueFunction.renameFunctions(old, newName);
		falseFunction.renameFunctions(old, newName);
		condition.renameFunctions(old, newName);
	}

	@Override
	public String toString() {
		return "if(" + condition + "){" + trueFunction + "}else{" + falseFunction + "}";
	}

	@Override
	public boolean usesChoise(String id) {
		return falseFunction.usesChoise(id) || trueFunction.usesChoise(id) || condition.usesChoise(id);
	}
}
