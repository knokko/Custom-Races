package nl.knokko.races.base;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.knokko.races.condition.Condition;
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
import nl.knokko.races.function.FunctionConstant;
import nl.knokko.races.function.NamedFunction;
import nl.knokko.races.item.ReflectedItem;
import nl.knokko.races.potion.PermanentEffect;
import nl.knokko.races.potion.PermanentPotionFunction;
import nl.knokko.races.potion.PotionFunction;
import nl.knokko.races.potion.ReflectedEffect;
import nl.knokko.races.potion.ReflectedEffectType;
import nl.knokko.races.progress.ProgressType;
import nl.knokko.races.progress.RaceChoise;
import nl.knokko.races.progress.ValueType;
import nl.knokko.races.script.encoding.ScriptEncoding;
import nl.knokko.races.utils.BitBuffer;
import static nl.knokko.races.item.ReflectedItem.*;

public class RaceFactory {
	
	private static final byte ID_SIMPLE_RACE1 = -127;
	private static final byte ID_ADVANCED_RACE1 = -126;
	
	public static void saveAsSimpleRace2(BitBuffer buffer, byte extraHealth, byte extraArmor, short extraDamage, 
			float strengthMultiplier, float speedMultiplier, float attackSpeedMultiplier, 
			float arrowDamageMultiplier, int onHitFireTicks, int onAttackFireTicks, 
			Collection<PermanentEffect> permanentEffects, List<ReflectedEffect> onHitEffects, 
			List<ReflectedEffect> onAttackEffects, double[] damageResistances, 
			Map<ReflectedEffectType,Float> effectResistances, SimpleRace.SimpleEquipment equipment){
		buffer.addByte(ID_SIMPLE_RACE1);
		buffer.addByte(extraHealth);
		buffer.addByte(extraArmor);
		buffer.addShort(extraDamage);
		buffer.addFloat(strengthMultiplier);
		buffer.addFloat(speedMultiplier);
		buffer.addFloat(attackSpeedMultiplier);
		buffer.addFloat(arrowDamageMultiplier);
		buffer.addInt(onHitFireTicks);
		buffer.addInt(onAttackFireTicks);
		
		buffer.addInt(permanentEffects.size());
		for(PermanentEffect pe : permanentEffects)
			pe.save(buffer);
		
		buffer.addInt(onHitEffects.size());
		for(ReflectedEffect effect : onHitEffects)
			savePotionEffect(buffer, effect);
		
		buffer.addInt(onAttackEffects.size());
		for(ReflectedEffect effect : onAttackEffects)
			savePotionEffect(buffer, effect);
		
		for(double resistance : damageResistances)
			buffer.addDouble(resistance);
		
		buffer.addInt(effectResistances.size());
		for(Entry<ReflectedEffectType,Float> entry : effectResistances.entrySet()){
			buffer.addString(entry.getKey().getType());
			buffer.addFloat(entry.getValue());
		}
		
		buffer.addBoolean(equipment.leatherBoots);
		buffer.addBoolean(equipment.leatherLeggings);
		buffer.addBoolean(equipment.leatherChestplate);
		buffer.addBoolean(equipment.leatherHelmet);
		
		buffer.addBoolean(equipment.goldBoots);
		buffer.addBoolean(equipment.goldLeggings);
		buffer.addBoolean(equipment.goldChestplate);
		buffer.addBoolean(equipment.goldHelmet);
		
		buffer.addBoolean(equipment.chainBoots);
		buffer.addBoolean(equipment.chainLeggings);
		buffer.addBoolean(equipment.chainChestplate);
		buffer.addBoolean(equipment.chainHelmet);
		
		buffer.addBoolean(equipment.ironBoots);
		buffer.addBoolean(equipment.ironLeggings);
		buffer.addBoolean(equipment.ironChestplate);
		buffer.addBoolean(equipment.ironHelmet);
		
		buffer.addBoolean(equipment.diamondBoots);
		buffer.addBoolean(equipment.diamondLeggings);
		buffer.addBoolean(equipment.diamondChestplate);
		buffer.addBoolean(equipment.diamondHelmet);
		
		buffer.addBoolean(equipment.pumpkin);
		buffer.addBoolean(equipment.head);
	}
	
