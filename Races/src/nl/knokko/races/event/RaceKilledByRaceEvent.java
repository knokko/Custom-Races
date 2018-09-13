package nl.knokko.races.event;

import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.conditions.RacePresentor;

public class RaceKilledByRaceEvent extends RaceDieEvent {
	
	private final RacePresentor killer;

	public RaceKilledByRaceEvent(RacePresentor race, RacePresentor killer, ReflectedCause cause) {
		super(race, cause);
		this.killer = killer;
	}
	
	public RacePresentor getKiller(){
		return killer;
	}
}
