package nl.knokko.races.plugin;

import java.io.File;

import nl.knokko.races.plugin.command.CommandRace;
import nl.knokko.races.plugin.command.CommandRaceProgress;
import nl.knokko.races.plugin.command.CommandRaces;
import nl.knokko.races.plugin.data.DataManager;
import nl.knokko.races.plugin.manager.RaceManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

public class RacesPlugin extends JavaPlugin {
	
	private static RacesPlugin instance;
	
	public static RacesPlugin instance(){
		if(instance == null)
			throw new IllegalStateException("Plugin is disabled!");
		return instance;
	}

	public RacesPlugin() {}

	public RacesPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
	}
	
	@Override
	public void onEnable(){
		instance = this;
		DataManager.start();
		RaceManager.enable();
		Bukkit.getPluginManager().registerEvents(new RacesEventHandler(), this);
		Bukkit.getPluginManager().registerEvents(new RacesMenu(), this);
		RacesEventHandler.startUpdater();
		getCommand("raceprogress").setExecutor(new CommandRaceProgress());
		getCommand("race").setExecutor(new CommandRace());
		getCommand("races").setExecutor(new CommandRaces());
	}
	
	@Override
	public void onDisable(){
		DataManager.stop();
		instance = null;
	}
}
