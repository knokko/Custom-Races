package nl.knokko.races.plugin.manager;

import java.io.File;
import java.util.logging.Level;

import nl.knokko.races.base.Race;
import nl.knokko.races.base.RaceFactory;
import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.conditions.EntityConditions;
import nl.knokko.races.conditions.EntityPresentor;
import nl.knokko.races.conditions.RacePresentor;
import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.plugin.RacesEventHandler;
import nl.knokko.races.plugin.RacesPlugin;
import nl.knokko.races.plugin.data.DataManager;
import nl.knokko.races.potion.ReflectedEffect;
import nl.knokko.races.progress.RaceProgress;
import nl.knokko.util.bits.ByteArrayBitInput;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class RaceManager {
	
	private static Race defaultRace;
	
	public static void enable(){
		try {
			File folder = new File(RacesPlugin.instance().getDataFolder() + File.separator + "races");
			folder.mkdirs();
			File[] files = folder.listFiles();
			for(File file : files){
				if(file.getName().endsWith(".race")){
					Race race = RaceFactory.loadRace(file.getName().substring(0, file.getName().length() - 5), ByteArrayBitInput.fromFile(file));
					if(defaultRace == null || race.getName().equalsIgnoreCase("default"))
						defaultRace = race;
				}
				else
					Bukkit.getLogger().log(Level.WARNING, "Ignored file " + file + " in races folder because it doesn't have the .race extension.");
			}
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	public static Race getRace(Player player){
		return DataManager.getPlayerData(player).getCurrentRace();
	}
	
	public static void setRace(Player player, Race race){
		Race old = getRace(player);
		DataManager.getPlayerData(player).setCurrentRace(race);
		RacesEventHandler.changeRace(player, old, race);
	}
	
	public static RaceStatsConditions getConditions(Player player, Race race){
		return new BukkitRaceConditions(player, race);
	}
	
	public static RaceStatsConditions getConditions(Player player){
		return getConditions(player, getRace(player));
	}
	
	public static EntityConditions getEntityConditions(Entity entity){
		if(entity instanceof Player)
			return getConditions((Player) entity);
		return new BukkitEntityConditions(entity);
	}
	
	public static RacePresentor getRacePresentor(Player player, Race race, RaceStatsConditions conds){
		return new BukkitRacePresentor(player, race, conds);
	}
	
	public static RacePresentor getRacePresentor(Player player, Race race){
		return getRacePresentor(player, race, getConditions(player, race));
	}
	
	public static RacePresentor getRacePresentor(Player player){
		return getRacePresentor(player, getRace(player));
	}
	
	public static EntityPresentor getEntityPresentor(Entity entity){
		if(entity instanceof Player)
			return getRacePresentor((Player) entity);
		return new BukkitEntityPresentor(entity);
	}
	
	public static Race getDefaultRace(){
		return defaultRace;
	}
	
	public static void setDefaultRace(Race newDefaultRace) {
		defaultRace = newDefaultRace;
	}
	
	public static RaceProgress getProgress(Player player){
		return DataManager.getPlayerData(player).getProgress();
	}
	
	private static class BukkitEntityConditions implements EntityConditions {
		
		final Entity entity;
		
		private BukkitEntityConditions(Entity entity){
			this.entity = entity;
		}

		@Override
		public long getWorldTime() {
			return entity.getWorld().getTime();
		}

		public double getTemperature() {
			return entity.getLocation().getBlock().getTemperature();
		}

		@SuppressWarnings("deprecation")
		public int getDimension() {
			return entity.getWorld().getEnvironment().getId();
		}

		@Override
		public int getLightLevel() {
			return entity.getLocation().getBlock().getLightLevel();
		}

		@Override
		public double getX() {
			return entity.getLocation().getX();
		}

		@Override
		public double getY() {
			return entity.getLocation().getY();
		}

		@Override
		public double getZ() {
			return entity.getLocation().getZ();
		}

		@Override
		public double getMotionX() {
			return entity.getVelocity().getX();
		}

		@Override
		public double getMotionY() {
			return entity.getVelocity().getY();
		}

		@Override
		public double getMotionZ() {
			return entity.getVelocity().getZ();
		}

		@Override
		public boolean inWater() {
			return entity.getLocation().getBlock().getType() == Material.WATER || entity.getLocation().getBlock().getType() == Material.STATIONARY_WATER;
		}

		@Override
		public boolean inLava() {
			return entity.getLocation().getBlock().getType() == Material.LAVA || entity.getLocation().getBlock().getType() == Material.STATIONARY_LAVA;
		}

		@Override
		public boolean seeSky() {
			return entity.getLocation().getBlock().getLightFromSky() > 0;
		}

		@Override
		public double getHearts() {
			if(entity instanceof Damageable)
				return ((Damageable) entity).getHealth() / 2;
			return 0;
		}

		@Override
		public double getMaxHearts() {
			if(entity instanceof Attributable)
				return ((Attributable) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2;
			return 0;
		}
		
		@Override
		public double getAttackDamage() {
			if (entity instanceof Attributable) {
				AttributeInstance ai = ((Attributable) entity).getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
				if (ai != null) {
					return ai.getValue();
				}
			}
			return 0;
		}

		@Override
		public double getMovementSpeed() {
			if(entity instanceof Attributable)
				return ((Attributable) entity).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
			return 0;
		}

		@Override
		public double getCurrentSpeed() {
			Vector v = entity.getVelocity();
			return 20 * Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ());
		}

		@Override
		public int getFireTicks() {
			return entity.getFireTicks();
		}

		@Override
		public String getName() {
			return entity.getName();
		}
		
	}
	
	private static class BukkitEntityPresentor extends BukkitEntityConditions implements EntityPresentor {
		
		private BukkitEntityPresentor(Entity entity){
			super(entity);
		}

		@Override
		@SuppressWarnings("deprecation")
		public void attack(ReflectedCause cause, double amount) {
			if(entity instanceof Damageable){
				entity.setLastDamageCause(new EntityDamageEvent(entity, DamageCause.valueOf(cause.name()), amount));
				((Damageable) entity).damage(amount);
			}
		}

		@Override
		public void heal(double amount) {
			if(entity instanceof Damageable && entity instanceof Attributable)
				((Damageable) entity).setHealth(Math.min(((Damageable) entity).getHealth() + amount, ((Attributable) entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
		}

		@Override
		public void teleport(double x, double y, double z) {
			entity.teleport(new Location(entity.getWorld(), x, y, z));
		}

		@Override
		public void launch(double motionX, double motionY, double motionZ) {
			entity.setVelocity(entity.getVelocity().add(new Vector(motionX, motionY, motionZ)));
		}

		@Override
		public void addPotionEffect(ReflectedEffect effect) {
			if(entity instanceof LivingEntity)
				((LivingEntity)entity).addPotionEffect(RacesEventHandler.toBukkitEffect(effect));
		}

		@Override
		public void setFire(int ticks) {
			entity.setFireTicks(ticks);
		}
	}
	
	public static class BukkitRaceConditions implements RaceStatsConditions {
		
		final Player player;
		final Race race;
		
		private BukkitRaceConditions(Player player, Race race){
			this.player = player;
			this.race = race;
		}
		
		public Player getPlayer() {
			return player;
		}
		
		public Race getRace(){
			return race;
		}
		
		public RaceProgress getProgress() {
			return RaceManager.getProgress(player);
		}

		public long getWorldTime() {
			return player.getWorld().getTime();
		}

		public double getTemperature() {
			return player.getLocation().getBlock().getTemperature();
		}

		@SuppressWarnings("deprecation")
		public int getDimension() {
			return player.getWorld().getEnvironment().getId();
		}

		@Override
		public int getLightLevel() {
			return player.getLocation().getBlock().getLightLevel();
		}

		@Override
		public double getX() {
			return player.getLocation().getX();
		}

		@Override
		public double getY() {
			return player.getLocation().getY();
		}

		@Override
		public double getZ() {
			return player.getLocation().getZ();
		}

		@Override
		public double getMotionX() {
			return player.getVelocity().getX();
		}

		@Override
		public double getMotionY() {
			return player.getVelocity().getY();
		}

		@Override
		public double getMotionZ() {
			return player.getVelocity().getZ();
		}

		@Override
		public boolean inWater() {
			return player.getLocation().getBlock().getType() == Material.WATER || player.getLocation().getBlock().getType() == Material.STATIONARY_WATER;
		}

		@Override
		public boolean inLava() {
			return player.getLocation().getBlock().getType() == Material.LAVA || player.getLocation().getBlock().getType() == Material.STATIONARY_LAVA;
		}

		@Override
		public boolean seeSky() {
			return player.getLocation().getBlock().getLightFromSky() > 0;
		}

		@Override
		public int getFoodLevel() {
			return player.getFoodLevel();
		}

		@Override
		public double getHearts() {
			return player.getHealth() / 2;
		}
		
		@Override
		public double getAttackDamage() {
			return player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue();
		}

		@Override
		public double getMaxHearts() {
			return player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 2;
		}

		@Override
		public double getMovementSpeed() {
			return player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue();
		}

		@Override
		public double getCurrentSpeed() {
			Vector v = player.getVelocity();
			return 20 * Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ());
		}

		@Override
		public int getFireTicks() {
			return player.getFireTicks();
		}

		@Override
		public String getName() {
			return player.getName();
		}
	}
	
	private static class BukkitRacePresentor extends BukkitRaceConditions implements RacePresentor {
		
		final RaceStatsConditions conditions;
		
		private BukkitRacePresentor(Player player, Race race, RaceStatsConditions conditions){
			super(player, race);
			this.conditions = conditions;
		}
		
		@Override
		@SuppressWarnings("deprecation")
		public void attack(ReflectedCause cause, double amount) {
			player.setLastDamageCause(new EntityDamageEvent(player, DamageCause.valueOf(cause.name()), amount));
			player.damage(amount);
		}

		@Override
		public void heal(double amount) {
			player.setHealth(Math.min(player.getHealth() + amount, player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
		}

		@Override
		public void teleport(double x, double y, double z) {
			player.teleport(new Location(player.getWorld(), x, y, z));
		}

		@Override
		public void launch(double motionX, double motionY, double motionZ) {
			player.setVelocity(player.getVelocity().add(new Vector(motionX, motionY, motionZ)));
		}

		@Override
		public void addPotionEffect(ReflectedEffect effect) {
			double resistance = race.getResistance(effect.getType(), conditions);
			if(resistance >= 1)
				return;
			int duration = (int) Math.round(effect.getDuration() * (1 - resistance));
			player.addPotionEffect(new PotionEffect(RacesEventHandler.toBukkitEffectType(effect.getType()), duration, effect.getLevel() - 1, effect.isAmbient(), effect.showParticles(), RacesEventHandler.toBukkitColor(effect.getColor())));
		}

		@Override
		public void setFire(int ticks) {
			player.setFireTicks(ticks);
		}
	}
}
