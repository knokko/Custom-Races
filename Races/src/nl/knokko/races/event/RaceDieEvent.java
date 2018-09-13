package nl.knokko.races.event;

import nl.knokko.races.base.ReflectedCause;
import nl.knokko.races.conditions.RacePresentor;

public class RaceDieEvent extends RaceEvent {
	
	protected final ReflectedCause cause;

	public RaceDieEvent(RacePresentor race, ReflectedCause cause) {
		super(race);
		this.cause = cause;
	}
	
	public RacePresentor getDeadRace(){
		return player;
	}
	
	public ReflectedCause getCause(){
		return cause;
	}
}
