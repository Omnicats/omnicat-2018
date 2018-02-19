package mechanism.sensor;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class CubeDetector {
    UsbCamera camera0;
    UsbCamera camera1;
	private static final int IMG_WIDTH = 640;
	private static final int IMG_HEIGHT = 480;
	private static final int FPS = 30;
	private Thread visionThread;
	private boolean cam0;
	private double centerX;
	private final Object imgLock = new Object();
	private double visionFinding = 0.0;
	private double visionRunning = 0.0;

    
    public CubeDetector() {
    	camera0 = CameraServer.getInstance().startAutomaticCapture(0);
    	camera0.setResolution(IMG_WIDTH, IMG_HEIGHT);
    	camera0.setFPS(FPS);
    	visionThread = new Thread(() -> {
			CvSink cvSink0 = CameraServer.getInstance().getVideo(camera0);
			//CvSink cvSink1 = CameraServer.getInstance().getVideo(camera1);
			CvSource outputStream = CameraServer.getInstance().putVideo("Vision", 640, 480);
			Mat mat = new Mat();
			final GripPipeline pipeline = new GripPipeline();
			while (!Thread.interrupted()) {
				/*if(j.getRawButton(1)){
					cam0 = true;
				}
				if(j.getRawButton(2)){
					cam0 = false;
				}
				if(cam0){*/
					if (cvSink0.grabFrame(mat, 1) == 0) {
						outputStream.notifyError(cvSink0.getError());
						continue;
					}
				/*}
				else{
					if (cvSink1.grabFrame(mat) == 0) {
						outputStream.notifyError(cvSink1.getError());
						continue;
					}
				}*/
				outputStream.putFrame(mat);
				pipeline.process(mat);
		        if (pipeline.filterContoursOutput().size() > 0) {
		        	Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
	                visionFinding++;
		            SmartDashboard.putNumber("Vision Finding", visionFinding);
		            synchronized (imgLock) {
		            	centerX = (r.x + (r.width / 2));
		            }
		        }else{
		        	centerX = 0;
		        }
		        SmartDashboard.putNumber("Center X", centerX);
	            visionRunning++;
	            SmartDashboard.putNumber("Vision Running", visionRunning);
			}
		});
	    visionThread.start();
    }
    
    public double getCenterX() {
    	double centerX;
    	synchronized(imgLock) {
    		centerX = this.centerX;
    	}
    	return centerX;
    }

}
