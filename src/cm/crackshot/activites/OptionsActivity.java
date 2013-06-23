package cm.crackshot.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import cm.crackshot.R;

public class OptionsActivity extends Activity implements OnClickListener 
{

	private String measurementSystem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
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
	public void onClick(View view) 
	{
		
		if (view.getId() == R.id.OptionsActivity_meters_radiobox)
		{
			RadioButton yards = (RadioButton)findbyViewId(R.id.OptionsActivity_yards_radiobox);
			
			if(yards.isChecked())
			{
				yards.setChecked(false);
			}
		}
		else if (view.getId() == R.id.OptionsActivity_yards_radiobox)
		{
			RadioButton meters = (RadioButton)findbyViewId(R.id.OptionsActivity_yards_radiobox);
			
			if(meters.isChecked())
			{
				meters.setChecked(false);
			}
		}
		
		
		if (view.getId() == R.id.OptionsActivity_backButton)
		{
			startMainMenuActivityFromIntent();
		}
	}

	private View findbyViewId(int optionsactivityYardsRadiobox) {
		// TODO Auto-generated method stub
		return null;
	}

	private void startMainMenuActivityFromIntent() 
	{
		Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
		
		startActivity(mainMenuIntent);
		finish();
	}

}
