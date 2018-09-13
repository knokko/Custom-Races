package nl.knokko.races.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import nl.knokko.races.base.Race;
import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.plugin.manager.RaceManager;
import nl.knokko.races.progress.RaceChoise;
import nl.knokko.races.progress.RaceChoise.Value;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RacesMenu implements Listener {
	
	private static final String RACE_MENU = "Race Menu";
	private static final String RACES_MENU = "Choose Your Race";
	private static final String CHOISES_MENU = "Available Choises";
	private static final String CHOISE_MENU_PRE = "Choise: ";
	
	public static void openMenu(Player player, Inventory menu){
		Bukkit.getScheduler().runTask(RacesPlugin.instance(), new MenuOpener(player, menu));
	}
	
	public static void closeMenu(Player player){
		Bukkit.getScheduler().runTask(RacesPlugin.instance(), new MenuCloser(player));
	}
	
	public static Inventory createRaceMenu(){
		Inventory menu = Bukkit.createInventory(null, 9, RACE_MENU);
		menu.setItem(0, getNamedStack(Material.BARRIER, "Close", "Close this menu and", "return to the game."));
		menu.setItem(3, getNamedStack(Material.GOLD_INGOT, "Change Race", "Open a menu where", "you can choose", "another race."));
		menu.setItem(6, getNamedStack(Material.BOOK_AND_QUILL, "Choises", "Open a menu where", "you can make", "choises about", "your race."));
		return menu;
	}
	
	private static Inventory createRacesMenu(Player player){
		Set<String> races = Race.getAllRaces();
		String current = RaceManager.getRace(player).getName();
		Inventory menu = Bukkit.createInventory(null, multipleOf9(1 + races.size()), RACES_MENU);
		menu.setItem(0, getNamedStack(Material.BARRIER, "Back", "Return to the", "previous menu."));
		int index = 1;
		for(String race : races)
			menu.setItem(index++, getNamedStack(Material.GOLD_INGOT, (race.equals(current) ? ChatColor.WHITE : ChatColor.AQUA) + race, "Choose " + (race.equals(current) ? ChatColor.WHITE : ChatColor.AQUA) + race, "as your race."));
		return menu;
	}
	
	private static Inventory createChoisesMenu(Player player){
		Race race = RaceManager.getRace(player);
		RaceStatsConditions conditions = RaceManager.getConditions(player, race);
		Collection<RaceChoise> choises = race.getChoises();
		Collection<RaceChoise> visibleChoises = new ArrayList<RaceChoise>(choises.size());
		for(RaceChoise choise : choises){
			if(choise.isVisible(conditions))
				visibleChoises.add(choise);
		}
		Inventory menu = Bukkit.createInventory(null, multipleOf9(1 + visibleChoises.size()), CHOISES_MENU);
		menu.setItem(0, getNamedStack(Material.BARRIER, "Back", "Return to the", "previous menu."));
		int index = 1;
		for(RaceChoise choise : visibleChoises)
			menu.setItem(index++, getNamedStack(Material.PAPER, choise.getDisplayName()));
		return menu;
	}
	
	private static Inventory createChoiseMenu(Player player, String choiseName, Collection<Value> values){
		Inventory menu = Bukkit.createInventory(null, multipleOf9(1 + values.size()), CHOISE_MENU_PRE + choiseName);
		menu.setItem(0, getNamedStack(Material.BARRIER, "Back", "Return to the", "choises menu."));
		int index = 0;
		for(Value value : values)
			menu.setItem(index++, getNamedStack(Material.IRON_INGOT, value.getName()));
		return menu;
	}
	
	private static int multipleOf9(int number){
		int num = number / 9;
		if(num != number / 9d)
			return num * 9 + 9;
		return number;
	}
	
	private static ItemStack getNamedStack(Material material, String name){
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
		meta.setDisplayName(name);
		ItemStack stack = new ItemStack(material);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private static ItemStack getNamedStack(Material material, String name, String... lore){
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
		meta.setDisplayName(name);
		ArrayList<String> loreList = new ArrayList<String>();
		for(String line : lore)
			loreList.add(line);
		meta.setLore(loreList);
		ItemStack stack = new ItemStack(material);
		stack.setItemMeta(meta);
		return stack;
	}
	
	private static class MenuOpener implements Runnable {
		
		private final Player player;
		
		private final Inventory menu;
		
		private MenuOpener(Player player, Inventory menu){
			this.player = player;
			this.menu = menu;
		}

		public void run() {
			player.openInventory(menu);
		}
	}
	
	private static class MenuCloser implements Runnable {
		
		private final Player player;
		
		private MenuCloser(Player player){
			this.player = player;
		}
		
		public void run(){
			player.closeInventory();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event){
		if(event.getWhoClicked() instanceof Player){
			String name = event.getInventory().getName();
			if(name == null)
				return;
			Player player = (Player) event.getWhoClicked();
			Material item = event.getCurrentItem() != null ? event.getCurrentItem().getType() : null;
			if(name.equals(RACE_MENU)){
				event.setCancelled(true);
				if(item == Material.BARRIER){
					closeMenu(player);
					return;
				}
				if(item == Material.GOLD_INGOT){
					openMenu(player, createRacesMenu(player));
					return;
				}
				if(item == Material.BOOK_AND_QUILL){
					openMenu(player, createChoisesMenu(player));
					return;
				}
				return;
			}
			if(name.equals(RACES_MENU)){
				event.setCancelled(true);
				if(item == Material.BARRIER){
					openMenu(player, createRaceMenu());
					return;
				}
				if(item == Material.GOLD_INGOT){
					ItemMeta meta = event.getCurrentItem().getItemMeta();
					if(meta != null){
						String raceName = ChatColor.stripColor(meta.getDisplayName());
						if(raceName != null && !raceName.isEmpty()){
							Race newRace = Race.fromName(raceName);
							if(newRace == null)
								player.sendMessage(ChatColor.RED + "This race doesn't exist (anymore).");
							else if(newRace == RaceManager.getRace(player))
								player.sendMessage(ChatColor.RED + "You are already " + raceName);
							else {
								RaceManager.setRace(player, newRace);
								closeMenu(player);
							}
						}
					}
					return;
				}
				return;
			}
			if(name.equals(CHOISES_MENU)){
				event.setCancelled(true);
				if(item == Material.BARRIER){
					openMenu(player, createRaceMenu());
					return;
				}
				if(item == Material.PAPER){
					ItemMeta meta = event.getCurrentItem().getItemMeta();
					if(meta != null && meta.getDisplayName() != null){
						String choiseName = meta.getDisplayName();
						Race race = RaceManager.getRace(player);
						RaceStatsConditions conditions = RaceManager.getConditions(player, race);
						Collection<RaceChoise> choises = race.getChoises();
						for(RaceChoise choise : choises){
							if(choise.getDisplayName().equals(choiseName)){
								Collection<Value> values = choise.getAvailableChoises(conditions);
								openMenu(player, createChoiseMenu(player, choiseName, values));
								return;
							}
						}
						player.sendMessage(ChatColor.RED + "Can't find that choise.");
						closeMenu(player);
					}
				}
				return;
			}
			if(name.startsWith(CHOISE_MENU_PRE)){
				event.setCancelled(true);
				if(item == Material.BARRIER){
					openMenu(player, createChoisesMenu(player));
					return;
				}
				if(item == Material.IRON_INGOT){
					ItemMeta meta = event.getCurrentItem().getItemMeta();
					if(meta != null && meta.getDisplayName() != null){
						String valueName = meta.getDisplayName();
						String choiseName = name.substring(CHOISE_MENU_PRE.length());
						Race race = RaceManager.getRace(player);
						RaceStatsConditions conditions = RaceManager.getConditions(player, race);
						Collection<RaceChoise> choises = race.getChoises();
						for(RaceChoise choise : choises){
							if(choise.getDisplayName().equals(choiseName)){
								Value[] values = choise.getAllChoises();
								for(Value value : values){
									if(value.getName().equals(valueName) && value.getChooseCondition().value(conditions)){
										RaceManager.getProgress(player, race).choose(choise, value);
										player.sendMessage(ChatColor.GREEN + "Your " + choiseName + " has been changed to " + valueName + "!");
										closeMenu(player);
										return;
									}
								}
								player.sendMessage(ChatColor.RED + "The choise you are trying to make no longer exists.");
								closeMenu(player);
								return;
							}
						}
						player.sendMessage(ChatColor.RED + "Can't find the choise.");
						closeMenu(player);
					}
					return;
				}
				return;
			}
		}
	}
}