	public static void saveAsAdvancedRace1(BitBuffer buffer, List<ProgressType> fields, List<NamedFunction> functions, List<RaceChoise> choises,
			Function health, Function damage, Function strength, Function speed, Function attackSpeed, Function armor, Function archery,
			Function hitFireTicks, Function attackFireTicks, PotionFunction[] hitPotions, PotionFunction[] attackPotions,
			PermanentPotionFunction[] permanentEffects, Function[] damageResistances, Map<ReflectedEffectType,Function> effectResistances){
		
		buffer.addByte(ID_ADVANCED_RACE1);
		
		buffer.addInt(fields.size());
		for(ProgressType pt : fields){
			buffer.addString(pt.getName());
			buffer.addByte(pt.getType().getID());
		}
		
		buffer.addInt(functions.size());
		for(NamedFunction nf : functions){
			buffer.addString(nf.getName());
			nf.getFunction().save(buffer);
		}
		
		buffer.addInt(choises.size());
		for(RaceChoise rc : choises)
			rc.save(buffer);
		
		health.save(buffer);
		damage.save(buffer);
		strength.save(buffer);
		speed.save(buffer);
		attackSpeed.save(buffer);
		armor.save(buffer);
		archery.save(buffer);
		
		hitFireTicks.save(buffer);
		attackFireTicks.save(buffer);
		
		buffer.addInt(hitPotions.length);
		for(PotionFunction pf : hitPotions)
			pf.save(buffer);
		
		buffer.addInt(attackPotions.length);
		for(PotionFunction pf : attackPotions)
			pf.save(buffer);
		
		buffer.addInt(permanentEffects.length);
		for(PermanentPotionFunction ppf : permanentEffects)
			ppf.save(buffer);
		
		buffer.addInt(damageResistances.length);//allow newer MC versions to load the race as well
		for(Function resistance : damageResistances)
			resistance.save(buffer);
		
		Set<Entry<ReflectedEffectType,Function>> effectResistanceSet = effectResistances.entrySet();
		buffer.addInt(effectResistances.size());
		for(Entry<ReflectedEffectType,Function> entry : effectResistanceSet){
			buffer.addString(entry.getKey().getType());
			entry.getValue().save(buffer);
		}
	}
	
	public static Race loadRace(String name, BitBuffer buffer){
		byte type = buffer.readByte();
		if(type == ID_SIMPLE_RACE1)
			return loadSimpleRace2(name, buffer);
		if(type == ID_ADVANCED_RACE1)
			return loadAdvancedRace1(name, buffer);
		throw new IllegalArgumentException("Unknown type: " + type);
	}
	
	public static SimpleRace loadSimpleRace2(String name, BitBuffer buffer){
		byte health = buffer.readByte();
		byte armor = buffer.readByte();
		short damage = buffer.readShort();
		float strength = buffer.readFloat();
		float speed = buffer.readFloat();
		float attackSpeed = buffer.readFloat();
		float archery = buffer.readFloat();
		int hitFireTicks = buffer.readInt();
		int attackFireTicks = buffer.readInt();
		
		int permanentCount = buffer.readInt();
		Collection<PermanentEffect> permEffects = new ArrayList<PermanentEffect>();
		for(int i = 0; i < permanentCount; i++)
			permEffects.add(PermanentEffect.load(buffer));
		
		int onHitCount = buffer.readInt();
		List<ReflectedEffect> onHitEffects = new ArrayList<ReflectedEffect>(onHitCount);
		for(int i = 0; i < onHitCount; i++)
			onHitEffects.add(readPotionEffect(buffer));
		
		int onAttackCount = buffer.readInt();
		List<ReflectedEffect> onAttackEffects = new ArrayList<ReflectedEffect>(onAttackCount);
		for(int i = 0; i < onAttackCount; i++)
			onAttackEffects.add(readPotionEffect(buffer));
		
		double[] damageResistances = new double[ReflectedCause.values().length];
		for(int i = 0; i < damageResistances.length; i++)
			damageResistances[i] = buffer.readDouble();
		
		int effectResistanceCount = buffer.readInt();
		Map<ReflectedEffectType,Float> effectResistances = new HashMap<ReflectedEffectType,Float>();
		for(int i = 0; i < effectResistanceCount; i++)
			effectResistances.put(new ReflectedEffectType(buffer.readString()), buffer.readFloat());
		
		SimpleRace.SimpleEquipment equipment = new SimpleRace.SimpleEquipment(
				buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(),
				buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(),
				buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(),
				buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(),
				buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(),
				buffer.readBoolean(), buffer.readBoolean());
		return new SimpleRace(name, health, armor, damage, strength, speed, attackSpeed, archery, hitFireTicks, 
				attackFireTicks, permEffects, onHitEffects, onAttackEffects, damageResistances, 
				effectResistances, equipment);
	}
	
