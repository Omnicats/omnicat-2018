package mechanism.sensor;

import util.sensor.analog.rangefinder.Rangefinder;

public class RangefinderArray {
	Rangefinder left;
	Rangefinder right;	
	
	public RangefinderArray(int leftPort, int rightPort) {
		left = new Rangefinder(leftPort);
		right = new Rangefinder(rightPort);
	}
	
	public double[] getValues() {
		double[] values = new double[2];
		values[0] = left.getDistanceMM();
		values[1] = right.getDistanceMM();
		return values;
	}
	
}
