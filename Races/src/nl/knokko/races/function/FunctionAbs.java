package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class FunctionAbs extends Function {
	
	private Function function;

	public FunctionAbs(Function function) {
		this.function = function;
	}

	public FunctionAbs(BitInput buffer) {
		super(buffer);
		function = Function.fromBits(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		return Math.abs(function.value(params));
	}

	@Override
	protected void saveSubData(BitOutput buffer) {
		function.save(buffer);
	}

	@Override
	protected byte getID() {
		return 13;
	}

	@Override
	public boolean usesField(String name) {
		return function.usesField(name);
	}

	@Override
	public boolean usesFunction(String name) {
		return function.usesFunction(name);
	}

	@Override
	public void renameFields(String old, String newName) {
		function.renameFields(old, newName);
	}

	@Override
	public void renameFunctions(String old, String newName) {
		function.renameFunctions(old, newName);
	}

	@Override
	public String toString() {
		return "|" + function + "|";
	}

	@Override
	public boolean usesChoise(String id) {
		return function.usesChoise(id);
	}
}