	public static AdvancedRace loadAdvancedRace1(String name, BitBuffer buffer){
		int fieldCount = buffer.readInt();
		List<ProgressType> fields = new ArrayList<ProgressType>(fieldCount);
		for(int i = 0; i < fieldCount; i++){
			String varName = buffer.readString();
			ValueType type = ValueType.fromID(buffer.readByte());
			fields.add(new ProgressType(varName, type, type.load(buffer)));
		}
		
		int functionCount = buffer.readInt();
		List<NamedFunction> functions = new ArrayList<NamedFunction>(functionCount);
		for(int i = 0; i < functionCount; i++)
			functions.add(new NamedFunction(buffer.readString(), Function.fromBits(buffer)));
		
		int choiseCount = buffer.readInt();
		List<RaceChoise> choises = new ArrayList<RaceChoise>(choiseCount);
		for(int i = 0; i < choiseCount; i++)
			choises.add(RaceChoise.fromBits(buffer));
		
		Function health = Function.fromBits(buffer);
		Function damage = Function.fromBits(buffer);
		Function strength = Function.fromBits(buffer);
		Function speed = Function.fromBits(buffer);
		Function attackSpeed = Function.fromBits(buffer);
		Function armor = Function.fromBits(buffer);
		Function archery = Function.fromBits(buffer);
		Function hitFire = Function.fromBits(buffer);
		Function attackFire = Function.fromBits(buffer);
		
		PotionFunction[] hitEffects = new PotionFunction[buffer.readInt()];
		for(int i = 0; i < hitEffects.length; i++)
			hitEffects[i] = PotionFunction.fromBits(buffer);
		
		PotionFunction[] attackEffects = new PotionFunction[buffer.readInt()];
		for(int i = 0; i < attackEffects.length; i++)
			attackEffects[i] = PotionFunction.fromBits(buffer);
		
		PermanentPotionFunction[] permEffects = new PermanentPotionFunction[buffer.readInt()];
		for(int i = 0; i < permEffects.length; i++)
			permEffects[i] = PermanentPotionFunction.fromBits(buffer);
		
		Function[] damageResistances = new Function[ReflectedCause.values().length];
		int storedDamageResistanceCount = buffer.readInt();
		int j = 0;
		for(; j < storedDamageResistanceCount; j++)
			damageResistances[j] = Function.fromBits(buffer);
		for(; j < damageResistances.length; j++)
			damageResistances[j] = new FunctionConstant.Byte((byte) 0);//make compatible with older MC versions
		
		int effectMapSize = buffer.readInt();
		Map<ReflectedEffectType,Function> effectResistances = new HashMap<ReflectedEffectType,Function>();
		for(int i = 0; i < effectMapSize; i++)
			effectResistances.put(new ReflectedEffectType(buffer.readString()), Function.fromBits(buffer));
		
		return new AdvancedRace(name, frequency, fields, functions, choises, health, damage, strength, speed, attackSpeed,
				armor, archery, hitFire, attackFire, hitEffects, attackEffects, permEffects, damageResistances, effectResistances, advancedEquipment);
	}
	
	private static ReflectedEffect readPotionEffect(BitBuffer buffer){
		return new ReflectedEffect(new ReflectedEffectType(buffer.readString()), buffer.readInt(), buffer.readByte() + 1, buffer.readBoolean(), buffer.readBoolean(), readColor(buffer));
	}
	
	private static void savePotionEffect(BitBuffer buffer, ReflectedEffect effect){
		buffer.addString(effect.getType().getType());
		buffer.addInt(effect.getDuration());
		buffer.addByte(effect.getAmplifier());
		buffer.addBoolean(effect.showParticles());
		buffer.addBoolean(effect.isAmbient());
		saveColor(buffer, effect.getColor());
	}
	
	public static Color readColor(BitBuffer buffer){
		return new Color(buffer.readByte() + 128, buffer.readByte() + 128, buffer.readByte() + 128);
	}
	
