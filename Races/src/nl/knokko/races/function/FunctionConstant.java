package nl.knokko.races.function;

import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.utils.BitBuffer;

public abstract class FunctionConstant {
	
	public static final Double PI = new Double(Math.PI);
	public static final Double e =  new Double(Math.E);
	
	public static Function getConstant(String input){
		if(input.contains(".") || input.contains(",")){
			double doubleValue = java.lang.Double.parseDouble(input);
			float floatValue = java.lang.Float.parseFloat(input);
			if(doubleValue == floatValue)
				return new Float(floatValue);
			return new Double(doubleValue);
		}
		else {
			long value = java.lang.Long.parseLong(input);
			if(value >= java.lang.Byte.MIN_VALUE && value <= java.lang.Byte.MAX_VALUE)
				return new Byte((byte) value);
			if(value >= java.lang.Short.MIN_VALUE && value <= java.lang.Short.MAX_VALUE)
				return new Short((short) value);
			if(value >= Character.MIN_VALUE && value <= Character.MAX_VALUE)
				return new Char((char) value);
			if(value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE)
				return new Int((int) value);
			return new Long(value);
		}
	}

	public static class Byte extends Function {
		
		private final byte value;
		
		public Byte(byte value){
			this.value = value;
		}
		
		public Byte(BitBuffer buffer){
			value = buffer.readByte();
		}

		@Override
		public double value(RaceStatsConditions params) {
			return value;
		}

		@Override
		public void saveSubData(BitBuffer buffer) {
			buffer.addByte(value);
		}

		@Override
		public byte getID() {
			return 0;
		}

		@Override
		public boolean usesField(String name) {
			return false;
		}

		@Override
		public boolean usesFunction(String name) {
			return false;
		}

		@Override
		public void renameFields(String old, String newName) {}

		@Override
		public void renameFunctions(String old, String newName) {}

		@Override
		public String toString() {
			return value + "";
		}
		
		@Override
		public boolean usesChoise(String id) {
			return false;
		}
	}
	
	public static class Short extends Function {
		
		private final short value;
		
		public Short(short value){
			this.value = value;
		}
		
		public Short(BitBuffer buffer){
			value = buffer.readShort();
		}

		@Override
		public double value(RaceStatsConditions params) {
			return value;
		}

		@Override
		public void saveSubData(BitBuffer buffer) {
			buffer.addShort(value);
		}

		@Override
		public byte getID() {
			return 1;
		}
		
		@Override
		public boolean usesField(String name) {
			return false;
		}

		@Override
		public boolean usesFunction(String name) {
			return false;
		}

		@Override
		public void renameFields(String old, String newName) {}

		@Override
		public void renameFunctions(String old, String newName) {}

		@Override
		public String toString() {
			return value + "";
		}
		
		@Override
		public boolean usesChoise(String id) {
			return false;
		}
	}
	
	public static class Char extends Function {
		
		private final char value;
		
		public Char(char value){
			this.value = value;
		}
		
		public Char(BitBuffer buffer){
			value = buffer.readChar();
		}

		@Override
		public double value(RaceStatsConditions params) {
			return value;
		}

		@Override
		public void saveSubData(BitBuffer buffer) {
			buffer.addChar(value);
		}

		@Override
		public byte getID() {
			return 2;
		}
		
		@Override
		public boolean usesField(String name) {
			return false;
		}

		@Override
		public boolean usesFunction(String name) {
			return false;
		}

		@Override
		public void renameFields(String old, String newName) {}

		@Override
		public void renameFunctions(String old, String newName) {}

		@Override
		public String toString() {
			return value + "";
		}
		
		@Override
		public boolean usesChoise(String id) {
			return false;
		}
	}
	
	public static class Int extends Function {
		
		private final int value;
		
		public Int(int value){
			this.value = value;
		}
		
		public Int(BitBuffer buffer){
			value = buffer.readInt();
		}

		@Override
		public double value(RaceStatsConditions params) {
			return value;
		}

		@Override
		public void saveSubData(BitBuffer buffer) {
			buffer.addInt(value);
		}

		@Override
		public byte getID() {
			return 3;
		}
		
		@Override
		public boolean usesField(String name) {
			return false;
		}

		@Override
		public boolean usesFunction(String name) {
			return false;
		}

		@Override
		public void renameFields(String old, String newName) {}

		@Override
		public void renameFunctions(String old, String newName) {}

		@Override
		public String toString() {
			return value + "";
		}
		
		@Override
		public boolean usesChoise(String id) {
			return false;
		}
	}
	
	public static class Float extends Function {
		
		private final float value;
		
		public Float(float value){
			this.value = value;
		}
		
		public Float(BitBuffer buffer){
			value = buffer.readFloat();
		}

		@Override
		public double value(RaceStatsConditions params) {
			return value;
		}

		@Override
		public void saveSubData(BitBuffer buffer) {
			buffer.addFloat(value);
		}

		@Override
		public byte getID() {
			return 4;
		}
		
		@Override
		public boolean usesField(String name) {
			return false;
		}

		@Override
		public boolean usesFunction(String name) {
			return false;
		}

		@Override
		public void renameFields(String old, String newName) {}

		@Override
		public void renameFunctions(String old, String newName) {}

		@Override
		public String toString() {
			return value + "";
		}
		
		@Override
		public boolean usesChoise(String id) {
			return false;
		}
	}
	
	public static class Long extends Function {
		
		private final long value;
		
		public Long(long value){
			this.value = value;
		}
		
		public Long(BitBuffer buffer){
			value = buffer.readLong();
		}

		@Override
		public double value(RaceStatsConditions params) {
			return value;
		}

		@Override
		public void saveSubData(BitBuffer buffer) {
			buffer.addLong(value);
		}

		@Override
		public byte getID() {
			return 5;
		}
		
		@Override
		public boolean usesField(String name) {
			return false;
		}

		@Override
		public boolean usesFunction(String name) {
			return false;
		}

		@Override
		public void renameFields(String old, String newName) {}

		@Override
		public void renameFunctions(String old, String newName) {}

		@Override
		public String toString() {
			return value + "";
		}
		
		@Override
		public boolean usesChoise(String id) {
			return false;
		}
	}
	
	public static class Double extends Function {
		
		private final double value;
		
		public Double(double value){
			this.value = value;
		}
		
		public Double(BitBuffer buffer){
			value = buffer.readDouble();
		}

		@Override
		public double value(RaceStatsConditions params) {
			return value;
		}

		@Override
		public void saveSubData(BitBuffer buffer) {
			buffer.addDouble(value);
		}

		@Override
		public byte getID() {
			return 6;
		}
		
		@Override
		public boolean usesField(String name) {
			return false;
		}

		@Override
		public boolean usesFunction(String name) {
			return false;
		}

		@Override
		public void renameFields(String old, String newName) {}

		@Override
		public void renameFunctions(String old, String newName) {}

		@Override
		public String toString() {
			return value + "";
		}
		
		@Override
		public boolean usesChoise(String id) {
			return false;
		}
	}
}
