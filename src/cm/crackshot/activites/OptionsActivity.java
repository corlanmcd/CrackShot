package cm.crackshot.activites;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import cm.crackshot.R;

public class OptionsActivity extends Activity implements OnClickListener
{

	private char measurementSystem;
	
	private RadioButton yards;
	private RadioButton meters;
	private SharedPreferences	sharedPrefs;
	private SharedPreferences.Editor	editor;
	private Spinner		reticleColorSpinner;
	private Spinner		scopeColorSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
		sharedPrefs 		= PreferenceManager.getDefaultSharedPreferences(this);
		editor				= sharedPrefs.edit();
		
		measurementSystem 	= (char)sharedPrefs.getInt("measurementSystem", Character.getNumericValue('y')); //yards is default
		
		meters 				= (RadioButton)findViewById(R.id.OptionsActivity_meters_radio_button);
		yards 				= (RadioButton)findViewById(R.id.OptionsActivity_yards_radio_button);
		reticleColorSpinner = (Spinner)findViewById(R.id.OptionsActivity_reticle_color_spinner);
		scopeColorSpinner 	= (Spinner)findViewById(R.id.OptionsActivity_scope_color_spinner);
		
		selectCorrectMeasurementSystem();
		populateScopeAndReticleColorSpinner();
		registerViewListeners();
	}
	
	private void registerViewListeners() 
	{
		((Button) 		findViewById(R.id.OptionsActivity_back_button)).setOnClickListener(this);
		((RadioButton)	findViewById(R.id.OptionsActivity_meters_radio_button)).setOnClickListener(this);
		((RadioButton)	findViewById(R.id.OptionsActivity_yards_radio_button)).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_options, menu);
		return true;
	}

	@Override
    public void onBackPressed()
    {
    	Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
		startActivity(mainMenuIntent);
		
		finish();
    }
	
	@Override
	public void onClick(View view) 
	{
		if (view.getId() == R.id.OptionsActivity_meters_radio_button)
		{			
			if(yards.isChecked())
			{
				yards.setChecked(false);
			}
			
			measurementSystem = 'm';
		}
		else if (view.getId() == R.id.OptionsActivity_yards_radio_button)
		{			
			if(meters.isChecked())
			{
				meters.setChecked(false);
			}
			
			measurementSystem = 'y';
		}
		
		editor.putInt("measurementSystem", measurementSystem);
		
		if (view.getId() == R.id.OptionsActivity_back_button)
		{
			startMainMenuActivityFromIntent();
		}
	}

	private void selectCorrectMeasurementSystem()
	{
		if(measurementSystem == 'm')
		{
			meters.setChecked(true);
			yards.setChecked(false);
		}
	}
	
	private void populateScopeAndReticleColorSpinner()
	{
		List<String> colorList = new ArrayList<String>();
		
		colorList.add("Blue");
		colorList.add("Green");
		colorList.add("Orange");
		colorList.add("Purple");
		colorList.add("Red");
		colorList.add("Yellow");
		
		ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colorList);
		
		colorAdapter		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		reticleColorSpinner	.setAdapter(colorAdapter);
		
		reticleColorSpinner	.setSelection(sharedPrefs.getInt("reticleColorIndex", 4));
		scopeColorSpinner	.setAdapter(colorAdapter);
		scopeColorSpinner	.setSelection(sharedPrefs.getInt("scopeColorIndex", 2));
	}

	private void startMainMenuActivityFromIntent() 
	{
		Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
		
		editor.putInt("measurementSystem", measurementSystem);
		editor.putInt("reticleColor", getReticleColor());
		editor.putInt("scopeColor", getScopeColor());
		editor.commit();
		
		startActivity(mainMenuIntent);
		finish();
	}

	private int getReticleColor() 
	{
		String color = (String)reticleColorSpinner.getSelectedItem();
		
		editor.putInt("reticleColorIndex", reticleColorSpinner.getSelectedItemPosition());
		
		if(color.equals("Blue"))
		{
			return 0xff0099CC;
		}
		else if(color.equals("Green"))
		{
			return 0xff669900;
		}
		else if(color.equals("Orange"))
		{
			return 0xffff8800;
		}
		else if(color.equals("Purple"))
		{
			return 0xff9933cc;
		}
		else if(color.equals("Red"))
		{
			return 0xffCC0000;
		}
		else if(color.equals("Yellow"))
		{
			return 0xffffde00;
		}
			
		return 0xffff8800;
	}
	
	private int getScopeColor() 
	{
		String color = (String)scopeColorSpinner.getSelectedItem();
		
		editor.putInt("scopeColorIndex", scopeColorSpinner.getSelectedItemPosition());
		
		if(color.equals("Blue"))
		{
			return 0xff0099CC;
		}
		else if(color.equals("Green"))
		{
			return 0xff669900;
		}
		else if(color.equals("Orange"))
		{
			return 0xffff8800;
		}
		else if(color.equals("Purple"))
		{
			return 0xff9933cc;
		}
		else if(color.equals("Red"))
		{
			return 0xffCC0000;
		}
		else if(color.equals("Yellow"))
		{
			return 0xffffde00;
		}
			
		return 0xffff8800;
	}
}
