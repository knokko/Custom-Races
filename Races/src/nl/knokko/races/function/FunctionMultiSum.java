package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public class FunctionMultiSum extends Function {
	
	private static final byte AMOUNT_BITS = 4;
	
	private static final byte MIN_AMOUNT = 2;
	private static final byte MAX_AMOUNT = 17;
	
	private final Function[] functions;

	public FunctionMultiSum(Function[] functionsToSum) {
		if(functionsToSum.length < MIN_AMOUNT)
			throw new IllegalArgumentException("At least two functions are required to sum: (" + functionsToSum.length + ")");
		if(functionsToSum.length > MAX_AMOUNT)
			throw new IllegalArgumentException("Too many functions to sum: " + functionsToSum.length);
		functions = functionsToSum;
	}

	public FunctionMultiSum(BitBuffer buffer) {
		super(buffer);
		functions = new Function[(int) buffer.readNumber(AMOUNT_BITS, false) + MIN_AMOUNT];
		for(int index = 0; index < functions.length; index++)
			functions[index] = Function.fromBits(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		double value = functions[0].value(params) + functions[1].value(params);
		for(int i = MIN_AMOUNT; i < functions.length; i++)
			value += functions[i].value(params);
		return value;
	}

	@Override
	protected void saveSubData(BitBuffer buffer) {
		buffer.addNumber(functions.length - MIN_AMOUNT, AMOUNT_BITS, false);
		for(Function f : functions)
			f.save(buffer);
	}

	@Override
	protected byte getID() {
		return 8;
	}
	
	@Override
	public boolean usesField(String name) {
		for(Function function : functions)
			if(function.usesField(name))
				return true;
		return false;
	}

	@Override
	public boolean usesFunction(String name) {
		for(Function function : functions)
			if(function.usesFunction(name))
				return true;
		return false;
	}

	@Override
	public void renameFields(String old, String newName) {
		for(Function function : functions)
			function.renameFields(old, newName);
	}

	@Override
	public void renameFunctions(String old, String newName) {
		for(Function function : functions)
			function.renameFunctions(old, newName);
	}
	
	@Override
	public String toString() {
		String string = "(" + functions[0] + ")";
		for(int i = 1; i < functions.length; i++)
			string += " + (" + functions[i] + ")";
		return string;
	}
	
	@Override
	public boolean usesChoise(String id) {
		for(Function function : functions)
			if(function.usesChoise(id))
				return true;
		return false;
	}
}
