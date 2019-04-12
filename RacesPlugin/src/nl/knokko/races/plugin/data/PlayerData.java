package nl.knokko.races.plugin.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import nl.knokko.races.base.Race;
import nl.knokko.races.plugin.manager.RaceManager;
import nl.knokko.races.progress.RaceChoise;
import nl.knokko.races.progress.RaceProgress;
import nl.knokko.races.progress.RaceProgress.MaybeResult;
import nl.knokko.races.utils.Maths;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class PlayerData {

	private final File file;

	private Race currentRace;
	private RaceProgress currentProgress;

	private List<Pair> racesProgress;

	public PlayerData(File playersFolder, UUID playerID) {
		file = new File(playersFolder + "/" + playerID + ".pydt");
	}

	public RaceProgress getProgress() {
		return currentProgress;
	}

	public Race getCurrentRace() {
		return currentRace;
	}

	public void setCurrentRace(Race newRace) {
		currentRace = newRace;

		for (Pair pair : racesProgress) {
			if (pair.race == newRace) {
				currentProgress = pair.progress;
				return;
			}
		}

		// If the progress for the new race was found, we would have returned already
		currentProgress = new RaceProgress(newRace);
		racesProgress.add(new Pair(newRace, currentProgress, -1));
	}

	public void load(String playerName) {
		if (file.exists()) {
			if (file.isFile()) {
				try {
					BitInput input = ByteArrayBitInput.fromFile(file);

					String currentRaceName = input.readString();
					currentRace = Race.fromName(currentRaceName);
					if (currentRace == null) {
						Bukkit.getLogger().warning("Player " + playerName + " was race " + currentRaceName
								+ ", but that race no longer exists!");
						currentRace = RaceManager.getDefaultRace();
					}
					
					EncodingMap encodingMap = DataManager.getEncodings();

					int racesDataAmount = input.readInt();
					racesProgress = new ArrayList<Pair>(racesDataAmount);
					for (int counter = 0; counter < racesDataAmount; counter++) {
						String nextRaceName = input.readString();
						Race nextRace = Race.fromName(nextRaceName);
						int encodingID = input.readInt();
						Encoding encoding = encodingMap.getEncoding(encodingID);
						
						int fieldsAmount = encoding.getFieldAmount();
						int choiceAmount = encoding.getChoiseAmount();

						if (nextRace == null) {

							// Discard the data (but it must still be consumed)
							for (int index = 0; index < fieldsAmount; index++) {
								encoding.getField(index).getType().load(input);
							}
							for (int index = 0; index < choiceAmount; index++) {
								input.readNumber(Maths.logUp(encoding.getChoise(index).getBackingOptions().length), false);
							}
							Bukkit.getLogger().warning("Player " + playerName + " had progress for race " + nextRaceName
									+ ", but that race has been removed.");
						} else {

							int raceEncodingID = encodingMap.findEncoding(nextRace);
							RaceProgress nextProgress = new RaceProgress(nextRace);

							if (raceEncodingID != encodingID) {
								
								/*
								 * If the raceEncoding doesn't match the loaded encoding, the data that
								 * was saved was for a previous version of the race that had other choices
								 * or fields than the current version of the race.
								 * Now that this is the case, we will restore everything that can be
								 * restored.
								 */

								for (int fieldIndex = 0; fieldIndex < fieldsAmount; fieldIndex++) {
									Encoding.FieldPair currentField = encoding.getField(fieldIndex);
									MaybeResult result = nextProgress.maybeSetValue(currentField.getName(),
											currentField.getType().load(input), currentField.getType());
									if (result == MaybeResult.UPGRADED_TYPE) {
										Bukkit.getLogger().warning("The type of " + currentField.getName()
												+ " has been changed from " + currentField.getType());
									}
									if (result == MaybeResult.TYPE_FAIL) {
										Bukkit.getLogger().warning("The value of " + currentField.getName()
												+ " for player " + playerName + " has been reset due to type problems");
									}
									if (result == MaybeResult.FAIL) {
										Bukkit.getLogger().warning("The value of " + currentField.getName() + " for "
												+ playerName + " has been removed");
									}
								}
								
								for (int choiseIndex = 0; choiseIndex < choiceAmount; choiseIndex++) {
									Encoding.ChoisePair encodingChoise = encoding.getChoise(choiseIndex);
									RaceChoise raceChoise = nextProgress.getChoise(encodingChoise.getID());
									
									byte optionBits = Maths.logUp(encodingChoise.getBackingOptions().length);
									int encodedOrdinal = (int) input.readNumber(optionBits, false);
									if (raceChoise == null) {
										Bukkit.getLogger().warning("Deleted the choice of " + playerName + " for " + encodingChoise.getID());
									} else {
										Encoding.ChoisePair.Value chosen = encodingChoise.getBackingOptions()[encodedOrdinal];
										RaceChoise.Value raceValue = raceChoise.getByString(chosen.getName());
										if (raceValue == null) {
											Bukkit.getLogger().warning("Resetted the choice of " + playerName + " for " + encodingChoise.getID() + " because his old choice has been removed");
										} else {
											nextProgress.choose(raceChoise, raceValue);
										}
									}
								}
							} else {
								
								// This is the nice and fast normal load method
								nextProgress.load(input);
							}
							
							racesProgress.add(new Pair(nextRace, nextProgress, raceEncodingID));
							
							if (nextRace == currentRace) {
								currentProgress = nextProgress;
							}
						}
					}

					if (currentProgress == null) {
						currentProgress = new RaceProgress(currentRace);
						racesProgress.add(new Pair(currentRace, currentProgress, -1));
					}

					input.terminate();
				} catch (IOException ioex) {
					Bukkit.getLogger().log(Level.SEVERE, "Failed to open data file for player " + playerName + ":",
							ioex);
					Bukkit.getLogger().warning("Creating new data for player " + playerName);
					initialSetup();
				}
			} else {
				Bukkit.getLogger().severe("The data file for player " + playerName + " is not a regular file!");
				Bukkit.getLogger().warning("Creating new data for player " + playerName);
				initialSetup();
			}
		} else {
			Bukkit.getLogger().warning(
					"Couldn't find data for player " + playerName + "; creating initial data for this player...");
			initialSetup();
		}
	}

	private void initialSetup() {
		currentRace = RaceManager.getDefaultRace();
		currentProgress = new RaceProgress(currentRace);
		racesProgress = new ArrayList<Pair>(2);
		racesProgress.add(new Pair(currentRace, currentProgress, DataManager.getEncodings().findEncoding(currentRace)));
	}

	public void save() {
		ByteArrayBitOutput output = new ByteArrayBitOutput();
		output.addString(currentRace.getName());
		output.addInt(racesProgress.size());
		for (Pair pair : racesProgress) {
			output.addString(pair.race.getName());
			output.addInt(pair.encodingToSave);
			pair.progress.save(output);
		}
		try {
			OutputStream fileOutput = Files.newOutputStream(file.toPath());
			fileOutput.write(output.getBytes());
			fileOutput.flush();
			fileOutput.close();
		} catch (IOException ioex) {
			Bukkit.getLogger().log(Level.SEVERE, "Failed to save data player data to " + file, ioex);
		}
	}

	private static class Pair {

		private Race race;
		private RaceProgress progress;

		private int encodingToSave;

		private Pair(Race race, RaceProgress progress, int encodingToSave) {
			this.race = race;
			this.progress = progress;
			this.encodingToSave = encodingToSave;
		}
	}
}