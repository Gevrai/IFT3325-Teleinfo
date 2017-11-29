package network;

import java.util.Random;

import utils.Log;

/** Creates an entity whose sole purpose is to maybe generate an error on input byte array
 *  based on a given ratio. A value of zero will never introduce any error, while a value of
 *  1.0 will always introduce errors, and 0.5 has a 50% chance to introduce an error.
 *
 */
public class ErrorGenerator {
	
	double errorRatio;
	Random randomizer;
	
	ErrorGenerator(double errorRatio) {
		if (errorRatio < 0.0) {
			Log.println("Can't have an error ratio smaller than zero : defaulting to 0.0");
			this.errorRatio = 0.0;
		} else if (errorRatio >= 1.0) {
			Log.println("Can't have an error ratio greater than one : defaulting to 1.0");
			this.errorRatio = 1.0;
		} else
			this.errorRatio = errorRatio;
		
		this.randomizer = new Random(System.currentTimeMillis());
	}
	
	public byte[] apply(byte[] input) {
		if(randomizer.nextDouble() < this.errorRatio) {
			byte badByte = (byte) (randomizer.nextDouble()*Byte.SIZE);
			input[(int) (randomizer.nextDouble()*input.length)] = badByte;
		}
		return input;
	}
}
