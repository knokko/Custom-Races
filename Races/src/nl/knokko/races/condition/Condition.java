package nl.knokko.races.condition;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class Condition {
	
	private static final byte ID_BITS = 4;
	
	private static final Class<?>[] ID_MAP = {
		ConditionGreater.class, ConditionEqual.class, ConditionSmaller.class, ConditionEqualGreater.class, ConditionEqualSmaller.class,
		ConditionNotEqual.class, ConditionAnd.class, ConditionOr.class, ConditionTrue.class, ConditionFalse.class
	};
	
	public static Condition fromBits(BitInput buffer){
		byte idIndex = (byte) buffer.readNumber(ID_BITS, false);
		try {
			return (Condition) ID_MAP[idIndex].getConstructor(BitInput.class).newInstance(buffer);
		} catch(Exception ex){
			throw new IllegalArgumentException(ex);
		}
	}
	
	public Condition(){}

	public Condition(BitInput bits) {}
	
	public final void save(BitOutput buffer){
		buffer.addNumber(getID(), ID_BITS, false);
		saveSubData(buffer);
	}
	
	public abstract boolean value(RaceStatsConditions params);
	
	protected abstract byte getID();
	
	protected abstract void saveSubData(BitOutput buffer);
	
	public abstract String toString();
	
	public abstract boolean usesField(String name);
	
	public abstract boolean usesFunction(String name);
	
	public abstract boolean usesChoise(String id);
	
	public abstract void renameFields(String old, String newName);
	
	public abstract void renameFunctions(String old, String newName);
}
