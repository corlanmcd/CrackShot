package cm.crackshot.activites;

import cm.crackshot.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity implements OnClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		registerViewListeners();
	}

	private void registerViewListeners() 
	{
		((Button) findViewById(R.id.MainMenuActivity_startButton)).setOnClickListener(this);
		((Button) findViewById(R.id.MainMenuActivity_optionsButton)).setOnClickListener(this);
		((Button) findViewById(R.id.MainMenuActivity_aboutButton)).setOnClickListener(this);
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
		if (view.getId() == R.id.MainMenuActivity_startButton)
		{
			startScopeActvityFromIntent();
		}
		else if (view.getId() == R.id.MainMenuActivity_optionsButton)
		{
			startOptionsActivityFromIntent();
		}
		else
		{
			startAboutActivityFromIntent();
		}
	}

	private void startScopeActvityFromIntent() 
	{
		Intent scopeIntent = new Intent(this, StartActivity.class);
		startActivity(scopeIntent);
	}
	
	private void startOptionsActivityFromIntent() 
	{
		Intent optionsIntent = new Intent(this, OptionsActivity.class);
		startActivity(optionsIntent);
	}
	
	private void startAboutActivityFromIntent() 
	{
		Intent aboutIntent = new Intent(this, AboutActivity.class);
		startActivity(aboutIntent);
	}
}
