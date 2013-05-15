package cm.crackshot.activites;

import java.lang.Math;
import java.util.List;
import java.util.Timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import cm.crackshot.R;
import cm.crackshot.data.Ballistics;
import cm.crackshot.data.SensorAngles;
import cm.crackshot.fragments.CalibrationDialogFragment;
import cm.crackshot.views.CameraScopeView;
import cm.crackshot.views.CrosshairView;

public class StartActivity extends FragmentActivity implements SensorEventListener, 
	CalibrationDialogFragment.OnOptionSelectedListener, OnTouchListener
{
	private static final int TIME_CONSTANT = 30;

    private boolean centerpointLocked = false;
	private boolean hasCamera;
	private boolean hasGyroscope;
	private int 	selectedRange = 25;
	
	private Ballistics 					ballistics;
	private Camera 						mCamera;
	private CameraInstanceTask 			cameraInstanceTask;
    private CameraScopeView 			mPreview;
    private CrosshairView 				crosshairView;
	private DisplayMetrics 				metrics;	
	private Handler 					handler;
	private Point 						centerpoint;
	private SensorAngles 				sensorAngles;
	private SensorManager 				sensorManager;
	private SharedPreferences 			sharedPref;
	private SharedPreferences.Editor 	editor;
	private Timer 						fuseTimer;
	private TextView 					angleView;
	private TextView 					selectedRangeView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView		(R.layout.activity_start);
		
		sensorManager 		= (SensorManager) getSystemService(SENSOR_SERVICE);

		handler 			= new Handler();
		centerpoint 		= new Point();
		sharedPref 			= getPreferences(Context.MODE_PRIVATE);
		
		angleView 			= (TextView)findViewById(R.id.gyroangle);
		selectedRangeView 	= (TextView)findViewById(R.id.selectedRange);
		crosshairView 		= (CrosshairView)findViewById(R.id.crosshairView);
				
		initializationProcess();

		retreieveCenterPointFromUserOrHistory();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_start, menu);
		
		return true;
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();
		
		if(sensorManager == null) 
		{
			registerSensorListeners();
		}
	}
	
    @Override
    public void onStop() 
    {
    	super.onStop();
    	releaseCamera();
    	saveCenterpointToSharedPref();
    	sensorManager.unregisterListener(this);
    }
    
    @Override
    protected void onPause() 
    {
        super.onPause();
        releaseCamera();
        sensorManager.unregisterListener(this);
    }

    private void initializationProcess() 
	{
		checkCameraAndGyrscopeAvailability();
		initializeSensorAngles();
		initializeFuseTimer();
		initializeMetrics();
		registerSensorListeners();
		getCenterpointLockFromSharedPref();
	}
    
    private void checkCameraAndGyrscopeAvailability() 
	{
		hasCamera 		= getIntent().getExtras().getBoolean("hasCamera");
		hasGyroscope 	= getIntent().getExtras().getBoolean("hasGyroscope");
		
		if(hasCamera)
		{	
			initializeCamera();
		}
	}
    
    private void initializeCamera() 
	{
		if(mCamera == null)
		{
			cameraInstanceTask = new CameraInstanceTask();
			cameraInstanceTask.execute();
		}
	}
    
    private void initializeSensorAngles() 
	{
		sensorAngles = new SensorAngles(hasGyroscope);
	}

	private void initializeFuseTimer() 
	{
		fuseTimer = new Timer();
		fuseTimer.scheduleAtFixedRate(sensorAngles.new calculateFusedOrientationTask(), 1000, TIME_CONSTANT);
	}
	
	private void initializeMetrics() 
	{
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
	}
	
	private void registerSensorListeners() 
	{
		sensorManager.registerListener(	this, 
										sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
										SensorManager.SENSOR_DELAY_FASTEST);
		
		sensorManager.registerListener(	this, 
										sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
										SensorManager.SENSOR_DELAY_FASTEST);
		
		if (hasGyroscope) 
		{
			sensorManager.registerListener(	this, 
											sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 
											SensorManager.SENSOR_DELAY_FASTEST);
		}
	}
	
	private void getCenterpointLockFromSharedPref() 
	{
		centerpointLocked = sharedPref.getBoolean("centerpointLocked", false);
	}
	
	private void retreieveCenterPointFromUserOrHistory() 
	{
		if (!centerpointLocked)
		{
			askUserCenterPointOption();
		}
		else
		{
			getCenterPointFromSharedPref();
			drawHUD();
		}		
	}
	
	private void askUserCenterPointOption() 
	{
		DialogFragment calibrationOptionDialog = new CalibrationDialogFragment();
		
		calibrationOptionDialog.setCancelable(false);
		calibrationOptionDialog.show(getSupportFragmentManager(), "calibration");
	}
	
	private void getCenterPointFromSharedPref()
	{
		centerpoint.x = sharedPref.getInt("centerpointX", 1);
		centerpoint.y = sharedPref.getInt("centerpointY", 1);
	}
	
	private void drawHUD() 
	{
		drawCrosshair();
		//TODO dawOtherHUDComponents();
	}

	private void drawCrosshair() 
	{
		crosshairView.setCenterPoint(centerpoint);
		crosshairView.setSelectedRange(getRange());
		crosshairView.setRotation(sensorAngles.getPitchAngle());
		crosshairView.setVisibility(View.VISIBLE);
	}
	
	private void drawCrosshair(Point point)
	{
		//TODO 
		crosshairView.setCenterPoint(point); //centerpoint);
		crosshairView.setSelectedRange(getRange());
		crosshairView.setRotation(sensorAngles.getPitchAngle());
		crosshairView.setVisibility(View.VISIBLE);
	}
	
	public int getRange()
	{
		return Integer.valueOf((String) selectedRangeView.getText());
	}
	
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) 
    {
        int action 	= event.getAction();
        int keyCode = event.getKeyCode();
        Point point = new Point();
        
        switch (keyCode) 
        {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_UP) 
                {
                	switch (selectedRange)
                    {
                    	case 25:
                    		break;
                    	case 50:
                    		selectedRange = 25;
                    		break;
                    	case 75:
                    		selectedRange = 50;
                    		break;
                    	case 100:
                    		selectedRange = 75;
                    		break;
                    	case 125:
                    		selectedRange = 100;
                    		break;
                    }
                	//point.x = centerpoint.x;
                	//point.y = point.y - (selectedRange);
                    //updateSelectedRangeTextAndTargeting(point);
                }
                return true;
                
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) 
                {
                	switch (selectedRange)
                    {
                    	case 25:
                    		selectedRange = 50;
                    		break;
                    	case 50:
                    		selectedRange = 75;
                    		break;
                    	case 75:
                    		selectedRange = 100;
                    		break;
                    	case 100:
                    		selectedRange = 125;
                    		break;
                    	case 125:
                    		break;
                    }
                	//point.x = centerpoint.x;
                	//point.y = centerpoint.y + selectedRange;
                    //updateSelectedRangeTextAndTargeting(point);
                }
                return true;
                
            default:
                return super.dispatchKeyEvent(event);
        }
    }
    
	private void updateSelectedRangeTextAndTargeting(Point point)
	{
		selectedRangeView.setText(Integer.toString(selectedRange));
		crosshairView.setTargetingBoxPosition(point);
		//updateTargetingBox();
	}

	private void updateTargeting() 
	{
	}

	private void setCenterPointDefault() 
	{
		centerpointLocked 	= true;
		centerpoint 		= new Point(metrics.widthPixels/2, metrics.heightPixels/2);
		
		saveCenterpointToSharedPref();
		drawHUD();
	}

	// Configure from Touch
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if(!centerpointLocked)
		{	
			centerpoint 		= new Point((int)event.getX(), (int)event.getY());
			centerpointLocked 	= true;

			saveCenterpointToSharedPref();
			Log.e("Touch", centerpoint.x + " " + centerpoint.y);

			drawHUD();
		}

		return false;
	}

	private void saveCenterpointToSharedPref()
	{
		editor = sharedPref.edit();
		
		editor.putBoolean("centerpointLocked", centerpointLocked);
		editor.putInt("centerpointX", centerpoint.x);
		editor.putInt("centerpointY", centerpoint.y);
		editor.commit();
	}
	
	@Override
	public void onDialogOptionSelected(int id) 
	{
		if(id == -1) // Dialog "Positive" Button Selected - "Configure"
		{
			Log.e("CALLBACK", "Configure Callback Works");
			//configureCenterPoint();
			
			//Grab coordinates from OnTouchEvent Listener
		}
		if (id == -2) // Dialog "Negative" Button Selected - "Default"
		{
			Log.e("CALLBACK", "Default Callback Works");
			setCenterPointDefault();
		}
	}
    
    @Override
	public void onAccuracyChanged(Sensor arg0, int arg1) 
    {
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		synchronized(this) 
		{
			switch (event.sensor.getType()) 
			{
				case Sensor.TYPE_ACCELEROMETER:
					sensorAngles.calculateAccelMagneticOrientation(event);
					break;
				
				case Sensor.TYPE_GYROSCOPE:
					sensorAngles.calculateGyroscopicOrientation(event);
					break;
				
				case Sensor.TYPE_MAGNETIC_FIELD:
					sensorAngles.setMagneticField(event);
					break;
			}
		}
		
		updateHUD();
	}
	
	private void updateHUD()
	{
		handler.post(updateCrosshairAngleTask);
	}
	
	private Runnable updateCrosshairAngleTask = new Runnable()
	{
		public void run()
		{
			updateCrosshairAngle();
			
			if (Math.abs(sensorAngles.getPitchAngle() - getRange()) < .15)
			{
				crosshairView.setReticleColor(android.graphics.Color.parseColor("99CC00"));
			}
		}
	};
	
	private void updateCrosshairAngle()
	{
		angleView		.setText(Float.toString(sensorAngles.getPitchAngle()));
		crosshairView	.setSelectedRange(getRange());
		crosshairView	.invalidate();
		crosshairView	.setRotation(-(int)sensorAngles.getRollAngle());
	}

 	@Override
 	public boolean onTouch(View v, MotionEvent event) 
 	{
 		return false;
 	}
	
 	private class CameraInstanceTask extends AsyncTask<Void, Void, Camera>
 	{
 		@Override
		protected Camera doInBackground(Void... arg) 
 		{
			Camera c = null;
		    
		    try 
		    {
		        c = Camera.open(); // attempt to get a Camera instance
		    }
		    catch (Exception e)
		    {
		        // Camera is not available (in use or does not exist)
		    	Log.e("Camera", "Could not get instance of camera!");
		    }
		    
		    return c; // returns null if camera is unavailable
		}

 	     protected void onPostExecute(Camera c) 
 	     {
 	    	mCamera = c;
 	    	mCamera.setDisplayOrientation(90);
			mCamera.startPreview();
			
			enableCameraFocusFeature();
			addCameraToCameraView();
 	     }
 	 }
 	
 	private void enableCameraFocusFeature() 
	{
		Camera.Parameters params 	= mCamera.getParameters();
		List<String> focusModes 	= params.getSupportedFocusModes();
		
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) 
		{
			params	.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			mCamera	.setParameters(params);
		}
	}
 	
 	private void addCameraToCameraView()
	{
		mPreview 				= new CameraScopeView(this, mCamera);
        FrameLayout preview 	= (FrameLayout) findViewById(R.id.camera_layout);
        
        preview.addView(mPreview);	
	}
 	
 	private void releaseCamera()
	{
		if (mCamera != null)
		{
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }		
	}
}
