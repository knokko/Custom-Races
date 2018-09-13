package nl.knokko.races.plugin.command;

import nl.knokko.races.plugin.data.DataManager;
import nl.knokko.races.plugin.data.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRaceProgress implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!sender.isOp()){
			sender.sendMessage(ChatColor.RED + "Only operators can use this command!");
			return false;
		}
		if(args.length == 3){
			if(args[0].equals("get")){
				Player player = Bukkit.getPlayer(args[1]);
				if(player == null){
					sender.sendMessage(ChatColor.RED + "Can't find online player with name " + args[1]);
					return false;
				}
				PlayerData data = DataManager.getPlayerData(player);
				try {
					sender.sendMessage(ChatColor.GREEN + "The value of " + args[2] + " for player " + args[1] + " is " + data.getProgress().getValue(args[2]));
				} catch(IllegalArgumentException iae){
					sender.sendMessage(ChatColor.RED + iae.getMessage());
				}
				return false;
			}
		}
		if(args.length == 4){
			if(args[0].equals("set")){
				Player player = Bukkit.getPlayer(args[1]);
				if(player == null){
					sender.sendMessage(ChatColor.RED + "Can't find online player with name " + args[1]);
					return false;
				}
				PlayerData data = DataManager.getPlayerData(player);
				try {
					data.getProgress().setValueOf(args[2], args[3]);
					sender.sendMessage(ChatColor.GREEN + "The value of " + args[2] + " for player " + args[1] + " has been set to " + args[3]);
				} catch(IllegalArgumentException iae){
					sender.sendMessage(ChatColor.RED + iae.getMessage());
				}
				return false;
			}
		}
		sendUseage(sender);
		return false;
	}
	
	private static void sendUseage(CommandSender sender){
		sender.sendMessage(ChatColor.RED + "/raceprogress set [player] [variable name] [value]");
		sender.sendMessage(ChatColor.RED + "/raceprogress get [player] [variable name]");
	}
}
