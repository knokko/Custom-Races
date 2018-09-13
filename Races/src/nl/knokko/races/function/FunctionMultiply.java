package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public class FunctionMultiply extends Function {
	
	private final Function function1;
	private final Function function2;

	public FunctionMultiply(Function function1, Function function2) {
		this.function1 = function1;
		this.function2 = function2;
	}

	public FunctionMultiply(BitBuffer buffer) {
		super(buffer);
		function1 = Function.fromBits(buffer);
		function2 = Function.fromBits(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		return function1.value(params) * function2.value(params);
	}

	@Override
	protected void saveSubData(BitBuffer buffer) {
		function1.save(buffer);
		function2.save(buffer);
	}

	@Override
	protected byte getID() {
		return 21;
	}
	
	@Override
	public boolean usesField(String name) {
		return function1.usesField(name) || function2.usesField(name);
	}

	@Override
	public boolean usesFunction(String name) {
		return function1.usesFunction(name) || function2.usesFunction(name);
	}

	@Override
	public void renameFields(String old, String newName) {
		function1.renameFields(old, newName);
		function2.renameFields(old, newName);
	}

	@Override
	public void renameFunctions(String old, String newName) {
		function1.renameFunctions(old, newName);
		function2.renameFunctions(old, newName);
	}

	@Override
	public String toString() {
		return "(" + function1 + ") * (" + function2 + ")";
	}
	
	@Override
	public boolean usesChoise(String id) {
		return function1.usesChoise(id) || function2.usesChoise(id);
	}
}
