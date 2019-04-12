package nl.knokko.races.plugin.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import nl.knokko.races.base.Race;
import nl.knokko.races.plugin.manager.RaceManager;
import nl.knokko.races.progress.RaceProgress;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class FilePlayerData extends AbstractPlayerData {
	
	private static final String EXTENSION = ".rdat";
	
	private final File folder;
	private final File general;
	
	private Race race;
	
	private Map<Race,RaceProgress> activeRacesProgress;

	public FilePlayerData(File playerFolder) {
		activeRacesProgress = new HashMap<Race,RaceProgress>();
		folder = playerFolder;
		general = new File(playerFolder + File.separator + "general.pdat");
	}

	@Override
	public Race getCurrentRace() {
		return race;
	}
	
	@Override
	public void setCurrentRace(Race race){
		this.race = race;
	}
	
	public void onJoin(){
		if(!general.exists()){
			createFirstTime();
		}
		else {
			try {
				BitInput generalBuffer = ByteArrayBitInput.fromFile(general);
				race = Race.fromName(generalBuffer.readString());
			} catch(IOException ex){
				Bukkit.getLogger().log(Level.WARNING, "Failed to load player data:", ex);
				createFirstTime();
			}
		}
		System.out.println("FilePlayerData.onJoin() race is " + race);
	}
	
	private void createFirstTime(){
		folder.mkdirs();
		race = RaceManager.getDefaultRace();
		activeRacesProgress.put(race, new RaceProgress(race));
	}

	@Override
	public void onQuit() {
		try {
			ByteArrayBitOutput generalBuffer = new ByteArrayBitOutput(32 + race.getName().length() * 2);
			generalBuffer.addString(race.getName());
			OutputStream generalOutput = Files.newOutputStream(general.toPath());
			generalOutput.write(generalBuffer.getBytes());
			generalOutput.flush();
			generalOutput.close();
			
			Iterator<Entry<Race,RaceProgress>> iterator = activeRacesProgress.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<Race,RaceProgress> entry = iterator.next();
				ByteArrayBitOutput raceBuffer = new ByteArrayBitOutput(entry.getValue().getExpectedBits() / 8 + 1);
				entry.getValue().save(raceBuffer);
				OutputStream raceOutput = Files.newOutputStream(getFile(entry.getKey()).toPath());
				raceOutput.write(raceBuffer.getBytes());
				raceOutput.flush();
				raceOutput.close();
			}
		} catch(Exception ex){
			Bukkit.getLogger().log(Level.SEVERE, "Couldn't save player data:", ex);
		}
	}
	
	@Override
	public RaceProgress getProgress(Race race) {
		return get(race);
	}
	
	private RaceProgress get(Race race){
		RaceProgress rp = activeRacesProgress.get(race);
		if(rp != null)
			return rp;
		return load(race);
	}
	
	private RaceProgress load(Race race){
		RaceProgress data = new RaceProgress(race);
		File file = getFile(race);
		if(file.exists()){
			try {
				BitInput buffer = ByteArrayBitInput.fromFile(file);
				data.load(buffer);
			} catch(IOException ex){
				throw new RuntimeException("Strange error occured while opening file " + file, ex);
			}
		}
		activeRacesProgress.put(race, data);
		return data;
	}
	
	private File getFile(Race race){
		return new File(folder + File.separator + race.getName() + EXTENSION);
	}
}
