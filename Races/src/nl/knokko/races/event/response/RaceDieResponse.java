package nl.knokko.races.event.response;

import nl.knokko.races.event.RaceDieEvent;

public interface RaceDieResponse {
	
	void execute(RaceDieEvent event);
}