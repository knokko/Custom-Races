package nl.knokko.races.progress;

import nl.knokko.races.utils.BitBuffer;

public abstract class ValueType {
	
	public static final ValueType BOOLEAN = new ValueType((byte)-128){

		@Override
		public void save(BitBuffer buffer, Object value) {
			buffer.addBoolean((Boolean) value);
		}
		
		@Override
		public Boolean load(BitBuffer buffer){
			return buffer.readBoolean();
		}

		@Override
		public int getExpectedBits(Object value) {
			return 1;
		}

		@Override
		public Boolean valueOf(String string) {
			return string.equals("true");
		}
	};
	
	public static final ValueType BYTE = new ValueType((byte)-127){

		@Override
		public void save(BitBuffer buffer, Object value) {
			buffer.addByte((Byte) value);
		}
		
		@Override
		public Byte load(BitBuffer buffer){
			return buffer.readByte();
		}

		@Override
		public int getExpectedBits(Object value) {
			return 8;
		}

		@Override
		public Byte valueOf(String string) {
			return Byte.valueOf(string);
		}
	};
	
	public static final ValueType CHAR = new ValueType((byte)-126){

		@Override
		public void save(BitBuffer buffer, Object value) {
			buffer.addChar((Character) value);
		}
		
		@Override
		public Character load(BitBuffer buffer){
			return buffer.readChar();
		}

		@Override
		public int getExpectedBits(Object value) {
			return 16;
		}

		@Override
		public Character valueOf(String string) {
			if(string.length() != 1)
				throw new IllegalArgumentException("The string " + string + " is longer than 1 character!");
			return string.charAt(0);
		}
	};
	
	public static final ValueType SHORT = new ValueType((byte)-125){

		@Override
		public void save(BitBuffer buffer, Object value) {
			buffer.addShort((Short) value);
		}
		
		@Override
		public Short load(BitBuffer buffer){
			return buffer.readShort();
		}

		@Override
		public int getExpectedBits(Object value) {
			return 16;
		}

		@Override
		public Short valueOf(String string) {
			return Short.valueOf(string);
		}
	};
	
	public static final ValueType INT = new ValueType((byte)-124){

		@Override
		public void save(BitBuffer buffer, Object value) {
			buffer.addInt((Integer)value);
		}

		@Override
		public Integer load(BitBuffer buffer) {
			return buffer.readInt();
		}

		@Override
		public int getExpectedBits(Object value) {
			return 32;
		}

		@Override
		public Integer valueOf(String string) {
			return Integer.valueOf(string);
		}
	};
	
	public static final ValueType LONG = new ValueType((byte)-123){

		@Override
		public void save(BitBuffer buffer, Object value) {
			buffer.addLong((Long) value);
		}

		@Override
		public Long load(BitBuffer buffer) {
			return buffer.readLong();
		}

		@Override
		public int getExpectedBits(Object value) {
			return 64;
		}

		@Override
		public Long valueOf(String string) {
			return Long.valueOf(string);
		}
	};
	
	public static final ValueType FLOAT = new ValueType((byte)-122){

		@Override
		public void save(BitBuffer buffer, Object value) {
			buffer.addFloat((Float) value);
		}

		@Override
		public Float load(BitBuffer buffer) {
			return buffer.readFloat();
		}

		@Override
		public int getExpectedBits(Object value) {
			return 32;
		}

		@Override
		public Float valueOf(String string) {
			return Float.valueOf(string);
		}
	};
	
	public static final ValueType DOUBLE = new ValueType((byte)-121){

		@Override
		public void save(BitBuffer buffer, Object value) {
			buffer.addDouble((Double) value);
		}

		@Override
		public Double load(BitBuffer buffer) {
			return buffer.readDouble();
		}

		@Override
		public int getExpectedBits(Object value) {
			return 64;
		}

		@Override
		public Double valueOf(String string) {
			return Double.valueOf(string);
		}
	};
	
	public static final ValueType STRING = new ValueType((byte)-120){

		@Override
		public void save(BitBuffer buffer, Object value) {
			String s = (String) value;
			buffer.addInt(s.length());
			for(int i = 0; i < s.length(); i++)
				buffer.addChar(s.charAt(i));
		}

		@Override
		public String load(BitBuffer buffer) {
			int length = buffer.readInt();
			char[] chars = new char[length];
			for(int i = 0; i < length; i++)
				chars[i] = buffer.readChar();
			return new String(chars);
		}

		@Override
		public int getExpectedBits(Object value) {
			return 4 + ((String)value).length() * 2;
		}

		@Override
		public String valueOf(String string) {
			return string;
		}
	};
	
	private static final ValueType[] VALUES = {BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, STRING};
	
	public static ValueType fromID(byte id){
		return VALUES[id + 128];
	}
	
	private final byte typeID;
	
	public ValueType(byte typeID){
		this.typeID = typeID;
	}
	
	public byte getID(){
		return typeID;
	}
	
	public abstract void save(BitBuffer buffer, Object value);
	
	public abstract Object load(BitBuffer buffer);
	
	//public abstract Object getDefaultValue();//TODO use default value of the variable instead
	
	public abstract Object valueOf(String string);
	
	public abstract int getExpectedBits(Object value);
}
