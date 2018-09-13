package nl.knokko.races.potion;

import java.lang.reflect.Field;

public class ReflectedEffectType implements Comparable<ReflectedEffectType> {
	
	public static final ReflectedEffectType SPEED = new ReflectedEffectType("speed");
	public static final ReflectedEffectType SLOWNESS = new ReflectedEffectType("slow");
	public static final ReflectedEffectType HASTE = new ReflectedEffectType("fast_digging");
	public static final ReflectedEffectType FATIQUE = new ReflectedEffectType("slow_digging");
	public static final ReflectedEffectType STRENGTH = new ReflectedEffectType("increase_damage");
	//heal and harm aren't necessary
	public static final ReflectedEffectType JUMP_BOOST = new ReflectedEffectType("jump");
	public static final ReflectedEffectType NAUSEA = new ReflectedEffectType("confusion");
	public static final ReflectedEffectType REGENERATION = new ReflectedEffectType("regeneration");
	public static final ReflectedEffectType RESISTANCE = new ReflectedEffectType("damage_resistance");
	public static final ReflectedEffectType FIRE_RESISTANCE = new ReflectedEffectType("fire_resistance");
	public static final ReflectedEffectType WATER_BREATHING = new ReflectedEffectType("water_breathing");
	public static final ReflectedEffectType INVISIBILITY = new ReflectedEffectType("invisibility");
	public static final ReflectedEffectType BLINDNESS = new ReflectedEffectType("blindness");
	public static final ReflectedEffectType NIGHT_VISION = new ReflectedEffectType("night_vision");
	public static final ReflectedEffectType HUNGER = new ReflectedEffectType("hunger");
	public static final ReflectedEffectType WEAKNESS = new ReflectedEffectType("weakness");
	public static final ReflectedEffectType POISON = new ReflectedEffectType("poison");
	public static final ReflectedEffectType WITHER = new ReflectedEffectType("wither");
	public static final ReflectedEffectType HEALTH_BOOST = new ReflectedEffectType("health_boost");
	public static final ReflectedEffectType ABSORPTION = new ReflectedEffectType("absorption");
	public static final ReflectedEffectType SATURATION = new ReflectedEffectType("saturation");
	public static final ReflectedEffectType GLOWING = new ReflectedEffectType("glowing");
	public static final ReflectedEffectType LEVITATION = new ReflectedEffectType("levitation");
	public static final ReflectedEffectType LUCK = new ReflectedEffectType("luck");
	public static final ReflectedEffectType UNLUCK = new ReflectedEffectType("unluck");
	
	public static final ReflectedEffectType[] VALUES = new ReflectedEffectType[25];
	
	static {
		try {
			Field[] fields = ReflectedEffectType.class.getFields();
			int index = 0;
			for(Field field : fields){
				if(field.getType() == ReflectedEffectType.class){
					VALUES[index] = (ReflectedEffectType) field.get(null);
					index++;
				}
			}
		} catch(Exception ex){
			throw new Error(ex);
		}
	}
	
	private final String type;

	public ReflectedEffectType(String type) {
		this.type = type.toLowerCase();
	}
	
	@Override
	public String toString(){
		return "Reflected effect: " + type;
	}
	
	@Override
	public int hashCode(){
		return type.hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof ReflectedEffectType)
			return type.equals(((ReflectedEffectType) other).type);
		return false;
	}
	
	public int compareTo(ReflectedEffectType ret){
		return type.compareTo(ret.type);
	}
	
	public String getType(){
		return type;
	}
}
