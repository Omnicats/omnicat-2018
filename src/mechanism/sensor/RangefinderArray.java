package mechanism.sensor;

import util.sensor.analog.rangefinder.Rangefinder;

public class RangefinderArray {
	Rangefinder left;
	Rangefinder center;
	Rangefinder right;	
	
	public RangefinderArray(int leftPort, int centerPort, int rightPort) {
		left = new Rangefinder(leftPort);
		center = new Rangefinder(centerPort);
		right = new Rangefinder(rightPort);
	}
	
	public double[] getValues() {
		double[] values = new double[3];
		values[0] = left.getDistanceMM();
		values[1] = center.getDistanceMM();
		values[2] = right.getDistanceMM();
		return values;
	}
}
