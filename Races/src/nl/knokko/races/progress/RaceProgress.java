package nl.knokko.races.progress;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knokko.races.base.Race;
import nl.knokko.races.progress.RaceChoise.Value;
import nl.knokko.races.utils.BitBuffer;

public class RaceProgress {
	
	private final Race race;
	
	private final ValuePair[] values;
	private final ChoisePair[] choises;
	
	public RaceProgress(Race race){
		this.race = race;
		List<ProgressType> types = race.getFields();
		values = new ValuePair[types.size()];
		int i = 0;
		for(ProgressType pt : types){
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
	
	public void save(BitBuffer buffer){
		buffer.addInt(values.length);
		for(ValuePair vp : values){
			buffer.addString(vp.getType().getName());
			vp.getType().getType().save(buffer, vp.getValue());
		}
		Map<String,String> choiseMap = new HashMap<String,String>(values.length);
		for(ChoisePair cp : choises)
			choiseMap.put(cp.getChoise().getID(), cp.getValue().getName());
		buffer.addStringMap(choiseMap);
		//for(ChoisePair cp : choises)
			//cp.getChoise().saveValue(cp.getValue(), buffer);
	}
	
	public void load(BitBuffer buffer){
		//for(ValuePair vp : values)
			//vp.setValue(vp.getType().getType().load(buffer));
		
		int size = buffer.readInt();
		for(int i = 0; i < size; i++){
			String name = buffer.readString();
			for(ValuePair vp : values){
				if(vp.getType().getName().equals(name)){
					vp.setValue(vp.getType().getType().load(buffer));
					name = null;
					break;
				}
			}
			if(name != null)
				System.out.println("It looks like the variable with name " + name + " has been removed.");
		}
		
		Map<String,String> choiseMap = buffer.readStringMap();
		for(ChoisePair cp : choises){
			String value = choiseMap.get(cp.getChoise().getID());
			if(value != null)
				cp.setValue(cp.getChoise().getByString(value));
			else {
				cp.setValue(cp.getChoise().getDefaultValue());
				System.out.println("The value for choise " + cp.getChoise().getID() + " can't be loaded, is it new?");
			}
		}
		for(ChoisePair cp : choises)
			cp.setValue(cp.getChoise().loadValue(buffer));
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
		throw new IllegalArgumentException("There is no field with name " + key);
	}
	
	public Object getValue(String key, ValueType type){
		for(ValuePair vp : values)
			if(vp.getType().getType() == type && vp.getType().getName().equals(key))
				return vp.getValue();
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
		for(ValuePair vp : values)
			if(vp.getType().getType() == type && vp.getType().getName().equals(key))
				vp.setValue(value);
		throw new IllegalArgumentException("There is no field with type " + type + " and name " + key);
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
		throw new IllegalArgumentException("There is no variable with name " + key);
	}
	
	public void setBoolean(String key, boolean value){
		setValue(key, value, ValueType.BOOLEAN);
	}
	
	public void setByte(String key, byte value){
		setValue(key, value, ValueType.BYTE);
	}
	
	public void setChar(String key, char value){
		setValue(key, value, ValueType.CHAR);
	}
	
	public void setShort(String key, short value){
		setValue(key, value, ValueType.SHORT);
	}
	
	public void setInt(String key, int value){
		setValue(key, value, ValueType.INT);
	}
	
	public void setLong(String key, long value){
		setValue(key, value, ValueType.LONG);
	}
	
	public void setFloat(String key, float value){
		setValue(key, value, ValueType.FLOAT);
	}
	
	public void setDouble(String key, double value){
		setValue(key, value, ValueType.DOUBLE);
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
		for(ChoisePair cp : choises)
			if(cp.getChoise().equals(choise))
				cp.setValue(value);
	}
	
	public RaceChoise getChoise(String choise){
		for(ChoisePair cp : choises)
			if(cp.getChoise().getID().equals(choise))
				return cp.getChoise();
		return null;
	}
}