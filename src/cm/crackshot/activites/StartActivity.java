package cm.crackshot.activites;

import java.util.Timer;

import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import cm.crackshot.R;
import cm.crackshot.data.SensorAngles;
import cm.crackshot.fragments.CalibrationDialogFragment;
import cm.crackshot.views.CameraScopeView;
import cm.crackshot.views.CrosshairView;

public class StartActivity extends FragmentActivity implements SensorEventListener, CalibrationDialogFragment.OnOptionSelectedListener
{
	private static final int TIME_CONSTANT = 30;
	private Camera mCamera;
    private CameraScopeView mPreview;
	
	
	private boolean hasCamera;
	private boolean hasGyroscope;
	
	private DisplayMetrics metrics;
	
	public Handler handler;
	
	public Point centerPoint;
	
	private SensorAngles sensorAngles;
	
	private SensorManager sensorManager;
	
	private Timer fuseTimer;
	
	private TextView angleView;
	private View crosshair;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mCamera = getCameraInstance();
		mCamera.setDisplayOrientation(90);
		
		mPreview = new CameraScopeView(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_layout);
        preview.addView(mPreview);
		
		handler = new Handler();
		
		angleView = (TextView)findViewById(R.id.gyroangle);
		crosshair = (View)findViewById(R.id.crosshairView);
				
		initializationProcess();

		retreieveCenterPointFromUserOrHistory();
	}

	private void retreieveCenterPointFromUserOrHistory() 
	{
		if (getIntent().getExtras().getBoolean("firstTimeRunningApp"))
		{
			askUserCenterPointOption();
		}
		else
		{
			retrieveCenterPointFromHistory();
		}		
	}

	private void initializationProcess() 
	{
		checkCameraAndGyrscopeAvailability();
		initializeSensorAngles();
		initializeFuseTimer();
		initializeMetrics();
		registerSensorListeners();
	}

	private void checkCameraAndGyrscopeAvailability() 
	{
		hasCamera = getIntent().getExtras().getBoolean("hasCamera");
		hasGyroscope = getIntent().getExtras().getBoolean("hasGyroscope");
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

	@Override
	public void onResume() 
	{
		super.onResume();
		
		if(sensorManager != null) 
		{
			registerSensorListeners();
		}
	}
	
    @Override
    public void onStop() 
    {
    	super.onStop();
    	mCamera.release();
    	sensorManager.unregisterListener(this);
    }
    
    @Override
    protected void onPause() 
    {
        super.onPause();
        mCamera.release();
        sensorManager.unregisterListener(this);
    }
	
	private void registerSensorListeners() 
	{
		sensorManager.registerListener(this, 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				SensorManager.SENSOR_DELAY_FASTEST);
		
		sensorManager.registerListener(this, 
				sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
				SensorManager.SENSOR_DELAY_FASTEST);
		
		if (hasGyroscope) {
			sensorManager.registerListener(this, 
					sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), 
					SensorManager.SENSOR_DELAY_FASTEST);
		}
	}
	
	private void retrieveCenterPointFromHistory()
	{
		// TODO Retrieve centerpoint from History		
	}

	private void drawHUD() 
	{
		drawCrosshair();
		//dawOtherHUDComponents();
	}

	private void drawCrosshair() 
	{
		CrosshairView crosshairView = (CrosshairView) findViewById(R.id.crosshairView);
		crosshairView.setCenterPoint(centerPoint);
		crosshairView.setRotation(sensorAngles.getPitchAngle());
		crosshairView.setVisibility(View.VISIBLE);
	}

	private void configureCenterPoint() 
	{
		// TODO Grab coordinates from touchscreen tap
	}

	private void askUserCenterPointOption() 
	{
		DialogFragment newFragment = new CalibrationDialogFragment();
		newFragment.show(getSupportFragmentManager(), "calibration");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}
	
	public void askUserToSelectCenterPointFromScreen()
	{
		// TODO Implement touch and grab coordinates
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
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

	public void updateHUD()
	{
		handler.post(updateCrosshairAngleTask);
	}

	private void setCenterPointDefault() 
	{
		centerPoint = new Point(metrics.widthPixels/2, metrics.heightPixels/2);
		drawHUD();
	}
	
	private Runnable updateCrosshairAngleTask = new Runnable() {
		public void run()
		{
			updateCrosshairAngle();
		}
	};
	
	private void updateCrosshairAngle()
	{
		angleView.setText(Float.toString(sensorAngles.getPitchAngle()));
		crosshair.setRotation(-(int)sensorAngles.getRollAngle());
	}
	
	@Override
	public void onOptionSelected(int id) 
	{
		if(id == -1) // Dialog "Positive" Button Selected - "Configure"
		{
			Log.e("CALLBACK", "Configure Callback Works");
			configureCenterPoint();
		}
		if (id == -2) // Dialog "Negative" Button Selected - "Default"
		{
			// TODO Auto-generated method stub
			Log.e("CALLBACK", "Default Callback Works");
			setCenterPointDefault();
			
		}
	}
	
	public static Camera getCameraInstance()
	{
	    Camera c = null;
	    
	    try 
	    {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e)
	    {
	        // Camera is not available (in use or does not exist)
	    }
	    
	    return c; // returns null if camera is unavailable
	}
}
