package nl.knokko.races.potion;

import java.awt.Color;

import nl.knokko.races.condition.Condition;
import nl.knokko.races.conditions.RaceStatsConditions;
import nl.knokko.races.function.Function;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class PermanentPotionFunction {
	
	private ReflectedEffectType type;
	
	private Function level;
	
	private Condition particles;
	private Condition ambient;
	
	private Function red;
	private Function green;
	private Function blue;
	
	public static PermanentPotionFunction fromBits(BitInput buffer){
		return new PermanentPotionFunction(new ReflectedEffectType(buffer.readString()), Function.fromBits(buffer),
				Condition.fromBits(buffer), Condition.fromBits(buffer), Function.fromBits(buffer), Function.fromBits(buffer), Function.fromBits(buffer));
	}

	public PermanentPotionFunction(ReflectedEffectType type, Function level, Condition hasParticles, Condition isAmbient, Function red, Function green, Function blue) {
		this.type = type;
		this.level = level;
		particles = hasParticles;
		ambient = isAmbient;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public ReflectedEffectType getType(){
		return type;
	}
	
	public Function getLevel(){
		return level;
	}
	
	public Condition hasParticles(){
		return particles;
	}
	
	public Condition isAmbient(){
		return ambient;
	}
	
	public Function getRed(){
		return red;
	}
	
	public Function getGreen(){
		return green;
	}
	
	public Function getBlue(){
		return blue;
	}
	
	public PermanentEffect getEffect(RaceStatsConditions params){
		int level = this.level.intValue(params);
		if(level != 0){
			return new PermanentEffect(type, level, particles.value(params), ambient.value(params),
					new Color(ensureColor(red.floatValue(params)), ensureColor(green.floatValue(params)),
							ensureColor(blue.floatValue(params))));
		}
		return null;
	}
	
	public void save(BitOutput buffer){
		buffer.addString(type.getType());
		level.save(buffer);
		particles.save(buffer);
		ambient.save(buffer);
		red.save(buffer);
		green.save(buffer);
		blue.save(buffer);
	}
	
	private float ensureColor(float value){
		if(value > 1)
			value = 1;
		if(value < 0)
			value = 0;
		return value;
	}
}
