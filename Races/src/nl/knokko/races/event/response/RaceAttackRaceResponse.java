package nl.knokko.races.event.response;

import nl.knokko.races.event.RaceAttackRaceEvent;

public interface RaceAttackRaceResponse {
	
	void execute(RaceAttackRaceEvent event);
}