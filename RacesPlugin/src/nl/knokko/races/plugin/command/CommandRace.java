package nl.knokko.races.plugin.command;

import nl.knokko.races.plugin.RacesMenu;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRace implements CommandExecutor {
	
	static void sendUseage(CommandSender sender){
		sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.AQUA + "/races" + ChatColor.YELLOW + " to see all races");
		sender.sendMessage(ChatColor.YELLOW + "Use " + ChatColor.AQUA + "/race [name of the race you want to be]" + ChatColor.YELLOW + " to choose your race.");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "The " + ChatColor.AQUA + "/race" + ChatColor.AQUA + " command can only be used by players.");
			sender.sendMessage(ChatColor.RED + "You may be looking for the " + ChatColor.AQUA + "/raceprogress" + ChatColor.RED + " command.");
			return false;
		}
		/*
		Player player = (Player) sender;
		if(args.length == 0){
			Set<String> races = Race.getAllRaces();
			String current = RaceManager.getRace(player).getName();
			sender.sendMessage(ChatColor.YELLOW + "The available races are:");
			for(String race : races)
				if(race.equals(current))
					sender.sendMessage(ChatColor.GREEN + race);
				else
					sender.sendMessage(ChatColor.AQUA + race);
		}
		else if(args.length == 1){
			Race race = Race.fromName(args[0]);
			if(race != null){
				RaceManager.setRace(player, race);
				sender.sendMessage(ChatColor.GREEN + "You have become a" + (addN(race.getName()) ? 'n' : "") + " " + ChatColor.AQUA + race.getName());
			}
			else {
				sender.sendMessage(ChatColor.RED + "There is no race with the name " + ChatColor.AQUA + args[0] + ChatColor.RED + ".");
				sender.sendMessage(ChatColor.RED + "Use " + ChatColor.AQUA + "/races" + ChatColor.RED + " to see all the available races.");
			}
		}
		else
			sendUseage(sender);
			*/
		((Player)sender).openInventory(RacesMenu.createRaceMenu());
		return false;
	}
	
	boolean addN(String r){
		return r.startsWith("a") || r.startsWith("e") || r.startsWith("i") || r.startsWith("o");
	}
}
