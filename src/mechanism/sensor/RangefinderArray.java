package mechanism.sensor;

import util.sensor.analog.rangefinder.Rangefinder;

public class RangefinderArray {
	private Rangefinder left;
	private Rangefinder center;
	private Rangefinder right;	
	
	public RangefinderArray(int leftPort, int centerPort, int rightPort) {
		left = new Rangefinder(leftPort);
		center = new Rangefinder(centerPort);
		right = new Rangefinder(rightPort);
	}
	
	public double[] getValues() {
		double[] values = new double[3];
		values[0] = left.getDistanceInches();
		values[1] = center.getDistanceInches();
		values[2] = right.getDistanceInches();
		return values;
	}
	
}
