package nl.knokko.races.plugin.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import nl.knokko.races.base.Race;
import nl.knokko.races.plugin.data.Encoding.ChoisePair;
import nl.knokko.races.plugin.data.Encoding.FieldPair;
import nl.knokko.races.progress.ProgressType;
import nl.knokko.races.progress.RaceChoise;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class EncodingMap {
	
	private final File file;
	
	private Encoding[] encodings;
	private List<BoundEncoding> raceEncodings;
	
	public EncodingMap(File storageFile) {
		file = storageFile;
	}
	
	public Encoding getEncoding(int id) {
		if (id < 0 || id >= encodings.length) {
			throw new IllegalArgumentException("id is " + id + " and amount is " + encodings.length);
		}
		return encodings[id];
	}
	
	public int findEncoding(Race race) {
		if (raceEncodings == null) {
			raceEncodings = new ArrayList<BoundEncoding>(Race.getAllRaces().size());
		}
		for (BoundEncoding bound : raceEncodings) {
			if (bound.race == race) {
				return bound.encoding;
			}
		}
		
		// We haven't found the right encoding for that race yet, so lets search through our encodings
		List<ProgressType> fields = race.getFields();
		List<RaceChoise> choises = race.getChoises();
		
		outerLoop:
		for (int encodingID = 0; encodingID < encodings.length; encodingID++) {
			Encoding encoding = encodings[encodingID];
			if (encoding.getChoiseAmount() != choises.size() || encoding.getFieldAmount() != fields.size()) {
				// A quick but power check. If the amounts aren't equal, they can't fit
				continue;
			}
			
			int fieldIndex = 0;
			for (ProgressType raceField : fields) {
				FieldPair encodingField = encoding.getField(fieldIndex++);
				if (!encodingField.getName().equals(raceField.getName()) || encodingField.getType() != raceField.getType()) {
					// If any field is not equal, this encoding won't fit
					continue outerLoop;
				}
			}
			
			int choiseIndex = 0;
			for (RaceChoise raceChoise : choises) {
				ChoisePair encodingChoise = encoding.getChoise(choiseIndex++);
				if (!encodingChoise.getID().equals(raceChoise.getID()) || encodingChoise.getBackingOptions().length != raceChoise.getAllChoises().length) {
					// If the choise ID or option count doesn't match, this encoding won't fit
					continue outerLoop;
				}
				RaceChoise.Value[] raceOptions = raceChoise.getAllChoises();
				ChoisePair.Value[] encodingOptions = encodingChoise.getBackingOptions();
				// We already checked that they have the same length
				for (int optionIndex = 0; optionIndex < raceOptions.length; optionIndex++) {
					RaceChoise.Value raceOption = raceOptions[optionIndex];
					ChoisePair.Value encodingOption = encodingOptions[optionIndex];
					if (!raceOption.getName().equals(encodingOption.getName())) {
						// If any name doesn't match, this encoding won't fit
						continue outerLoop;
					}
				}
			}
			
			// If we reach this, the encoding will fit!
			return encodingID;
		}
		
		// If we reach this, there is no encoding that will fit, so we create a new one
		return addEncoding(new Encoding(race));
	}
	
	private int addEncoding(Encoding encoding) {
		Encoding[] newEncodings = Arrays.copyOf(encodings, encodings.length + 1);
		newEncodings[encodings.length] = encoding;
		encodings = newEncodings;
		return newEncodings.length - 1;
	}
	
	public void load() {
		if (file.exists()) {
			if (file.isFile()) {
				try {
					BitInput input = ByteArrayBitInput.fromFile(file);
					int amount = input.readInt();
					encodings = new Encoding[amount];
					
					for (int index = 0; index < amount; index++) {
						encodings[index] = Encoding.load(input);
					}
					
					input.terminate();
				} catch (IOException ioex) {
					Bukkit.getLogger().log(Level.SEVERE, "Failed to load race data encodings:", ioex);
					Bukkit.getLogger().warning("Starting with no encodings!");
					encodings = new Encoding[0];
				}
			} else {
				Bukkit.getLogger().severe("The race data encoding file (" + file + ") is not a normal file!");
				Bukkit.getLogger().warning("Starting with no encodings!");
				encodings = new Encoding[0];
			}
		} else {
			Bukkit.getLogger().warning("Could not find the race data encoding file; assuming this is the first time the plug-in is used");
			encodings = new Encoding[0];
		}
	}
	
	public void save() {
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		output.addInt(encodings.length);
		for (Encoding encoding : encodings) {
			encoding.save(output);
		}
		try {
			OutputStream fileOutput = Files.newOutputStream(file.toPath());
			fileOutput.write(output.getBytes());
			fileOutput.flush();
			fileOutput.close();
		} catch (IOException ioex) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save race progress encodings:", ioex);
		}
	}
	
	private static class BoundEncoding {
		
		private final Race race;
		private final int encoding;
		
		private BoundEncoding(Race race, int encoding) {
			this.race = race;
			this.encoding = encoding;
		}
	}
}