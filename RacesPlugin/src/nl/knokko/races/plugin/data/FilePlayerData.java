package nl.knokko.races.plugin.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import nl.knokko.races.base.Race;
import nl.knokko.races.plugin.manager.RaceManager;
import nl.knokko.races.progress.RaceProgress;
import nl.knokko.races.utils.BitBuffer;

public class FilePlayerData extends PlayerData {
	
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
				BitBuffer generalBuffer = new BitBuffer(general);
				int raceLength = generalBuffer.readInt();
				char[] chars = new char[raceLength];
				for(int i = 0; i < raceLength; i++)
					chars[i] = generalBuffer.readChar();
				race = Race.fromName(new String(chars));
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
			BitBuffer generalBuffer = new BitBuffer(32 + race.getName().length() * 2);
			generalBuffer.addInt(race.getName().length());
			for(int i = 0; i < race.getName().length(); i++)
				generalBuffer.addChar(race.getName().charAt(i));
			generalBuffer.save(general);
			Iterator<Entry<Race,RaceProgress>> iterator = activeRacesProgress.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<Race,RaceProgress> entry = iterator.next();
				BitBuffer raceBuffer = new BitBuffer(entry.getValue().getExpectedBits());
				entry.getValue().save(raceBuffer);
				raceBuffer.save(getFile(entry.getKey()));
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
				BitBuffer buffer = new BitBuffer(file);
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
