package nl.knokko.races.event.response;

import nl.knokko.races.event.RaceHurtEvent;

public interface RaceHurtResponse {
	
	void execute(RaceHurtEvent event);
}