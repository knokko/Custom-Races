package nl.knokko.races.plugin.command;

import nl.knokko.core.plugin.CorePlugin;
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
		CorePlugin.getInstance().getMenuHandler().openMenu((Player) sender, RacesMenu.getRaceMenu());
		return false;
	}
	
	boolean addN(String r){
		return r.startsWith("a") || r.startsWith("e") || r.startsWith("i") || r.startsWith("o");
	}
}
