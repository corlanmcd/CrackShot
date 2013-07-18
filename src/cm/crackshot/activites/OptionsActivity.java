package cm.crackshot.activites;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
	private Spinner		reticleColorSpinner;
	private Spinner		scopeColorSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
		measurementSystem 	= 'y'; //yards is default
		
		meters 				= (RadioButton)findViewById(R.id.OptionsActivity_meters_radiobox);
		yards 				= (RadioButton)findViewById(R.id.OptionsActivity_yards_radiobox);
		reticleColorSpinner = (Spinner)findViewById(R.id.OptionsActivity_reticle_color_spinner);
		scopeColorSpinner 	= (Spinner)findViewById(R.id.OptionsActivity_scope_color_spinner);
		
		populateScopeAndReticleColorSpinner();
		registerViewListeners();
	}
	
	private void registerViewListeners() 
	{
		((Button) 		findViewById(R.id.OptionsActivity_backButton)).setOnClickListener(this);
		((RadioButton)	findViewById(R.id.OptionsActivity_meters_radiobox)).setOnClickListener(this);
		((RadioButton)	findViewById(R.id.OptionsActivity_yards_radiobox)).setOnClickListener(this);
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
		
		if (view.getId() == R.id.OptionsActivity_meters_radiobox)
		{			
			if(yards.isChecked())
			{
				yards.setChecked(false);
			}
			
			measurementSystem = 'm';
		}
		else if (view.getId() == R.id.OptionsActivity_yards_radiobox)
		{			
			if(meters.isChecked())
			{
				meters.setChecked(false);
			}
			
			measurementSystem = 'y';
		}
		
		if (view.getId() == R.id.OptionsActivity_backButton)
		{
			startMainMenuActivityFromIntent();
		}
	}

	private void populateScopeAndReticleColorSpinner()
	{
		List<String> colorList = new ArrayList<String>();
		
		colorList.add("Blue");
		colorList.add("Green");
		colorList.add("Indigo");
		colorList.add("Orange");
		colorList.add("Red");
		colorList.add("Yellow");
		
		ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colorList);
		
		colorAdapter		.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		reticleColorSpinner	.setAdapter(colorAdapter);
		reticleColorSpinner	.setSelection(4);
		scopeColorSpinner	.setAdapter(colorAdapter);
		scopeColorSpinner	.setSelection(3);
	}

	private void startMainMenuActivityFromIntent() 
	{
		Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
		
		mainMenuIntent.putExtra("measurementSystem", measurementSystem);
		mainMenuIntent.putExtra("reticleColor", getReticleColor());
		mainMenuIntent.putExtra("scopeColor", getScopeColor());
		startActivity(mainMenuIntent);
		finish();
	}

	private int getReticleColor() 
	{
		String color = (String)reticleColorSpinner.getSelectedItem();
		
		if(color.equals("Blue"))
		{
			return 0xff0000ff;
		}
		else if(color.equals("Green"))
		{
			return 0xff00ff00;
		}
		else if(color.equals("Indigo"))
		{
			return 0xff6600ff;
		}
		else if(color.equals("Orange"))
		{
			return 0xffff8800;
		}
		else if(color.equals("Red"))
		{
			return 0xffff0000;
		}
		else if(color.equals("Yellow"))
		{
			return 0xffffff00;
		}
			
		return 0xffff8800;
	}
	
	private int getScopeColor() 
	{
		String color = (String)scopeColorSpinner.getSelectedItem();
		
		if(color.equals("Blue"))
		{
			return 0xff0000ff;
		}
		else if(color.equals("Green"))
		{
			return 0xff00ff00;
		}
		else if(color.equals("Indigo"))
		{
			return 0xff6600ff;
		}
		else if(color.equals("Orange"))
		{
			return 0xffff8800;
		}
		else if(color.equals("Red"))
		{
			return 0xffff0000;
		}
		else if(color.equals("Yellow"))
		{
			return 0xffffff00;
		}
			
		return 0xffff8800;
	}
}
