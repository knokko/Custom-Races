package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public abstract class Function {
	
	private static final byte ID_BITS = 6;
	
	private static final Class<?>[] ID_MAP = {
		FunctionConstant.Byte.class, FunctionConstant.Short.class, FunctionConstant.Char.class,
		FunctionConstant.Int.class, FunctionConstant.Float.class, FunctionConstant.Long.class,
		FunctionConstant.Double.class, FunctionVariable.class,
		FunctionMultiSum.class, FunctionMultiSubstract.class, FunctionMultiMultiply.class, FunctionMultiDivide.class,
		FunctionPower.class, FunctionAbs.class, FunctionSqrt.class, FunctionNegate.class,
		FunctionRound.class, FunctionLog.class, FunctionFunction.class,
		FunctionSum.class, FunctionSubstract.class, FunctionMultiply.class, FunctionDivide.class,
		FunctionIf.class, FunctionIfElse.class,
		FunctionTan.class, FunctionSin.class, FunctionCos.class,
		FunctionArcTan.class, FunctionArcSin.class, FunctionArcCos.class,
		FunctionTime.class, FunctionTemperature.class, FunctionDimension.class
	};
	
	public static Function fromBits(BitBuffer buffer){
		byte idIndex = (byte) buffer.readNumber(ID_BITS, false);
		try {
			return (Function) ID_MAP[idIndex].getConstructor(BitBuffer.class).newInstance(buffer);
		} catch(Exception ex){
			throw new IllegalArgumentException(ex);
		}
	}
	
	public Function(){}
	
	public Function(BitBuffer buffer){}
	
	public final void save(BitBuffer buffer){
		buffer.addNumber(getID(), ID_BITS, false);
		saveSubData(buffer);
	}
	
	public abstract double value(RaceStatsConditions params);
	
	public int intValue(RaceStatsConditions params){
		return (int) Math.round(value(params));
	}
	
	public float floatValue(RaceStatsConditions params){
		return (float) value(params);
	}
	
	protected abstract void saveSubData(BitBuffer buffer);
	
	public abstract String toString();
	
	protected abstract byte getID();

	public abstract boolean usesField(String name);
	
	public abstract boolean usesFunction(String name);
	
	public abstract boolean usesChoise(String id);
	
	public abstract void renameFields(String old, String newName);
	
	public abstract void renameFunctions(String old, String newName);
}
