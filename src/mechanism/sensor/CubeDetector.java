package mechanism.sensor;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Threads;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CubeDetector {
	private static final int IMG_WIDTH = 320;
	private static final int IMG_HEIGHT = 240;
	private static final int FPS = 30;
	private Thread visionThread;
	private Thread processingThread;
	private double centerX;
	private Mat mat = new Mat();
	private Rect r;
	private final Object centerLock = new Object();
	private final Object imgLock = new Object();
	private double visionFinding = 0.0;
	private double visionRunning = 0.0;
	private Joystick j;

    
    public CubeDetector(Joystick joystick) {    	
    	this.j = joystick;
    	UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);
    	camera.setResolution(IMG_WIDTH, IMG_HEIGHT);
    	camera.setFPS(FPS);
    	
    	visionThread = new Thread(() -> {
			Threads.setCurrentThreadPriority(true, 5);
        	UsbCamera camera0 = new UsbCamera ("USB Camera 0", 0);
        	camera0.setResolution(IMG_WIDTH, IMG_HEIGHT);
        	camera0.setFPS(FPS);
        	
        	UsbCamera camera1 = new UsbCamera ("USB Camera 1", 1);
        	camera1.setResolution(IMG_WIDTH, IMG_HEIGHT);
        	camera1.setFPS(FPS);

        	CvSink cvSink = CameraServer.getInstance().getVideo(camera0);
        	CvSource outputStream = CameraServer.getInstance().putVideo("Vision", IMG_WIDTH, IMG_HEIGHT);
        	outputStream.setFPS(FPS);
        	int cameraRunning = 0;
        	//Rect r = null;
			
			while (!Thread.interrupted()) {
				if(j.getRawButton(3)){
					SmartDashboard.putString("camera", "front");
					cvSink.setSource(camera0);
				}
				else if(j.getRawButton(4)){
					SmartDashboard.putString("camera", "back");
					cvSink.setSource(camera1);
				}
				//synchronized(imgLock) {
					if (cvSink.grabFrame(mat) == 0) {
						outputStream.notifyError(cvSink.getError());
						continue;
					}
					//r = this.r;
				//}
				/*if(r != null) {
					Imgproc.rectangle(mat, new Point(r.x, r.y), new Point(r.x + r.width, r.y + r.height), new Scalar(255, 255, 255), 2);
				}*/
				outputStream.putFrame(mat);
				cameraRunning++;
				SmartDashboard.putNumber("Camera Running", cameraRunning);
				SmartDashboard.putNumber("Running Difference", cameraRunning - SmartDashboard.getNumber("Vision Running", 0));
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
    	visionThread.setDaemon(true);
	    //visionThread.start();
	    
	    processingThread = new Thread(() -> {
	    	Threads.setCurrentThreadPriority(true, 10);
			final GripPipeline pipeline = new GripPipeline();
			Mat mat;
			//Rect r = null;
	    	while(!Thread.interrupted()) {
	    		synchronized(imgLock) {
	    			//this.r = r;
	    			mat = this.mat;
	    		}
	    		if(mat.height() <= 0) {
	    			continue;
	    		}
		    	pipeline.process(mat);
		        if (pipeline.filterContoursOutput().size() > 0) {
		        	r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
	                visionFinding++;
		            SmartDashboard.putNumber("Vision Finding", visionFinding);
		            synchronized (centerLock) {
		            	centerX = (r.x + (r.width / 2));
		            }
		        }else{
		        	centerX = 0;
		        }
		        SmartDashboard.putNumber("Center X", centerX);
	            visionRunning++;
	            SmartDashboard.putNumber("Vision Running", visionRunning);
	            try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    });
    	processingThread.setDaemon(true);
	    //processingThread.start();
    }
    
    public double getCenterX() {
    	double centerX;
    	synchronized(centerLock) {
    		centerX = this.centerX;
    	}
    	return centerX;
    }

}
