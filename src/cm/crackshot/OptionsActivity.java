package cm.crackshot;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class OptionsActivity extends Activity implements OnClickListener 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);
		
		registerViewListeners();
	}
	
	private void registerViewListeners() 
	{
		((Button) findViewById(R.id.OptionsActivity_backButton)).setOnClickListener(this);
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
		if (view.getId() == R.id.OptionsActivity_backButton)
		{
			startMainMenuActivityFromIntent();
		}
	}

	private void startMainMenuActivityFromIntent() {
		Intent mainMenuIntent = new Intent(this, MainMenuActivity.class);
		startActivity(mainMenuIntent);
		finish();
	}

}
