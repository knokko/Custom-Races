package nl.knokko.races.event.response;

import nl.knokko.races.event.RaceHurtByEntityEvent;

public interface RaceHurtByEntityResponse {
	
	void execute(RaceHurtByEntityEvent event);
}