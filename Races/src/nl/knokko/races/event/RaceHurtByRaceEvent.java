package nl.knokko.races.event;

import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.conditions.RacePresentor;

public class RaceHurtByRaceEvent extends RaceHurtEvent {
	
	private final RacePresentor attacker;

	public RaceHurtByRaceEvent(RacePresentor victim, RacePresentor attacker, ReflectedCause cause, double damage) {
		super(victim, cause, damage);
		this.attacker = attacker;
	}
	
	public RacePresentor getAttackingRace(){
		return attacker;
	}
}