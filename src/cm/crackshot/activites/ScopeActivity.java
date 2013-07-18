package cm.crackshot.activites;

import java.util.List;
import java.util.Timer;

import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import cm.crackshot.R;
import cm.crackshot.data.AngleManager;
import cm.crackshot.data.Ballistics;
import cm.crackshot.data.RepeatListener;
import cm.crackshot.fragments.CalibrationDialogFragment;
import cm.crackshot.views.CameraScopeView;
import cm.crackshot.views.CrosshairView;

public class ScopeActivity extends FragmentActivity implements SensorEventListener, 
	CalibrationDialogFragment.OnOptionSelectedListener, OnTouchListener, OnClickListener
{
	private static final int TIME_CONSTANT = 30;


	private boolean hasCamera;
	private boolean hasGyroscope;
	private char	measurementSystem;
	private int		currentExposure;
	private int		minExposure;
	private int		maxExposure;
	private int		reticleColor;
	private int		scopeColor;
	private int 	selectedRange;
	private String	ammoType;
	
	private Ballistics 				ballistics;
	private Button					ammoSelectionButton;
	private Button					brightnessDecreaseButton;
	private Button					brightnessIncreaseButton;
	private Button					windageCenterButton;
	private Button					windageLeftButton;
	private Button					windageRightButton;
	private Button					windageSetButton;
	private Camera 					mCamera;
	private CameraInstanceTask 		cameraInstanceTask;
    private CameraScopeView 		mPreview;
    private Camera.Parameters 		params;
    private CrosshairView 			crosshairView;
	private DisplayMetrics 			metrics;	
	private Handler 				handler;
	private Point 					centerpoint;
	private AngleManager 			angleManager;
	private SensorManager 			sensorManager;
	private Timer 					fuseTimer;
	private TextView 				angleView;
	private TextView 				selectedRangeView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView		(R.layout.activity_scope);
		
		sensorManager 		= (SensorManager) getSystemService(SENSOR_SERVICE);
		measurementSystem	= getIntent().getExtras().getChar("measurementSystem");
		reticleColor		= getIntent().getExtras().getInt("reticleColor");
		scopeColor			= getIntent().getExtras().getInt("scopeColor");
		
		hasCamera 			= false;
		hasGyroscope		= false;
		selectedRange 		= 25;
		ammoType 			= "round";
		ballistics 			= new Ballistics();
		handler 			= new Handler();
		centerpoint 		= new Point();
		mCamera 			= null;
		mPreview 			= null;
		
		findViews();
		setSelectedRangeViewText();
		setListeners();
			
		
		initializeCameraSensorsAndHUD();
	}
	
	private void findViews()
	{
		ammoSelectionButton 		= (Button)findViewById(R.id.ammoSelectionButton);		
		brightnessDecreaseButton 	= (Button)findViewById(R.id.ScopeActivity_decrease_brightness_button);
		brightnessIncreaseButton 	= (Button)findViewById(R.id.ScopeActivity_increase_brightness_button);
		windageLeftButton			= (Button)findViewById(R.id.StartActivity_WindageLeftButton);
		windageRightButton			= (Button)findViewById(R.id.StartActivity_WindageRightButton);
		windageSetButton			= (Button)findViewById(R.id.StartActivity_WindageSet);
		windageCenterButton 		= (Button)findViewById(R.id.ScopeActivity_windage_center_button);
		angleView 					= (TextView)findViewById(R.id.gyroangle);
		selectedRangeView 			= (TextView)findViewById(R.id.selectedRange);
		crosshairView 				= (CrosshairView)findViewById(R.id.crosshairView);
	}
	
	private void setListeners()
	{
		ammoSelectionButton.setOnClickListener(this);
		
		windageLeftButton.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
			  @Override
			  public void onClick(View view) {
			    crosshairView.subtractPixelFromRadius(-5);
			  }
			}));
		
		windageRightButton.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
			  @Override
			  public void onClick(View view) {
			    crosshairView.subtractPixelFromRadius(5);
			  }
			}));
		
		windageSetButton.setOnClickListener(this);
		
		windageCenterButton.setOnClickListener(this);
		
		brightnessDecreaseButton.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
			@Override
			public void onClick(View view) 
			{
				if((currentExposure - 1) >= minExposure)
				{
					params.setExposureCompensation(currentExposure - 1);
					mCamera.setParameters(params);

					currentExposure--;
				}
			}
		}));
		
		brightnessIncreaseButton.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
			@Override
			public void onClick(View view) {
				if((currentExposure + 1) <= maxExposure)
				{
					params.setExposureCompensation(currentExposure + 1);
					mCamera.setParameters(params);

					currentExposure++;
				}
			}
		}));
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
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			registerSensorListeners();
		}
		
		if(mCamera == null && cameraInstanceTask.getStatus() == AsyncTask.Status.FINISHED)
		{
			initializeCamera();
		}
	}
	
    @Override
    public void onStop() 
    {
    	super.onStop();
    	
    	releaseCamera();
        
    	if(sensorManager != null)
    	{
    		sensorManager.unregisterListener(this);
    		sensorManager = null;
    	}
    }
    
    @Override
    protected void onPause() 
    {
        super.onPause();
        releaseCamera();
        sensorManager.unregisterListener(this);
        sensorManager = null;
    }
    
    @Override
    public void onBackPressed()
    {
    	Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
		startActivity(mainMenuIntent);
		
		finish();
    }

    private void initializeCameraSensorsAndHUD() 
	{
		if(reticleColor == 0)
			reticleColor 	= android.graphics.Color.RED;
		if(scopeColor == 0)
			scopeColor 		= 0xffff8800;
    	
    	if(hasCameraAndGyrscopeAvailability())
		{
			initializeAngleManagerAndFuseTimer();
			initializeMetrics();
			registerSensorListeners();
			askUserCenterPointOption();
			setReticleColor();
			setScopeColor();
		}
	}
    
    private boolean hasCameraAndGyrscopeAvailability() 
	{
		hasCamera 		= getIntent().getExtras().getBoolean("hasCamera");
		hasGyroscope 	= getIntent().getExtras().getBoolean("hasGyroscope");
		
		if(hasCamera)
		{	
			initializeCamera();
			
			return true;
		}
		
		return false;
	}
    
    private void initializeCamera() 
	{
		if(mCamera == null)
		{
			cameraInstanceTask 	= new CameraInstanceTask();
			cameraInstanceTask.execute();
		}
	}
    
    private void initializeAngleManagerAndFuseTimer() 
	{
		angleManager = new AngleManager(hasGyroscope);
		
		fuseTimer = new Timer();
		fuseTimer.scheduleAtFixedRate(angleManager.new calculateFusedOrientationTask(), 1000, TIME_CONSTANT);
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
	
	private void setReticleColor()
	{
		crosshairView.setReticleColor(reticleColor);
	}
	
	private void setScopeColor()
	{
		crosshairView.setScopeColor(scopeColor);
	}
	
	private void askUserCenterPointOption() 
	{
		DialogFragment calibrationOptionDialog = new CalibrationDialogFragment();
		
		calibrationOptionDialog.setCancelable(false);
		calibrationOptionDialog.show(getSupportFragmentManager(), "calibration");
	}
		
	private void drawHUD() 
	{
		drawCrosshair();
	}

	private void drawCrosshair() 
	{
		crosshairView.setCenterPoint(centerpoint);
		crosshairView.setSelectedRange(selectedRange);
		crosshairView.setRotation(angleManager.getPitchAngle());
		crosshairView.setVisibility(View.VISIBLE);
	}
			
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) 
    {
        int action 	= event.getAction();
        int keyCode = event.getKeyCode();
        
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
                    		if(ammoType.equals("shaped"))
                    		{
                    			selectedRange = 75;
                    		}
                    		break;
                    	case 125:
                    		selectedRange = 100;
                    		break;
                    }
                    updateSelectedRangeTextAndTargeting();
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
                    		if(ammoType.equals("shaped"))
                    		{
                    			selectedRange = 75;
                    		}
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
                    updateSelectedRangeTextAndTargeting();
                }
                return true;
                
            default:
                return super.dispatchKeyEvent(event);
        }
    }
    
	private void updateSelectedRangeTextAndTargeting()
	{
		setSelectedRangeViewText();
	}

	private void setCenterPointDefault() 
	{
		centerpoint 		= new Point(metrics.widthPixels/2, metrics.heightPixels/2);
		
		drawHUD();
	}

	@Override
	public void onDialogOptionSelected(int id) 
	{
		if(id == -1) // Dialog "Positive" Button Selected - "Configure"
		{
			Log.e("CALLBACK", "Configure Callback Works");
			windageCenterButton	.setVisibility(View.VISIBLE);
			windageCenterButton	.setEnabled(true);
			windageLeftButton	.setVisibility(View.VISIBLE);
			windageLeftButton	.setEnabled(true);
			windageRightButton	.setVisibility(View.VISIBLE);
			windageRightButton	.setEnabled(true);
			windageSetButton	.setVisibility(View.VISIBLE);
			windageSetButton	.setEnabled(true);
			
			setCenterPointDefault();
			
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
					angleManager.calculateAccelMagneticOrientation(event);
					break;
				
				case Sensor.TYPE_GYROSCOPE:
					angleManager.calculateGyroscopicOrientation(event);
					break;
				
				case Sensor.TYPE_MAGNETIC_FIELD:
					angleManager.setMagneticField(event);
					break;
			}
		}
		
		updateHUD();
	}
	
	private void updateHUD()
	{
		handler.post(updateAngleAndTargetting);
	}
	
	private Runnable updateAngleAndTargetting = new Runnable()
	{
		public void run()
		{
			updateCrosshairAngle();
			updateTargetingBox();
		}
	};
	
	private void updateCrosshairAngle()
	{
		angleView			.setText(Float.toString(angleManager.getPitchAngle()));
		angleView			.setTextColor(android.graphics.Color.RED);
		selectedRangeView	.setTextColor(android.graphics.Color.BLACK);
		crosshairView		.setSelectedRange(selectedRange);
		crosshairView		.invalidate();
		crosshairView		.setRotation(-(int)angleManager.getRollAngle());
	}
	
	private void updateTargetingBox()
	{
		// The value of the pitch angle above the horizontal is always negative (due to camera coord remap)
		if (Math.abs(angleManager.getPitchAngle() - ballistics.getAngleByDistance(ammoType, selectedRange)) <= 1.0f)
		{
			crosshairView.setGoodAngle(true);
		}
		else
		{
			crosshairView.setGoodAngle(false);
		}
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
			
			params 	= mCamera.getParameters();
			
			enableCameraFocusFeature();
			addCameraToCameraView();
 	     }
 	 }
 	
 	private void enableCameraFocusFeature() 
	{
		List<String> focusModes 	= params.getSupportedFocusModes();
		
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) 
		{
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		}
		
		currentExposure = params.getExposureCompensation();
		minExposure 	= params.getMinExposureCompensation();
		maxExposure 	= params.getMaxExposureCompensation();
		
		params.setSceneMode(Camera.Parameters.SCENE_MODE_HDR);
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
		{
			if(params.isVideoStabilizationSupported())
			{
				params.setVideoStabilization(true);
			}
		}
		
		if(params.getMinExposureCompensation() != 0 && params.getMaxExposureCompensation() != 0)
		{
			params.setExposureCompensation(params.getMaxExposureCompensation());
		}
		
		mCamera.setParameters(params);
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
            //mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
			mCamera.release();        // release the camera for other applications
            mCamera = null;
        }		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View view) 
	{
		if(view.getId() == R.id.ammoSelectionButton)
		{
			if(ammoType.equals("shaped"))
			{
				if(selectedRange > 50)
				{
					selectedRange = 50;
				}
				
				ammoType = "round";
				//ammoSelectionButton.setText("ROUND");
				ammoSelectionButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ammoselectionround));
			}
			else
			{
				ammoType = "shaped";
				//ammoSelectionButton	.setText("SHAPED");
				ammoSelectionButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ammoselectionshaped));
			}
			
			setSelectedRangeViewText();
			crosshairView.setAmmoType(ammoType);
		}
		
		if(view.getId() == R.id.StartActivity_WindageSet)
		{
			windageCenterButton	.setVisibility(View.GONE);
			windageCenterButton	.setEnabled(false);
			windageLeftButton	.setVisibility(View.GONE);
			windageLeftButton	.setEnabled(false);
			windageRightButton	.setVisibility(View.GONE);
			windageRightButton	.setEnabled(false);
			windageSetButton	.setVisibility(View.GONE);
			windageSetButton	.setEnabled(false);
		}
		if(view.getId() == R.id.ScopeActivity_windage_center_button)
		{
			crosshairView.resetScopeOffset();
		}
	}

	private void setSelectedRangeViewText() 
	{
		if(measurementSystem == 'm')
		{
			selectedRangeView.setText(Integer.toString((int)Math.ceil(selectedRange * 0.9144)) + " m");
		}
		else
		{
			selectedRangeView.setText(Integer.toString(selectedRange) + " yd");
		}
	}
}
