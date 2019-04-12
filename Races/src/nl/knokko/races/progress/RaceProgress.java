package nl.knokko.races.progress;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.knokko.races.base.Race;
import nl.knokko.races.progress.RaceChoise.Value;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class RaceProgress {
	
	private final Race race;
	
	private final ValuePair[] values;
	private final ChoisePair[] choises;
	
	public RaceProgress(Race race){
		this.race = race;
		List<ProgressType> fields = race.getFields();
		values = new ValuePair[fields.size()];
		int i = 0;
		for(ProgressType pt : fields){
			values[i] = new ValuePair(pt);
			i++;
		}
		
		Collection<RaceChoise> choises = race.getChoises();
		this.choises = new ChoisePair[choises.size()];
		i = 0;
		for(RaceChoise rc : choises){
			this.choises[i] = new ChoisePair(rc);
			i++;
		}
	}
	
	public Race getRace(){
		return race;
	}
	
	public void save(BitOutput buffer){
		
		buffer.ensureExtraCapacity(getExpectedBits());
		for(ValuePair vp : values){
			vp.getType().getType().save(buffer, vp.getValue());
		}
		for(ChoisePair cp : choises)
			cp.getChoise().saveValue(cp.getValue(), buffer);
	}
	
	public void load(BitInput buffer){
		for (ValuePair vp : values) {
			vp.setValue(vp.getType().getType().load(buffer));
		}
		
		for(ChoisePair cp : choises) {
			cp.setValue(cp.getChoise().loadValue(buffer));
		}
	}
	
	public int getExpectedBits(){
		int expected = 0;
		for(ValuePair vp : values)
			expected += vp.getType().getType().getExpectedBits(vp.getValue());
		for(ChoisePair cp : choises)
			expected += cp.getChoise().getValueBitCount();
		return expected;
	}
	
	public Object getValue(String key){
		for(ValuePair vp : values)
			if(vp.getType().getName().equals(key))
				return vp.getValue();
		System.out.println("values are " + Arrays.toString(values));
		throw new IllegalArgumentException("There is no field with name " + key);
	}
	
	public Object getValue(String key, ValueType type){
		for(ValuePair vp : values)
			if(vp.getType().getType() == type && vp.getType().getName().equals(key))
				return vp.getValue();
		System.out.println("values are " + Arrays.toString(values));
		throw new IllegalArgumentException("There is no field of type " + type + " with name " + key);
	}
	
	public boolean getBoolean(String key){
		return (Boolean) getValue(key, ValueType.BOOLEAN);
	}
	
	public byte getByte(String key){
		return (Byte) getValue(key, ValueType.BYTE);
	}
	
	public char getChar(String key){
		return (Character) getValue(key, ValueType.CHAR);
	}
	
	public short getShort(String key){
		return (Short) getValue(key, ValueType.SHORT);
	}
	
	public int getInt(String key){
		return (Integer) getValue(key, ValueType.INT);
	}
	
	public long getLong(String key){
		return (Long) getValue(key, ValueType.LONG);
	}
	
	public float getFloat(String key){
		return (Float) getValue(key, ValueType.FLOAT);
	}
	
	public double getDouble(String key){
		return (Double) getValue(key, ValueType.DOUBLE);
	}
	
	public String getString(String key){
		return (String) getValue(key, ValueType.STRING);
	}
	
	public void setValue(String key, Object value, ValueType type){
		for(ValuePair vp : values) {
			if(vp.getType().getType() == type && vp.getType().getName().equals(key)) {
				vp.setValue(value);
				return;
			}
		}
		System.out.println("values is " + Arrays.toString(values));
		throw new IllegalArgumentException("There is no field with type " + type + " and name " + key);
	}
	
	/**
	 * Attempts to set the value for the specified key. The return value of this method will tell whether or
	 * not the operation succeeded. If the value for the given key can't be set, this RaceProgress instance
	 * won't be changed.
	 * @param key The name/key of the value to set
	 * @param value The new value to give to the specified key
	 * @param type The type of the new value
	 * @return the result of this method
	 */
	public MaybeResult maybeSetValue(String key, Object value, ValueType type) {
		for (ValuePair vp : values) {
			if (vp.getType().getName().equals(key)) {
				ValueType ownType = vp.getType().getType();
				if (ownType == type) {
					vp.setValue(value);
					return MaybeResult.SUCCESS;
				} else {
					
					// Now we check if we can upgrade the type
					if (ownType.canConvertFrom(type)) {
						vp.setValue(ownType.valueOf(value.toString()));
						return MaybeResult.UPGRADED_TYPE;
					} else {
						return MaybeResult.TYPE_FAIL;
					}
				}
			}
		}
		return MaybeResult.FAIL;
	}
	
	public static enum MaybeResult {
		
		/**
		 * The operation succeeded normally.
		 */
		SUCCESS,
		
		/**
		 * The operation has succeeded, but type conversion took place because the given type
		 * was not equal to this type and this type was capable of taking the value over
		 * from the other type.
		 */
		UPGRADED_TYPE,
		
		/**
		 * The operation failed because the given type wasn't equal to this type and we can not
		 * safely convert values from the given type to this type.
		 */
		TYPE_FAIL,
		
		/**
		 * The operation failed because this race progress doesn't have a variable with the given key.
		 */
		FAIL;
	}
	
	public void setValueOf(String key, String value){
		for(ValuePair vp : values){
			if(vp.getType().getName().equals(key)){
				try {
					vp.setValue(vp.getType().getType().valueOf(value));
					return;
				} catch(NumberFormatException nfe){
					throw new IllegalArgumentException(value + " is supposed to be a number (" + nfe.getMessage() + ")");
				}
			}
		}
		System.out.println("values is " + values);
		throw new IllegalArgumentException("There is no variable with name " + key);
	}
	
	public void setBoolean(String key, boolean value){
		setValue(key, value, ValueType.BOOLEAN);
	}
	
	public void setByte(String key, byte value){
		setValue(key, value, ValueType.BYTE);
	}
	
	public void increaseByte(String key, byte amount) {
		setByte(key, (byte) (getByte(key) + amount));
	}
	
	public void setChar(String key, char value){
		setValue(key, value, ValueType.CHAR);
	}
	
	public void increaseChar(String key, char amount) {
		setChar(key, (char) (getChar(key) + amount));
	}
	
	public void setShort(String key, short value){
		setValue(key, value, ValueType.SHORT);
	}
	
	public void increaseShort(String key, short amount) {
		setShort(key, (short) (getShort(key) + amount));
	}
	
	public void setInt(String key, int value){
		setValue(key, value, ValueType.INT);
	}
	
	public void increaseInt(String key, int amount) {
		setInt(key, getInt(key) + amount);
	}
	
	public void setLong(String key, long value){
		setValue(key, value, ValueType.LONG);
	}
	
	public void increaseLong(String key, long amount) {
		setLong(key, getLong(key) + amount);
	}
	
	public void setFloat(String key, float value){
		setValue(key, value, ValueType.FLOAT);
	}
	
	public void increaseFloat(String key, float amount) {
		setFloat(key, getFloat(key) + amount);
	}
	
	public void setDouble(String key, double value){
		setValue(key, value, ValueType.DOUBLE);
	}
	
	public void increaseDouble(String key, double amount) {
		setDouble(key, getDouble(key) + amount);
	}
	
	public void setString(String key, String value){
		if(value == null)
			throw new IllegalArgumentException("String values can't be null!");
		setValue(key, value, ValueType.STRING);
	}
	
	public Value getChoise(RaceChoise choise){
		for(ChoisePair cp : choises)
			if(cp.getChoise().equals(choise))
				return cp.getValue();
		return null;
	}
	
	public void choose(RaceChoise choise, Value value){
		for(ChoisePair cp : choises) {
			if(cp.getChoise().equals(choise)) {
				cp.setValue(value);
				return;
			}
		}
		System.out.println("choises are " + Arrays.toString(choises));
		throw new IllegalArgumentException("Race " + race + " doesn't have choise " + choise);
	}
	
	public RaceChoise getChoise(String choise){
		for(ChoisePair cp : choises)
			if(cp.getChoise().getID().equals(choise))
				return cp.getChoise();
		return null;
	}
}