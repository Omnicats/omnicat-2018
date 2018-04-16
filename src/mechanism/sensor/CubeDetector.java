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
import edu.wpi.cscore.VideoSink;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
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
	private boolean cameraFront;
	private Joystick j;

    
    public CubeDetector(Joystick joystick) {    	
    	this.j = joystick;
    	
    	visionThread = new Thread(() -> {
        	UsbCamera camera0 = CameraServer.getInstance().startAutomaticCapture(0);
        	camera0.setResolution(IMG_WIDTH, IMG_HEIGHT);
        	camera0.setFPS(FPS);
        	
        	UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture(1);
        	camera1.setResolution(IMG_WIDTH, IMG_HEIGHT);
        	camera1.setFPS(FPS);

        	CvSink cvSink0 = new CvSink("cam0");
        	cvSink0.setSource(camera0);
        	cvSink0.setEnabled(true);
        	
        	CvSink cvSink1 = new CvSink("cam1");
        	cvSink1.setSource(camera1);
        	cvSink1.setEnabled(true);
        	
        	VideoSink server;
        	server = CameraServer.getInstance().getServer();
        	server.setSource(camera0);
        	cameraFront = true;
        	int cameraRunning = 0;
			
			while (!Thread.interrupted()) {
				if(j.getRawButtonPressed(5)){
					SmartDashboard.putString("camera", "front");
					server.setSource(camera0);
					cameraFront = true;
				}
				else if(j.getRawButtonPressed(7)){
					SmartDashboard.putString("camera", "back");
					server.setSource(camera1);
					cameraFront = false;
				}
				synchronized(imgLock) {
					if (cvSink0.grabFrame(mat) == 0) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						continue;
					}
				}
				cameraRunning++;
				SmartDashboard.putNumber("Camera Running", cameraRunning);
				SmartDashboard.putNumber("Running Difference", cameraRunning - SmartDashboard.getNumber("Vision Running", 0));
				/*try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
			}
		});
    	visionThread.setDaemon(true);
	    visionThread.start();
	    
	    processingThread = new Thread(() -> {
			final GripPipeline pipeline = new GripPipeline();
			Mat mat;
			Preferences prefs = Preferences.getInstance();
	    	while(!Thread.interrupted()) {
	    		synchronized(imgLock) {
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
					Thread.sleep((long) prefs.getDouble("Processing Delay", 50));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    });
    	processingThread.setDaemon(true);
	    processingThread.start();
    }
    
    public double getCenterX() {
    	double centerX;
    	synchronized(centerLock) {
    		centerX = this.centerX;
    	}
    	return centerX;
    }

}
