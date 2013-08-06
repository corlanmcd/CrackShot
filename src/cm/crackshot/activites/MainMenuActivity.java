/************************************************************************************
 * Author: 		Corlan McDonald
 * Date:		February - July 2013
 * Program:		Crack.Shot
 * Version:		1.0 
 * Description:	Crack.Shot is an Android application that serves as a digital 
 * 				scope for Paintball. It provides targeting guidance based on the 
 * 				selected distance between the user and the target, and will then 
 * 				provide a targeting box calibrated for that distance while also
 * 				recommending the angle at which the paintball gun should be held.
 * 				Crack.Shot also provides a few other nifty features, such as the 
 * 				ability to switch between ammunition types (rounded & shaped),
 * 				scope zeroing, scope customization, and dynamic orientation of the
 * 				scope/reticle.
 * 
 * 				Currently, I'm only sure that Crack.Shot works on Android 4.0+ 
 * 				devices. I had very limited, somewhat successful testing with 2.2+
 * 				devices, but then my friend who owned the 2.2 device lost his
 * 				phone... otherwise, pending unusual camera orientations, it should
 * 				work.
 * 
 * 				Additionally, comments are quite limited, in attempt to write 
 * 				"self-documenting" code in the way of method naming schemes, 
 * 				constant refactoring, and limited many functions to their named
 * 				purposes (although this idea went to the wind as deadlines came up).
 * 				So hopefully, with the exception of a few strings of spaghetti code,
 * 				it is readable without the need for comments. In the future,
 * 				with continued improvement, the architecture of Crack.Shot
 * 				will be cleaned up.
 ************************************************************************************/

package cm.crackshot.activites;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cm.crackshot.R;

public class MainMenuActivity extends Activity implements OnClickListener
{
	private boolean hasCamera = false;
	private boolean hasGyroscope = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		registerViewListeners();
		checkCameraGyroscopeAvailabililty();
		checkCameraAvailability();
	}
	
	private void checkCameraGyroscopeAvailabililty() 
	{
		checkCameraAvailability();
		checkGyroscopeAvailability();
	}

	private void checkCameraAvailability() 
	{
		PackageManager pm = getPackageManager();
		hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}
	
	private void checkGyroscopeAvailability() 
	{
		PackageManager pm = getPackageManager();
		hasGyroscope = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
	}

	private void registerViewListeners() 
	{
		((Button) findViewById(R.id.MainMenuActivity_start_button)).setOnClickListener(this);
		((Button) findViewById(R.id.MainMenuActivity_options_button)).setOnClickListener(this);
		((Button) findViewById(R.id.MainMenuActivity_about_button)).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_menu, menu);
		return true;
	}

	@Override
	public void onClick(View view) 
	{
		if (view.getId() == R.id.MainMenuActivity_start_button)
		{
			startScopeActvityFromIntent();
		}
		else if (view.getId() == R.id.MainMenuActivity_options_button)
		{
			startOptionsActivityFromIntent();
		}
		else if (view.getId() == R.id.MainMenuActivity_about_button)
		{
			startAboutActivityFromIntent();
		}
	}

	private void startScopeActvityFromIntent() 
	{
		Intent scopeIntent = new Intent(this, ScopeActivity.class);
		
		scopeIntent.putExtra("hasCamera", hasCamera);
		scopeIntent.putExtra("hasGyroscope", hasGyroscope);
		
		if(getIntent().getExtras() != null)
		{
			if(getIntent().getExtras().containsKey("measurementSystem"))
			{
				scopeIntent.putExtra("measurementSystem", getIntent().getExtras().getChar("measurementSystem"));
			}
			
			if(getIntent().getExtras().containsKey("reticleColor"))
			{
				scopeIntent.putExtra("reticleColor", getIntent().getExtras().getInt("reticleColor"));
			}
			
			if(getIntent().getExtras().containsKey("scopeColor"))
			{
				scopeIntent.putExtra("scopeColor", getIntent().getExtras().getInt("scopeColor"));
			}
		}
		
		startActivity(scopeIntent);
		
		finish();
	}
	
	private void startOptionsActivityFromIntent() 
	{
		Intent optionsIntent = new Intent(this, OptionsActivity.class);
		startActivity(optionsIntent);
		
		finish();
	}
	
	private void startAboutActivityFromIntent() 
	{
		Intent aboutIntent = new Intent(this, AboutActivity.class);		
		startActivity(aboutIntent);
		
		finish();
	}
}
