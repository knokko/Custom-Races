package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public class FunctionRound extends Function {
	
	private final Function function;
	private final Mode mode;

	public FunctionRound(Function function, Mode roundingMode) {
		this.function = function;
		mode = roundingMode;
	}

	public FunctionRound(BitBuffer buffer) {
		super(buffer);
		mode = Mode.values()[(int) buffer.readNumber(Mode.BITS, false)];
		function = Function.fromBits(buffer);
	}

	@Override
	public double value(RaceStatsConditions params) {
		return mode.round(function.value(params));
	}

	@Override
	protected void saveSubData(BitBuffer buffer) {
		buffer.addNumber(mode.ordinal(), Mode.BITS, false);
		function.save(buffer);
	}
	
	@Override
	public String toString() {
		return "round" + mode.name().toLowerCase() + "(" + function + ")";
	}

	@Override
	protected byte getID() {
		return 16;
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
	public boolean usesChoise(String id) {
		return function.usesChoise(id);
	}
	
	public static enum Mode {
		
		TO_ZERO {
			@Override
			double round(double d) {
				return (int) d;
			}
		},
		FROM_ZERO {
			@Override
			double round(double d) {
				return d > 0 ? Math.ceil(d) : Math.floor(d);
			}
		},
		UP {
			@Override
			double round(double d) {
				return Math.ceil(d);
			}
		},
		DOWN {
			@Override
			double round(double d) {
				return Math.floor(d);
			}
		},
		NEAREST {
			@Override
			double round(double d) {
				return Math.round(d);
			}
		};
		
		private static final byte BITS = 3;
		
		abstract double round(double d);
	}
}
