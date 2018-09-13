package nl.knokko.races.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import nl.knokko.races.event.RaceRightClickEvent;
import nl.knokko.races.event.RaceUpdateEvent;
import nl.knokko.races.function.Function;
import nl.knokko.races.function.NamedFunction;
import nl.knokko.races.item.ReflectedItem;
import nl.knokko.races.potion.PermanentEffect;
import nl.knokko.races.potion.ReflectedEffectType;
import nl.knokko.races.progress.ProgressType;
import nl.knokko.races.progress.RaceChoise;

public abstract class Race {
	
	private static final Map<String,Race> NAME_MAP = new HashMap<String,Race>();
	
	public static final Race fromName(String name){
		return NAME_MAP.get(name);
	}
	
	public static final Set<String> getAllRaces(){
		return NAME_MAP.keySet();
	}
	
	private final String name;
	
	private final double frequency;
	private final int period;

	public Race(String name, double updateFrequency) {
		this.name = name;
		if(NAME_MAP.containsKey(name))
			throw new Error("Multiple races are registered under the name " + name + ": (" + this + " and " + NAME_MAP.get(name) + ")");
		NAME_MAP.put(name, this);
		frequency = updateFrequency;
		if(frequency > 0)
			period = Math.round((float) (20 / updateFrequency));
		else
			period = -1;
	}
	
	@Override
	public final boolean equals(Object other){
		if(other instanceof Race)
			return ((Race)other).getName().equals(getName());
		return false;
	}
	
	public final String getName(){
		return name;
	}
	
	public final int getUpdatePeriod(){
		return period;
	}
	
	public final double getFrequency(){
		return frequency;
	}
	
	public final boolean needsUpdate(){
		return period != -1;
	}
	
	public Function getFunction(String name){
		List<NamedFunction> functions = getFunctions();
		for(NamedFunction f : functions)
			if(f.getName().equals(name))
				return f.getFunction();
		return null;
	}
	
	public abstract List<ProgressType> getFields();
	
	public abstract List<NamedFunction> getFunctions();
	
	public abstract double getExtraHealth(RaceStatsConditions stats);
	
	public abstract double getExtraDamage(RaceStatsConditions stats);
	
	public abstract double getStrengthMultiplier(RaceStatsConditions stats);
	
	public abstract double getSpeedMultiplier(RaceStatsConditions stats);
	
	public abstract double getAttackSpeedMultiplier(RaceStatsConditions stats);
	
	public abstract double getExtraArmor(RaceStatsConditions stats);
	
	public abstract double getArcheryFactor(RaceStatsConditions stats);
	
	public abstract double getResistance(ReflectedCause cause, RaceStatsConditions stats);
	
	public abstract float getResistance(ReflectedEffectType type, RaceStatsConditions stats);
	
	public double[] getDamageResistances(RaceStatsConditions stats){
		double[] values = new double[ReflectedCause.values().length];
		for(int i = 0; i < values.length; i++)
			values[i] = getResistance(ReflectedCause.values()[i], stats);
		return values;
	}
	
	public abstract Collection<PermanentEffect> getPermanentEffects(RaceStatsConditions stats);
	
	public abstract Collection<RaceChoise> getChoises();
	
	public boolean isImmune(ReflectedCause cause, RaceStatsConditions stats){
		return getResistance(cause, stats) >= 1;
	}
	
	public boolean isImmune(ReflectedEffectType type, RaceStatsConditions stats){
		return getResistance(type, stats) >= 1;
	}
	
	public abstract boolean canEquipHelmet(RaceStatsConditions stats, ReflectedItem helmet);
	
	public abstract boolean canEquipChestplate(RaceStatsConditions stats, ReflectedItem plate);
	
	public abstract boolean canEquipLeggings(RaceStatsConditions stats, ReflectedItem leggings);
	
	public abstract boolean canEquipBoots(RaceStatsConditions stats, ReflectedItem boots);
	
	public abstract void raceHurtByEntity(RaceHurtByEntityEvent event);
	
	public abstract void raceHurtByPlayer(RaceHurtByRaceEvent event);
	
	public abstract void raceHurtEvent(RaceHurtEvent event);
	
	public abstract void raceAttacksEntity(RaceAttackEntityEvent event);
	
	public abstract void raceAttacksRace(RaceAttackRaceEvent event);
	
	public abstract void raceUpdate(RaceUpdateEvent event);
	
	public abstract void raceLeftClick(RaceLeftClickEvent event);
	
	public abstract void raceRightClick(RaceRightClickEvent event);
	
	public abstract void raceBreakBlock(RaceBreakBlockEvent event);
	
	public abstract void raceKillEntity(RaceKillEntityEvent event);
	
	public abstract void raceKillRace(RaceKillRaceEvent event);
	
	public abstract void raceKilledByEntity(RaceKilledByEntityEvent event);
	
	public abstract void raceKilledByRaceEvent(RaceKilledByRaceEvent event);
	
	public abstract void raceDie(RaceDieEvent event);
}
