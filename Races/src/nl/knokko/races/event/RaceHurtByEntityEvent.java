package nl.knokko.races.event;

import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.conditions.EntityPresentor;
import nl.knokko.races.conditions.RacePresentor;

public class RaceHurtByEntityEvent extends RaceHurtEvent {
	
	private final EntityPresentor attacker;

	public RaceHurtByEntityEvent(RacePresentor victim, EntityPresentor attacker, ReflectedCause cause, double damage) {
		super(victim, cause, damage);
		this.attacker = attacker;
	}
	
	public EntityPresentor getAttackingEntity(){
		return attacker;
	}
}