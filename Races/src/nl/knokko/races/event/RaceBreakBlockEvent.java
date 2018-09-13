package nl.knokko.races.event;

import nl.knokko.races.block.ReflectedBlock;
import nl.knokko.races.conditions.RacePresentor;

public class RaceBreakBlockEvent extends RaceEvent {
	
	private final ReflectedBlock block;
	private final int x,y,z;

	public RaceBreakBlockEvent(RacePresentor player, ReflectedBlock block, int blockX, int blockY, int blockZ) {
		super(player);
		this.block = block;
		x = blockX;
		y = blockY;
		z = blockZ;
	}
	
	public RacePresentor getPlayer(){
		return player;
	}
	
	public ReflectedBlock getBlockType(){
		return block;
	}
	
	public int getBlockX(){
		return x;
	}
	
	public int getBlockY(){
		return y;
	}
	
	public int getBlockZ(){
		return z;
	}
}
