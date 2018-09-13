package nl.knokko.races.event;

import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.conditions.EntityPresentor;
import nl.knokko.races.conditions.RacePresentor;

public class RaceKilledByEntityEvent extends RaceDieEvent {
	
	private final EntityPresentor killer;

	public RaceKilledByEntityEvent(RacePresentor race, EntityPresentor killer, ReflectedCause cause) {
		super(race, cause);
		this.killer = killer;
	}
	
	public EntityPresentor getKiller(){
		return killer;
	}
}
