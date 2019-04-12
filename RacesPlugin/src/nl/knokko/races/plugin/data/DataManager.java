package nl.knokko.races.plugin.data;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import nl.knokko.races.function.Function;
import nl.knokko.races.function.NamedFunction;
import nl.knokko.races.plugin.RacesPlugin;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.ByteArrayBitInput;
import nl.knokko.util.bits.ByteArrayBitOutput;

public class DataManager {
	
	private static final Map<UUID,PlayerData> MAP = new TreeMap<UUID,PlayerData>();
	
	private static List<NamedFunction> globalFunctions;
	
	private static EncodingMap encodings = new EncodingMap(new File(RacesPlugin.instance().getDataFolder() + "/encodings.bin"));
	
	private static File playersFolder;
	
	public static void stop(){
		for(PlayerData pd : MAP.values())
			pd.save();
		
		// It's important that the encodings are saved after the players data have been saved
		encodings.save();
		try {
			RacesPlugin.instance().getDataFolder().mkdirs();
			ByteArrayBitOutput buffer = new ByteArrayBitOutput(800);
			buffer.addInt(globalFunctions.size());
			for(NamedFunction function : globalFunctions){
				buffer.addString(function.getName());
				function.getFunction().save(buffer);
			}
			OutputStream fileOutput = Files.newOutputStream(new File(RacesPlugin.instance().getDataFolder() + "/globalfunctions.list").toPath());
			fileOutput.write(buffer.getBytes());
			fileOutput.flush();
			fileOutput.close();
		} catch(IOException ioex){
			Bukkit.getLogger().warning("Failed to save the global functions: " + ioex.getMessage());
		}
	}
	
	public static void start(){
		encodings.load();
		try {
			File file = new File(RacesPlugin.instance().getDataFolder() + "/globalfunctions.list");
			BitInput buffer = ByteArrayBitInput.fromFile(file);
			int size = buffer.readInt();
			globalFunctions = new ArrayList<NamedFunction>(size);
			for(int i = 0; i < size; i++)
				globalFunctions.add(new NamedFunction(buffer.readString(), Function.fromBits(buffer)));
		} catch(IOException ioex){
			Bukkit.getLogger().warning("Could not load the global functions: " + ioex.getMessage());
			Bukkit.getLogger().info("This is ok if this is the first time you use this plug-in.");
			globalFunctions = new ArrayList<NamedFunction>();
		}
	}
	
	private static File getPlayersFolder() {
		if (playersFolder == null) {
			playersFolder = new File(RacesPlugin.instance().getDataFolder() + "/players");
			playersFolder.mkdirs();
		}
		return playersFolder;
	}
	
	public static void join(Player player){
		PlayerData data = new PlayerData(getPlayersFolder(), player.getUniqueId());
		data.load(player.getName());
		MAP.put(player.getUniqueId(), data);
	}
	
	public static void quit(Player player){
		PlayerData removed = MAP.remove(player.getUniqueId());
		if (removed != null) {
			removed.save();
		} else {
			Bukkit.getLogger().warning("Data for player " + player.getName() + " has been cleared before he quit");
		}
	}
	
	public static EncodingMap getEncodings() {
		return encodings;
	}
	
	public static PlayerData getPlayerData(Player player){
		PlayerData data = MAP.get(player.getUniqueId());
		if(data == null)
			throw new IllegalStateException("player " + player.getName() + " is not marked as online!");
		return data;
	}
	
	public static NamedFunction getGlobalFunction(String name){
		for(NamedFunction nf : globalFunctions)
			if(nf.getName().equals(name))
				return nf;
		return null;
	}
	
	public static List<NamedFunction> getGlobalFunctions(){
		return globalFunctions;
	}
}
