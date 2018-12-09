package nl.knokko.races.potion;

import java.awt.Color;

import nl.knokko.races.base.RaceFactory;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class PermanentEffect {
	
	public static PermanentEffect load(BitInput buffer){
		return new PermanentEffect(new ReflectedEffectType(buffer.readString()), buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), RaceFactory.readColor(buffer));
	}
	
	private ReflectedEffectType type;
	
	private int level;
	
	private boolean ambient;
	private boolean particles;
	
	private Color color;

	public PermanentEffect(ReflectedEffectType type, int level, boolean isAmbient, boolean hasParticles, Color color) {
		this.type = type;
		this.level = level;
		ambient = isAmbient;
		particles = hasParticles;
		this.color = color;
	}
	
	@Override
	public String toString(){
		return "PermanentEffect(" + type.toString().toLowerCase() + "," + level + ")";
	}
	
	@Override
	public int hashCode(){
		return type.hashCode() - level;
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof PermanentEffect){
			PermanentEffect p = (PermanentEffect) other;
			return p.type.equals(type) && p.level == level;
		}
		return false;
	}
	
	public void save(BitOutput buffer){
		buffer.addString(type.getType());
		buffer.addInt(level);
		buffer.addBoolean(ambient);
		buffer.addBoolean(particles);
		RaceFactory.saveColor(buffer, color);
	}
	
	public ReflectedEffectType getType(){
		return type;
	}
	
	public int getLevel(){
		return level;
	}
	
	public int getAmplifier(){
		return level - 1;
	}
	
	public boolean isAmbient(){
		return ambient;
	}
	
	public boolean hasParticles(){
		return particles;
	}
	
	public Color getColor(){
		return color;
	}
}
