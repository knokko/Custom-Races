package nl.knokko.races.condition;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public abstract class Condition {
	
	private static final byte ID_BITS = 4;
	
	private static final Class<?>[] ID_MAP = {
		ConditionGreater.class, ConditionEqual.class, ConditionSmaller.class, ConditionEqualGreater.class, ConditionEqualSmaller.class,
		ConditionNotEqual.class, ConditionAnd.class, ConditionOr.class, ConditionTrue.class, ConditionFalse.class
	};
	
	public static Condition fromBits(BitBuffer buffer){
		byte idIndex = (byte) buffer.readNumber(ID_BITS, false);
		try {
			return (Condition) ID_MAP[idIndex].getConstructor(BitBuffer.class).newInstance(buffer);
		} catch(Exception ex){
			throw new IllegalArgumentException(ex);
		}
	}
	
	public Condition(){}

	public Condition(BitBuffer bits) {}
	
	public final void save(BitBuffer buffer){
		buffer.addNumber(getID(), ID_BITS, false);
		saveSubData(buffer);
	}
	
	public abstract boolean value(RaceStatsConditions params);
	
	protected abstract byte getID();
	
	protected abstract void saveSubData(BitBuffer buffer);
	
	public abstract String toString();
	
	public abstract boolean usesField(String name);
	
	public abstract boolean usesFunction(String name);
	
	public abstract boolean usesChoise(String id);
	
	public abstract void renameFields(String old, String newName);
	
	public abstract void renameFunctions(String old, String newName);
}
