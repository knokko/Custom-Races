package nl.knokko.races.base;

public enum ReflectedCause {
	
	CONTACT,
	ENTITY_ATTACK,
	ENTITY_SWEEP_ATTACK,
	PROJECTILE,
	SUFFOCATION,
	FALL,
	FIRE,
	FIRE_TICK,
	MELTING,
	LAVA,
	DROWNING,
	BLOCK_EXPLOSION,
	ENTITY_EXPLOSION,
	VOID,
	LIGHTNING,
	SUICIDE,
	STARVATION,
	POISON,
	MAGIC,
	WITHER,
	FALLING_BLOCK,
	THORNS,
	DRAGON_BREATHE,
	CUSTOM,
	FLY_INTO_WALL,
	HOT_FLOOR,
	CRAMMING;
	
	public static ReflectedCause fromBukkitCause(Enum<?> bukkitCause){
		return valueOf(bukkitCause.name());
	}
	
	ReflectedCause(){}
}
