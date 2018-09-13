package nl.knokko.races.plugin.command;

import java.util.Set;

import nl.knokko.races.base.Race;
import nl.knokko.races.plugin.manager.RaceManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRaces implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0){
			Set<String> races = Race.getAllRaces();
			String current = sender instanceof Player ? RaceManager.getRace((Player) sender).getName() : null;
			sender.sendMessage(ChatColor.YELLOW + "The available races are:");
			for(String race : races)
				if(race.equals(current))
					sender.sendMessage(ChatColor.GREEN + race);
				else
					sender.sendMessage(ChatColor.AQUA + race);
			return false;
		}
		sender.sendMessage(ChatColor.RED + "Just use " + ChatColor.AQUA + "/races");
		return false;
	}

}
