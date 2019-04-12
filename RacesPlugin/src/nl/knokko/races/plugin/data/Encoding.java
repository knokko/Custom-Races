package nl.knokko.races.plugin.data;

import java.util.List;

import nl.knokko.races.base.Race;
import nl.knokko.races.progress.ProgressType;
import nl.knokko.races.progress.RaceChoise;
import nl.knokko.races.progress.ValueType;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class Encoding {
	
	static Encoding load(BitInput input) {
		int fieldAmount = input.readInt();
		FieldPair[] fields = new FieldPair[fieldAmount];
		for (int index = 0; index < fieldAmount; index++) {
			fields[index] = new FieldPair(input.readString(), ValueType.fromID(input.readByte()));
		}
		int choiseAmount = input.readInt();
		ChoisePair[] choises = new ChoisePair[choiseAmount];
		for (int index = 0; index < choiseAmount; index++) {
			String choiseID = input.readString();
			int optionAmount = input.readByte() & 0xFF;
			ChoisePair.Value[] options = new ChoisePair.Value[optionAmount];
			for (int optionIndex = 0; optionIndex < optionAmount; optionIndex++) {
				options[optionIndex] = new ChoisePair.Value(input.readString(), (byte) optionIndex);
			}
			choises[index] = new ChoisePair(choiseID, options);
		}
		return new Encoding(fields, choises);
	}
	
	private final FieldPair[] fields;
	private final ChoisePair[] choises;
	
	private Encoding(FieldPair[] fields, ChoisePair[] choises) {
		this.fields = fields;
		this.choises = choises;
	}
	
	Encoding(Race race){
		List<ProgressType> raceFields = race.getFields();
		List<RaceChoise> raceChoises = race.getChoises();
		
		fields = new FieldPair[raceFields.size()];
		int fieldIndex = 0;
		for (ProgressType raceField : raceFields) {
			fields[fieldIndex++] = new FieldPair(raceField.getName(), raceField.getType());
		}
		
		choises = new ChoisePair[raceChoises.size()];
		int choiseIndex = 0;
		for (RaceChoise raceChoise : raceChoises) {
			RaceChoise.Value[] raceOptions = raceChoise.getAllChoises();
			ChoisePair.Value[] options = new ChoisePair.Value[raceOptions.length];
			for (int optionIndex = 0; optionIndex < options.length; optionIndex++) {
				options[optionIndex] = new ChoisePair.Value(raceOptions[optionIndex].getName(), raceOptions[optionIndex].getOrdinal());
			}
			choises[choiseIndex++] = new ChoisePair(raceChoise.getID(), options);
		}
	}
	
	public FieldPair getField(int index) {
		return fields[index];
	}
	
	public int getFieldAmount() {
		return fields.length;
	}
	
	public ChoisePair getChoise(int index) {
		return choises[index];
	}
	
	public int getChoiseAmount() {
		return choises.length;
	}
	
	public void save(BitOutput output) {
		output.addInt(fields.length);
		for (FieldPair pair : fields) {
			output.addString(pair.getName());
			output.addByte(pair.getType().getID());
		}
		
		output.addInt(choises.length);
		for (ChoisePair pair : choises) {
			output.addString(pair.getID());
			output.addByte((byte) pair.options.length);
			for (ChoisePair.Value option : pair.options) {
				output.addString(option.name);
			}
		}
	}
	
	public static class FieldPair {
		
		private final ValueType type;
		private final String name;
		
		public FieldPair(String name, ValueType type) {
			this.name = name;
			this.type = type;
		}
		
		public String getName() {
			return name;
		}
		
		public ValueType getType() {
			return type;
		}
	}
	
	public static class ChoisePair {
		
		private final String id;
		private final Value[] options;
		
		public ChoisePair(String id, Value[] options) {
			this.id = id;
			this.options = options;
		}
		
		public String getID() {
			return id;
		}
		
		public Value[] getBackingOptions() {
			return options;
		}
		
		public static class Value {
			
			private final String name;
			private final byte ordinal;
			
			public Value(String name, byte ordinal) {
				this.name = name;
				this.ordinal = ordinal;
			}
			
			public String getName() {
				return name;
			}
			
			public byte getOrdinal() {
				return ordinal;
			}
		}
	}
}