	public static void saveColor(BitBuffer buffer, Color color){
		if(color != null){
			buffer.addByte((byte) (color.getRed() - 128));
			buffer.addByte((byte) (color.getGreen() - 128));
			buffer.addByte((byte) (color.getBlue() - 128));
		}
		else {
			throw new NullPointerException("color is null");
		}
	}
	
	public static class SimpleRace extends Race {
		
		private static final List<ProgressType> FIELDS = new ArrayList<ProgressType>(0);
		private static final Collection<RaceChoise> CHOISES = new ArrayList<RaceChoise>(0);
		private static final List<NamedFunction> FUNCTIONS = new ArrayList<NamedFunction>(0);
		
		private final byte extraHealth;
		private final byte extraArmor;
		private final short extraDamage;
		private final int onHitFireTicks;
		private final int onAttackFireTicks;
		
		private final float strengthMultiplier;
		private final float speedMultiplier;
		private final float attackSpeedMultiplier;
		private final float archery;
		
		private final double[] damageResistances;
		
		private final Collection<PermanentEffect> permanentEffects;
		private final List<ReflectedEffect> onHitEffects;
		private final List<ReflectedEffect> onAttackEffects;
		private final SimpleEquipment equipment;
		
		private final Map<ReflectedEffectType,Float> effectResistances;
		
		private SimpleRace(String name, byte extraHealth, byte extraArmor, short extraDamage, 
				float strengthMultiplier, float speedMultiplier, float attackSpeedMultiplier, 
				float arrowDamageMultiplier, int onHitFireTicks, int onAttackFireTicks, 
				Collection<PermanentEffect> permanentEffects, List<ReflectedEffect> onHitEffects, 
				List<ReflectedEffect> onAttackEffects, double[] damageResistances, 
				Map<ReflectedEffectType,Float> effectResistances, SimpleEquipment equipment){
			super(name, 0);
			this.extraHealth = extraHealth;
			this.extraArmor = extraArmor;
			this.extraDamage = extraDamage;
			this.strengthMultiplier = strengthMultiplier;
			this.speedMultiplier = speedMultiplier;
			this.attackSpeedMultiplier = attackSpeedMultiplier;
			this.archery = arrowDamageMultiplier;
			this.onHitFireTicks = onHitFireTicks;
			this.onAttackFireTicks = onAttackFireTicks;
			this.damageResistances = damageResistances;
			this.effectResistances = effectResistances;
			this.permanentEffects = permanentEffects;
			this.onHitEffects = onHitEffects;
			this.onAttackEffects = onAttackEffects;
			this.equipment = equipment;
		}

		@Override
		public List<ProgressType> getFields() {
			return FIELDS;
		}

		@Override
		public double getExtraHealth(RaceStatsConditions stats) {
			return extraHealth;
		}

		@Override
		public double getExtraDamage(RaceStatsConditions stats) {
			return extraDamage;
		}

		@Override
		public double getStrengthMultiplier(RaceStatsConditions stats) {
			return strengthMultiplier;
		}

		@Override
		public double getSpeedMultiplier(RaceStatsConditions stats) {
			return speedMultiplier;
		}

		@Override
		public double getAttackSpeedMultiplier(RaceStatsConditions stats) {
			return attackSpeedMultiplier;
		}

		@Override
		public double getExtraArmor(RaceStatsConditions stats) {
			return extraArmor;
		}
		
		@Override
		public double getArcheryFactor(RaceStatsConditions stats){
			return archery;
		}

		@Override
		public double getResistance(ReflectedCause cause, RaceStatsConditions stats) {
			return damageResistances[cause.ordinal()];
		}
		
		@Override
		public float getResistance(ReflectedEffectType type, RaceStatsConditions stats) {
			return effectResistances.get(type);
		}

		@Override
		public Collection<PermanentEffect> getPermanentEffects(RaceStatsConditions stats) {
			return permanentEffects;
		}

		@Override
		public Collection<RaceChoise> getChoises() {
			return CHOISES;
		}

		@Override
		public List<NamedFunction> getFunctions() {
			return FUNCTIONS;
		}
		
		@Override
		public Function getFunction(String name){
			return null;
		}

