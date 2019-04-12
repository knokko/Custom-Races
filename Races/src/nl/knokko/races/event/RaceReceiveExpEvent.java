package nl.knokko.races.event;

import nl.knokko.races.conditions.RacePresentor;

public class RaceReceiveExpEvent extends RaceEvent {
	
	private int amount;

	public RaceReceiveExpEvent(RacePresentor race, int amount) {
		super(race);
		this.amount = amount;
	}
	
	public RacePresentor getReceiver() {
		return player;
	}
	
	/**
	 * Warning: this clears the xp and will cancel the actual event.
	 * @return the amount of xp in the event (before it is wiped)
	 */
	public int consumeAll() {
		int amount = this.amount;
		this.amount = 0;
		return amount;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void cancel() {
		amount = 0;
	}
	
	public boolean isCancelled() {
		return amount <= 0;
	}
}