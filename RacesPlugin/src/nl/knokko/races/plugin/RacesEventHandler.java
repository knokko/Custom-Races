package nl.knokko.races.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import nl.knokko.core.plugin.player.Players;
import nl.knokko.races.base.Race;
import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.block.ReflectedBlock;
import nl.knokko.races.conditions.RacePresentor;
import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.event.RaceAttackEntityEvent;
import nl.knokko.races.event.RaceAttackRaceEvent;
import nl.knokko.races.event.RaceBreakBlockEvent;
import nl.knokko.races.event.RaceDieEvent;
import nl.knokko.races.event.RaceHurtByEntityEvent;
import nl.knokko.races.event.RaceHurtByRaceEvent;
import nl.knokko.races.event.RaceHurtEvent;
import nl.knokko.races.event.RaceKillEntityEvent;
import nl.knokko.races.event.RaceKillRaceEvent;
import nl.knokko.races.event.RaceKilledByEntityEvent;
import nl.knokko.races.event.RaceKilledByRaceEvent;
import nl.knokko.races.event.RaceLeftClickEvent;
import nl.knokko.races.event.RaceReceiveExpEvent;
import nl.knokko.races.event.RaceRightClickEvent;
import nl.knokko.races.event.RaceUpdateEvent;
import nl.knokko.races.item.ReflectedItem;
import nl.knokko.races.plugin.data.DataManager;
import nl.knokko.races.plugin.manager.RaceManager;
import nl.knokko.races.plugin.manager.RaceManager.BukkitRaceConditions;
import nl.knokko.races.potion.PermanentEffect;
import nl.knokko.races.potion.ReflectedEffect;
import nl.knokko.races.potion.ReflectedEffectType;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RacesEventHandler implements Listener {

	private static final UUID ID_SPEED = new UUID(498523, 290489);
	private static final UUID ID_ATTACK_SPEED = new UUID(5137278L, 2348379274237L);
	private static final UUID ID_HEALTH = new UUID(62836628238L, 8293783478347L);
	private static final UUID ID_ARMOR = new UUID(182367246L, 924782438346L);

	private static final Map<Race, RaceUpdater> updaters = new HashMap<Race, RaceUpdater>();

	public static void startUpdater() {
		
		// Delay it to give other plug-ins a chance to register races before we start the updater
		Bukkit.getScheduler().scheduleSyncDelayedTask(RacesPlugin.instance(), () -> {
			Set<String> races = Race.getAllRaces();
			for (String r : races) {
				Race race = Race.fromName(r);
				if (race.needsUpdate()) {
					RaceUpdater updater = new RaceUpdater(race);
					Bukkit.getScheduler().scheduleSyncRepeatingTask(RacesPlugin.instance(), updater, 0,
							race.getUpdatePeriod());
					updaters.put(race, updater);
					race.setUpdater(updater);
				} else {

					// Still allow manual updates
					race.setUpdater(new RaceUpdater(race));
				}
			}
			Bukkit.getScheduler().scheduleSyncRepeatingTask(RacesPlugin.instance(), new EffectUpdater(), 0, 40);
			// TODO something is wrong with the particles, but it seems to be a bug in
			// Bukkit
		}, 5);
	}

	/**
	 * Should only be called from RaceManager.setRace()
	 */
	public static void changeRace(Player player, Race old, Race newRace) {
		RaceUpdater oldUpdater = updaters.get(old);
		if (oldUpdater != null) {
			Iterator<RaceUpdater.PlayerEntry> it = oldUpdater.players.iterator();
			while (it.hasNext()) {
				RaceUpdater.PlayerEntry entry = it.next();
				if (entry.player.equals(player))
					it.remove();
			}
		}
		RaceUpdater newUpdater = updaters.get(newRace);
		if (newUpdater != null)
			newUpdater.players.add(new RaceUpdater.PlayerEntry(player, RaceManager.getRacePresentor(player)));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
	public void onItemUse(PlayerInteractEvent event) {
		Race race = RaceManager.getRace(event.getPlayer());
		Action action = event.getAction();
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)
			race.raceLeftClick(new RaceLeftClickEvent(RaceManager.getRacePresentor(event.getPlayer(), race),
					itemFromBukkitMaterial(event.getMaterial())));
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
			race.raceRightClick(new RaceRightClickEvent(RaceManager.getRacePresentor(event.getPlayer(), race),
					itemFromBukkitMaterial(event.getMaterial())));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Race race = RaceManager.getRace(event.getPlayer());
		Block b = event.getBlock();
		RaceBreakBlockEvent raceEvent = new RaceBreakBlockEvent(RaceManager.getRacePresentor(event.getPlayer(), race),
				blockFromBukkitMaterial(b.getType()), b.getX(), b.getY(), b.getZ());
		race.raceBreakBlock(raceEvent);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onXP(PlayerExpChangeEvent event) {
		Race race = RaceManager.getRace(event.getPlayer());
		RaceReceiveExpEvent raceEvent = new RaceReceiveExpEvent(RaceManager.getRacePresentor(event.getPlayer()),
				event.getAmount());
		race.onReceiveXP(raceEvent);
		event.setAmount(raceEvent.getAmount());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void applyRaceEffects(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			Race race = RaceManager.getRace(damager);
			RaceStatsConditions cond = RaceManager.getConditions(damager, race);
			double damage = event.getDamage();
			damage *= race.getStrengthMultiplier(cond);
			damage += race.getExtraDamage(cond);
			/*
			 * event.getEntity().setFireTicks(race.getOnAttackFireTicks(cond));
			 * if(event.getEntity() instanceof LivingEntity) applyEffects((LivingEntity)
			 * event.getEntity(), race.getOnAttackPotionEffects(cond));
			 * event.setDamage(event.getDamage() * race.getStrengthMultiplier(cond) +
			 * race.getExtraDamage(cond));
			 */
			ReflectedItem weapon = null;
			ItemStack mainWeapon = damager.getInventory().getItemInMainHand();
			if (mainWeapon != null && mainWeapon.getType() != Material.AIR && mainWeapon.getAmount() > 0)
				weapon = itemFromBukkitMaterial(mainWeapon.getType());
			RaceAttackEntityEvent entityEvent = new RaceAttackEntityEvent(
					RaceManager.getRacePresentor(damager, race, cond),
					RaceManager.getEntityPresentor(event.getEntity()), weapon, damage);
			race.raceAttacksEntity(entityEvent);
			damage = entityEvent.getDamage();
			if (damage <= 0) {
				event.setCancelled(true);
				event.setDamage(0);
				return;
			}
			event.setDamage(damage);
			if (event.getEntity() instanceof Player) {
				Player victim = (Player) event.getEntity();
				RaceAttackRaceEvent playerEvent = new RaceAttackRaceEvent(
						RaceManager.getRacePresentor(damager, race, cond), RaceManager.getRacePresentor(victim),
						damage);
				race.raceAttacksRace(playerEvent);
				damage = playerEvent.getDamage();
				if (damage <= 0) {
					event.setCancelled(true);
					event.setDamage(0);
					return;
				}
				event.setDamage(damage);
			}
		}
		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				Player damager = (Player) arrow.getShooter();
				Race race = RaceManager.getRace(damager);
				RaceStatsConditions cond = RaceManager.getConditions(damager, race);
				event.setDamage(event.getDamage() * race.getArcheryFactor(cond));
				// RaceShootEntityEvent ?
			}
		}
		if (event.getEntity() instanceof Player) {
			Player target = (Player) event.getEntity();
			Race race = RaceManager.getRace(target);
			RaceStatsConditions stats = RaceManager.getConditions(target, race);
			/*
			 * event.getDamager().setFireTicks(race.getOnHitFireTicks(stats));
			 * if(event.getDamager() instanceof LivingEntity) applyEffects((LivingEntity)
			 * event.getDamager(), race.getOnHitPotionEffects(stats)); checkEffects(target);
			 */
			double damage = event.getDamage();
			RaceHurtByEntityEvent entityEvent = new RaceHurtByEntityEvent(
					RaceManager.getRacePresentor(target, race, stats),
					RaceManager.getEntityPresentor(event.getDamager()),
					ReflectedCause.fromBukkitCause(event.getCause()), damage);
			race.raceHurtByEntity(entityEvent);
			damage = entityEvent.getDamage();
			if (damage <= 0) {
				event.setCancelled(true);
				event.setDamage(0);
				return;
			}
			event.setDamage(damage);
			if (event.getDamager() instanceof Player) {
				Player attacker = (Player) event.getDamager();
				RaceHurtByRaceEvent playerEvent = new RaceHurtByRaceEvent(
						RaceManager.getRacePresentor(target, race, stats), RaceManager.getRacePresentor(attacker),
						ReflectedCause.fromBukkitCause(event.getCause()), damage);
				race.raceHurtByPlayer(playerEvent);
				damage = playerEvent.getDamage();
				if (damage <= 0) {
					event.setCancelled(true);
					event.setDamage(0);
					return;
				}
				event.setDamage(damage);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void applyDamageReduction(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Race race = RaceManager.getRace(player);
			RaceStatsConditions stats = RaceManager.getConditions(player, race);
			ReflectedCause cause = ReflectedCause.fromBukkitCause(event.getCause());
			double resistance = race.getResistance(cause, stats);
			if (resistance >= 1)
				event.setCancelled(true);
			else if (resistance != 0)
				event.setDamage(event.getDamage() * (1 - resistance));
			RaceHurtEvent raceEvent = new RaceHurtEvent(RaceManager.getRacePresentor(player), cause, event.getDamage());
			race.raceHurtEvent(raceEvent);
			double damage = raceEvent.getDamage();
			if (damage <= 0) {
				event.setDamage(0);
				event.setCancelled(true);
				return;
			}
			event.setDamage(damage);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelItemEffects(PlayerItemConsumeEvent event) {
		Material type = event.getItem().getType();
		if (type == Material.POTION || type == Material.MILK_BUCKET || type == Material.ROTTEN_FLESH
				|| type == Material.SPIDER_EYE || type == Material.RAW_CHICKEN || type == Material.GOLDEN_APPLE)
			checkEffects(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelSplashEffects(PotionSplashEvent event) {
		Collection<PotionEffect> effects = event.getPotion().getEffects();
		boolean proceed = false;
		for (PotionEffect effect : effects) {
			if (!effect.getType().isInstant()) {
				proceed = true;
				break;
			}
		}
		if (!proceed)
			return;
		Collection<LivingEntity> targets = event.getAffectedEntities();
		for (LivingEntity target : targets)
			if (target instanceof Player)
				checkEffects((Player) target);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelConsoleEffectCommand(ServerCommandEvent event) {
		String cmd = event.getCommand();
		if (cmd.startsWith("/effect") || cmd.startsWith("effect")) {
			int index1 = cmd.indexOf(" ") + 1;
			int index2 = cmd.indexOf(" ", index1);
			if (index1 != -1 && index2 != -1) {
				String playerName = cmd.substring(index1, index2);
				Player player = Players.getOnline(playerName);
				if (player != null)
					checkEffects(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void cancelPlayerEffectCommand(PlayerCommandPreprocessEvent event) {
		String cmd = event.getMessage();
		if (cmd.startsWith("/effect") || cmd.startsWith("effect")) {
			int index1 = cmd.indexOf(" ") + 1;
			int index2 = cmd.indexOf(" ", index1);
			if (index1 != -1 && index2 != -1) {
				String playerName = cmd.substring(index1, index2);
				Player player = Players.getOnline(playerName);
				if (player != null)
					checkEffects(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void applyRaceAttributes(PlayerJoinEvent event) {
		DataManager.join(event.getPlayer());
		EffectUpdater.register(event.getPlayer());
		checkEffects(event.getPlayer());
		updatePlayerAttributesAndLevel(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void applyRaceAttributes(PlayerRespawnEvent event) {
		checkEffects(event.getPlayer());
		updatePlayerAttributesAndLevel(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRaceDie(PlayerDeathEvent event) {
		EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
		ReflectedCause cause = fromBukkitCause(damageEvent.getCause());
		Race race = RaceManager.getRace(event.getEntity());
		RacePresentor presentor = RaceManager.getRacePresentor(event.getEntity(), race);
		RaceDieEvent dieEvent = new RaceDieEvent(presentor, cause);
		race.raceDie(dieEvent);
		if (damageEvent instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) damageEvent;
			race.raceKilledByEntity(new RaceKilledByEntityEvent(presentor,
					RaceManager.getEntityPresentor(damageByEntityEvent.getDamager()), cause));
			if (damageByEntityEvent.getDamager() instanceof Player) {
				Player player = event.getEntity();
				Race killingRace = RaceManager.getRace(player);
				RacePresentor killer = RaceManager.getRacePresentor(player, killingRace);
				race.raceKilledByRaceEvent(new RaceKilledByRaceEvent(presentor, killer, cause));
				killingRace.raceKillRace(new RaceKillRaceEvent(killer, presentor));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDie(EntityDeathEvent event) {
		EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
		if (damageEvent instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent damageByEntityEvent = (EntityDamageByEntityEvent) damageEvent;
			if (damageByEntityEvent.getDamager() instanceof Player) {
				Player player = (Player) damageByEntityEvent.getDamager();
				Race race = RaceManager.getRace(player);
				RacePresentor presentor = RaceManager.getRacePresentor(player, race);
				race.raceKillEntity(
						new RaceKillEntityEvent(presentor, RaceManager.getEntityPresentor(event.getEntity())));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void unregister(PlayerQuitEvent event) {
		Race old = RaceManager.getRace(event.getPlayer());
		RaceUpdater oldUpdater = updaters.get(old);
		if (oldUpdater != null) {
			Iterator<RaceUpdater.PlayerEntry> it = oldUpdater.players.iterator();
			while (it.hasNext()) {
				RaceUpdater.PlayerEntry entry = it.next();
				if (entry.player.equals(event.getPlayer()))
					it.remove();
			}
		}
		EffectUpdater.unregister(event.getPlayer());
		DataManager.quit(event.getPlayer());
	}

	public static void disable() {
		EffectUpdater.clear();
		Collection<RaceUpdater> col = updaters.values();
		for (RaceUpdater updater : col)
			updater.players.clear();
		updaters.clear();
	}

	private static void checkEffects(Player player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(RacesPlugin.instance(), new EffectChecker(player), 5);
	}

	private static void clearPlayerAttributes(Player player) {
		removeModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, ID_SPEED);
		removeModifier(player, Attribute.GENERIC_MAX_HEALTH, ID_HEALTH);
		removeModifier(player, Attribute.GENERIC_ARMOR, ID_ARMOR);
		removeModifier(player, Attribute.GENERIC_ATTACK_SPEED, ID_ATTACK_SPEED);
	}

	private static void updatePlayerAttributesAndLevel(Player player) {
		Race race = RaceManager.getRace(player);
		RaceStatsConditions stats = RaceManager.getConditions(player, race);
		updatePlayerAttributes(race, stats, player);
		updatePlayerLevel(race, stats, player);
	}

	private static void updatePlayerAttributes(Race race, RaceStatsConditions stats, Player player) {
		clearPlayerAttributes(player);
		player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(new AttributeModifier(ID_SPEED,
				"rpg.race.speed", race.getSpeedMultiplier(stats) - 1, Operation.ADD_SCALAR));
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(
				new AttributeModifier(ID_HEALTH, "rpg.race.health", race.getExtraHealth(stats), Operation.ADD_NUMBER));
		player.getAttribute(Attribute.GENERIC_ARMOR).addModifier(
				new AttributeModifier(ID_ARMOR, "rpg.race.armor", race.getExtraArmor(stats), Operation.ADD_NUMBER));
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).addModifier(new AttributeModifier(ID_ATTACK_SPEED,
				"rpg.race.berserk", race.getAttackSpeedMultiplier(stats) - 1, Operation.ADD_SCALAR));
	}

	private static void updatePlayerLevel(Race race, RaceStatsConditions stats, Player player) {
		float levelProgress = race.getLevelProgressToShow(stats);
		if (levelProgress == levelProgress) {
			player.setExp(levelProgress);
		}
		int level = race.getLevelToShow(stats);
		if (level != -1) {
			player.setLevel(level);
		}
	}

	private static AttributeModifier getModifier(Player player, Attribute attribute, UUID id) {
		Collection<AttributeModifier> modifiers = player.getAttribute(attribute).getModifiers();
		for (AttributeModifier mod : modifiers)
			if (mod.getUniqueId().equals(id))
				return mod;
		return null;
	}

	private static void removeModifier(Player player, Attribute attribute, UUID id) {
		AttributeModifier mod = getModifier(player, attribute, id);
		if (mod != null)
			player.getAttribute(attribute).removeModifier(mod);
	}

	private static class EffectChecker implements Runnable {

		private Collection<PotionEffect> effects;
		private Player player;

		private EffectChecker(Player player) {
			this.player = player;
			this.effects = player.getActivePotionEffects();
		}

		public void run() {
			Collection<PotionEffect> newEffects = player.getActivePotionEffects();
			Race race = RaceManager.getRace(player);
			RaceStatsConditions stats = RaceManager.getConditions(player, race);
			for (PotionEffect effect : newEffects) {
				if (!effect.getType().isInstant() && !hadPotionEffect(effect.getType())) {
					float resistance = race.getResistance(new ReflectedEffectType(effect.getType().getName()), stats);
					if (resistance >= 1)
						player.removePotionEffect(effect.getType());
					else if (resistance != 0) {
						player.removePotionEffect(effect.getType());
						player.addPotionEffect(new PotionEffect(effect.getType(),
								(int) (effect.getDuration() * (1 - resistance)), effect.getAmplifier(),
								effect.isAmbient(), effect.hasParticles(), effect.getColor()));
					}
				}
			}
			Collection<PermanentEffect> pEffects = race.getPermanentEffects(stats);
			for (PermanentEffect permEffect : pEffects) {
				if (!player.hasPotionEffect(PotionEffectType.getByName(permEffect.getType().getType())))
					player.addPotionEffect(
							new PotionEffect(PotionEffectType.getByName(permEffect.getType().getType().toUpperCase()),
									Integer.MAX_VALUE, permEffect.getAmplifier(), permEffect.isAmbient(),
									permEffect.hasParticles(), toBukkitColor(permEffect.getColor())));
			}
		}

		private boolean hadPotionEffect(PotionEffectType type) {
			for (PotionEffect effect : effects)
				if (effect.getType() == type)
					return true;
			return false;
		}
	}

	private static class EffectUpdater implements Runnable {

		private static List<PlayerEffectUpdater> list = new ArrayList<PlayerEffectUpdater>();

		public static void register(Player player) {
			list.add(new PlayerEffectUpdater(player));
		}

		public static void unregister(Player player) {
			Iterator<PlayerEffectUpdater> it = list.iterator();
			while (it.hasNext()) {
				PlayerEffectUpdater peu = it.next();
				if (peu.player.equals(player)) {
					peu.quit();
					it.remove();
					return;
				}
			}
		}

		public static void clear() {
			Iterator<PlayerEffectUpdater> it = list.iterator();
			while (it.hasNext())
				it.next().quit();
			list.clear();
		}

		public void run() {
			for (PlayerEffectUpdater peu : list)
				peu.update();
		}

		private static class PlayerEffectUpdater {

			private Player player;

			private Collection<PermanentEffect> previousEffects;

			private PlayerEffectUpdater(Player player) {
				this.player = player;
				previousEffects = getCurrentEffects();
			}

			private Collection<PermanentEffect> getCurrentEffects() {
				Race race = RaceManager.getRace(player);
				return race.getPermanentEffects(RaceManager.getConditions(player, race));
			}

			private void update() {
				Collection<PermanentEffect> currentEffects = getCurrentEffects();
				if (!currentEffects.equals(previousEffects)) {
					for (PermanentEffect pe : previousEffects)
						player.removePotionEffect(toBukkitEffectType(pe.getType()));
					for (PermanentEffect pe : currentEffects)
						player.addPotionEffect(new PotionEffect(toBukkitEffectType(pe.getType()), Integer.MAX_VALUE,
								pe.getAmplifier(), pe.isAmbient(), pe.hasParticles(), toBukkitColor(pe.getColor())));
				}
				updatePlayerAttributesAndLevel(player);
				previousEffects = currentEffects;
			}

			private void quit() {
				for (PermanentEffect pe : previousEffects)
					player.removePotionEffect(toBukkitEffectType(pe.getType()));
			}
		}
	}

	private static class RaceUpdater implements Runnable, Race.UpdateAgent {

		private final Race race;

		private final List<PlayerEntry> players;

		private RaceUpdater(Race race) {
			this.race = race;
			this.players = new ArrayList<PlayerEntry>();
		}

		@Override
		public void run() {
			for (PlayerEntry player : players) {
				updatePlayerAttributesAndLevel(player.player);
				race.raceUpdate(player.event);
			}
		}

		private static class PlayerEntry {

			private final Player player;
			private final RaceUpdateEvent event;

			private PlayerEntry(Player player, RacePresentor presentor) {
				this.player = player;
				event = new RaceUpdateEvent(presentor);
			}
		}

		@Override
		public void updateLevel(RaceStatsConditions stats) {
			updatePlayerLevel(race, stats, ((BukkitRaceConditions) stats).getPlayer());
		}

		@Override
		public void updateAttributes(RaceStatsConditions stats) {
			updatePlayerAttributes(race, stats, ((BukkitRaceConditions) stats).getPlayer());
		}
	}

	public static PotionEffectType toBukkitEffectType(ReflectedEffectType type) {
		return PotionEffectType.getByName(type.getType().toUpperCase());
	}

	public static Color toBukkitColor(java.awt.Color color) {
		return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
	}

	public static PotionEffect toBukkitEffect(ReflectedEffect effect) {
		return new PotionEffect(toBukkitEffectType(effect.getType()), effect.getDuration(), effect.getAmplifier(),
				effect.isAmbient(), effect.showParticles(), toBukkitColor(effect.getColor()));
	}

	public static ReflectedItem itemFromBukkitMaterial(Material material) {
		try {
			return ReflectedItem.valueOf(material.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static ReflectedBlock blockFromBukkitMaterial(Material material) {
		try {
			return ReflectedBlock.valueOf(material.name());
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static ReflectedCause fromBukkitCause(DamageCause cause) {
		return ReflectedCause.fromBukkitCause(cause);
	}
}