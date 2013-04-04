package cm.crackshot.activites;

import cm.crackshot.R;
import cm.crackshot.data.SensorAngles;
import cm.crackshot.fragments.CalibrationDialogFragment;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class StartActivity extends FragmentActivity {

	SensorAngles sensorAngles;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		sensorAngles = new SensorAngles();
		
		askUserToSelectCenterPoint();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_start, menu);
		return true;
	}

	private void askUserToSelectCenterPoint()
	{
		DialogFragment newFragment = new CalibrationDialogFragment();
		newFragment.show(getSupportFragmentManager(), "calibration");
	}
}