		@Override
		public boolean canEquipHelmet(RaceStatsConditions stats, ReflectedItem helmet) {
			if(helmet == LEATHER_HELMET)
				return equipment.leatherHelmet;
			if(helmet == GOLD_HELMET)
				return equipment.goldHelmet;
			if(helmet == CHAINMAIL_HELMET)
				return equipment.chainHelmet;
			if(helmet == IRON_HELMET)
				return equipment.ironHelmet;
			if(helmet == DIAMOND_HELMET)
				return equipment.diamondHelmet;
			if(helmet == SKULL_ITEM)
				return equipment.head;
			if(helmet == PUMPKIN)
				return equipment.pumpkin;
			return true;//maybe, I forgot some helmets
		}

		@Override
		public boolean canEquipChestplate(RaceStatsConditions stats, ReflectedItem plate) {
			if(plate == LEATHER_CHESTPLATE)
				return equipment.leatherChestplate;
			if(plate == GOLD_CHESTPLATE)
				return equipment.goldChestplate;
			if(plate == CHAINMAIL_CHESTPLATE)
				return equipment.chainChestplate;
			if(plate == IRON_CHESTPLATE)
				return equipment.ironChestplate;
			if(plate == DIAMOND_CHESTPLATE)
				return equipment.diamondChestplate;
			return true;//just in case
		}

		@Override
		public boolean canEquipLeggings(RaceStatsConditions stats, ReflectedItem leggings) {
			if(leggings == LEATHER_LEGGINGS)
				return equipment.leatherLeggings;
			if(leggings == GOLD_LEGGINGS)
				return equipment.goldLeggings;
			if(leggings == CHAINMAIL_LEGGINGS)
				return equipment.chainLeggings;
			if(leggings == IRON_LEGGINGS)
				return equipment.ironLeggings;
			if(leggings == DIAMOND_LEGGINGS)
				return equipment.diamondLeggings;
			return true;
		}

		@Override
		public boolean canEquipBoots(RaceStatsConditions stats, ReflectedItem boots) {
			if(boots == LEATHER_BOOTS)
				return equipment.leatherBoots;
			if(boots == GOLD_BOOTS)
				return equipment.goldBoots;
			if(boots == CHAINMAIL_BOOTS)
				return equipment.chainBoots;
			if(boots == IRON_BOOTS)
				return equipment.ironBoots;
			if(boots == DIAMOND_BOOTS)
				return equipment.diamondBoots;
			return true;
		}
		
		public int getOnHitFireTicks(){
			return onHitFireTicks;
		}
		
		public int getOnAttackFireTicks(){
			return onAttackFireTicks;
		}
		
		public Collection<ReflectedEffect> getOnHitEffects(){
			return onHitEffects;
		}
		
		public Collection<ReflectedEffect> getOnAttackEffects(){
			return onAttackEffects;
		}
		
		@Override
		public void raceHurtByEntity(RaceHurtByEntityEvent event) {
			for(ReflectedEffect effect : onHitEffects)
				event.getAttackingEntity().addPotionEffect(effect);
			if(onHitFireTicks > event.getAttackingEntity().getFireTicks())
				event.getAttackingEntity().setFire(onHitFireTicks);
		}

		@Override
		public void raceHurtByPlayer(RaceHurtByRaceEvent event) {}

		@Override
		public void raceAttacksEntity(RaceAttackEntityEvent event) {
			for(ReflectedEffect effect : onAttackEffects)
				event.getAttackedEntity().addPotionEffect(effect);
			if(onAttackFireTicks > event.getAttackedEntity().getFireTicks())
				event.getAttackedEntity().setFire(onAttackFireTicks);
		}

		@Override
		public void raceAttacksRace(RaceAttackRaceEvent event) {}
		
		public static class SimpleEquipment {
			
			public boolean leatherBoots;
			public boolean leatherLeggings;
			public boolean leatherChestplate;
			public boolean leatherHelmet;
			
			public boolean goldBoots;
			public boolean goldLeggings;
			public boolean goldChestplate;
			public boolean goldHelmet;
			
			public boolean chainBoots;
			public boolean chainLeggings;
			public boolean chainChestplate;
			public boolean chainHelmet;
			
			public boolean ironBoots;
			public boolean ironLeggings;
			public boolean ironChestplate;
			public boolean ironHelmet;
			
			public boolean diamondBoots;
			public boolean diamondLeggings;
			public boolean diamondChestplate;
			public boolean diamondHelmet;
			
			public boolean pumpkin;
			public boolean head;
			
