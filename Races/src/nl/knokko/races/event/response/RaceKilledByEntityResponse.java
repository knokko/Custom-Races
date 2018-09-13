package nl.knokko.races.event.response;

import nl.knokko.races.event.RaceKilledByEntityEvent;

public interface RaceKilledByEntityResponse {
	
	void execute(RaceKilledByEntityEvent event);
}