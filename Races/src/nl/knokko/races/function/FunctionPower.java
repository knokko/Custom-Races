package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public class FunctionPower extends Function {
	
	private final Function base;
	private final Function exponent;

	public FunctionPower(Function base, Function exponent) {
		this.base = base;
		this.exponent = exponent;
	}

	public FunctionPower(BitBuffer buffer) {
		super(buffer);
		base = Function.fromBits(buffer);
		exponent = Function.fromBits(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		return Math.pow(base.value(params), exponent.value(params));
	}

	@Override
	protected void saveSubData(BitBuffer buffer) {
		base.save(buffer);
		exponent.save(buffer);
	}

	@Override
	protected byte getID() {
		return 12;
	}
	
	@Override
	public boolean usesField(String name) {
		return base.usesField(name) || exponent.usesField(name);
	}

	@Override
	public boolean usesFunction(String name) {
		return base.usesFunction(name) || exponent.usesFunction(name);
	}

	@Override
	public void renameFields(String old, String newName) {
		base.renameFields(old, newName);
		exponent.renameFields(old, newName);
	}

	@Override
	public void renameFunctions(String old, String newName) {
		base.renameFunctions(old, newName);
		exponent.renameFunctions(old, newName);
	}

	@Override
	public String toString() {
		return "(" + base + ") ^ (" + exponent + ")";
	}
	
	@Override
	public boolean usesChoise(String id) {
		return base.usesChoise(id) || exponent.usesChoise(id);
	}
}