			public SimpleEquipment(
					boolean leatherBoots, boolean leatherLeggings, boolean leatherChestplate, boolean leatherHelmet,
					boolean goldBoots, boolean goldLeggings, boolean goldChestplate, boolean goldHelmet,
					boolean chainBoots, boolean chainLeggings, boolean chainChestplate, boolean chainHelmet,
					boolean ironBoots, boolean ironLeggings, boolean ironChestplate, boolean ironHelmet,
					boolean diamondBoots, boolean diamondLeggings, boolean diamondChestplate, boolean diamondHelmet,
					boolean pumpkin, boolean head){
				this.leatherBoots = leatherBoots;
				this.leatherLeggings = leatherLeggings;
				this.leatherChestplate = leatherChestplate;
				this.leatherHelmet = leatherHelmet;
				this.goldBoots = goldBoots;
				this.goldLeggings = goldLeggings;
				this.goldChestplate = goldChestplate;
				this.goldHelmet = goldHelmet;
				this.chainBoots = chainBoots;
				this.chainLeggings = chainLeggings;
				this.chainChestplate = chainChestplate;
				this.chainHelmet = chainHelmet;
				this.ironBoots = ironBoots;
				this.ironLeggings = ironLeggings;
				this.ironChestplate = ironChestplate;
				this.ironHelmet = ironHelmet;
				this.diamondBoots = diamondBoots;
				this.diamondLeggings = diamondLeggings;
				this.diamondChestplate = diamondChestplate;
				this.diamondHelmet = diamondHelmet;
				this.pumpkin = pumpkin;
				this.head = head;
			}
		}

		@Override
		public void raceHurtEvent(RaceHurtEvent event) {}

		@Override
		public void raceUpdate(RaceUpdateEvent event) {}

		@Override
		public void raceLeftClick(RaceLeftClickEvent event) {}

		@Override
		public void raceRightClick(RaceRightClickEvent event) {}

		@Override
		public void raceBreakBlock(RaceBreakBlockEvent event) {}

		@Override
		public void raceKillEntity(RaceKillEntityEvent event) {}

		@Override
		public void raceKillRace(RaceKillRaceEvent event) {}

		@Override
		public void raceKilledByEntity(RaceKilledByEntityEvent event) {}

		@Override
		public void raceKilledByRaceEvent(RaceKilledByRaceEvent event) {}

		@Override
		public void raceDie(RaceDieEvent event) {}
	}
	
	public static class AdvancedRace extends Race {
		
		private final List<ProgressType> fields;
		private final List<NamedFunction> functions;
		private final List<RaceChoise> choises;
		
		private final Function healthFunction;
		private final Function damageFunction;
		private final Function strengthFunction;
		private final Function speedFunction;
		private final Function attackSpeedFunction;
		private final Function armorFunction;
		private final Function archeryFunction;
		
		//private final Function onHitFireFunction;
		//private final Function onAttackFireFunction;
		
		//private final PotionFunction[] onHitPotionFunctions;
		//private final PotionFunction[] onAttackPotionFunctions;
		
		private final PermanentPotionFunction[] permanentEffects;
		
		private final Function[] damageResistanceFunctions;
		private final Map<ReflectedEffectType,Function> effectResistanceFunctions;
		private final AdvancedEquipment equipment;

		public AdvancedRace(String name, double frequency, List<ProgressType> fields, List<NamedFunction> functions, List<RaceChoise> choises,
				Function health, Function damage, Function strength, Function speed, Function attackSpeed, Function armor, Function archery,
				Function onHitFireTicks, Function onAttackFireTicks, PotionFunction[] onHitEffects, PotionFunction[] onAttackEffects,
				PermanentPotionFunction[] permanentEffects, Function[] damageResistances, Map<ReflectedEffectType,Function> effectResistances,
				AdvancedEquipment equipment) {
			super(name, frequency);
			this.fields = fields;
			this.functions = functions;
			this.choises = choises;
			healthFunction = health;
			damageFunction = damage;
			strengthFunction = strength;
			speedFunction = speed;
			attackSpeedFunction = attackSpeed;
			armorFunction = armor;
			archeryFunction = archery;
			//onHitFireFunction = onHitFireTicks;
			//onAttackFireFunction = onAttackFireTicks;
			//onHitPotionFunctions = onHitEffects;
			//onAttackPotionFunctions = onAttackEffects;
			this.permanentEffects = permanentEffects;
			damageResistanceFunctions = damageResistances;
			effectResistanceFunctions = effectResistances;
			this.equipment = equipment;
		}

