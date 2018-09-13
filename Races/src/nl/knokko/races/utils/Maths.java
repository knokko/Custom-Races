package nl.knokko.races.utils;

public class Maths {
	
	private static final short[] POWERS = {1,2,4,8,16,32,64,128,256,512,1024,2048};
	
	public static byte logUp(int number){
		for(byte i = 0; i < POWERS.length; i++)
			if(number <= POWERS[i])
				return i;
		return (byte) POWERS.length;
	}
}
