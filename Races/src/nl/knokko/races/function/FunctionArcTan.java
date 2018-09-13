package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public class FunctionArcTan extends Function {
	
	private final Function function;

	public FunctionArcTan(Function function) {
		this.function = function;
	}

	public FunctionArcTan(BitBuffer buffer) {
		super(buffer);
		function = Function.fromBits(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		return Math.toDegrees(Math.atan(function.value(params)));
	}

	@Override
	protected void saveSubData(BitBuffer buffer) {
		function.save(buffer);
	}

	@Override
	protected byte getID() {
		return 28;
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
		return "arctan(" + function + ")";
	}
	
	@Override
	public boolean usesChoise(String id) {
		return function.usesChoise(id);
	}
}