		@Override
		public List<ProgressType> getFields() {
			return fields;
		}

		@Override
		public List<NamedFunction> getFunctions() {
			return functions;
		}

		@Override
		public double getExtraHealth(RaceStatsConditions stats) {
			return healthFunction.value(stats);
		}

		@Override
		public double getExtraDamage(RaceStatsConditions stats) {
			return damageFunction.value(stats);
		}

		@Override
		public double getStrengthMultiplier(RaceStatsConditions stats) {
			return strengthFunction.value(stats);
		}

		@Override
		public double getSpeedMultiplier(RaceStatsConditions stats) {
			return speedFunction.value(stats);
		}

		@Override
		public double getAttackSpeedMultiplier(RaceStatsConditions stats) {
			return attackSpeedFunction.value(stats);
		}

		@Override
		public double getExtraArmor(RaceStatsConditions stats) {
			return armorFunction.value(stats);
		}

		@Override
		public double getArcheryFactor(RaceStatsConditions stats) {
			return archeryFunction.value(stats);
		}

		@Override
		public double getResistance(ReflectedCause cause, RaceStatsConditions stats) {
			return damageResistanceFunctions[cause.ordinal()].value(stats);
		}

		@Override
		public float getResistance(ReflectedEffectType type, RaceStatsConditions stats) {
			Function function = effectResistanceFunctions.get(type);
			if(function != null)
				return function.floatValue(stats);
			return 0;
		}

		@Override
		public Collection<PermanentEffect> getPermanentEffects(RaceStatsConditions stats) {
			List<PermanentEffect> list = new ArrayList<PermanentEffect>(permanentEffects.length);
			for(PermanentPotionFunction ppf : permanentEffects){
				PermanentEffect effect = ppf.getEffect(stats);
				if(effect != null)
					list.add(effect);
			}
			return list;
		}

		@Override
		public Collection<RaceChoise> getChoises() {
			return choises;
		}
		
		public Function health(){
			return healthFunction;
		}
		
		public Function damage(){
			return damageFunction;
		}
		
		public Function strength(){
			return strengthFunction;
		}
		
		public Function speed(){
			return speedFunction;
		}
		
		public Function attackSpeed(){
			return attackSpeedFunction;
		}
		
		public Function armor(){
			return armorFunction;
		}
		
		public Function archery(){
			return archeryFunction;
		}
		
		public Function[] getDamageResistanceFunctions(){
			return damageResistanceFunctions;
		}
		
		public AdvancedEquipment getAllowedEquipment(){
			return equipment;
		}
		
		public static class AdvancedEquipment {
			
			public Condition leatherBoots;
			public Condition leatherLeggings;
			public Condition leatherChestplate;
			public Condition leatherHelmet;
			
			public Condition goldBoots;
			public Condition goldLeggings;
			public Condition goldChestplate;
			public Condition goldHelmet;
			
			public Condition chainBoots;
			public Condition chainLeggings;
			public Condition chainChestplate;
			public Condition chainHelmet;
			
			public Condition ironBoots;
			public Condition ironLeggings;
			public Condition ironChestplate;
			public Condition ironHelmet;
			
			public Condition diamondBoots;
			public Condition diamondLeggings;
			public Condition diamondChestplate;
			public Condition diamondHelmet;
			
			public Condition pumpkin;
			public Condition head;
			
			public AdvancedEquipment(
					Condition leatherBoots, Condition leatherLeggings, Condition leatherChestplate, Condition leatherHelmet,
					Condition goldBoots, Condition goldLeggings, Condition goldChestplate, Condition goldHelmet,
					Condition chainBoots, Condition chainLeggings, Condition chainChestplate, Condition chainHelmet,
					Condition ironBoots, Condition ironLeggings, Condition ironChestplate, Condition ironHelmet,
					Condition diamondBoots, Condition diamondLeggings, Condition diamondChestplate, Condition diamondHelmet,
					Condition pumpkin, Condition head){
				this.leatherBoots = leatherBoots;
				this.leatherLeggings = leatherLeggings;
				this.leatherChestplate = leatherChestplate;
				this.leatherHelmet = leatherHelmet;
				this.goldBoots = goldBoots;
				this.goldLeggings = goldLeggings;
				this.goldChestplate = goldChestplate;
				this.goldHelmet = goldHelmet;
				this.chainBoots = chainBoots;
				this.chainLeggings = chainLeggings;
				this.chainChestplate = chainChestplate;
				this.chainHelmet = chainHelmet;
				this.ironBoots = ironBoots;
				this.ironLeggings = ironLeggings;
				this.ironChestplate = ironChestplate;
				this.ironHelmet = ironHelmet;
				this.diamondBoots = diamondBoots;
				this.diamondLeggings = diamondLeggings;
				this.diamondChestplate = diamondChestplate;
				this.diamondHelmet = diamondHelmet;
				this.pumpkin = pumpkin;
				this.head = head;
			}
		}

