package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public class FunctionTan extends Function {
	
	private final Function function;

	public FunctionTan(Function function) {
		this.function = function;
	}

	public FunctionTan(BitBuffer buffer) {
		super(buffer);
		function = Function.fromBits(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		return Math.tan(Math.toRadians(function.value(params)));
	}

	@Override
	protected void saveSubData(BitBuffer buffer) {
		function.save(buffer);
	}

	@Override
	protected byte getID() {
		return 25;
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
		return "tan(" + function + ")";
	}
	
	@Override
	public boolean usesChoise(String id) {
		return function.usesChoise(id);
	}
}