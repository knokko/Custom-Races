package nl.knokko.races.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import nl.knokko.core.plugin.CorePlugin;
import nl.knokko.core.plugin.menu.Menu;
import nl.knokko.races.base.Race;
import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.plugin.manager.RaceManager;
import nl.knokko.races.progress.RaceChoise;
import nl.knokko.races.progress.RaceChoise.Value;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RacesMenu {
	
	private static Menu raceMenu = new Menu("Race Menu", 9);
	
	static {
		raceMenu.setItem(0, Material.BARRIER, "Close", (Player player) -> {
			player.closeInventory();
		}, "Close this menu and", "return to the game.");
		raceMenu.setItem(3, Material.GOLD_INGOT, "Change Race", (Player player) -> {
			CorePlugin.getInstance().getMenuHandler().openMenu(player, createRacesMenu(player));
		}, "Open a menu where", "you can choose", "another race.");
		raceMenu.setItem(6, Material.BOOK_AND_QUILL, "Choises", (Player player) -> {
			CorePlugin.getInstance().getMenuHandler().openMenu(player, createChoisesMenu(player));
		}, "Open a menu where", "you can make", "choises about", "your race.");
	}
	
	public static Menu getRaceMenu() {
		return raceMenu;
	}
	
	private static Menu createRacesMenu(Player player) {
		Set<String> races = Race.getAllRaces();
		String current = RaceManager.getRace(player).getName();
		Menu menu = new Menu("Choose Your Race", 1 + races.size());
		menu.setItem(0, Material.BARRIER, "Back", (Player p) -> {
			CorePlugin.getInstance().getMenuHandler().openMenu(player, raceMenu);
		}, "Return to the", "previous menu.");
		int index = 1;
		for(String race : races)
			menu.setItem(index++, Material.GOLD_INGOT, (race.equals(current) ? ChatColor.WHITE : ChatColor.AQUA) + race, (Player p) -> {
				Race newRace = Race.fromName(race);
				if(newRace == null)
					player.sendMessage(ChatColor.RED + "This race doesn't exist (anymore).");
				else if(newRace == RaceManager.getRace(player))
					player.sendMessage(ChatColor.RED + "You are already " + race);
				else {
					RaceManager.setRace(player, newRace);
					player.closeInventory();
				}
			}, "Choose " + (race.equals(current) ? ChatColor.WHITE : ChatColor.AQUA) + race, "as your race.");
		return menu;
	}
	
	private static Menu createChoisesMenu(Player player){
		Race race = RaceManager.getRace(player);
		RaceStatsConditions conditions = RaceManager.getConditions(player, race);
		Collection<RaceChoise> choises = race.getChoises();
		Collection<RaceChoise> visibleChoises = new ArrayList<RaceChoise>(choises.size());
		for(RaceChoise choise : choises){
			if(choise.isVisible(conditions)) {
				visibleChoises.add(choise);
			}
		}
		Menu menu = new Menu("Available choises", 1 + visibleChoises.size());
		menu.setItem(0, Material.BARRIER, "Back", (Player p) -> {
			CorePlugin.getInstance().getMenuHandler().openMenu(p, raceMenu);
		}, "Return to the", "previous menu.");
		int index = 1;
		for(RaceChoise choise : visibleChoises) {
			menu.setItem(index++, Material.PAPER, choise.getDisplayName(), (Player p) -> {
				CorePlugin.getInstance().getMenuHandler().openMenu(p, createChoiseMenu(choise, race, conditions, player));
			});
		}
		return menu;
	}
	
	private static Menu createChoiseMenu(RaceChoise choise, Race race, RaceStatsConditions conditions, Player player) {
		Collection<Value> values = choise.getAvailableChoises(conditions);
		Menu menu = new Menu("Choise: " + choise.getDisplayName(), 1 + values.size());
		menu.setItem(0, Material.BARRIER, "Back", (Player p) -> {
			CorePlugin.getInstance().getMenuHandler().openMenu(p, createChoisesMenu(p));
		}, "Return to the", "choises menu.");
		int index = 0;
		for(Value value : values) {
			menu.setItem(index++, Material.IRON_INGOT, value.getName(), (Player p) -> {
				RaceManager.getProgress(player, race).choose(choise, value);
				player.sendMessage(ChatColor.GREEN + "Your " + choise.getDisplayName() + " has been changed to " + value.getName() + "!");
				player.closeInventory();
			});
		}
		return menu;
	}
}