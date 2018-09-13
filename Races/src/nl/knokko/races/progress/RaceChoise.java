package nl.knokko.races.progress;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.knokko.races.condition.Condition;
import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;
import nl.knokko.races.utils.Maths;

public class RaceChoise {
	
	public static RaceChoise fromBits(BitBuffer buffer){
		String id = buffer.readString();
		byte length = buffer.readByte();
		String[] choises = new String[length];
		Condition[] conditions = new Condition[length];
		for(byte i = 0; i < length; i++){
			choises[i] = buffer.readString();
			conditions[i] = Condition.fromBits(buffer);
		}
		return new RaceChoise(id, choises, conditions);
	}
	
	private final Value[] choises;
	
	private final String id;
	
	public RaceChoise(String id, String[] choises, Condition[] conditions){
		if(id == null) throw new NullPointerException("id");
		if(choises == null) throw new NullPointerException("choises");
		for(int index = 0; index < choises.length; index++)
			if(choises[index] == null)
				throw new NullPointerException("choises[" + index + "]");
		if(conditions == null) throw new NullPointerException("conditions");
		for(int index = 0; index < conditions.length; index++)
			if(conditions[index] == null)
				throw new NullPointerException("conditions[" + index + "]");
		if(choises.length != conditions.length)
			throw new IllegalArgumentException("The length of choises and conditions must be equal! (" + choises.length + " and " + conditions.length + ")");
		if(choises.length > Byte.MAX_VALUE)
			throw new IllegalArgumentException("The amount of options can't exceed the " + Byte.MAX_VALUE);
		this.id = id;
		this.choises = new Value[choises.length];
		for(byte i = 0; i < choises.length; i++)
			this.choises[i] = new Value(choises[i], i, conditions[i]);
	}
	
	@Override
	public String toString(){
		return getID();
	}
	
	@Override
	public int hashCode(){
		return getID().hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof RaceChoise)
			return ((RaceChoise) other).getID().equals(getID());
		return false;
	}
	
	public String getDisplayName(){
		return getID();
	}
	
	public String getID(){
		return id;
	}
	
	public Value[] getAllChoises(){
		return choises;
	}
	
	public byte getValueBitCount(){
		return Maths.logUp(getAllChoises().length);
	}
	
	public Value getByIndex(byte index){
		return choises[index];
	}
	
	public Value getByString(String string){
		for(Value value : choises)
			if(value.name.equals(string))
				return value;
		System.out.println("It seems like the option " + string + " of " + id + " has been removed; setting default value");
		return getDefaultValue();
	}
	
	public void saveValue(Value value, BitBuffer buffer){
		buffer.addNumber(value.getOrdinal(), getValueBitCount(), false);
	}
	
	public Value loadValue(BitBuffer buffer){
		return choises[(int) buffer.readNumber(getValueBitCount(), false)];
	}
	
	public void save(BitBuffer buffer){
		buffer.addString(getID());
		buffer.addByte((byte) choises.length);
		for(Value choise : choises){
			buffer.addString(choise.getName());
			choise.getChooseCondition().save(buffer);
		}
	}
	
	public Value getDefaultValue(){
		return choises[0];
	}
	
	public Collection<Value> getAvailableChoises(RaceStatsConditions conditions){
		List<Value> list = new ArrayList<Value>(choises.length);
		for(Value choise : choises)
			if(choise.getChooseCondition().value(conditions))
				list.add(choise);
		return list;
	}
	
	public boolean isVisible(RaceStatsConditions conditions){
		for(Value choise :choises)
			if(choise.getChooseCondition().value(conditions))
				return true;
		return false;
	}
	
	public static class Value {
		
		private final String name;
		private final Condition condition;
		
		private final byte ordinal;
		
		public Value(String name, byte ordinal, Condition condition){
			this.name = name;
			this.ordinal = ordinal;
			this.condition = condition;
		}
		
		@Override
		public String toString(){
			return name + " (" + ordinal + ")";
		}
		
		@Override
		public int hashCode(){
			return ordinal;
		}
		
		@Override
		public boolean equals(Object other){
			if(other instanceof Value)
				return ((Value)other).ordinal == ordinal;
			return false;
		}
		
		public String getName(){
			return name;
		}
		
		public byte getOrdinal(){
			return ordinal;
		}
		
		public Condition getChooseCondition(){
			return condition;
		}
	}
}
