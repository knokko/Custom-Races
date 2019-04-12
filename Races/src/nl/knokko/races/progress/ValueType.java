package nl.knokko.races.progress;

import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public abstract class ValueType {

	public static final ValueType BOOLEAN = new ValueType((byte) -128) {

		@Override
		public void save(BitOutput buffer, Object value) {
			buffer.addBoolean((Boolean) value);
		}

		@Override
		public Boolean load(BitInput buffer) {
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

		@Override
		public String toString() {
			return "ValueType boolean";
		}
	};

	public static final ValueType BYTE = new ValueType((byte) -127) {

		@Override
		public void save(BitOutput buffer, Object value) {
			buffer.addByte((Byte) value);
		}

		@Override
		public Byte load(BitInput buffer) {
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

		@Override
		public String toString() {
			return "ValueType byte";
		}
	};

	public static final ValueType CHAR = new ValueType((byte) -126) {

		@Override
		public void save(BitOutput buffer, Object value) {
			buffer.addChar((Character) value);
		}

		@Override
		public Character load(BitInput buffer) {
			return buffer.readChar();
		}

		@Override
		public int getExpectedBits(Object value) {
			return 16;
		}

		@Override
		public Character valueOf(String string) {
			if (string.length() != 1)
				throw new IllegalArgumentException("The string " + string + " is longer than 1 character!");
			return string.charAt(0);
		}

		@Override
		public String toString() {
			return "ValueType char";
		}
	};

	public static final ValueType SHORT = new ValueType((byte) -125) {

		@Override
		public void save(BitOutput buffer, Object value) {
			buffer.addShort((Short) value);
		}

		@Override
		public Short load(BitInput buffer) {
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

		@Override
		public String toString() {
			return "ValueType short";
		}
	};

	public static final ValueType INT = new ValueType((byte) -124) {

		@Override
		public void save(BitOutput buffer, Object value) {
			buffer.addInt((Integer) value);
		}

		@Override
		public Integer load(BitInput buffer) {
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

		@Override
		public String toString() {
			return "ValueType int";
		}
	};

	public static final ValueType LONG = new ValueType((byte) -123) {

		@Override
		public void save(BitOutput buffer, Object value) {
			buffer.addLong((Long) value);
		}

		@Override
		public Long load(BitInput buffer) {
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

		@Override
		public String toString() {
			return "ValueType long";
		}
	};

	public static final ValueType FLOAT = new ValueType((byte) -122) {

		@Override
		public void save(BitOutput buffer, Object value) {
			buffer.addFloat((Float) value);
		}

		@Override
		public Float load(BitInput buffer) {
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

		@Override
		public String toString() {
			return "ValueType float";
		}
	};

	public static final ValueType DOUBLE = new ValueType((byte) -121) {

		@Override
		public void save(BitOutput buffer, Object value) {
			buffer.addDouble((Double) value);
		}

		@Override
		public Double load(BitInput buffer) {
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

		@Override
		public String toString() {
			return "ValueType double";
		}
	};

	public static final ValueType STRING = new ValueType((byte) -120) {

		@Override
		public void save(BitOutput buffer, Object value) {
			buffer.addString((String) value);
		}

		@Override
		public String load(BitInput buffer) {
			return buffer.readString();
		}

		@Override
		public int getExpectedBits(Object value) {
			return 4 + ((String) value).length() * 2;
		}

		@Override
		public String valueOf(String string) {
			return string;
		}

		@Override
		public String toString() {
			return "ValueType String";
		}
	};

	private static final ValueType[] VALUES = { BOOLEAN, BYTE, CHAR, SHORT, INT, LONG, FLOAT, DOUBLE, STRING };

	public static ValueType fromID(byte id) {
		return VALUES[id + 128];
	}

	private final byte typeID;

	private ValueType(byte typeID) {
		this.typeID = typeID;
	}

	public byte getID() {
		return typeID;
	}

	public abstract void save(BitOutput buffer, Object value);

	public abstract Object load(BitInput buffer);

	public abstract Object valueOf(String string);

	public abstract int getExpectedBits(Object value);

	/**
	 * Determines if a variable of this type can take over a value of the given
	 * other variable type
	 * 
	 * @param from The other variable type
	 * @return true if values from from can be given to variables with this type
	 */
	public boolean canConvertFrom(ValueType from) {

		if (this == from) {
			return true;
		}

		// Abuse the fact that the value types are in order
		for (ValueType next : VALUES) {

			// this value type comes first, so the other has higher precision and thus
			// conversion is not possible
			if (next == this) {
				return false;
			}
			
			// the other type comes first, so this type must have higher precision and
			// thus the conversion is possible
			if (next == from) {
				return true;
			}
		}

		throw new IllegalArgumentException("This (" + this + ") and from (" + from + ") are not in VALUES");
	}
}