		public Map<ReflectedEffectType,Function> getEffectResistanceFunctions() {
			return effectResistanceFunctions;
		}

		@Override
		public boolean canEquipHelmet(RaceStatsConditions stats, ReflectedItem helmet) {
			if(helmet == LEATHER_HELMET)
				return equipment.leatherHelmet.value(stats);
			if(helmet == GOLD_HELMET)
				return equipment.goldHelmet.value(stats);
			if(helmet == CHAINMAIL_HELMET)
				return equipment.chainHelmet.value(stats);
			if(helmet == IRON_HELMET)
				return equipment.ironHelmet.value(stats);
			if(helmet == DIAMOND_HELMET)
				return equipment.diamondHelmet.value(stats);
			if(helmet == SKULL_ITEM)
				return equipment.head.value(stats);
			if(helmet == PUMPKIN)
				return equipment.pumpkin.value(stats);
			return true;//maybe, I forgot some helmets
		}

		@Override
		public boolean canEquipChestplate(RaceStatsConditions stats, ReflectedItem plate) {
			if(plate == LEATHER_CHESTPLATE)
				return equipment.leatherChestplate.value(stats);
			if(plate == GOLD_CHESTPLATE)
				return equipment.goldChestplate.value(stats);
			if(plate == CHAINMAIL_CHESTPLATE)
				return equipment.chainChestplate.value(stats);
			if(plate == IRON_CHESTPLATE)
				return equipment.ironChestplate.value(stats);
			if(plate == DIAMOND_CHESTPLATE)
				return equipment.diamondChestplate.value(stats);
			return true;//just in case
		}

		@Override
		public boolean canEquipLeggings(RaceStatsConditions stats, ReflectedItem leggings) {
			if(leggings == LEATHER_LEGGINGS)
				return equipment.leatherLeggings.value(stats);
			if(leggings == GOLD_LEGGINGS)
				return equipment.goldLeggings.value(stats);
			if(leggings == CHAINMAIL_LEGGINGS)
				return equipment.chainLeggings.value(stats);
			if(leggings == IRON_LEGGINGS)
				return equipment.ironLeggings.value(stats);
			if(leggings == DIAMOND_LEGGINGS)
				return equipment.diamondLeggings.value(stats);
			return true;
		}

		@Override
		public boolean canEquipBoots(RaceStatsConditions stats, ReflectedItem boots) {
			if(boots == LEATHER_BOOTS)
				return equipment.leatherBoots.value(stats);
			if(boots == GOLD_BOOTS)
				return equipment.goldBoots.value(stats);
			if(boots == CHAINMAIL_BOOTS)
				return equipment.chainBoots.value(stats);
			if(boots == IRON_BOOTS)
				return equipment.ironBoots.value(stats);
			if(boots == DIAMOND_BOOTS)
				return equipment.diamondBoots.value(stats);
			return true;
		}

		@Override
		public void raceHurtByEntity(RaceHurtByEntityEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceHurtByPlayer(RaceHurtByRaceEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceAttacksEntity(RaceAttackEntityEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceAttacksRace(RaceAttackRaceEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceHurtEvent(RaceHurtEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceUpdate(RaceUpdateEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceLeftClick(RaceLeftClickEvent event) {
			// TODO Auto-generated method stub
		}

		@Override
		public void raceRightClick(RaceRightClickEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceBreakBlock(RaceBreakBlockEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceKillEntity(RaceKillEntityEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceKillRace(RaceKillRaceEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceKilledByEntity(RaceKilledByEntityEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceKilledByRaceEvent(RaceKilledByRaceEvent event) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void raceDie(RaceDieEvent event) {
			// TODO Auto-generated method stub
			
		}
	}
}
