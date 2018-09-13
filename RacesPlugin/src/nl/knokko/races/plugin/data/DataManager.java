package nl.knokko.races.plugin.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import nl.knokko.races.function.Function;
import nl.knokko.races.function.NamedFunction;
import nl.knokko.races.plugin.RacesPlugin;
import nl.knokko.races.utils.BitBuffer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DataManager {
	
	private static final Map<UUID,PlayerData> MAP = new TreeMap<UUID,PlayerData>();
	private static List<NamedFunction> globalFunctions;
	
	public static void stop(){
		for(PlayerData pd : MAP.values())
			pd.onQuit();
		try {
			RacesPlugin.instance().getDataFolder().mkdirs();
			BitBuffer buffer = new BitBuffer(800);
			buffer.addInt(globalFunctions.size());
			for(NamedFunction function : globalFunctions){
				buffer.addString(function.getName());
				function.getFunction().save(buffer);
			}
			buffer.save(new File(RacesPlugin.instance().getDataFolder() + "/globalfunctions.list"));
		} catch(IOException ioex){
			Bukkit.getLogger().warning("Failed to save the global functions: " + ioex.getMessage());
		}
	}
	
	public static void start(){
		try {
			BitBuffer buffer = new BitBuffer(new File(RacesPlugin.instance().getDataFolder() + "/globalfunctions.list"));
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
	
	public static void join(Player player){
		PlayerData data = new FilePlayerData(getPlayerFile(player));
		data.onJoin();
		MAP.put(player.getUniqueId(), data);
	}
	
	public static void quit(Player player){
		MAP.remove(player.getUniqueId()).onQuit();
	}
	
	public static PlayerData getPlayerData(Player player){
		PlayerData data = MAP.get(player.getUniqueId());
		if(data == null)
			throw new IllegalStateException("player " + player.getName() + " is not marked as online!");
		return data;
	}
	
	private static File getPlayerFile(Player player){
		return new File(RacesPlugin.instance().getDataFolder() + File.separator + player.getUniqueId());
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
