package nl.knokko.races.event.response;

import nl.knokko.races.event.RaceHurtByRaceEvent;

public interface RaceHurtByRaceResponse {
	
	void execute(RaceHurtByRaceEvent event);
}