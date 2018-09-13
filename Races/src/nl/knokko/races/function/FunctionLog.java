package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public class FunctionLog extends Function {
	
	private final Function number;
	private final Function exponent;

	public FunctionLog(Function number, Function exponent) {
		this.number = number;
		this.exponent = exponent;
	}

	public FunctionLog(BitBuffer buffer) {
		super(buffer);
		number = Function.fromBits(buffer);
		exponent = Function.fromBits(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		return Math.log10(number.value(params)) / Math.log10(exponent.value(params));
	}

	@Override
	protected void saveSubData(BitBuffer buffer) {
		number.save(buffer);
		exponent.save(buffer);
	}

	@Override
	protected byte getID() {
		return 17;
	}
	
	@Override
	public boolean usesField(String name) {
		return number.usesField(name) || exponent.usesField(name);
	}

	@Override
	public boolean usesFunction(String name) {
		return number.usesFunction(name) || exponent.usesFunction(name);
	}

	@Override
	public void renameFields(String old, String newName) {
		number.renameFields(old, newName);
		exponent.renameFields(old, newName);
	}

	@Override
	public void renameFunctions(String old, String newName) {
		number.renameFunctions(old, newName);
		exponent.renameFunctions(old, newName);
	}

	@Override
	public String toString() {
		return exponent + "_log(" + number + ")";
	}

	@Override
	public boolean usesChoise(String id) {
		return number.usesChoise(id) || exponent.usesChoise(id);
	}
}
